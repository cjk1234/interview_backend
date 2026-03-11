package com.interviewpractice.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.interviewpractice.entity.Evaluation;
import com.interviewpractice.mapper.EvaluationMapper;
import com.interviewpractice.utils.ApiResponse;
import com.interviewpractice.utils.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/evaluation")
public class EvaluationController {

    @Autowired
    private EvaluationMapper evaluationMapper;

    @Autowired
    private JwtUtil jwtUtil;

    @PostMapping("/submit")
    public ApiResponse<Evaluation> submitEvaluation(@RequestBody Evaluation evaluation,
                                                     HttpServletRequest request) {
        Long userId = getUserIdFromRequest(request);
        evaluation.setEvaluatorId(userId);
        evaluation.setCreatedAt(LocalDateTime.now());
        evaluationMapper.insert(evaluation);
        return ApiResponse.success(evaluation);
    }

    @GetMapping("/room/{roomId}")
    public ApiResponse<List<Evaluation>> getRoomEvaluations(@PathVariable Long roomId) {
        LambdaQueryWrapper<Evaluation> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Evaluation::getRoomId, roomId);
        wrapper.orderByDesc(Evaluation::getCreatedAt);
        List<Evaluation> evaluations = evaluationMapper.selectList(wrapper);
        return ApiResponse.success(evaluations);
    }

    @GetMapping("/user/{userId}")
    public ApiResponse<Map<String, Object>> getUserEvaluations(@PathVariable Long userId) {
        // 获取该用户被评价的所有记录
        LambdaQueryWrapper<Evaluation> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Evaluation::getEvaluatedUserId, userId);
        List<Evaluation> evaluations = evaluationMapper.selectList(wrapper);

        Map<String, Object> result = new HashMap<>();
        result.put("evaluations", evaluations);
        result.put("count", evaluations.size());

        if (!evaluations.isEmpty()) {
            double avgLeadership = evaluations.stream().mapToInt(Evaluation::getLeadershipScore).average().orElse(0);
            double avgCommunication = evaluations.stream().mapToInt(Evaluation::getCommunicationScore).average().orElse(0);
            double avgLogic = evaluations.stream().mapToInt(Evaluation::getLogicScore).average().orElse(0);
            double avgCooperation = evaluations.stream().mapToInt(Evaluation::getCooperationScore).average().orElse(0);

            Map<String, Double> averages = new HashMap<>();
            averages.put("leadership", Math.round(avgLeadership * 10.0) / 10.0);
            averages.put("communication", Math.round(avgCommunication * 10.0) / 10.0);
            averages.put("logic", Math.round(avgLogic * 10.0) / 10.0);
            averages.put("cooperation", Math.round(avgCooperation * 10.0) / 10.0);
            averages.put("overall", Math.round((avgLeadership + avgCommunication + avgLogic + avgCooperation) / 4.0 * 10.0) / 10.0);
            result.put("averages", averages);
        }

        return ApiResponse.success(result);
    }

    @GetMapping("/my")
    public ApiResponse<List<Evaluation>> getMyEvaluations(HttpServletRequest request) {
        Long userId = getUserIdFromRequest(request);
        LambdaQueryWrapper<Evaluation> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Evaluation::getEvaluatedUserId, userId);
        wrapper.orderByDesc(Evaluation::getCreatedAt);
        return ApiResponse.success(evaluationMapper.selectList(wrapper));
    }

    private Long getUserIdFromRequest(HttpServletRequest request) {
        String token = request.getHeader("Authorization").substring(7);
        return Long.parseLong(jwtUtil.getUserIdFromToken(token));
    }
}
