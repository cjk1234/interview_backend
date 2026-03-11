package com.interviewpractice.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.interviewpractice.entity.Topic;
import com.interviewpractice.mapper.TopicMapper;
import com.interviewpractice.utils.ApiResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/topic")
public class TopicController {

    @Autowired
    private TopicMapper topicMapper;

    @GetMapping("/list")
    public ApiResponse<List<Topic>> listTopics(
            @RequestParam(required = false) String category,
            @RequestParam(required = false) String difficulty) {
        LambdaQueryWrapper<Topic> wrapper = new LambdaQueryWrapper<>();
        if (category != null && !category.isEmpty()) {
            wrapper.eq(Topic::getCategory, category);
        }
        if (difficulty != null && !difficulty.isEmpty()) {
            wrapper.eq(Topic::getDifficulty, difficulty);
        }
        wrapper.orderByDesc(Topic::getCreatedAt);
        List<Topic> topics = topicMapper.selectList(wrapper);
        return ApiResponse.success(topics);
    }

    @GetMapping("/categories")
    public ApiResponse<List<String>> getCategories() {
        List<Topic> topics = topicMapper.selectList(null);
        List<String> categories = topics.stream()
                .map(Topic::getCategory)
                .distinct()
                .collect(Collectors.toList());
        return ApiResponse.success(categories);
    }

    @GetMapping("/{id}")
    public ApiResponse<Topic> getTopicById(@PathVariable Long id) {
        Topic topic = topicMapper.selectById(id);
        if (topic == null) {
            return ApiResponse.error("题目不存在", "TOPIC_NOT_FOUND");
        }
        return ApiResponse.success(topic);
    }
}
