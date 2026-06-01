package com.wcpl.controller;

import com.wcpl.dto.request.UpdateProfileRequest;
import com.wcpl.dto.response.UserProfileResponse;
import com.wcpl.entity.User;
import com.wcpl.entity.UserAchievement;
import com.wcpl.service.AchievementService;
import com.wcpl.service.CreditService;
import com.wcpl.service.PredictionService;
import com.wcpl.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/users/me")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;
    private final PredictionService predictionService;
    private final CreditService creditService;
    private final AchievementService achievementService;

    @GetMapping
    public ResponseEntity<UserProfileResponse> getProfile(@AuthenticationPrincipal User user) {
        return ResponseEntity.ok(userService.getProfile(user.getId()));
    }

    @PutMapping
    public ResponseEntity<UserProfileResponse> updateProfile(
            @AuthenticationPrincipal User user,
            @Valid @RequestBody UpdateProfileRequest req) {
        return ResponseEntity.ok(userService.updateProfile(user.getId(), req));
    }

    @PostMapping("/avatar")
    public ResponseEntity<UserProfileResponse> uploadAvatar(
            @AuthenticationPrincipal User user,
            @RequestParam("file") MultipartFile file) {
        return ResponseEntity.ok(userService.uploadAvatar(user.getId(), file));
    }

    @PutMapping("/avatar/default")
    public ResponseEntity<UserProfileResponse> setDefaultAvatar(
            @AuthenticationPrincipal User user,
            @RequestParam String name) {
        return ResponseEntity.ok(userService.setDefaultAvatar(user.getId(), name));
    }

    @GetMapping("/avatar/defaults")
    public ResponseEntity<List<String>> getDefaultAvatars() {
        return ResponseEntity.ok(userService.getDefaultAvatars());
    }

    @GetMapping("/predictions")
    public ResponseEntity<Page<?>> getMyPredictions(
            @AuthenticationPrincipal User user,
            @PageableDefault(size = 20) Pageable pageable) {
        return ResponseEntity.ok(predictionService.getUserPredictions(user.getId(), pageable));
    }

    @GetMapping("/credits/history")
    public ResponseEntity<Page<?>> getCreditHistory(
            @AuthenticationPrincipal User user,
            @PageableDefault(size = 20) Pageable pageable) {
        return ResponseEntity.ok(creditService.getHistory(user.getId(), pageable));
    }

    @GetMapping("/achievements")
    public ResponseEntity<List<UserAchievement>> getMyAchievements(@AuthenticationPrincipal User user) {
        return ResponseEntity.ok(achievementService.getUserAchievements(user.getId()));
    }

    @GetMapping("/stats")
    public ResponseEntity<Map<String, Object>> getStats(@AuthenticationPrincipal User user) {
        return ResponseEntity.ok(predictionService.getUserStats(user.getId()));
    }
}
