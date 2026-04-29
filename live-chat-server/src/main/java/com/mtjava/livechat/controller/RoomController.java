package com.mtjava.livechat.controller;

import com.mtjava.livechat.common.ApiResponse;
import com.mtjava.livechat.service.MessageService;
import com.mtjava.livechat.service.RoomService;
import com.mtjava.livechat.vo.MessageVO;
import com.mtjava.livechat.vo.RoomDetailVO;
import com.mtjava.livechat.vo.RoomListItemVO;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * 直播间相关查询接口，包括房间列表、详情和历史消息。
 */
@RestController
@RequestMapping("/api/rooms")
public class RoomController {

    private final RoomService roomService;
    private final MessageService messageService;

    public RoomController(RoomService roomService, MessageService messageService) {
        this.roomService = roomService;
        this.messageService = messageService;
    }

    /**
     * 查询当前可用的直播间列表。
     */
    @GetMapping
    public ApiResponse<List<RoomListItemVO>> listRooms() {
        return ApiResponse.success(roomService.listRooms());
    }

    /**
     * 查询单个直播间详情。
     */
    @GetMapping("/{roomId}")
    public ApiResponse<RoomDetailVO> getRoomDetail(@PathVariable Long roomId) {
        return ApiResponse.success(roomService.getRoomDetail(roomId));
    }

    /**
     * 分页拉取直播间历史消息，支持按消息 id 向前翻页。
     */
    @GetMapping("/{roomId}/messages")
    public ApiResponse<List<MessageVO>> getHistory(@PathVariable Long roomId,
                                                   @RequestParam(defaultValue = "20") Integer pageSize,
                                                   @RequestParam(required = false) Long beforeId) {
        return ApiResponse.success(messageService.listHistory(roomId, pageSize, beforeId));
    }
}
