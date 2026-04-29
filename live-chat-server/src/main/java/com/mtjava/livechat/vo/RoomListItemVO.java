package com.mtjava.livechat.vo;

/**
 * 房间列表页使用的单个房间展示对象。
 */
public record RoomListItemVO(
        Long id,
        String roomName,
        Long ownerUserId,
        String coverUrl,
        String roomStatus,
        Long onlineCount
) {
}
