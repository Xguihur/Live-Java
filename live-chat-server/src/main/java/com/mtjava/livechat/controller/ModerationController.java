package com.mtjava.livechat.controller;

import com.mtjava.livechat.common.ApiResponse;
import com.mtjava.livechat.dto.BanUserRequest;
import com.mtjava.livechat.dto.UnbanUserRequest;
import com.mtjava.livechat.service.ModerationService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 房间禁言管理接口，对应管理员/房主的操作入口。
 */
@RestController
@RequestMapping("/api/rooms/{roomId}")
public class ModerationController {

    private final ModerationService moderationService;

    public ModerationController(ModerationService moderationService) {
        this.moderationService = moderationService;
    }

    /**
     * 在指定房间内禁言某个成员。
     */
    @PostMapping("/ban")
    public ApiResponse<Void> banUser(@PathVariable Long roomId, @Valid @RequestBody BanUserRequest request) {
        moderationService.banUser(roomId, request);
        return ApiResponse.success();
    }

    /**
     * 解除指定房间内某个成员的禁言状态。
     */
    @PostMapping("/unban")
    public ApiResponse<Void> unbanUser(@PathVariable Long roomId, @Valid @RequestBody UnbanUserRequest request) {
        moderationService.unbanUser(roomId, request.userId());
        return ApiResponse.success();
    }
}
