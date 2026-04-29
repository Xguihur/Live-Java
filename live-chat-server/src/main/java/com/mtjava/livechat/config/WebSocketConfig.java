package com.mtjava.livechat.config;

import com.mtjava.livechat.ws.ChatHandshakeInterceptor;
import com.mtjava.livechat.ws.ChatWebSocketHandler;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;

import java.util.List;

/**
 * WebSocket 入口配置，把聊天 handler 挂载到固定的连接地址上。
 */
@Configuration
@EnableWebSocket
public class WebSocketConfig implements WebSocketConfigurer {

    private final ChatWebSocketHandler chatWebSocketHandler;
    private final ChatHandshakeInterceptor chatHandshakeInterceptor;
    private final List<String> allowedOrigins;

    public WebSocketConfig(ChatWebSocketHandler chatWebSocketHandler,
                           ChatHandshakeInterceptor chatHandshakeInterceptor,
                           @Value("${live.cors.allowed-origins:http://localhost:5173}") List<String> allowedOrigins) {
        this.chatWebSocketHandler = chatWebSocketHandler;
        this.chatHandshakeInterceptor = chatHandshakeInterceptor;
        this.allowedOrigins = allowedOrigins;
    }

    /**
     * 注册聊天 WebSocket，并在握手阶段完成鉴权和房间参数校验。
     */
    @Override
    public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
        registry.addHandler(chatWebSocketHandler, "/ws/chat")
                .setAllowedOrigins(allowedOrigins.toArray(String[]::new))
                .addInterceptors(chatHandshakeInterceptor);
    }
}
