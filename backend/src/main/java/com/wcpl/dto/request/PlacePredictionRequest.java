package com.wcpl.dto.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

public record PlacePredictionRequest(
        @NotNull(message = "Betting line ID không được để trống")
        Long bettingLineId,

        @NotNull(message = "Số credit đặt không được để trống")
        @Min(value = 1, message = "Số credit đặt phải lớn hơn 0")
        Integer creditBet
) {}
