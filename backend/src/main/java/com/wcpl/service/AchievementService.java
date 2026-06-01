package com.wcpl.service;

import com.wcpl.entity.*;
import com.wcpl.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Objects;

@Service
@RequiredArgsConstructor
public class AchievementService {

    private final AchievementRepository achievementRepository;
    private final UserAchievementRepository userAchievementRepository;
    private final PredictionRepository predictionRepository;
    private final ActivityFeedRepository activityFeedRepository;

    public List<UserAchievement> getUserAchievements(Long userId) {
        return userAchievementRepository.findByUserId(userId);
    }

    @Transactional
    public void checkAndAward(User user, Match match) {
        long userId = Objects.requireNonNull(user.getId());
        List<Prediction> recent = predictionRepository.findRecentByUser(userId, PageRequest.of(0, 100));

        checkStreakWin(user, match, recent);
        checkStreakLose(user, match, recent);
        checkExactScoreWins(user, match, recent);
        checkHighOddsWins(user, match, recent);
        checkCreditMilestones(user);
    }

    private void checkStreakWin(User user, Match match, List<Prediction> recent) {
        int streak = 0;
        for (Prediction p : recent) {
            if ("WON".equals(p.getStatus())) streak++;
            else break;
        }
        if (streak >= 10) award(user, "PROPHET", match);
    }

    private void checkStreakLose(User user, Match match, List<Prediction> recent) {
        int streak = 0;
        for (Prediction p : recent) {
            if ("LOST".equals(p.getStatus())) streak++;
            else break;
        }
        if (streak >= 10) award(user, "BAD_LUCK_KING", match);
    }

    private void checkExactScoreWins(User user, Match match, List<Prediction> recent) {
        long exactWins = recent.stream()
                .filter(p -> "WON".equals(p.getStatus()) && "EXACT_SCORE".equals(p.getBettingLine().getLineType()))
                .count();
        if (exactWins >= 5) award(user, "SCORE_SNIPER", match);
    }

    private void checkHighOddsWins(User user, Match match, List<Prediction> recent) {
        long highOddsWins = recent.stream()
                .filter(p -> "WON".equals(p.getStatus()) && p.getBettingLine().getOdds() >= 5.0)
                .count();
        if (highOddsWins >= 3) award(user, "LUCKY_STAR", match);
    }

    private void checkCreditMilestones(User user) {
        if (user.getCredits() <= 0) award(user, "BROKE", null);
        if (user.getCredits() >= 10000) award(user, "TYCOON", null);
    }

    private void award(User user, String code, Match match) {
        Achievement achievement = achievementRepository.findByCode(code).orElse(null);
        if (achievement == null || !achievement.getIsActive()) return;

        long userId = Objects.requireNonNull(user.getId());
        long achievementId = Objects.requireNonNull(achievement.getId());

        if (userAchievementRepository.existsByUserIdAndAchievementId(userId, achievementId)) return;

        UserAchievement ua = new UserAchievement();
        ua.setUser(user);
        ua.setAchievement(achievement);
        ua.setMatch(match);
        userAchievementRepository.save(ua);

        ActivityFeed feed = new ActivityFeed();
        feed.setUser(user);
        feed.setActivityType("ACHIEVEMENT");
        feed.setMessage(user.getDisplayName() + " vừa nhận huy hiệu: " + achievement.getName() + " " + achievement.getIcon());
        feed.setReferenceId(achievementId);
        activityFeedRepository.save(feed);
    }
}
