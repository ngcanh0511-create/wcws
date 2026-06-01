package com.wcpl.controller;

import com.wcpl.dto.request.ChangePasswordRequest;
import com.wcpl.dto.request.LoginRequest;
import com.wcpl.dto.request.RefreshTokenRequest;
import com.wcpl.dto.response.AuthResponse;
import com.wcpl.entity.User;
import com.wcpl.service.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@Valid @RequestBody LoginRequest req) {
        return ResponseEntity.ok(authService.login(req));
    }

    @PostMapping("/refresh")
    public ResponseEntity<Map<String, String>> refresh(@Valid @RequestBody RefreshTokenRequest req) {
        return ResponseEntity.ok(authService.refresh(req));
    }

    @PostMapping("/change-password")
    public ResponseEntity<Map<String, String>> changePassword(
            @AuthenticationPrincipal User user,
            @Valid @RequestBody ChangePasswordRequest req) {
        authService.changePassword(user.getId(), req);
        return ResponseEntity.ok(Map.of("message", "Đổi mật khẩu thành công"));
    }
}
