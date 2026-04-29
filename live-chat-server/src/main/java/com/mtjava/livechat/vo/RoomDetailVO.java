package com.mtjava.livechat.vo;

/**
 * 房间详情接口返回给前端的视图对象。
 */
public record RoomDetailVO(
        Long id,
        String roomName,
        Long ownerUserId,
        String coverUrl,
        String roomStatus,
        Long onlineCount
) {
}
