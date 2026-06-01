package com.wcpl.integration.worldcup;

import java.time.LocalDateTime;

public record SyncedMatch(
        String externalId,
        String homeTeamExternalId,
        String awayTeamExternalId,
        LocalDateTime matchDate,
        String stage,
        Integer homeScore,
        Integer awayScore,
        String status
) {}
