package com.interviewpractice.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.interviewpractice.entity.InterviewRoom;
import com.interviewpractice.entity.RoomParticipant;
import com.interviewpractice.entity.User;
import com.interviewpractice.utils.ApiResponse;

import java.util.List;

public interface InterviewRoomService extends IService<InterviewRoom> {
    InterviewRoom createRoom(Long userId, String topic, String description, Integer maxParticipants);
    void deleteRoom(Long userId, Long roomId);
    ApiResponse<InterviewRoom> joinRoom(Long roomId, User user);
    void leaveRoom(Long roomId, User user);
    List<InterviewRoom> getAvailableRooms();
    List<RoomParticipant> getRoomParticipants(Long roomId);
    InterviewRoom getRoomDetail(Long roomId);
    void startRoom(Long roomId);
    void completeRoom(Long roomId);
}
