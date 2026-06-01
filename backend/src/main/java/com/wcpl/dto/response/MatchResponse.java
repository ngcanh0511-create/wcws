package com.wcpl.dto.response;

import java.time.LocalDateTime;
import java.util.List;

public record MatchResponse(
        Long id,
        String stage,
        LocalDateTime matchDate,
        LocalDateTime predictionLockedAt,
        boolean locked,
        String status,
        TeamInfo homeTeam,
        TeamInfo awayTeam,
        Integer homeScore,
        Integer awayScore,
        List<BettingLineInfo> bettingLines
) {
    public record TeamInfo(Long id, String name, String shortName, String flagUrl) {}

    public record BettingLineInfo(Long id, String lineType, String description, Double odds, String result) {}
}
