package com.wcpl.dto.response;

import com.wcpl.entity.Team;

import java.util.List;

public record TournamentPredictionOptionsResponse(
        List<Team> teams,
        List<String> topScorerPlayers
) {}
