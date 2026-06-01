package com.wcpl.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record AdminBettingLinesTextRequest(
        @NotBlank(message = "Noi dung keo khong duoc de trong")
        @Size(max = 20000, message = "Noi dung keo toi da 20000 ky tu")
        String content
) {}
