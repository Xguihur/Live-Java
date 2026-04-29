package com.mtjava.livechat.ws.session;

import org.springframework.stereotype.Component;
import org.springframework.web.socket.WebSocketSession;

import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArraySet;

/**
 * WebSocket 房间会话管理器，负责维护“房间 -> 连接集合”以及“连接 -> 用户”的映射关系。
 */
@Component
public class ChatRoomSessionManager {

    private final Map<Long, Set<WebSocketSession>> roomSessions = new ConcurrentHashMap<>();
    private final Map<String, SessionUser> sessionUsers = new ConcurrentHashMap<>();

    /**
     * 把一个新连接登记到对应房间中。
     */
    public void addSession(SessionUser user, WebSocketSession session) {
        roomSessions.computeIfAbsent(user.roomId(), ignored -> new CopyOnWriteArraySet<>()).add(session);
        sessionUsers.put(session.getId(), user);
    }

    /**
     * 移除连接并返回它原先对应的用户信息，便于调用方继续做离线清理。
     */
    public SessionUser removeSession(WebSocketSession session) {
        SessionUser user = sessionUsers.remove(session.getId());
        if (user == null) {
            return null;
        }
        Set<WebSocketSession> sessions = roomSessions.get(user.roomId());
        if (sessions != null) {
            sessions.remove(session);
            if (sessions.isEmpty()) {
                roomSessions.remove(user.roomId());
            }
        }
        return user;
    }

    /**
     * 获取某个房间当前持有的全部连接。
     */
    public Set<WebSocketSession> getRoomSessions(Long roomId) {
        return roomSessions.getOrDefault(roomId, Set.of());
    }
}
