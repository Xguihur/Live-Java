package com.mtjava.livechat.mapper;

import com.mtjava.livechat.model.LiveRoomMember;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

/**
 * 房间成员关系的数据访问接口。
 */
@Mapper
public interface RoomMemberMapper {

    /**
     * 查询某个用户是否已经加入指定房间。
     */
    LiveRoomMember findByRoomIdAndUserId(@Param("roomId") Long roomId, @Param("userId") Long userId);

    /**
     * 新增一条房间成员记录。
     */
    int insert(LiveRoomMember member);
}
