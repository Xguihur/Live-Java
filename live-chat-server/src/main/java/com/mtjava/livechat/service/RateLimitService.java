package com.mtjava.livechat.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;

/**
 * 简单的发言限流服务，限制用户在一个时间窗口内可发送的消息数量。
 */
@Service
public class RateLimitService {

    private final StringRedisTemplate stringRedisTemplate;
    private final int windowSeconds;
    private final int maxMessages;

    public RateLimitService(StringRedisTemplate stringRedisTemplate,
                            @Value("${live.rate-limit.window-seconds:3}") int windowSeconds,
                            @Value("${live.rate-limit.max-messages:3}") int maxMessages) {
        this.stringRedisTemplate = stringRedisTemplate;
        this.windowSeconds = windowSeconds;
        this.maxMessages = maxMessages;
    }

    /**
     * 基于 Redis 自增计数判断本次消息是否允许发送。
     */
    public boolean allowMessage(Long userId) {
        String key = "live:user:send_limit:" + userId;
        Long count = stringRedisTemplate.opsForValue().increment(key);
        if (count != null && count == 1L) {
            stringRedisTemplate.expire(key, Duration.ofSeconds(windowSeconds));
        }
        return count == null || count <= maxMessages;
    }
}
