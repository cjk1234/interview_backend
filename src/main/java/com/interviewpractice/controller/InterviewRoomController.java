package com.interviewpractice.controller;

import com.interviewpractice.entity.InterviewRoom;
import com.interviewpractice.entity.RoomParticipant;
import com.interviewpractice.entity.User;
import com.interviewpractice.service.InterviewRoomService;
import com.interviewpractice.service.UserService;
import com.interviewpractice.utils.ApiResponse;
import com.interviewpractice.utils.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

@RestController
@RequestMapping("/room")
public class InterviewRoomController {

    @Autowired
    private InterviewRoomService roomService;

    @Autowired
    private UserService userService;

    @Autowired
    private JwtUtil jwtUtil;

    @PostMapping("/create")
    public InterviewRoom createRoom(@RequestParam String topic,
                                    @RequestParam(required = false) String description,
                                    @RequestParam(required = false) Integer maxParticipants,
                                    HttpServletRequest request) {
        Long userId = getUserIdFromRequest(request);
        User user = userService.getUserInfo(userId);

        return roomService.createRoom(topic, description, maxParticipants);
    }

    @GetMapping("/{roomId}")
    public InterviewRoom getRoomDetail(@PathVariable Long roomId) {
        return roomService.getRoomDetail(roomId);
    }

    @PostMapping("/{roomId}/join")
    public ApiResponse<InterviewRoom> joinRoom(@PathVariable Long roomId, HttpServletRequest request) {
        Long userId = getUserIdFromRequest(request);
        User user = userService.getUserInfo(userId);
        return roomService.joinRoom(roomId, user);
    }

    @PostMapping("/{roomId}/leave")
    public void leaveRoom(@PathVariable Long roomId, HttpServletRequest request) {
        Long userId = getUserIdFromRequest(request);
        User user = userService.getUserInfo(userId);
        roomService.leaveRoom(roomId, user);
    }

    @GetMapping("/{roomId}/getRoomParticipants")
    public List<RoomParticipant> getRoomParticipants(@PathVariable Long roomId) {
        return roomService.getRoomParticipants(roomId);
    }

    @GetMapping("/available")
    public List<InterviewRoom> getAvailableRooms() {
        return roomService.getAvailableRooms();
    }

    @PostMapping("/{roomId}/start")
    public void startRoom(@PathVariable Long roomId) {
        roomService.startRoom(roomId);
    }

    @PostMapping("{roomId}/complete")
    public void completeRoom(@PathVariable Long roomId) {
        roomService.completeRoom(roomId);
    }

    private Long getUserIdFromRequest(HttpServletRequest request) {
        String token = request.getHeader("Authorization").substring(7);
        return Long.parseLong(jwtUtil.getUserIdFromToken(token));
    }
}
