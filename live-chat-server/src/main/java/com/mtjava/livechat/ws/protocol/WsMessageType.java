package com.mtjava.livechat.ws.protocol;

/**
 * WebSocket 通信里约定的消息类型枚举。
 */
public enum WsMessageType {
    CHAT_MESSAGE,
    HEARTBEAT,
    NEW_MESSAGE,
    SYSTEM_NOTICE,
    ROOM_ONLINE_COUNT,
    ERROR
}
