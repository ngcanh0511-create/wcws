package com.wcpl.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record UpdateProfileRequest(
        @NotBlank(message = "Tên hiển thị không được để trống")
        @Size(max = 50, message = "Tên hiển thị tối đa 50 ký tự")
        String displayName
) {}
