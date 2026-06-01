package com.wcpl.service;

import com.wcpl.dto.request.*;
import com.wcpl.dto.response.UserProfileResponse;
import com.wcpl.entity.*;
import com.wcpl.exception.AppException;
import com.wcpl.repository.*;
import com.wcpl.util.BettingLineParser;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;

@Service
@RequiredArgsConstructor
public class AdminService {

    private final UserRepository userRepository;
    private final MatchRepository matchRepository;
    private final BettingLineRepository bettingLineRepository;
    private final SystemConfigRepository configRepository;
    private final AchievementRepository achievementRepository;
    private final CreditService creditService;
    private final PredictionService predictionService;
    private final AchievementService achievementService;
    private final UserService userService;
    private final WorldCupSyncService worldCupSyncService;
    private final PasswordEncoder passwordEncoder;

    // ── User Management ──────────────────────────────────────────────────────

    public List<UserProfileResponse> getAllUsers() {
        return userRepository.findAll().stream()
                .map(userService::toResponse).toList();
    }

    @Transactional
    public UserProfileResponse registerUser(AdminRegisterRequest req) {
        if (userRepository.existsByUsername(req.username())) {
            throw AppException.badRequest("USERNAME_TAKEN", "Username đã tồn tại");
        }
        User user = new User();
        user.setUsername(req.username());
        user.setDisplayName(req.displayName());
        user.setPasswordHash(passwordEncoder.encode(req.password()));

        int initialCredits = getConfigInt("initial_credits", 1000);
        user.setCredits(initialCredits);

        userRepository.save(user);

        creditService.addCredits(user, initialCredits, "INITIAL",
                null, "Credit ban đầu khi tạo tài khoản");

        return userService.toResponse(user);
    }

    @Transactional
    public void lockUser(long userId) {
        User user = findUser(userId);
        user.setIsLocked(true);
        userRepository.save(user);
    }

    @Transactional
    public void unlockUser(long userId) {
        User user = findUser(userId);
        user.setIsLocked(false);
        userRepository.save(user);
    }

    @Transactional
    public UserProfileResponse updateCredits(long userId, AdminUpdateCreditRequest req) {
        User user = findUser(userId);
        if (req.amount() > 0) {
            creditService.addCredits(user, req.amount(), "ADMIN_TOPUP", null, req.reason());
        } else if (req.amount() < 0) {
            creditService.deductCredits(user, -req.amount(), "ADMIN_DEDUCT", null, req.reason());
        }
        return userService.toResponse(user);
    }

    @Transactional
    public void resetPassword(long userId, AdminResetPasswordRequest req) {
        User user = findUser(userId);
        user.setPasswordHash(passwordEncoder.encode(req.newPassword()));
        userRepository.save(user);
    }

    // ── Match Management ─────────────────────────────────────────────────────

    @Transactional
    public void uploadBettingLines(long matchId, String content) {
        Match match = matchRepository.findById(matchId)
                .orElseThrow(() -> AppException.notFound("Trận đấu không tồn tại"));

        List<BettingLine> existingLines = bettingLineRepository.findByMatchId(matchId);
        Map<Long, BettingLine> existingById = new HashMap<>();
        Map<String, BettingLine> existingByDescription = new HashMap<>();
        for (BettingLine line : existingLines) {
            if (line.getId() != null) {
                existingById.put(line.getId(), line);
            }
            existingByDescription.putIfAbsent(normalizeBettingLineDescription(line.getDescription()), line);
        }

        List<BettingLineParser.ParsedMatch> parsed = BettingLineParser.parse(content);
        for (BettingLineParser.ParsedMatch pm : parsed) {
            for (BettingLineParser.ParsedLine pl : pm.lines()) {
                BettingLine line = findExistingBettingLine(pl, existingById, existingByDescription);
                if (line == null) {
                    line = new BettingLine();
                }

                line.setMatch(match);
                line.setLineType(pl.lineType());
                line.setDescription(pl.description());
                line.setOdds(pl.odds());
                bettingLineRepository.save(line);
            }
        }
    }

    private BettingLine findExistingBettingLine(
            BettingLineParser.ParsedLine parsedLine,
            Map<Long, BettingLine> existingById,
            Map<String, BettingLine> existingByDescription) {
        if (parsedLine.id() != null) {
            BettingLine byId = existingById.get(parsedLine.id());
            if (byId != null) return byId;
        }
        return existingByDescription.get(normalizeBettingLineDescription(parsedLine.description()));
    }

    private String normalizeBettingLineDescription(String description) {
        return description == null
                ? ""
                : description.trim().replaceAll("\\s+", " ").toLowerCase(Locale.ROOT);
    }

