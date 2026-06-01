package com.wcpl.dto.response;

import com.wcpl.entity.Prediction;

public record PlacePredictionResponse(
        Long id,
        BettingLineInfo bettingLine,
        Integer creditBet,
        String status,
        Integer creditsRemaining
) {
    public record BettingLineInfo(
            Long id,
            String description,
            Double odds
    ) {}

    public static PlacePredictionResponse from(Prediction prediction, Integer creditsRemaining) {
        return new PlacePredictionResponse(
                prediction.getId(),
                new BettingLineInfo(
                        prediction.getBettingLine().getId(),
                        prediction.getBettingLine().getDescription(),
                        prediction.getBettingLine().getOdds()
                ),
                prediction.getCreditBet(),
                prediction.getStatus(),
                creditsRemaining
        );
    }
}
