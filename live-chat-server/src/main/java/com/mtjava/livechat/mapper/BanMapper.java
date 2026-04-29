package com.mtjava.livechat.mapper;

import com.mtjava.livechat.model.LiveRoomBan;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.time.LocalDateTime;

/**
 * 房间禁言记录的数据访问接口。
 */
@Mapper
public interface BanMapper {

    /**
     * 插入一条新的禁言记录。
     */
    int insert(LiveRoomBan ban);

    /**
     * 查询当前时间仍处于生效状态的禁言记录。
     */
    LiveRoomBan findActiveBan(@Param("roomId") Long roomId,
                              @Param("userId") Long userId,
                              @Param("now") LocalDateTime now);

    /**
     * 删除当前仍有效的禁言记录，通常用于手动解禁。
     */
    int deleteActiveBan(@Param("roomId") Long roomId,
                        @Param("userId") Long userId,
                        @Param("now") LocalDateTime now);
}
