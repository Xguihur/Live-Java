package com.mtjava.livechat.vo;

/**
 * 登录成功后返回给前端的用户信息和 token。
 */
public record LoginResponse(
        String token,
        Long userId,
        String nickname,
        String avatar,
        String accountType
) {
}
