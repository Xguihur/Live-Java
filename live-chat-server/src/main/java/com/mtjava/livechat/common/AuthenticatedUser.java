package com.mtjava.livechat.common;

/**
 * 当前请求已经通过鉴权后的用户快照，通常放在线程上下文中供业务层读取。
 */
public record AuthenticatedUser(Long userId, String nickname, String accountType) {
}
