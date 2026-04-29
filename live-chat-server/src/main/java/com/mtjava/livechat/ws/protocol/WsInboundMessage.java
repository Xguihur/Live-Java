package com.mtjava.livechat.ws.protocol;

import lombok.Data;

/**
 * WebSocket 客户端上行消息的统一封装。
 */
@Data
public class WsInboundMessage {
    private WsMessageType type;
    private String traceId;
    private WsChatMessagePayload data;
}
