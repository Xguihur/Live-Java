package com.mtjava.livechat.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

/**
 * 房间禁言接口的请求体，包含目标用户、时长和原因。
 */
public record BanUserRequest(
        @NotNull(message = "被禁言用户不能为空")
        Long userId,
        @NotNull(message = "禁言时长不能为空")
        @Min(value = 1, message = "禁言时长至少 1 分钟")
        @Max(value = 1440, message = "禁言时长不能超过 1440 分钟")
        Integer durationMinutes,
        @NotBlank(message = "禁言原因不能为空")
        String reason
) {
}
