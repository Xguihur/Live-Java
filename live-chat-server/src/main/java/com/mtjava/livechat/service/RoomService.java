package com.mtjava.livechat.service;

import com.mtjava.livechat.common.BusinessException;
import com.mtjava.livechat.common.ErrorCode;
import com.mtjava.livechat.mapper.RoomMapper;
import com.mtjava.livechat.mapper.RoomMemberMapper;
import com.mtjava.livechat.model.LiveRoom;
import com.mtjava.livechat.model.LiveRoomMember;
import com.mtjava.livechat.vo.RoomDetailVO;
import com.mtjava.livechat.vo.RoomListItemVO;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 房间服务，负责房间基础查询、成员关系补齐以及房间存在性校验。
 */
@Service
public class RoomService {

    private final RoomMapper roomMapper;
    private final RoomMemberMapper roomMemberMapper;
    private final PresenceService presenceService;

    public RoomService(RoomMapper roomMapper, RoomMemberMapper roomMemberMapper, PresenceService presenceService) {
        this.roomMapper = roomMapper;
        this.roomMemberMapper = roomMemberMapper;
        this.presenceService = presenceService;
    }

    /**
     * 查询可展示的房间列表，并补充每个房间当前在线人数。
     */
    public List<RoomListItemVO> listRooms() {
        return roomMapper.findActiveRooms().stream()
                .map(room -> new RoomListItemVO(
                        room.getId(),
                        room.getRoomName(),
                        room.getOwnerUserId(),
                        room.getCoverUrl(),
                        room.getRoomStatus(),
                        presenceService.getOnlineCount(room.getId())
                ))
                .toList();
    }

    /**
     * 查询单个房间详情，并附带在线人数。
     */
    public RoomDetailVO getRoomDetail(Long roomId) {
        LiveRoom room = getRequiredRoom(roomId);
        return new RoomDetailVO(
                room.getId(),
                room.getRoomName(),
                room.getOwnerUserId(),
                room.getCoverUrl(),
                room.getRoomStatus(),
                presenceService.getOnlineCount(roomId)
        );
    }

    /**
     * 查询房间，不存在时直接抛业务异常，减少调用方重复判空。
     */
    public LiveRoom getRequiredRoom(Long roomId) {
        LiveRoom room = roomMapper.findById(roomId);
        if (room == null) {
            throw new BusinessException(ErrorCode.ROOM_NOT_FOUND);
        }
        return room;
    }

    /**
     * 确保用户和房间之间存在成员关系；第一次进入房间时会自动补一条 MEMBER 记录。
     */
    @Transactional
    public void ensureMembership(Long roomId, Long userId) {
        getRequiredRoom(roomId);
        LiveRoomMember existing = roomMemberMapper.findByRoomIdAndUserId(roomId, userId);
        if (existing != null) {
            return;
        }

        LiveRoomMember member = new LiveRoomMember();
        member.setRoomId(roomId);
        member.setUserId(userId);
        member.setRoomRole("MEMBER");
        member.setJoinedAt(LocalDateTime.now());
        roomMemberMapper.insert(member);
    }
}
