package com.interviewpractice.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.interviewpractice.entity.InterviewRoom;
import com.interviewpractice.entity.RoomParticipant;
import com.interviewpractice.entity.User;
import com.interviewpractice.mapper.InterviewRoomMapper;
import com.interviewpractice.mapper.RoomParticipantMapper;
import com.interviewpractice.mapper.UserMapper;
import com.interviewpractice.service.InterviewRoomService;
import com.interviewpractice.utils.ApiResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

@Service
public class InterviewRoomServiceImpl extends ServiceImpl<InterviewRoomMapper, InterviewRoom> implements InterviewRoomService {
    @Autowired
    private RoomParticipantMapper roomParticipantMapper;

    @Autowired
    private InterviewRoomMapper interviewRoomMapper;

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private SimpMessagingTemplate messagingTemplate;

    @Override
    public InterviewRoom createRoom(Long userId, String topic, String description, Integer maxParticipants) {
        InterviewRoom room = new InterviewRoom();
        room.setTopic(topic);
        room.setDescription(description);
        room.setMaxParticipants(maxParticipants != null ? maxParticipants : 6);
        room.setCurrentParticipants(0);
        room.setStatus("WAITING");
        room.setCreatedAt(LocalDateTime.now());
        room.setCreatorId(userId);

        //save(T entity) 方法是用于保存单个实体对象到数据库的核心方法
        save(room);

        Map<String, Object> createMessage = new HashMap<>();
        createMessage.put("eventType", "ROOM_CREATED");
        createMessage.put("room", room); // 发送整个房间对象
        createMessage.put("action", "ROOM_CREATED");
        createMessage.put("timestamp", LocalDateTime.now().toString());

        messagingTemplate.convertAndSend("/topic/room-list/update", createMessage);

        return room;
    }

    @Override
    public void deleteRoom(Long userId, Long roomId) {
        InterviewRoom room = getById(roomId);
        if (Objects.equals(room.getCreatorId(), userId)) interviewRoomMapper.deleteById(roomId);
        else System.err.println("Only creator can delete this room");

        Map<String, Object> createMessage = new HashMap<>();
        createMessage.put("eventType", "ROOM_DELETED");
        createMessage.put("roomId", roomId);
        createMessage.put("action", "ROOM_DELETED");
        createMessage.put("timestamp", LocalDateTime.now().toString());

        messagingTemplate.convertAndSend("/topic/room-list/update", createMessage);
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

        // 检查用户是否已在当前房间中
        Long count = roomParticipantMapper.selectCount(
                new QueryWrapper<RoomParticipant>()
                        .eq("room_id", roomId)
                        .eq("user_id", user.getId())
                        //扫描已加入但未离开的用户数据
                        .isNull("left_at")
                        .isNotNull("joined_at")
        );

        if (count > 0) {
            return ApiResponse.error("用户已在当前房间中", "ALREADY_JOINED_IN_IT");
        }

        if (user.isAlreadyJoined()) {
            return ApiResponse.error("用户已在其他房间中", "ALREADY_JOINED");
        }

        // 查询用户是否在当前房间有历史记录
        RoomParticipant existingParticipant = roomParticipantMapper.selectOne(
                new QueryWrapper<RoomParticipant>()
                        .eq("room_id", roomId)
                        .eq("user_id", user.getId())
                        .isNotNull("left_at")
        );

        RoomParticipant participant = new RoomParticipant();
        participant.setRoomId(roomId);
        participant.setUserId(user.getId());
        participant.setUserName(user.getUsername());
        participant.setRole("MEMBER");
        participant.setJoinedAt(LocalDateTime.now());

        if (existingParticipant != null) {
            existingParticipant.setJoinedAt(LocalDateTime.now());
            existingParticipant.setLeftAt(null);
            roomParticipantMapper.updateById(existingParticipant);
        } else {
            roomParticipantMapper.insert(participant);
        }
        // 更新房间人数
        room.setCurrentParticipants(room.getCurrentParticipants() + 1);
        updateById(room);
        user.setAlreadyJoined(true);
        userMapper.updateById(user);

        // 发送用户加入通知
        messagingTemplate.convertAndSend("/topic/userJoin/" + roomId, participant);

        Map<String, Object> roomUpdateMessage = new HashMap<>();
        roomUpdateMessage.put("eventType", "ROOM_UPDATED");
        roomUpdateMessage.put("roomId", roomId);
        roomUpdateMessage.put("currentParticipants", room.getCurrentParticipants());
        roomUpdateMessage.put("maxParticipants", room.getMaxParticipants());
        roomUpdateMessage.put("status", room.getStatus());
        roomUpdateMessage.put("action", "USER_JOINED");

        // 广播到所有监听房间列表的客户端
        messagingTemplate.convertAndSend("/topic/room-list/update", roomUpdateMessage);

        return ApiResponse.success(room);
    }

