package com.mtjava.livechat.service;

import com.mtjava.livechat.common.BusinessException;
import com.mtjava.livechat.common.ErrorCode;
import com.mtjava.livechat.mapper.MessageMapper;
import com.mtjava.livechat.model.LiveRoomMessage;
import com.mtjava.livechat.vo.MessageVO;
import com.mtjava.livechat.ws.protocol.WsChatMessagePayload;
import com.mtjava.livechat.ws.session.SessionUser;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

/**
 * 聊天消息服务，负责历史消息查询、发言校验、消息落库与最近消息缓存。
 */
@Service
public class MessageService {

    private final MessageMapper messageMapper;
    private final RoomService roomService;
    private final ModerationService moderationService;
    private final RateLimitService rateLimitService;
    private final StringRedisTemplate stringRedisTemplate;
    private final int recentMessageLimit;

    public MessageService(MessageMapper messageMapper,
                          RoomService roomService,
                          ModerationService moderationService,
                          RateLimitService rateLimitService,
                          StringRedisTemplate stringRedisTemplate,
                          @Value("${live.message.recent-limit:50}") int recentMessageLimit) {
        this.messageMapper = messageMapper;
        this.roomService = roomService;
        this.moderationService = moderationService;
        this.rateLimitService = rateLimitService;
        this.stringRedisTemplate = stringRedisTemplate;
        this.recentMessageLimit = recentMessageLimit;
    }

    /**
     * 查询房间历史消息；数据库按时间倒序取回后，这里会反转成前端更容易直接渲染的正序。
     */
    public List<MessageVO> listHistory(Long roomId, Integer pageSize, Long beforeId) {
        roomService.getRequiredRoom(roomId);
        int finalPageSize = Math.max(1, Math.min(pageSize, 50));
        List<LiveRoomMessage> messages = beforeId == null
                ? messageMapper.findRecentByRoomId(roomId, finalPageSize)
                : messageMapper.findRecentByRoomIdBeforeId(roomId, beforeId, finalPageSize);
        Collections.reverse(messages);
        return messages.stream().map(this::toMessageVO).toList();
    }

    /**
     * 处理一条聊天消息：校验房间、禁言和限流后写入数据库，再补一份最近消息缓存。
     */
    @Transactional
    public MessageVO handleChatMessage(SessionUser sender, WsChatMessagePayload payload) {
        if (!sender.roomId().equals(payload.roomId())) {
            throw new BusinessException(ErrorCode.INVALID_PARAM, "消息房间与当前连接不一致");
        }
        if (payload.content() == null || payload.content().isBlank()) {
            throw new BusinessException(ErrorCode.INVALID_PARAM, "消息内容不能为空");
        }

        roomService.ensureMembership(sender.roomId(), sender.userId());
        if (moderationService.isBanned(sender.roomId(), sender.userId())) {
            throw new BusinessException(ErrorCode.USER_BANNED);
        }
        if (!rateLimitService.allowMessage(sender.userId())) {
            throw new BusinessException(ErrorCode.MESSAGE_BLOCKED);
        }

        LiveRoomMessage message = new LiveRoomMessage();
        message.setRoomId(sender.roomId());
        message.setSenderId(sender.userId());
        message.setSenderName(sender.nickname());
        message.setMessageType("TEXT");
        message.setContent(payload.content().trim());
        message.setStatus("NORMAL");
        message.setSendTime(LocalDateTime.now());
        message.setCreatedAt(LocalDateTime.now());
        messageMapper.insert(message);

        cacheRecentMessage(message);
        return toMessageVO(message);
    }

    /**
     * 在 Redis 里维护最近消息的轻量缓存，方便后续扩展热点房间的快速读取。
     */
    private void cacheRecentMessage(LiveRoomMessage message) {
        String key = "live:room:recent_msgs:" + message.getRoomId();
        stringRedisTemplate.opsForList().leftPush(key, message.getContent());
        stringRedisTemplate.opsForList().trim(key, 0, Math.max(recentMessageLimit - 1, 0));
    }

    /**
     * 把数据库实体转换成面向接口/推送的消息视图对象。
     */
    private MessageVO toMessageVO(LiveRoomMessage message) {
        return new MessageVO(
                message.getId(),
                message.getRoomId(),
                message.getSenderId(),
                message.getSenderName(),
                message.getMessageType(),
                message.getContent(),
                message.getStatus(),
                message.getSendTime()
        );
    }
}
