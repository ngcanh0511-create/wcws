package com.wcpl.controller;

import com.wcpl.dto.response.LeaderboardEntry;
import com.wcpl.entity.User;
import com.wcpl.service.LeaderboardService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/leaderboard")
@RequiredArgsConstructor
public class LeaderboardController {

    private final LeaderboardService leaderboardService;

    @GetMapping
    public ResponseEntity<List<LeaderboardEntry>> getLeaderboard() {
        return ResponseEntity.ok(leaderboardService.getLeaderboard());
    }

    @GetMapping("/me")
    public ResponseEntity<LeaderboardEntry> getMyRank(@AuthenticationPrincipal User user) {
        return ResponseEntity.ok(leaderboardService.getMyRank(user.getId()));
    }
}
