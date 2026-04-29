package com.mtjava.livechat.mapper;

import com.mtjava.livechat.model.LiveRoomMessage;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 聊天消息的数据访问接口。
 */
@Mapper
public interface MessageMapper {

    /**
     * 写入一条新的聊天消息。
     */
    int insert(LiveRoomMessage message);

    /**
     * 查询房间最近一段历史消息。
     */
    List<LiveRoomMessage> findRecentByRoomId(@Param("roomId") Long roomId, @Param("limit") Integer limit);

    /**
     * 以消息 id 为游标向前翻页，查询更早的历史消息。
     */
    List<LiveRoomMessage> findRecentByRoomIdBeforeId(@Param("roomId") Long roomId,
                                                     @Param("beforeId") Long beforeId,
                                                     @Param("limit") Integer limit);
}
