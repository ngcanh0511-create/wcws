package com.wcpl.dto.response;

public record AuthResponse(
        String accessToken,
        String refreshToken,
        UserSummary user
) {
    public record UserSummary(
            Long id,
            String displayName,
            String avatarUrl,
            Integer credits,
            String role
    ) {}
}
