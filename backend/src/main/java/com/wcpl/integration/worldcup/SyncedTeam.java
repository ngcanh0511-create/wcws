package com.wcpl.integration.worldcup;

public record SyncedTeam(
        String externalId,
        String name,
        String shortName,
        String flagUrl,
        String groupName
) {}
