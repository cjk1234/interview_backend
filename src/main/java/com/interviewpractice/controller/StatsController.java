package com.interviewpractice.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.interviewpractice.entity.Evaluation;
import com.interviewpractice.entity.InterviewRoom;
import com.interviewpractice.entity.RoomParticipant;
import com.interviewpractice.mapper.EvaluationMapper;
import com.interviewpractice.mapper.InterviewRoomMapper;
import com.interviewpractice.mapper.RoomParticipantMapper;
import com.interviewpractice.utils.ApiResponse;
import com.interviewpractice.utils.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.time.Duration;
import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/stats")
public class StatsController {

    @Autowired
    private RoomParticipantMapper roomParticipantMapper;

    @Autowired
    private InterviewRoomMapper interviewRoomMapper;

    @Autowired
    private EvaluationMapper evaluationMapper;

    @Autowired
    private JwtUtil jwtUtil;

    @GetMapping("/dashboard")
    public ApiResponse<Map<String, Object>> getDashboardStats(HttpServletRequest request) {
        Long userId = getUserIdFromRequest(request);
        Map<String, Object> stats = new HashMap<>();

        // 参与的面试场次
        LambdaQueryWrapper<RoomParticipant> participantWrapper = new LambdaQueryWrapper<>();
        participantWrapper.eq(RoomParticipant::getUserId, userId);
        Long interviewCount = roomParticipantMapper.selectCount(participantWrapper);
        stats.put("interviewCount", interviewCount);

        // 合作伙伴数（去重）
        List<RoomParticipant> myParticipations = roomParticipantMapper.selectList(participantWrapper);
        Set<Long> roomIds = myParticipations.stream()
                .map(RoomParticipant::getRoomId)
                .collect(Collectors.toSet());
        
        long partnerCount = 0;
        if (!roomIds.isEmpty()) {
            LambdaQueryWrapper<RoomParticipant> partnerWrapper = new LambdaQueryWrapper<>();
            partnerWrapper.in(RoomParticipant::getRoomId, roomIds);
            partnerWrapper.ne(RoomParticipant::getUserId, userId);
            List<RoomParticipant> partners = roomParticipantMapper.selectList(partnerWrapper);
            partnerCount = partners.stream()
                    .map(RoomParticipant::getUserId)
                    .distinct()
                    .count();
        }
        stats.put("partnerCount", partnerCount);

        // 练习时长（分钟）
        long totalMinutes = 0;
        for (Long roomId : roomIds) {
            InterviewRoom room = interviewRoomMapper.selectById(roomId);
            if (room != null && room.getStartedAt() != null && room.getEndedAt() != null) {
                Duration duration = Duration.between(room.getStartedAt(), room.getEndedAt());
                totalMinutes += duration.toMinutes();
            }
        }
        stats.put("totalMinutes", totalMinutes);

        // 综合评分
        LambdaQueryWrapper<Evaluation> evalWrapper = new LambdaQueryWrapper<>();
        evalWrapper.eq(Evaluation::getEvaluatedUserId, userId);
        List<Evaluation> evaluations = evaluationMapper.selectList(evalWrapper);
        if (!evaluations.isEmpty()) {
            double avg = evaluations.stream()
                    .mapToDouble(e -> (e.getLeadershipScore() + e.getCommunicationScore() + e.getLogicScore() + e.getCooperationScore()) / 4.0)
                    .average().orElse(0);
            stats.put("overallScore", Math.round(avg * 10.0) / 10.0);
        } else {
            stats.put("overallScore", null);
        }

        return ApiResponse.success(stats);
    }

    @GetMapping("/history")
    public ApiResponse<List<Map<String, Object>>> getInterviewHistory(HttpServletRequest request) {
        Long userId = getUserIdFromRequest(request);

        LambdaQueryWrapper<RoomParticipant> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(RoomParticipant::getUserId, userId);
        wrapper.orderByDesc(RoomParticipant::getJoinedAt);
        List<RoomParticipant> participations = roomParticipantMapper.selectList(wrapper);

        List<Map<String, Object>> history = new ArrayList<>();
        for (RoomParticipant rp : participations) {
            InterviewRoom room = interviewRoomMapper.selectById(rp.getRoomId());
            if (room != null) {
                Map<String, Object> item = new HashMap<>();
                item.put("roomId", room.getId());
                item.put("topic", room.getTopic());
                item.put("description", room.getDescription());
                item.put("status", room.getStatus());
                item.put("role", rp.getRole());
                item.put("joinedAt", rp.getJoinedAt());
                item.put("leftAt", rp.getLeftAt());
                item.put("roomCreatedAt", room.getCreatedAt());
                item.put("startedAt", room.getStartedAt());
                item.put("endedAt", room.getEndedAt());
                item.put("maxParticipants", room.getMaxParticipants());
                item.put("currentParticipants", room.getCurrentParticipants());
                history.add(item);
            }
        }

        return ApiResponse.success(history);
    }

    private Long getUserIdFromRequest(HttpServletRequest request) {
        String token = request.getHeader("Authorization").substring(7);
        return Long.parseLong(jwtUtil.getUserIdFromToken(token));
    }
}
