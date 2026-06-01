package com.wcpl.service;

import com.wcpl.dto.response.LeaderboardEntry;
import com.wcpl.entity.User;
import com.wcpl.exception.AppException;
import com.wcpl.repository.PredictionRepository;
import com.wcpl.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Service
@RequiredArgsConstructor
public class LeaderboardService {

    private final UserRepository userRepository;
    private final PredictionRepository predictionRepository;

    public List<LeaderboardEntry> getLeaderboard() {
        List<User> users = userRepository.findLeaderboard();
        List<LeaderboardEntry> result = new ArrayList<>();

        for (int i = 0; i < users.size(); i++) {
            User u = users.get(i);
            long userId = Objects.requireNonNull(u.getId());

            var allPredictions = predictionRepository.findRecentByUser(userId, PageRequest.of(0, Integer.MAX_VALUE));
            long total = allPredictions.size();
            long won = allPredictions.stream().filter(p -> "WON".equals(p.getStatus())).count();
            double winRate = total > 0 ? Math.round((double) won / total * 1000.0) / 10.0 : 0;

            String avatarUrl = u.getAvatarPath() != null ? "/avatars/" + u.getAvatarPath() : null;

            result.add(new LeaderboardEntry(
                    i + 1,
                    userId,
                    u.getDisplayName(),
                    avatarUrl,
                    u.getCredits(),
                    total,
                    won,
                    winRate
            ));
        }
        return result;
    }

    public LeaderboardEntry getMyRank(Long userId) {
        return getLeaderboard().stream()
                .filter(entry -> Objects.equals(entry.userId(), userId))
                .findFirst()
                .orElseThrow(() -> AppException.notFound("Khong tim thay vi tri tren bang xep hang"));
    }
}
