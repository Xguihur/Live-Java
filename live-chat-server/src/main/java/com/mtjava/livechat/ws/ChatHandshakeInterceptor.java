package com.mtjava.livechat.ws;

import com.mtjava.livechat.service.JwtTokenService;
import com.mtjava.livechat.service.RoomService;
import com.mtjava.livechat.ws.session.SessionUser;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.http.server.ServletServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.server.HandshakeInterceptor;

import java.util.Map;

/**
 * WebSocket 握手拦截器，负责从连接参数中完成 JWT 鉴权并绑定房间用户信息。
 */
@Component
public class ChatHandshakeInterceptor implements HandshakeInterceptor {

    public static final String SESSION_USER_KEY = "SESSION_USER";

    private final JwtTokenService jwtTokenService;
    private final RoomService roomService;

    public ChatHandshakeInterceptor(JwtTokenService jwtTokenService, RoomService roomService) {
        this.jwtTokenService = jwtTokenService;
        this.roomService = roomService;
    }

    /**
     * 握手前检查 token 和 roomId，校验通过后把当前连接对应的用户信息放入 session attributes。
     */
    @Override
    public boolean beforeHandshake(ServerHttpRequest request,
                                   ServerHttpResponse response,
                                   WebSocketHandler wsHandler,
                                   Map<String, Object> attributes) {
        if (!(request instanceof ServletServerHttpRequest servletRequest)) {
            response.setStatusCode(HttpStatus.BAD_REQUEST);
            return false;
        }

        String token = servletRequest.getServletRequest().getParameter("token");
        String roomIdValue = servletRequest.getServletRequest().getParameter("roomId");
        if (token == null || roomIdValue == null) {
            response.setStatusCode(HttpStatus.UNAUTHORIZED);
            return false;
        }

        JwtTokenService.JwtUserClaims claims = jwtTokenService.parseToken(token);
        Long roomId = Long.valueOf(roomIdValue);
        roomService.getRequiredRoom(roomId);
        attributes.put(SESSION_USER_KEY, new SessionUser(roomId, claims.userId(), claims.nickname(), claims.accountType()));
        return true;
    }

    /**
     * 当前项目的握手阶段没有额外收尾动作，因此保持空实现。
     */
    @Override
    public void afterHandshake(ServerHttpRequest request, ServerHttpResponse response, WebSocketHandler wsHandler, Exception exception) {
    }
}
