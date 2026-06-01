package com.wcpl.dto.response;

import com.wcpl.entity.Team;
import com.wcpl.entity.TournamentPrediction;

import java.time.LocalDateTime;

public record TournamentPredictionResponse(
        Long id,
        String predictionType,
        TeamInfo team,
        String playerName,
        Integer creditBet,
        Integer creditResult,
        String status,
        LocalDateTime createdAt
) {
    public record TeamInfo(
            Long id,
            String name,
            String shortName,
            String flagUrl
    ) {}

    public static TournamentPredictionResponse from(TournamentPrediction prediction) {
        Team team = prediction.getTeam();
        TeamInfo teamInfo = team == null ? null : new TeamInfo(
                team.getId(),
                team.getName(),
                team.getShortName(),
                team.getFlagUrl()
        );

        return new TournamentPredictionResponse(
                prediction.getId(),
                prediction.getPredictionType(),
                teamInfo,
                prediction.getPlayerName(),
                prediction.getCreditBet(),
                prediction.getCreditResult(),
                prediction.getStatus(),
                prediction.getCreatedAt()
        );
    }
}
