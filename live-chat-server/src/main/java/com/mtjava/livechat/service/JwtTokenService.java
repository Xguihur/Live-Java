package com.mtjava.livechat.service;

import com.mtjava.livechat.common.BusinessException;
import com.mtjava.livechat.common.ErrorCode;
import com.mtjava.livechat.model.User;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;

/**
 * JWT 的生成与解析服务，屏蔽签名、过期时间和 claims 读写细节。
 */
@Service
public class JwtTokenService {

    private final String secret;
    private final long expireMinutes;
    private SecretKey secretKey;

    public JwtTokenService(@Value("${live.security.jwt-secret}") String secret,
                           @Value("${live.security.expire-minutes:1440}") long expireMinutes) {
        this.secret = secret;
        this.expireMinutes = expireMinutes;
    }

    /**
     * 在 Bean 初始化完成后，把配置里的密钥转换成 JJWT 可直接使用的 SecretKey。
     */
    @PostConstruct
    public void init() {
        this.secretKey = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
    }

    /**
     * 为当前用户生成一个带有基础身份信息的 JWT。
     */
    public String createToken(User user) {
        Instant now = Instant.now();
        return Jwts.builder()
                .subject(String.valueOf(user.getId()))
                .claim("nickname", user.getNickname())
                .claim("accountType", user.getAccountType())
                .issuedAt(Date.from(now))
                .expiration(Date.from(now.plus(expireMinutes, ChronoUnit.MINUTES)))
                .signWith(secretKey)
                .compact();
    }

    /**
     * 解析并校验 JWT；若签名不合法、已过期或格式错误，统一按未登录处理。
     */
    public JwtUserClaims parseToken(String token) {
        try {
            Claims claims = Jwts.parser()
                    .verifyWith(secretKey)
                    .build()
                    .parseSignedClaims(token)
                    .getPayload();
            return new JwtUserClaims(
                    Long.valueOf(claims.getSubject()),
                    claims.get("nickname", String.class),
                    claims.get("accountType", String.class)
            );
        } catch (Exception ex) {
            throw new BusinessException(ErrorCode.AUTH_INVALID);
        }
    }

    /**
     * 从 JWT 中抽取出的轻量用户信息，供拦截器和 WebSocket 握手阶段复用。
     */
    public record JwtUserClaims(Long userId, String nickname, String accountType) {
    }
}
