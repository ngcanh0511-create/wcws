package com.wcpl.dto.response;

public record LeaderboardEntry(
        int rank,
        Long userId,
        String displayName,
        String avatarUrl,
        Integer credits,
        long totalPredictions,
        long won,
        double winRate
) {}
