package com.wcpl.controller;

import com.wcpl.dto.request.*;
import com.wcpl.dto.response.UserProfileResponse;
import com.wcpl.entity.Achievement;
import com.wcpl.entity.SystemConfig;
import com.wcpl.service.AdminService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/admin")
@PreAuthorize("hasRole('ADMIN')")
@RequiredArgsConstructor
public class AdminController {

    private final AdminService adminService;

    // ── Dashboard ─────────────────────────────────────────────────────────────

    @GetMapping("/dashboard")
    public ResponseEntity<Map<String, Object>> getDashboard() {
        return ResponseEntity.ok(adminService.getDashboard());
    }

    // ── User Management ───────────────────────────────────────────────────────

    @GetMapping("/users")
    public ResponseEntity<List<UserProfileResponse>> getAllUsers() {
        return ResponseEntity.ok(adminService.getAllUsers());
    }

    @PostMapping("/users")
    public ResponseEntity<UserProfileResponse> registerUser(
            @Valid @RequestBody AdminRegisterRequest req) {
        return ResponseEntity.ok(adminService.registerUser(req));
    }

    @PostMapping("/users/{userId}/lock")
    public ResponseEntity<Map<String, String>> lockUser(@PathVariable long userId) {
        adminService.lockUser(userId);
        return ResponseEntity.ok(Map.of("message", "Đã khóa tài khoản"));
    }

    @PostMapping("/users/{userId}/unlock")
    public ResponseEntity<Map<String, String>> unlockUser(@PathVariable long userId) {
        adminService.unlockUser(userId);
        return ResponseEntity.ok(Map.of("message", "Đã mở khóa tài khoản"));
    }

    @PutMapping("/users/{userId}/credits")
    public ResponseEntity<UserProfileResponse> updateCredits(
            @PathVariable long userId,
            @Valid @RequestBody AdminUpdateCreditRequest req) {
        return ResponseEntity.ok(adminService.updateCredits(userId, req));
    }

    @PutMapping("/users/{userId}/password")
    public ResponseEntity<Map<String, String>> resetPassword(
            @PathVariable long userId,
            @Valid @RequestBody AdminResetPasswordRequest req) {
        adminService.resetPassword(userId, req);
        return ResponseEntity.ok(Map.of("message", "Đã reset mật khẩu"));
    }

    // ── Match Management ──────────────────────────────────────────────────────

    @PostMapping("/matches/{matchId}/betting-lines/upload")
    public ResponseEntity<Map<String, String>> uploadBettingLines(
            @PathVariable long matchId,
            @RequestParam("file") MultipartFile file) throws IOException {
        String content = new String(file.getBytes(), StandardCharsets.UTF_8);
        adminService.uploadBettingLines(matchId, content);
        return ResponseEntity.ok(Map.of("message", "Đã tải lên kèo thành công"));
    }

    @PostMapping("/matches/{matchId}/betting-lines/text")
    public ResponseEntity<Map<String, String>> saveBettingLinesText(
            @PathVariable long matchId,
            @Valid @RequestBody AdminBettingLinesTextRequest req) {
        adminService.uploadBettingLines(matchId, req.content());
        return ResponseEntity.ok(Map.of("message", "Da luu keo thanh cong"));
    }

    @PostMapping("/matches/sync")
    public ResponseEntity<Map<String, Object>> syncWorldCupData() {
        return ResponseEntity.ok(adminService.syncWorldCupData());
    }

    @PutMapping("/matches/{matchId}/result")
    public ResponseEntity<Map<String, String>> setMatchResult(
            @PathVariable long matchId,
            @RequestParam Integer homeScore,
            @RequestParam Integer awayScore) {
        adminService.setMatchResult(matchId, homeScore, awayScore);
        return ResponseEntity.ok(Map.of("message", "Đã cập nhật kết quả"));
    }

    @PostMapping("/matches/{matchId}/grade")
    public ResponseEntity<Map<String, Object>> gradePredictions(@PathVariable long matchId) {
        return ResponseEntity.ok(adminService.gradePredictions(matchId));
    }

    @PutMapping("/betting-lines/result")
    public ResponseEntity<Map<String, String>> setLineResult(
            @Valid @RequestBody AdminSetLineResultRequest req) {
        adminService.setLineResult(req);
        return ResponseEntity.ok(Map.of("message", "Đã cập nhật kết quả kèo"));
    }

    // ── System Config ─────────────────────────────────────────────────────────

    @GetMapping("/achievements")
    public ResponseEntity<List<Achievement>> getAchievements() {
        return ResponseEntity.ok(adminService.getAchievements());
    }

    @PostMapping("/achievements")
    public ResponseEntity<Achievement> createAchievement(
            @Valid @RequestBody AdminAchievementRequest req) {
        return ResponseEntity.ok(adminService.createAchievement(req));
    }

    @PutMapping("/achievements/{achievementId}")
    public ResponseEntity<Achievement> updateAchievement(
            @PathVariable long achievementId,
            @Valid @RequestBody AdminAchievementRequest req) {
        return ResponseEntity.ok(adminService.updateAchievement(achievementId, req));
    }

    @GetMapping("/configs")
    public ResponseEntity<List<SystemConfig>> getConfigs() {
        return ResponseEntity.ok(adminService.getConfigs());
    }

    @PutMapping("/configs/{key}")
    public ResponseEntity<Map<String, String>> updateConfig(
            @PathVariable String key,
            @RequestParam String value) {
        adminService.updateConfig(key, value);
        return ResponseEntity.ok(Map.of("message", "Đã cập nhật config"));
    }
}
