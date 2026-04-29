package com.mtjava.livechat.ws;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mtjava.livechat.common.BusinessException;
import com.mtjava.livechat.common.ErrorCode;
import com.mtjava.livechat.service.MessageService;
import com.mtjava.livechat.service.PresenceService;
import com.mtjava.livechat.service.RoomService;
import com.mtjava.livechat.vo.MessageVO;
import com.mtjava.livechat.ws.protocol.WsInboundMessage;
import com.mtjava.livechat.ws.protocol.WsMessageType;
import com.mtjava.livechat.ws.protocol.WsOutboundMessage;
import com.mtjava.livechat.ws.session.ChatRoomSessionManager;
import com.mtjava.livechat.ws.session.SessionUser;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.io.IOException;
import java.util.Map;

/**
 * 聊天 WebSocket 的核心处理器，负责连接生命周期、消息路由和房间广播。
 */
@Component
public class ChatWebSocketHandler extends TextWebSocketHandler {

    private final ObjectMapper objectMapper;
    private final ChatRoomSessionManager sessionManager;
    private final PresenceService presenceService;
    private final RoomService roomService;
    private final MessageService messageService;

    public ChatWebSocketHandler(ObjectMapper objectMapper,
                                ChatRoomSessionManager sessionManager,
                                PresenceService presenceService,
                                RoomService roomService,
                                MessageService messageService) {
        this.objectMapper = objectMapper;
        this.sessionManager = sessionManager;
        this.presenceService = presenceService;
        this.roomService = roomService;
        this.messageService = messageService;
    }

    /**
     * 连接建立后补齐房间成员关系、登记会话，并广播最新在线人数。
     */
    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        SessionUser user = getSessionUser(session);
        roomService.ensureMembership(user.roomId(), user.userId());
        sessionManager.addSession(user, session);
        presenceService.markOnline(user.roomId(), user.userId());
        broadcastOnlineCount(user.roomId());
    }

    /**
     * 处理客户端发来的文本消息，目前支持心跳和聊天消息两类。
     */
    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage textMessage) throws Exception {
        SessionUser user = getSessionUser(session);
        try {
            WsInboundMessage inboundMessage = objectMapper.readValue(textMessage.getPayload(), WsInboundMessage.class);
            if (inboundMessage.getType() == WsMessageType.HEARTBEAT) {
                send(session, new WsOutboundMessage<>(WsMessageType.HEARTBEAT, Map.of("status", "ok")));
                return;
            }

            if (inboundMessage.getType() != WsMessageType.CHAT_MESSAGE || inboundMessage.getData() == null) {
                throw new BusinessException(ErrorCode.INVALID_PARAM, "暂不支持的消息类型");
            }

            MessageVO message = messageService.handleChatMessage(user, inboundMessage.getData());
            broadcastToRoom(user.roomId(), new WsOutboundMessage<>(WsMessageType.NEW_MESSAGE, message));
        } catch (BusinessException ex) {
            send(session, new WsOutboundMessage<>(WsMessageType.ERROR, Map.of(
                    "code", ex.getErrorCode().getCode(),
                    "message", ex.getMessage()
            )));
        }
    }

    /**
     * 连接关闭后清理会话和在线状态，并通知房间内其他人更新在线人数。
     */
    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
        SessionUser user = sessionManager.removeSession(session);
        if (user != null) {
            presenceService.markOffline(user.roomId(), user.userId());
            broadcastOnlineCount(user.roomId());
        }
    }

    /**
     * 统计并广播某个房间的在线人数变化。
     */
    private void broadcastOnlineCount(Long roomId) throws IOException {
        long onlineCount = presenceService.getOnlineCount(roomId);
        broadcastToRoom(roomId, new WsOutboundMessage<>(WsMessageType.ROOM_ONLINE_COUNT, Map.of(
                "roomId", roomId,
                "onlineCount", onlineCount
        )));
    }

    /**
     * 向指定房间内所有仍处于打开状态的连接广播一条消息。
     */
    private void broadcastToRoom(Long roomId, WsOutboundMessage<?> message) throws IOException {
        for (WebSocketSession roomSession : sessionManager.getRoomSessions(roomId)) {
            if (roomSession.isOpen()) {
                send(roomSession, message);
            }
        }
    }

    /**
     * 对单个 WebSocketSession 做串行发送，避免并发写同一个连接。
     */
    private void send(WebSocketSession session, WsOutboundMessage<?> payload) throws IOException {
        synchronized (session) {
            session.sendMessage(new TextMessage(objectMapper.writeValueAsString(payload)));
        }
    }

    /**
     * 从握手阶段写入的 attributes 中取出当前连接对应的用户身份。
     */
    private SessionUser getSessionUser(WebSocketSession session) {
        Object value = session.getAttributes().get(ChatHandshakeInterceptor.SESSION_USER_KEY);
        if (value instanceof SessionUser user) {
            return user;
        }
        throw new BusinessException(ErrorCode.AUTH_INVALID);
    }
}
