package com.wcpl.dto.response;

import java.time.LocalDateTime;

public record UserProfileResponse(
        Long id,
        String username,
        String displayName,
        String avatarUrl,
        Integer credits,
        String role,
        LocalDateTime createdAt
) {}
