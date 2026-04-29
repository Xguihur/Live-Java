package com.mtjava.livechat.model;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * 房间成员关系实体，用于表示用户在房间中的角色和加入时间。
 */
@Data
public class LiveRoomMember {
    private Long id;
    private Long roomId;
    private Long userId;
    private String roomRole;
    private LocalDateTime joinedAt;
}
