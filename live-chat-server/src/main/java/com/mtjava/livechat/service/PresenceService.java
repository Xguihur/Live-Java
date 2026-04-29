package com.mtjava.livechat.service;

import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;

/**
 * 房间在线状态服务，使用 Redis 记录每个房间当前在线的用户集合。
 */
@Service
public class PresenceService {

    private final StringRedisTemplate stringRedisTemplate;

    public PresenceService(StringRedisTemplate stringRedisTemplate) {
        this.stringRedisTemplate = stringRedisTemplate;
    }

    /**
     * 标记用户进入房间在线，并顺带刷新集合过期时间，避免脏数据长期残留。
     */
    public void markOnline(Long roomId, Long userId) {
        String key = usersKey(roomId);
        stringRedisTemplate.opsForSet().add(key, String.valueOf(userId));
        stringRedisTemplate.expire(key, Duration.ofHours(12));
    }

    /**
     * 标记用户离线，从房间在线集合中移除。
     */
    public void markOffline(Long roomId, Long userId) {
        stringRedisTemplate.opsForSet().remove(usersKey(roomId), String.valueOf(userId));
    }

    /**
     * 统计房间当前在线人数，供房间列表、详情和 WebSocket 广播复用。
     */
    public long getOnlineCount(Long roomId) {
        Long size = stringRedisTemplate.opsForSet().size(usersKey(roomId));
        return size == null ? 0L : size;
    }

    /**
     * 统一生成房间在线用户集合的 key。
     */
    private String usersKey(Long roomId) {
        return "live:room:users:" + roomId;
    }
}
