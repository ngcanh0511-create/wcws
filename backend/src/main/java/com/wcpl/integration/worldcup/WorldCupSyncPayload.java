package com.wcpl.integration.worldcup;

import java.util.List;

public record WorldCupSyncPayload(
        List<SyncedTeam> teams,
        List<SyncedMatch> matches
) {}
