package com.interviewpractice.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.interviewpractice.entity.InterviewRoom;
import com.interviewpractice.entity.RoomParticipant;
import com.interviewpractice.entity.User;
import com.interviewpractice.mapper.InterviewRoomMapper;
import com.interviewpractice.mapper.RoomParticipantMapper;
import com.interviewpractice.service.InterviewRoomService;
import com.interviewpractice.utils.ApiResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

@Service
public class InterviewRoomServiceImpl extends ServiceImpl<InterviewRoomMapper, InterviewRoom> implements InterviewRoomService {
    @Autowired
    private RoomParticipantMapper roomParticipantMapper;


    @Override
    public InterviewRoom createRoom(String topic, String description, Integer maxParticipants) {
        InterviewRoom room = new InterviewRoom();
        room.setTopic(topic);
        room.setDescription(description);
        room.setMaxParticipants(maxParticipants != null ? maxParticipants : 6);
        room.setCurrentParticipants(0);
        room.setStatus("WAITING");
        room.setCreatedAt(LocalDateTime.now());

        //save(T entity) 方法是用于保存单个实体对象到数据库的核心方法
        save(room);

        return room;
    }

    @Override
    @Transactional
    public ApiResponse<InterviewRoom> joinRoom(Long roomId, User user) {
        InterviewRoom room = getById(roomId);
        if (room == null) {
            return ApiResponse.error("房间不存在", "ROOM_NOT_FOUND");
        }

        if (!"WAITING".equals(room.getStatus())) {
            return ApiResponse.error("房间开始或已结束", "ROOM_NOT_AVAILABLE");
        }

        if (room.getCurrentParticipants() >= room.getMaxParticipants()) {
            return ApiResponse.error("房间已满", "ROOM_FULL");
        }

        // 检查用户是否已在房间中
        Long count = roomParticipantMapper.selectCount(
                new QueryWrapper<RoomParticipant>()
                        .eq("room_id", roomId)
                        .eq("user_id", user.getId())
                        //扫描已加入但未离开的用户数据
                        .isNull("left_at")
                        .isNotNull("joined_at")
        );

        if (count > 0) {
            return ApiResponse.error("用户已在房间中", "ALREADY_JOINED");
        }

        RoomParticipant participant = new RoomParticipant();
        participant.setRoomId(roomId);
        participant.setUserId(user.getId());
        participant.setRole("MEMBER");
        participant.setJoinedAt(LocalDateTime.now());
        roomParticipantMapper.insert(participant);

        // 更新房间人数
        room.setCurrentParticipants(room.getCurrentParticipants() + 1);
        updateById(room);

        return ApiResponse.success(room);
    }

    @Override
    @Transactional
    public void leaveRoom(Long roomId, Long userId) {
        InterviewRoom room = getById(roomId);
        if (room == null) {
            throw new RuntimeException("房间不存在");
        }

        // 标记用户离开
        RoomParticipant participant = roomParticipantMapper.selectOne(
               new QueryWrapper<RoomParticipant>()
                       .eq("room_id", roomId)
                       .eq("user_id", userId)
                       .isNull("left_at")
        );

        if (participant != null) {
            participant.setLeftAt(LocalDateTime.now());
            roomParticipantMapper.updateById(participant);

            // 更新房间人数
            room.setCurrentParticipants(room.getCurrentParticipants() - 1);
            updateById(room);
        }
    }

    @Override
    public List<InterviewRoom> getAvailableRooms() {
        return lambdaQuery()
                .eq(InterviewRoom::getStatus, "WAITING")
//                .lt(InterviewRoom::getCurrentParticipants, com.baomidou.mybatisplus.core.toolkit.Wrappers.<InterviewRoom>query().getEntity().getMaxParticipants())
//                Wrappers.<InterviewRoom>query().getEntity() 返回 null，导致 getMaxParticipants() 抛出 NullPointerException。
                .apply("current_participants < max_participants")
                .list();
    }

    @Override
    public InterviewRoom getRoomDetail(Long roomId) {
        return getById(roomId);
    }

    @Override
    public void startRoom(Long roomId) {
        InterviewRoom room = getById(roomId);
        if (room != null && "WAITING".equals(room.getStatus())) {
            room.setStatus("ONGOING");
            room.setStartedAt(LocalDateTime.now());
            updateById(room);
        }
    }

    @Override
    public void completeRoom(Long roomId) {
        InterviewRoom room = getById(roomId);
        if (room != null && "ONGOING".equals(room.getStatus())) {
            room.setStatus("COMPLETED");
            room.setEndedAt(LocalDateTime.now());
            updateById(room);
        }
    }
}
