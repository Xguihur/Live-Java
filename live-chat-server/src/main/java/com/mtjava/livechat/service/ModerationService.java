package com.mtjava.livechat.service;

import com.mtjava.livechat.common.AuthenticatedUser;
import com.mtjava.livechat.common.BusinessException;
import com.mtjava.livechat.common.ErrorCode;
import com.mtjava.livechat.common.UserContext;
import com.mtjava.livechat.dto.BanUserRequest;
import com.mtjava.livechat.mapper.BanMapper;
import com.mtjava.livechat.mapper.RoomMemberMapper;
import com.mtjava.livechat.model.LiveRoomBan;
import com.mtjava.livechat.model.LiveRoomMember;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.LocalDateTime;

/**
 * 房间风控/管理服务，负责禁言、解禁以及管理员权限校验。
 */
@Service
public class ModerationService {

    private final BanMapper banMapper;
    private final RoomMemberMapper roomMemberMapper;
    private final RoomService roomService;
    private final StringRedisTemplate stringRedisTemplate;

    public ModerationService(BanMapper banMapper,
                             RoomMemberMapper roomMemberMapper,
                             RoomService roomService,
                             StringRedisTemplate stringRedisTemplate) {
        this.banMapper = banMapper;
        this.roomMemberMapper = roomMemberMapper;
        this.roomService = roomService;
        this.stringRedisTemplate = stringRedisTemplate;
    }

    /**
     * 给指定房间成员创建一条禁言记录，同时把禁言状态写入 Redis，便于高频判断。
     */
    @Transactional
    public void banUser(Long roomId, BanUserRequest request) {
        AuthenticatedUser operator = UserContext.getRequired();
        requireRoomOperator(roomId, operator.userId());
        roomService.ensureMembership(roomId, request.userId());

        LocalDateTime now = LocalDateTime.now();
        LiveRoomBan ban = new LiveRoomBan();
        ban.setRoomId(roomId);
        ban.setUserId(request.userId());
        ban.setBanUntil(now.plusMinutes(request.durationMinutes()));
        ban.setReason(request.reason());
        ban.setCreatedBy(operator.userId());
        ban.setCreatedAt(now);
        banMapper.insert(ban);

        String key = banKey(roomId, request.userId());
        stringRedisTemplate.opsForValue().set(key, "1", Duration.ofMinutes(request.durationMinutes()));
    }

    /**
     * 删除当前仍生效的禁言记录，并同步清理 Redis 缓存。
     */
    @Transactional
    public void unbanUser(Long roomId, Long userId) {
        AuthenticatedUser operator = UserContext.getRequired();
        requireRoomOperator(roomId, operator.userId());
        banMapper.deleteActiveBan(roomId, userId, LocalDateTime.now());
        stringRedisTemplate.delete(banKey(roomId, userId));
    }

    /**
     * 优先从 Redis 判断是否被禁言；缓存未命中时回源数据库，并把结果重新写回缓存。
     */
    public boolean isBanned(Long roomId, Long userId) {
        String key = banKey(roomId, userId);
        Boolean hasBan = stringRedisTemplate.hasKey(key);
        if (Boolean.TRUE.equals(hasBan)) {
            return true;
        }

        LiveRoomBan activeBan = banMapper.findActiveBan(roomId, userId, LocalDateTime.now());
        if (activeBan == null) {
            return false;
        }

        Duration ttl = Duration.between(LocalDateTime.now(), activeBan.getBanUntil());
        if (!ttl.isNegative() && !ttl.isZero()) {
            stringRedisTemplate.opsForValue().set(key, "1", ttl);
        }
        return true;
    }

    /**
     * 校验当前用户是否具备房间管理权限，允许房主和管理员执行管理操作。
     */
    public void requireRoomOperator(Long roomId, Long userId) {
        roomService.getRequiredRoom(roomId);
        LiveRoomMember member = roomMemberMapper.findByRoomIdAndUserId(roomId, userId);
        boolean admin = member != null && ("ADMIN".equals(member.getRoomRole()) || "OWNER".equals(member.getRoomRole()));
        boolean owner = roomService.getRequiredRoom(roomId).getOwnerUserId().equals(userId);
        if (!admin && !owner) {
            throw new BusinessException(ErrorCode.FORBIDDEN, "只有房主或房间管理员可以执行该操作");
        }
    }

    /**
     * 统一生成房间禁言缓存的 key。
     */
    private String banKey(Long roomId, Long userId) {
        return "live:room:ban:" + roomId + ":" + userId;
    }
}
