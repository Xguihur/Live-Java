package com.mtjava.livechat.common;

import lombok.Getter;

/**
 * 系统里约定好的业务错误码定义，便于前后端稳定识别错误语义。
 */
@Getter
public enum ErrorCode {
    INVALID_PARAM(40000, "请求参数不合法"),
    AUTH_REQUIRED(40100, "请先登录"),
    AUTH_INVALID(40101, "登录态已失效"),
    FORBIDDEN(40300, "没有权限执行该操作"),
    ROOM_NOT_FOUND(40400, "直播间不存在"),
    USER_NOT_FOUND(40401, "用户不存在"),
    MESSAGE_BLOCKED(40900, "消息发送过快，请稍后再试"),
    USER_BANNED(40901, "当前用户已被禁言"),
    INTERNAL_ERROR(50000, "服务内部异常");

    private final int code;
    private final String defaultMessage;

    ErrorCode(int code, String defaultMessage) {
        this.code = code;
        this.defaultMessage = defaultMessage;
    }
}
