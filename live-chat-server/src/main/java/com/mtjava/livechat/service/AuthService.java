package com.mtjava.livechat.service;

import com.mtjava.livechat.dto.LoginRequest;
import com.mtjava.livechat.mapper.UserMapper;
import com.mtjava.livechat.model.User;
import com.mtjava.livechat.vo.LoginResponse;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

/**
 * 认证服务，目前主要负责创建游客账号并签发 JWT 登录态。
 */
@Service
public class AuthService {

    private final UserMapper userMapper;
    private final JwtTokenService jwtTokenService;

    public AuthService(UserMapper userMapper, JwtTokenService jwtTokenService) {
        this.userMapper = userMapper;
        this.jwtTokenService = jwtTokenService;
    }

    /**
     * 根据前端传入的昵称创建一个游客用户，并返回给前端可直接使用的登录信息。
     */
    @Transactional
    public LoginResponse login(LoginRequest request) {
        String nickname = request.nickname().trim();
        User user = new User();
        user.setNickname(nickname);
        user.setAvatar("https://api.dicebear.com/9.x/initials/svg?seed=" + nickname);
        user.setAccountType("GUEST");
        user.setStatus("NORMAL");
        user.setCreatedAt(LocalDateTime.now());
        user.setUpdatedAt(LocalDateTime.now());
        userMapper.insert(user);

        String token = jwtTokenService.createToken(user);
        return new LoginResponse(token, user.getId(), user.getNickname(), user.getAvatar(), user.getAccountType());
    }
}