    @Transactional
    public void setMatchResult(long matchId, Integer homeScore, Integer awayScore) {
        Match match = matchRepository.findById(matchId)
                .orElseThrow(() -> AppException.notFound("Trận đấu không tồn tại"));

        match.setHomeScore(homeScore);
        match.setAwayScore(awayScore);
        match.setStatus("FINISHED");
        matchRepository.save(match);
    }

    @Transactional
    public void setLineResult(AdminSetLineResultRequest req) {
        if (!List.of("WIN", "LOSE", "VOID").contains(req.result())) {
            throw AppException.badRequest("INVALID_RESULT", "Kết quả phải là WIN, LOSE hoặc VOID");
        }

        BettingLine line = bettingLineRepository.findById(Objects.requireNonNull(req.bettingLineId()))
                .orElseThrow(() -> AppException.notFound("Betting line không tồn tại"));

        line.setResult(req.result());
        bettingLineRepository.save(line);
    }

    @Transactional
    public Map<String, Object> syncWorldCupData() {
        return worldCupSyncService.sync();
    }

    @Transactional
    public Map<String, Object> gradePredictions(long matchId) {
        Match match = matchRepository.findById(matchId)
                .orElseThrow(() -> AppException.notFound("Trận đấu không tồn tại"));

        predictionService.settlePredictionsForMatch(matchId);

        List<User> affectedUsers = userRepository.findAll();
        for (User u : affectedUsers) {
            achievementService.checkAndAward(u, match);
        }

        return Map.of("message", "Đã chấm điểm thành công cho trận " + Objects.requireNonNull(match.getId()));
    }

    // ── System Config ─────────────────────────────────────────────────────────

    public List<Achievement> getAchievements() {
        return achievementRepository.findAll();
    }

    @Transactional
    public Achievement createAchievement(AdminAchievementRequest req) {
        String code = normalizeCode(req.code());
        if (achievementRepository.findByCode(code).isPresent()) {
            throw AppException.badRequest("ACHIEVEMENT_CODE_TAKEN", "Code huy hieu da ton tai");
        }

        Achievement achievement = new Achievement();
        applyAchievementRequest(achievement, req, code);
        return achievementRepository.save(achievement);
    }

    @Transactional
    public Achievement updateAchievement(long achievementId, AdminAchievementRequest req) {
        Achievement achievement = achievementRepository.findById(achievementId)
                .orElseThrow(() -> AppException.notFound("Huy hieu khong ton tai"));

        String code = normalizeCode(req.code());
        achievementRepository.findByCode(code)
                .filter(existing -> !Objects.equals(existing.getId(), achievementId))
                .ifPresent(existing -> {
                    throw AppException.badRequest("ACHIEVEMENT_CODE_TAKEN", "Code huy hieu da ton tai");
                });

        applyAchievementRequest(achievement, req, code);
        return achievementRepository.save(achievement);
    }

    private void applyAchievementRequest(Achievement achievement, AdminAchievementRequest req, String code) {
        achievement.setCode(code);
        achievement.setName(req.name().trim());
        achievement.setDescription(req.description().trim());
        achievement.setIcon(req.icon().trim());
        achievement.setBadgeColor(req.badgeColor().trim());
        achievement.setConditionType(req.conditionType().trim().toUpperCase());
        achievement.setConditionValue(req.conditionValue());
        achievement.setIsActive(req.isActive() == null || req.isActive());
    }

    private String normalizeCode(String code) {
        return code.trim().toUpperCase();
    }

    public List<SystemConfig> getConfigs() {
        return configRepository.findAll();
    }

    @Transactional
    public void updateConfig(String key, String value) {
        SystemConfig config = configRepository.findById(Objects.requireNonNull(key))
                .orElseThrow(() -> AppException.notFound("Config key không tồn tại: " + key));
        config.setValue(value);
        configRepository.save(config);
    }

    // ── Dashboard ─────────────────────────────────────────────────────────────

    public Map<String, Object> getDashboard() {
        long totalUsers = userRepository.count();
        long totalMatches = matchRepository.count();
        long upcomingMatches = matchRepository.findByStatusOrderByMatchDateAsc("SCHEDULED").size();
        long finishedMatches = matchRepository.findByStatusOrderByMatchDateAsc("FINISHED").size();

        return Map.of(
                "totalUsers", totalUsers,
                "totalMatches", totalMatches,
                "upcomingMatches", upcomingMatches,
                "finishedMatches", finishedMatches
        );
    }

    private User findUser(long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> AppException.notFound("Tài khoản không tồn tại"));
    }

    private int getConfigInt(String key, int defaultVal) {
        return configRepository.findById(Objects.requireNonNull(key))
                .map(c -> Integer.parseInt(c.getValue()))
                .orElse(defaultVal);
    }
}
