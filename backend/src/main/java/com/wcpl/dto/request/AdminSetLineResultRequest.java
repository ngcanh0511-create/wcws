package com.wcpl.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record AdminSetLineResultRequest(
        @NotNull(message = "Betting line ID không được để trống")
        Long bettingLineId,

        @NotBlank(message = "Kết quả không được để trống")
        String result
) {}