    @Override
    @Transactional
    public void leaveRoom(Long roomId, User user) {
        InterviewRoom room = getById(roomId);
        if (room == null) {
            throw new RuntimeException("房间不存在");
        }

        // 标记用户离开
        RoomParticipant participant = roomParticipantMapper.selectOne(
               new QueryWrapper<RoomParticipant>()
                       .eq("room_id", roomId)
                       .eq("user_id", user.getId())
                       .isNull("left_at")
        );
        user.setAlreadyJoined(false);
        userMapper.updateById(user);
        if (participant != null) {
            participant.setLeftAt(LocalDateTime.now());
            roomParticipantMapper.updateById(participant);
            room.setCurrentParticipants(room.getCurrentParticipants() - 1);
            // 当房间空无一人，房间状态自动变为“WAITING”
            if (room.getCurrentParticipants() == 0) room.setStatus("WAITING");
            updateById(room);

            // 发送用户离开通知
            messagingTemplate.convertAndSend("/topic/userLeave/" + roomId, participant);

            Map<String, Object> roomUpdateMessage = new HashMap<>();
            roomUpdateMessage.put("eventType", "ROOM_UPDATED");
            roomUpdateMessage.put("roomId", roomId);
            roomUpdateMessage.put("currentParticipants", room.getCurrentParticipants());
            roomUpdateMessage.put("maxParticipants", room.getMaxParticipants());
            roomUpdateMessage.put("status", room.getStatus());
            roomUpdateMessage.put("action", "USER_LEFT");

            messagingTemplate.convertAndSend("/topic/room-list/update", roomUpdateMessage);
        }
    }

    @Override
    public List<InterviewRoom> getAvailableRooms() {
        return lambdaQuery()
//                .eq(InterviewRoom::getStatus, "WAITING")
//                .lt(InterviewRoom::getCurrentParticipants, com.baomidou.mybatisplus.core.toolkit.Wrappers.<InterviewRoom>query().getEntity().getMaxParticipants())
//                Wrappers.<InterviewRoom>query().getEntity() 返回 null，导致 getMaxParticipants() 抛出 NullPointerException。
                .apply("current_participants < max_participants")
                .list();
    }

    @Override
    public List<RoomParticipant> getRoomParticipants(Long roomId) {
        return roomParticipantMapper.selectList(
                new LambdaQueryWrapper<RoomParticipant>()
                        .eq(RoomParticipant::getRoomId, roomId)
                        .isNull(RoomParticipant::getLeftAt)
                        .orderByAsc(RoomParticipant::getJoinedAt)
        );
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

            Map<String, Object> statusMessage = new HashMap<>();
            statusMessage.put("action", "ROOM_STARTED");
            statusMessage.put("roomId", roomId);
            statusMessage.put("status", "ONGOING");
            statusMessage.put("startedAt", room.getStartedAt());

            messagingTemplate.convertAndSend("/topic/room/" + roomId + "/status", statusMessage);
            messagingTemplate.convertAndSend("/topic/room-list/update", statusMessage);
        }
    }

    @Override
    public void completeRoom(Long roomId) {
        InterviewRoom room = getById(roomId);
        if (room != null && "ONGOING".equals(room.getStatus())) {
            room.setStatus("COMPLETED");
            room.setEndedAt(LocalDateTime.now());
            updateById(room);

            Map<String, Object> statusMessage = new HashMap<>();
            statusMessage.put("action", "ROOM_COMPLETED");
            statusMessage.put("roomId", roomId);
            statusMessage.put("status", "COMPLETED");
            statusMessage.put("endedAt", room.getEndedAt());

            // 广播到房间的所有订阅者
            messagingTemplate.convertAndSend("/topic/room/" + roomId + "/status", statusMessage);
            messagingTemplate.convertAndSend("/topic/room-list/update", statusMessage);
        }
    }
}
