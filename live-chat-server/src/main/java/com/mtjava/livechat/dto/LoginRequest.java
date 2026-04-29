package com.mtjava.livechat.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/**
 * 游客登录接口的请求体，只要求提供昵称。
 */
public record LoginRequest(
        @NotBlank(message = "昵称不能为空")
        @Size(min = 2, max = 20, message = "昵称长度需要在 2 到 20 个字符之间")
        String nickname
) {
}
