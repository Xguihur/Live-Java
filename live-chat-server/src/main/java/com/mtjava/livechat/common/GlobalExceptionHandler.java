package com.mtjava.livechat.common;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * 将控制器和业务层抛出的异常统一转换成 ApiResponse，避免直接把异常细节暴露给前端。
 */
@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    /**
     * 业务异常直接按预定义错误码返回。
     */
    @ExceptionHandler(BusinessException.class)
    @ResponseStatus(HttpStatus.OK)
    public ApiResponse<Void> handleBusinessException(BusinessException ex) {
        return ApiResponse.failure(ex.getErrorCode(), ex.getMessage());
    }

    /**
     * 参数校验失败时，优先取第一个字段错误提示返回。
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.OK)
    public ApiResponse<Void> handleValidationException(MethodArgumentNotValidException ex) {
        String message = ex.getBindingResult().getFieldErrors().stream()
                .findFirst()
                .map(error -> error.getDefaultMessage() == null ? "请求参数不合法" : error.getDefaultMessage())
                .orElse(ErrorCode.INVALID_PARAM.getDefaultMessage());
        return ApiResponse.failure(ErrorCode.INVALID_PARAM, message);
    }

    /**
     * 兜底异常处理，记录日志并返回通用错误，保证接口响应结构一致。
     */
    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.OK)
    public ApiResponse<Void> handleException(Exception ex) {
        log.error("Unhandled exception", ex);
        return ApiResponse.failure(ErrorCode.INTERNAL_ERROR, ErrorCode.INTERNAL_ERROR.getDefaultMessage());
    }
}
