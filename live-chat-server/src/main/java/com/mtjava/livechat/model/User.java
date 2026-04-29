package com.mtjava.livechat.model;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * 用户实体，当前主要承载游客账号的基础资料。
 */
@Data
public class User {
    private Long id;
    private String nickname;
    private String avatar;
    private String accountType;
    private String status;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
