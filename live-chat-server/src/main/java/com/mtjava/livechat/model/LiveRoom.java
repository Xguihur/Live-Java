package com.mtjava.livechat.model;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * 直播间实体，对应房间的基础信息。
 */
@Data
public class LiveRoom {
    private Long id;
    private String roomName;
    private Long ownerUserId;
    private String coverUrl;
    private String roomStatus;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
