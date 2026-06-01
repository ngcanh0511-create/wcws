package com.wcpl.service;

import com.wcpl.dto.request.ChangePasswordRequest;
import com.wcpl.dto.request.LoginRequest;
import com.wcpl.dto.request.RefreshTokenRequest;
import com.wcpl.dto.response.AuthResponse;
import com.wcpl.entity.User;
import com.wcpl.exception.AppException;
import com.wcpl.repository.UserRepository;
import com.wcpl.security.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider tokenProvider;

    public AuthResponse login(LoginRequest req) {
        User user = userRepository.findByUsername(req.username())
                .orElseThrow(() -> AppException.unauthorized("Tên đăng nhập hoặc mật khẩu không đúng"));

        if (!passwordEncoder.matches(req.password(), user.getPasswordHash())) {
            throw AppException.unauthorized("Tên đăng nhập hoặc mật khẩu không đúng");
        }

        if (user.getIsLocked()) {
            throw AppException.forbidden("Tài khoản đã bị khóa");
        }

        return buildAuthResponse(user);
    }

    public Map<String, String> refresh(RefreshTokenRequest req) {
        String token = req.refreshToken();

        if (!tokenProvider.validateToken(token) || !tokenProvider.isRefreshToken(token)) {
            throw AppException.unauthorized("Refresh token không hợp lệ");
        }

        long userId = tokenProvider.getUserId(token);
        User user = userRepository.findById(userId)
                .orElseThrow(() -> AppException.unauthorized("Tài khoản không tồn tại"));

        if (user.getIsLocked()) {
            throw AppException.forbidden("Tài khoản đã bị khóa");
        }

        String newAccessToken = tokenProvider.generateAccessToken(user.getId(), user.getRole());
        return Map.of("accessToken", newAccessToken);
    }

    public void changePassword(Long userId, ChangePasswordRequest req) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> AppException.notFound("Tài khoản không tồn tại"));

        if (!passwordEncoder.matches(req.currentPassword(), user.getPasswordHash())) {
            throw AppException.badRequest("WRONG_PASSWORD", "Mật khẩu hiện tại không đúng");
        }

        user.setPasswordHash(passwordEncoder.encode(req.newPassword()));
        userRepository.save(user);
    }

    private AuthResponse buildAuthResponse(User user) {
        String accessToken = tokenProvider.generateAccessToken(user.getId(), user.getRole());
        String refreshToken = tokenProvider.generateRefreshToken(user.getId());

        String avatarUrl = user.getAvatarPath() != null ? "/avatars/" + user.getAvatarPath() : null;

        return new AuthResponse(
                accessToken,
                refreshToken,
                new AuthResponse.UserSummary(
                        user.getId(),
                        user.getDisplayName(),
                        avatarUrl,
                        user.getCredits(),
                        user.getRole()
                )
        );
    }
}
