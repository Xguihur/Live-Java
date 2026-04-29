package com.mtjava.livechat.model;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * 房间禁言记录实体，描述谁在什么房间被禁言到什么时候。
 */
@Data
public class LiveRoomBan {
    private Long id;
    private Long roomId;
    private Long userId;
    private LocalDateTime banUntil;
    private String reason;
    private Long createdBy;
    private LocalDateTime createdAt;
}
