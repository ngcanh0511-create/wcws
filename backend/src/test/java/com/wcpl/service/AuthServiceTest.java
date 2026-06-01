package com.wcpl.service;

import com.wcpl.dto.request.ChangePasswordRequest;
import com.wcpl.dto.request.LoginRequest;
import com.wcpl.dto.request.RefreshTokenRequest;
import com.wcpl.dto.response.AuthResponse;
import com.wcpl.entity.User;
import com.wcpl.exception.AppException;
import com.wcpl.repository.UserRepository;
import com.wcpl.security.JwtTokenProvider;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Map;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private JwtTokenProvider tokenProvider;

    @InjectMocks
    private AuthService authService;

    private User activeUser;

    @BeforeEach
    void setUp() {
        activeUser = new User();
        activeUser.setUsername("testuser");
        activeUser.setPasswordHash("$hashed$");
        activeUser.setDisplayName("Test User");
        activeUser.setCredits(1000);
        activeUser.setRole("USER");
        activeUser.setIsLocked(false);

        // Dùng reflection để set ID (vì @Id không có setter public)
        try {
            var idField = User.class.getDeclaredField("id");
            idField.setAccessible(true);
            idField.set(activeUser, 1L);
        } catch (Exception ignored) {}
    }

    // ── login ──────────────────────────────────────────────────────────────────

    @Test
    void login_validCredentials_returnsAuthResponse() {
        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(activeUser));
        when(passwordEncoder.matches("password123", "$hashed$")).thenReturn(true);
        when(tokenProvider.generateAccessToken(anyLong(), anyString())).thenReturn("access.token");
        when(tokenProvider.generateRefreshToken(anyLong())).thenReturn("refresh.token");

        AuthResponse response = authService.login(new LoginRequest("testuser", "password123"));

        assertThat(response.accessToken()).isEqualTo("access.token");
        assertThat(response.refreshToken()).isEqualTo("refresh.token");
        assertThat(response.user().displayName()).isEqualTo("Test User");
        assertThat(response.user().credits()).isEqualTo(1000);
    }

    @Test
    void login_userNotFound_throwsUnauthorized() {
        when(userRepository.findByUsername("nobody")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> authService.login(new LoginRequest("nobody", "pass")))
                .isInstanceOf(AppException.class)
                .hasMessageContaining("không đúng");
    }

    @Test
    void login_wrongPassword_throwsUnauthorized() {
        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(activeUser));
        when(passwordEncoder.matches("wrongpass", "$hashed$")).thenReturn(false);

        assertThatThrownBy(() -> authService.login(new LoginRequest("testuser", "wrongpass")))
                .isInstanceOf(AppException.class)
                .hasMessageContaining("không đúng");
    }

    @Test
    void login_lockedAccount_throwsForbidden() {
        activeUser.setIsLocked(true);
        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(activeUser));
        when(passwordEncoder.matches(anyString(), anyString())).thenReturn(true);

        assertThatThrownBy(() -> authService.login(new LoginRequest("testuser", "password123")))
                .isInstanceOf(AppException.class)
                .hasMessageContaining("bị khóa");
    }

    // ── refresh ────────────────────────────────────────────────────────────────

    @Test
    void refresh_validToken_returnsNewAccessToken() {
        when(tokenProvider.validateToken("valid.refresh")).thenReturn(true);
        when(tokenProvider.isRefreshToken("valid.refresh")).thenReturn(true);
        when(tokenProvider.getUserId("valid.refresh")).thenReturn(1L);
        when(userRepository.findById(1L)).thenReturn(Optional.of(activeUser));
        when(tokenProvider.generateAccessToken(anyLong(), anyString())).thenReturn("new.access");

        Map<String, String> result = authService.refresh(new RefreshTokenRequest("valid.refresh"));

        assertThat(result.get("accessToken")).isEqualTo("new.access");
    }

    @Test
    void refresh_invalidToken_throwsUnauthorized() {
        when(tokenProvider.validateToken("bad.token")).thenReturn(false);

        assertThatThrownBy(() -> authService.refresh(new RefreshTokenRequest("bad.token")))
                .isInstanceOf(AppException.class)
                .hasMessageContaining("không hợp lệ");
    }

    @Test
    void refresh_accessTokenUsedAsRefresh_throwsUnauthorized() {
        when(tokenProvider.validateToken("access.token")).thenReturn(true);
        when(tokenProvider.isRefreshToken("access.token")).thenReturn(false);

        assertThatThrownBy(() -> authService.refresh(new RefreshTokenRequest("access.token")))
                .isInstanceOf(AppException.class)
                .hasMessageContaining("không hợp lệ");
    }

    // ── changePassword ─────────────────────────────────────────────────────────

    @Test
    void changePassword_correctCurrentPassword_updatesHash() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(activeUser));
        when(passwordEncoder.matches("oldpass", "$hashed$")).thenReturn(true);
        when(passwordEncoder.encode("newpass")).thenReturn("$newHash$");

        authService.changePassword(1L, new ChangePasswordRequest("oldpass", "newpass"));

        ArgumentCaptor<User> captor = ArgumentCaptor.forClass(User.class);
        verify(userRepository).save(captor.capture());
        assertThat(captor.getValue())
                .isNotNull()
                .extracting(User::getPasswordHash)
                .isEqualTo("$newHash$");
    }

    @Test
    void changePassword_wrongCurrentPassword_throwsBadRequest() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(activeUser));
        when(passwordEncoder.matches("wrongold", "$hashed$")).thenReturn(false);

        assertThatThrownBy(() ->
                authService.changePassword(1L, new ChangePasswordRequest("wrongold", "newpass")))
                .isInstanceOf(AppException.class)
                .hasMessageContaining("không đúng");
    }
}
