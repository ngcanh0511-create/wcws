package com.wcpl.dto.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record TournamentPredictionRequest(
        @NotBlank(message = "Loại dự đoán không được để trống")
        String predictionType,

        Long teamId,

        String playerName,

        @NotNull(message = "Số credit đặt không được để trống")
        @Min(value = 1, message = "Số credit đặt phải lớn hơn 0")
        Integer creditBet
) {}
