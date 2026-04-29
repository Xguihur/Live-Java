package com.mtjava.livechat.ws.session;

/**
 * 绑定在 WebSocket 连接上的用户快照，描述这条连接属于哪个房间、哪个用户。
 */
public record SessionUser(
        Long roomId,
        Long userId,
        String nickname,
        String accountType
) {
}
