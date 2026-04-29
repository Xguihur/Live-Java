package com.mtjava.livechat.common;

import lombok.Getter;

/**
 * 业务异常，携带统一错误码，最终会被全局异常处理器转换成对前端友好的响应。
 */
@Getter
public class BusinessException extends RuntimeException {

    private final ErrorCode errorCode;

    public BusinessException(ErrorCode errorCode) {
        super(errorCode.getDefaultMessage());
        this.errorCode = errorCode;
    }

    public BusinessException(ErrorCode errorCode, String message) {
        super(message);
        this.errorCode = errorCode;
    }
}
