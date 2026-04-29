package com.mtjava.livechat.ws.protocol;

/**
 * WebSocket 聊天消息的业务载荷，目前包含房间 id 和文本内容。
 */
public record WsChatMessagePayload(
        Long roomId,
        String content
) {
}
