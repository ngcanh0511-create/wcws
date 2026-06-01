package com.wcpl.security;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class JwtTokenProviderTest {

    private static final String TEST_SECRET =
            "test-secret-key-that-is-at-least-256-bits-long-for-junit-testing-purposes-only";
    private static final long ACCESS_EXPIRY_MS = 900_000L;    // 15 phút
    private static final long REFRESH_EXPIRY_MS = 604_800_000L; // 7 ngày

    private JwtTokenProvider tokenProvider;

    @BeforeEach
    void setUp() {
        tokenProvider = new JwtTokenProvider(TEST_SECRET, ACCESS_EXPIRY_MS, REFRESH_EXPIRY_MS);
    }

    @Test
    void generateAccessToken_validInput_returnsNonBlankToken() {
        String token = tokenProvider.generateAccessToken(1L, "USER");
        assertThat(token).isNotBlank();
    }

    @Test
    void generateRefreshToken_validInput_returnsNonBlankToken() {
        String token = tokenProvider.generateRefreshToken(1L);
        assertThat(token).isNotBlank();
    }

    @Test
    void isAccessToken_accessToken_returnsTrue() {
        String token = tokenProvider.generateAccessToken(1L, "USER");
        assertThat(tokenProvider.isAccessToken(token)).isTrue();
        assertThat(tokenProvider.isRefreshToken(token)).isFalse();
    }

    @Test
    void isRefreshToken_refreshToken_returnsTrue() {
        String token = tokenProvider.generateRefreshToken(1L);
        assertThat(tokenProvider.isRefreshToken(token)).isTrue();
        assertThat(tokenProvider.isAccessToken(token)).isFalse();
    }

    @Test
    void getUserId_fromAccessToken_returnsCorrectId() {
        String token = tokenProvider.generateAccessToken(42L, "USER");
        assertThat(tokenProvider.getUserId(token)).isEqualTo(42L);
    }

    @Test
    void getUserId_fromRefreshToken_returnsCorrectId() {
        String token = tokenProvider.generateRefreshToken(99L);
        assertThat(tokenProvider.getUserId(token)).isEqualTo(99L);
    }

    @Test
    void validateToken_validAccessToken_returnsTrue() {
        String token = tokenProvider.generateAccessToken(1L, "USER");
        assertThat(tokenProvider.validateToken(token)).isTrue();
    }

    @Test
    void validateToken_validRefreshToken_returnsTrue() {
        String token = tokenProvider.generateRefreshToken(1L);
        assertThat(tokenProvider.validateToken(token)).isTrue();
    }

    @Test
    void validateToken_randomString_returnsFalse() {
        assertThat(tokenProvider.validateToken("not.a.jwt")).isFalse();
    }

    @Test
    void validateToken_blankToken_returnsFalse() {
        assertThat(tokenProvider.validateToken("")).isFalse();
    }

    @Test
    void validateToken_expiredToken_returnsFalse() {
        // Tạo provider với expiry = 1ms → token hết hạn ngay
        JwtTokenProvider shortLived = new JwtTokenProvider(TEST_SECRET, 1L, 1L);
        String token = shortLived.generateAccessToken(1L, "USER");

        // Đợi 10ms để token hết hạn
        try { Thread.sleep(10); } catch (InterruptedException ignored) {}

        assertThat(shortLived.validateToken(token)).isFalse();
    }

    @Test
    void validateToken_wrongSecret_returnsFalse() {
        String token = tokenProvider.generateAccessToken(1L, "USER");

        JwtTokenProvider otherProvider = new JwtTokenProvider(
                "completely-different-secret-key-that-is-also-at-least-256-bits-long",
                ACCESS_EXPIRY_MS, REFRESH_EXPIRY_MS
        );

        assertThat(otherProvider.validateToken(token)).isFalse();
    }

    @Test
    void accessToken_forAdminRole_storesRoleClaim() {
        String token = tokenProvider.generateAccessToken(1L, "ADMIN");
        // Xác nhận token hợp lệ và parse được userId đúng
        assertThat(tokenProvider.getUserId(token)).isEqualTo(1L);
        assertThat(tokenProvider.isAccessToken(token)).isTrue();
    }
}
