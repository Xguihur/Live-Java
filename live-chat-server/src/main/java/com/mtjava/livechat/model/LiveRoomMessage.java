package com.mtjava.livechat.model;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * 直播间消息实体，对应一条持久化的聊天消息。
 */
@Data
public class LiveRoomMessage {
    private Long id;
    private Long roomId;
    private Long senderId;
    private String senderName;
    private String messageType;
    private String content;
    private String status;
    private LocalDateTime sendTime;
    private LocalDateTime createdAt;
}
