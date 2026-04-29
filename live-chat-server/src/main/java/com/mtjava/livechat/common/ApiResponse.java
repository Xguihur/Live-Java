package com.mtjava.livechat.common;

/**
 * 统一的接口响应体，前后端约定使用 code/message/data 传递结果。
 */
public record ApiResponse<T>(int code, String message, T data) {

    public static <T> ApiResponse<T> success(T data) {
        return new ApiResponse<>(0, "success", data);
    }

    public static ApiResponse<Void> success() {
        return new ApiResponse<>(0, "success", null);
    }

    public static ApiResponse<Void> failure(ErrorCode errorCode, String message) {
        return new ApiResponse<>(errorCode.getCode(), message, null);
    }
}
