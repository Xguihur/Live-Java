package com.mtjava.livechat.mapper;

import com.mtjava.livechat.model.LiveRoom;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 直播间主表的数据访问接口。
 */
@Mapper
public interface RoomMapper {

    /**
     * 查询当前可展示的房间列表。
     */
    List<LiveRoom> findActiveRooms();

    /**
     * 按主键查询单个房间。
     */
    LiveRoom findById(@Param("id") Long id);
}
