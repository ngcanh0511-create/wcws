package com.wcpl.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record AdminRegisterRequest(
        @NotBlank(message = "Username không được để trống")
        @Size(min = 3, max = 30, message = "Username từ 3-30 ký tự")
        String username,

        @NotBlank(message = "Tên hiển thị không được để trống")
        @Size(max = 50, message = "Tên hiển thị tối đa 50 ký tự")
        String displayName,

        @NotBlank(message = "Password không được để trống")
        @Size(min = 6, message = "Password phải có ít nhất 6 ký tự")
        String password
) {}
