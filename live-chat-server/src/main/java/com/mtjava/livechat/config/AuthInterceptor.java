package com.mtjava.livechat.config;

import com.mtjava.livechat.common.AuthenticatedUser;
import com.mtjava.livechat.common.BusinessException;
import com.mtjava.livechat.common.ErrorCode;
import com.mtjava.livechat.common.UserContext;
import com.mtjava.livechat.service.JwtTokenService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import java.util.Set;

/**
 * HTTP 接口鉴权拦截器，负责校验 Bearer Token，并把用户信息写入 UserContext。
 */
@Component
public class AuthInterceptor implements HandlerInterceptor {

    private static final Set<String> WHITE_LIST = Set.of(
            "/api/auth/login",
            "/api/health"
    );

    private final JwtTokenService jwtTokenService;

    public AuthInterceptor(JwtTokenService jwtTokenService) {
        this.jwtTokenService = jwtTokenService;
    }

    /**
     * 白名单请求直接放行，其余请求要求携带合法 JWT。
     */
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        if ("OPTIONS".equalsIgnoreCase(request.getMethod()) || WHITE_LIST.contains(request.getRequestURI())) {
            return true;
        }

        String authorization = request.getHeader("Authorization");
        if (authorization == null || !authorization.startsWith("Bearer ")) {
            throw new BusinessException(ErrorCode.AUTH_REQUIRED);
        }

        JwtTokenService.JwtUserClaims claims = jwtTokenService.parseToken(authorization.substring("Bearer ".length()));
        UserContext.set(new AuthenticatedUser(claims.userId(), claims.nickname(), claims.accountType()));
        return true;
    }

    /**
     * 请求结束后清理线程上下文，避免线程复用时沿用旧用户信息。
     */
    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) {
        UserContext.clear();
    }
}
