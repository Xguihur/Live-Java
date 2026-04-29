package com.mtjava.livechat.mapper;

import com.mtjava.livechat.model.User;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

/**
 * 用户表的数据访问接口。
 */
@Mapper
public interface UserMapper {

    /**
     * 写入一条新的用户记录。
     */
    int insert(User user);

    /**
     * 按主键查询用户。
     */
    User findById(@Param("id") Long id);
}
