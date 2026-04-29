package com.mtjava.livechat.ws.protocol;

/**
 * WebSocket 服务端下行消息的统一封装。
 */
public record WsOutboundMessage<T>(
        WsMessageType type,
        T data
) {
}
