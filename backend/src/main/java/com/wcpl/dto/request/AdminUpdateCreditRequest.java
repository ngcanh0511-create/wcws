package com.wcpl.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record AdminUpdateCreditRequest(
        @NotNull(message = "Số credit không được để trống")
        Integer amount,

        @NotBlank(message = "Lý do không được để trống")
        String reason
) {}
