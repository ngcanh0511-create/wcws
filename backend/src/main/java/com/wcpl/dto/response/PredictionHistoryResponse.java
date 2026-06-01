package com.wcpl.dto.response;

import com.wcpl.entity.Match;
import com.wcpl.entity.Prediction;
import com.wcpl.entity.BettingLine;

import java.time.LocalDateTime;

public record PredictionHistoryResponse(
        Long id,
        Long matchId,
        String matchName,
        Long bettingLineId,
        String bettingLineDescription,
        Double odds,
        Integer creditBet,
        Integer creditResult,
        String status,
        LocalDateTime createdAt
) {
    public static PredictionHistoryResponse from(Prediction prediction) {
        Match match = prediction.getMatch();
        BettingLine bettingLine = prediction.getBettingLine();
        String matchName = match == null
                ? null
                : match.getHomeTeam().getName() + " vs " + match.getAwayTeam().getName();

        return new PredictionHistoryResponse(
                prediction.getId(),
                match == null ? null : match.getId(),
                matchName,
                bettingLine == null ? null : bettingLine.getId(),
                bettingLine == null ? "Kèo đã bị chỉnh sửa hoặc xóa" : bettingLine.getDescription(),
                bettingLine == null ? null : bettingLine.getOdds(),
                prediction.getCreditBet(),
                prediction.getCreditResult(),
                prediction.getStatus(),
                prediction.getCreatedAt()
        );
    }
}
