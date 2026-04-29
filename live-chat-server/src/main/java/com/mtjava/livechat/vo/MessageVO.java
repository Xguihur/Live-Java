package com.mtjava.livechat.vo;

import java.time.LocalDateTime;

/**
 * 对外返回的一条聊天消息视图。
 */
public record MessageVO(
        Long messageId,
        Long roomId,
        Long senderId,
        String senderName,
        String messageType,
        String content,
        String status,
        LocalDateTime sendTime
) {
}
