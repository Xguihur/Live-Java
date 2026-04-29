package com.mtjava.livechat.dto;

import jakarta.validation.constraints.NotNull;

/**
 * 房间解禁接口的请求体，只需要目标用户 id。
 */
public record UnbanUserRequest(
        @NotNull(message = "被解除禁言的用户不能为空")
        Long userId
) {
}
