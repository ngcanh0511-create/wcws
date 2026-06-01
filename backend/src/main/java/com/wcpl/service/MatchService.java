package com.wcpl.service;

import com.wcpl.dto.response.MatchResponse;
import com.wcpl.entity.BettingLine;
import com.wcpl.entity.Match;
import com.wcpl.exception.AppException;
import com.wcpl.repository.BettingLineRepository;
import com.wcpl.repository.MatchRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Objects;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class MatchService {

    private final MatchRepository matchRepository;
    private final BettingLineRepository bettingLineRepository;

    public List<MatchResponse> getUpcoming() {
        return matchRepository.findByStatusOrderByMatchDateAsc("SCHEDULED")
                .stream().map(this::toResponse).toList();
    }

    public List<MatchResponse> getByDate(LocalDate date) {
        LocalDateTime start = date.atStartOfDay();
        LocalDateTime end = date.plusDays(1).atStartOfDay();
        return matchRepository.findByDateRange(start, end)
                .stream().map(this::toResponse).toList();
    }

    public MatchResponse getById(Long id) {
        Match match = matchRepository.findById(id)
                .orElseThrow(() -> AppException.notFound("Trận đấu không tồn tại"));
        return toResponse(match);
    }

    public List<MatchResponse> getAll() {
        return matchRepository.findAll().stream()
                .sorted((a, b) -> a.getMatchDate().compareTo(b.getMatchDate()))
                .map(this::toResponse).toList();
    }

    public MatchResponse toResponse(Match match) {
        List<BettingLine> lines = bettingLineRepository.findByMatchId(Objects.requireNonNull(match.getId()));

        return new MatchResponse(
                match.getId(),
                match.getStage(),
                match.getMatchDate(),
                match.getPredictionLockedAt(),
                match.isPredictionLocked(),
                match.getStatus(),
                new MatchResponse.TeamInfo(
                        match.getHomeTeam().getId(),
                        match.getHomeTeam().getName(),
                        match.getHomeTeam().getShortName(),
                        match.getHomeTeam().getFlagUrl()
                ),
                new MatchResponse.TeamInfo(
                        match.getAwayTeam().getId(),
                        match.getAwayTeam().getName(),
                        match.getAwayTeam().getShortName(),
                        match.getAwayTeam().getFlagUrl()
                ),
                match.getHomeScore(),
                match.getAwayScore(),
                lines.stream().map(l -> new MatchResponse.BettingLineInfo(
                        l.getId(), l.getLineType(), l.getDescription(), l.getOdds(), l.getResult()
                )).toList()
        );
    }
}
