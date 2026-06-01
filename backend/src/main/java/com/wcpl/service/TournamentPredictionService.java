package com.wcpl.service;

import com.wcpl.dto.request.TournamentPredictionRequest;
import com.wcpl.dto.response.TournamentPredictionOptionsResponse;
import com.wcpl.dto.response.TournamentPredictionResponse;
import com.wcpl.entity.*;
import com.wcpl.exception.AppException;
import com.wcpl.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Objects;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class TournamentPredictionService {

    private static final Set<String> VALID_TYPES = Set.of("CHAMPION", "RUNNER_UP", "TOP_SCORER");

    private final TournamentPredictionRepository tournamentPredictionRepository;
    private final TeamRepository teamRepository;
    private final SystemConfigRepository configRepository;
    private final CreditService creditService;

    public List<TournamentPredictionResponse> getMyPredictions(Long userId) {
        return tournamentPredictionRepository.findByUserId(userId).stream()
                .map(TournamentPredictionResponse::from)
                .toList();
    }

    public TournamentPredictionOptionsResponse getOptions() {
        return new TournamentPredictionOptionsResponse(teamRepository.findAll(), List.of());
    }

    @Transactional
    public TournamentPredictionResponse place(User user, TournamentPredictionRequest req) {
        if (!VALID_TYPES.contains(req.predictionType())) {
            throw AppException.badRequest("INVALID_TYPE",
                    "Loại dự đoán phải là CHAMPION, RUNNER_UP hoặc TOP_SCORER");
        }

        if (tournamentPredictionRepository.findByUserIdAndPredictionType(
                Objects.requireNonNull(user.getId()), req.predictionType()).isPresent()) {
            throw AppException.badRequest("ALREADY_PREDICTED", "Bạn đã dự đoán loại này rồi");
        }

        checkLocked(req.predictionType());

        if (req.creditBet() > user.getCredits()) {
            throw AppException.badRequest("INSUFFICIENT_CREDITS", "Không đủ credit");
        }

        TournamentPrediction tp = new TournamentPrediction();
        tp.setUser(user);
        tp.setPredictionType(req.predictionType());
        tp.setCreditBet(req.creditBet());

        if ("TOP_SCORER".equals(req.predictionType())) {
            if (req.playerName() == null || req.playerName().isBlank()) {
                throw AppException.badRequest("PLAYER_NAME_REQUIRED", "Phải nhập tên cầu thủ");
            }
            tp.setPlayerName(req.playerName());
        } else {
            if (req.teamId() == null) {
                throw AppException.badRequest("TEAM_REQUIRED", "Phải chọn đội bóng");
            }
            Team team = teamRepository.findById(Objects.requireNonNull(req.teamId()))
                    .orElseThrow(() -> AppException.notFound("Đội bóng không tồn tại"));
            tp.setTeam(team);
        }

        creditService.deductCredits(user, req.creditBet(), "BET",
                null, "Dự đoán toàn giải: " + req.predictionType());

        return TournamentPredictionResponse.from(tournamentPredictionRepository.save(tp));
    }

    private void checkLocked(String predictionType) {
        // Admin khoá thủ công thông qua system_config key "tournament_pred_locked"
        configRepository.findById("tournament_pred_locked")
                .filter(c -> "true".equalsIgnoreCase(c.getValue()))
                .ifPresent(c -> {
                    throw AppException.badRequest("TOURNAMENT_LOCKED",
                            "Dự đoán toàn giải đã bị khoá");
                });
    }
}
