package com.interviewpractice.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.interviewpractice.entity.Message;
import com.interviewpractice.mapper.MessageMapper;
import com.interviewpractice.utils.ApiResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/message")
public class MessageController {

    @Autowired
    private MessageMapper messageMapper;

    @GetMapping("/{roomId}")
    public ApiResponse<List<Message>> getRoomMessages(
            @PathVariable Long roomId,
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "50") Integer size) {
        LambdaQueryWrapper<Message> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Message::getRoomId, roomId);
        wrapper.orderByAsc(Message::getCreatedAt);
        // Simple pagination
        wrapper.last("LIMIT " + size + " OFFSET " + (page - 1) * size);
        List<Message> messages = messageMapper.selectList(wrapper);
        return ApiResponse.success(messages);
    }
}
