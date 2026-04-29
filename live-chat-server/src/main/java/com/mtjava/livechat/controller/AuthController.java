package com.mtjava.livechat.controller;

import com.mtjava.livechat.common.ApiResponse;
import com.mtjava.livechat.dto.LoginRequest;
import com.mtjava.livechat.service.AuthService;
import com.mtjava.livechat.vo.LoginResponse;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 认证相关接口入口，目前主要负责游客登录。
 */
@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    /**
     * 根据昵称创建游客账号并返回登录态。
     */
    @PostMapping("/login")
    public ApiResponse<LoginResponse> login(@Valid @RequestBody LoginRequest request) {
        return ApiResponse.success(authService.login(request));
    }
}
