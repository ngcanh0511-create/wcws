package com.wcpl.service;

import com.wcpl.dto.request.PlacePredictionRequest;
import com.wcpl.dto.response.PlacePredictionResponse;
import com.wcpl.dto.response.PredictionHistoryResponse;
import com.wcpl.entity.*;
import com.wcpl.exception.AppException;
import com.wcpl.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.Objects;

@Service
@RequiredArgsConstructor
public class PredictionService {

    private final PredictionRepository predictionRepository;
    private final BettingLineRepository bettingLineRepository;
    private final MatchRepository matchRepository;
    private final UserRepository userRepository;
    private final SystemConfigRepository configRepository;
    private final CreditService creditService;

    @Transactional
    public PlacePredictionResponse place(User user, PlacePredictionRequest req) {
        BettingLine line = bettingLineRepository.findById(req.bettingLineId())
                .orElseThrow(() -> AppException.notFound("Kèo không tồn tại"));

        Match match = line.getMatch();

        if (match.isPredictionLocked()) {
            throw AppException.badRequest("PREDICTION_LOCKED", "Kèo đã bị khóa, không thể đặt nữa");
        }

        if (!"SCHEDULED".equals(match.getStatus())) {
            throw AppException.badRequest("MATCH_NOT_SCHEDULED", "Trận đấu không ở trạng thái có thể đặt kèo");
        }

        int minBet = getConfigInt("min_bet", 10);
        int maxBetPercent = getConfigInt("max_bet_percent", 100);
        int maxBet = (int) Math.ceil(user.getCredits() * maxBetPercent / 100.0);

        if (req.creditBet() < minBet) {
            throw AppException.badRequest("BET_TOO_SMALL", "Đặt cược tối thiểu là " + minBet + " credit");
        }
        if (req.creditBet() > user.getCredits()) {
            throw AppException.badRequest("INSUFFICIENT_CREDITS", "Không đủ credit");
        }
        if (req.creditBet() > maxBet) {
            throw AppException.badRequest("BET_TOO_LARGE", "Đặt tối đa " + maxBetPercent + "% credit hiện có");
        }

        creditService.deductCredits(user, req.creditBet(), "BET",
                Objects.requireNonNull(line.getId()),
                "Đặt kèo: " + line.getDescription());

        Prediction prediction = new Prediction();
        prediction.setUser(user);
        prediction.setBettingLine(line);
        prediction.setMatch(match);
        prediction.setCreditBet(req.creditBet());
        prediction.setStatus("PENDING");
        Prediction saved = predictionRepository.save(prediction);
        return PlacePredictionResponse.from(saved, user.getCredits());
    }

    @Transactional
    public void settlePredictionsForMatch(Long matchId) {
        List<Prediction> pending = predictionRepository.findByMatchIdAndStatus(matchId, "PENDING");

        for (Prediction p : pending) {
            BettingLine bettingLine = p.getBettingLine();
            if (bettingLine == null) continue;

            String lineResult = bettingLine.getResult();
            if (lineResult == null) continue;

            if ("WIN".equals(lineResult)) {
                int payout = (int) Math.round(p.getCreditBet() * bettingLine.getOdds());
                p.setCreditResult(payout);
                p.setStatus("WON");

                User user = userRepository.findById(Objects.requireNonNull(p.getUser().getId()))
                        .orElseThrow();
                creditService.addCredits(user, payout, "WIN",
                        Objects.requireNonNull(p.getId()),
                        "Thắng kèo: " + bettingLine.getDescription());

            } else if ("LOSE".equals(lineResult)) {
                p.setCreditResult(-p.getCreditBet());
                p.setStatus("LOST");

            } else if ("VOID".equals(lineResult)) {
                p.setCreditResult(0);
                p.setStatus("VOID");

                User user = userRepository.findById(Objects.requireNonNull(p.getUser().getId()))
                        .orElseThrow();
                creditService.addCredits(user, p.getCreditBet(), "VOID_REFUND",
                        Objects.requireNonNull(p.getId()),
                        "Hoàn trả kèo void: " + bettingLine.getDescription());
            }

            predictionRepository.save(p);
        }
    }

    @Transactional(readOnly = true)
    public Page<PredictionHistoryResponse> getUserPredictions(Long userId, Pageable pageable) {
        return predictionRepository.findByUserIdOrderByCreatedAtDesc(userId, pageable)
                .map(PredictionHistoryResponse::from);
    }

    public Map<String, Object> getUserStats(Long userId) {
        List<Prediction> all = predictionRepository
                .findRecentByUser(userId, PageRequest.of(0, Integer.MAX_VALUE));
        long total = all.size();
        long won = all.stream().filter(p -> "WON".equals(p.getStatus())).count();
        long lost = all.stream().filter(p -> "LOST".equals(p.getStatus())).count();
        double winRate = total > 0 ? (double) won / total * 100 : 0;

        return Map.of(
                "total", total,
                "won", won,
                "lost", lost,
                "winRate", Math.round(winRate * 10.0) / 10.0
        );
    }

    private int getConfigInt(String key, int defaultVal) {
        return configRepository.findById(key)
                .map(c -> Integer.parseInt(c.getValue()))
                .orElse(defaultVal);
    }
}
