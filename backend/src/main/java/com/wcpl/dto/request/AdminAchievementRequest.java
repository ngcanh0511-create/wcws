package com.wcpl.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record AdminAchievementRequest(
        @NotBlank(message = "Code khong duoc de trong")
        @Size(max = 50, message = "Code toi da 50 ky tu")
        @Pattern(regexp = "^[A-Z0-9_]+$", message = "Code chi gom chu in hoa, so va dau gach duoi")
        String code,

        @NotBlank(message = "Ten huy hieu khong duoc de trong")
        @Size(max = 100, message = "Ten huy hieu toi da 100 ky tu")
        String name,

        @NotBlank(message = "Mo ta khong duoc de trong")
        @Size(max = 500, message = "Mo ta toi da 500 ky tu")
        String description,

        @NotBlank(message = "Icon khong duoc de trong")
        @Size(max = 20, message = "Icon toi da 20 ky tu")
        String icon,

        @NotBlank(message = "Mau badge khong duoc de trong")
        @Pattern(regexp = "^#[0-9A-Fa-f]{6}$", message = "Mau badge phai co dang #RRGGBB")
        String badgeColor,

        @NotBlank(message = "Loai dieu kien khong duoc de trong")
        @Size(max = 50, message = "Loai dieu kien toi da 50 ky tu")
        String conditionType,

        Integer conditionValue,

        Boolean isActive
) {}
