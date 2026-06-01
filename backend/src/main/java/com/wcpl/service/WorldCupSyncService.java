package com.wcpl.service;

import com.wcpl.entity.Match;
import com.wcpl.entity.Team;
import com.wcpl.integration.worldcup.SyncedMatch;
import com.wcpl.integration.worldcup.SyncedTeam;
import com.wcpl.integration.worldcup.WorldCupDataProvider;
import com.wcpl.integration.worldcup.WorldCupSyncPayload;
import com.wcpl.repository.MatchRepository;
import com.wcpl.repository.SystemConfigRepository;
import com.wcpl.repository.TeamRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;

@Service
@RequiredArgsConstructor
public class WorldCupSyncService {

    private final WorldCupDataProvider dataProvider;
    private final TeamRepository teamRepository;
    private final MatchRepository matchRepository;
    private final SystemConfigRepository configRepository;

    @Transactional
    public Map<String, Object> sync() {
        WorldCupSyncPayload payload = dataProvider.fetchTournamentData();

        int teamsUpserted = 0;
        int matchesUpserted = 0;
        int matchesSkipped = 0;

        for (SyncedTeam syncedTeam : payload.teams()) {
            upsertTeam(syncedTeam);
            teamsUpserted++;
        }

        for (SyncedMatch syncedMatch : payload.matches()) {
            Team home = teamRepository.findByExternalId(syncedMatch.homeTeamExternalId()).orElse(null);
            Team away = teamRepository.findByExternalId(syncedMatch.awayTeamExternalId()).orElse(null);
            if (home == null || away == null) {
                matchesSkipped++;
                continue;
            }

            upsertMatch(syncedMatch, home, away);
            matchesUpserted++;
        }

        return Map.of(
                "teamsUpserted", teamsUpserted,
                "matchesUpserted", matchesUpserted,
                "matchesSkipped", matchesSkipped
        );
    }

    private void upsertTeam(SyncedTeam syncedTeam) {
        Team team = teamRepository.findByExternalId(syncedTeam.externalId())
                .orElseGet(Team::new);

        team.setExternalId(syncedTeam.externalId());
        team.setName(syncedTeam.name());
        team.setShortName(syncedTeam.shortName());
        team.setFlagUrl(syncedTeam.flagUrl());
        team.setGroupName(syncedTeam.groupName());

        teamRepository.save(team);
    }

    private void upsertMatch(SyncedMatch syncedMatch, Team home, Team away) {
        Match match = matchRepository.findByExternalId(syncedMatch.externalId())
                .orElseGet(Match::new);

        match.setExternalId(syncedMatch.externalId());
        match.setHomeTeam(home);
        match.setAwayTeam(away);
        match.setMatchDate(syncedMatch.matchDate());
        match.setPredictionLockedAt(syncedMatch.matchDate().minusMinutes(lockMinutesBeforeMatch()));
        match.setStage(syncedMatch.stage());
        match.setHomeScore(syncedMatch.homeScore());
        match.setAwayScore(syncedMatch.awayScore());
        match.setStatus(syncedMatch.status());

        matchRepository.save(match);
    }

    private int lockMinutesBeforeMatch() {
        return configRepository.findById("lock_minutes_before_match")
                .map(config -> Integer.parseInt(config.getValue()))
                .orElse(15);
    }
}
