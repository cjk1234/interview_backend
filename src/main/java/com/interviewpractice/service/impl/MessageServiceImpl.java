package com.interviewpractice.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.interviewpractice.entity.Message;
import com.interviewpractice.mapper.MessageMapper;
import com.interviewpractice.service.MessageService;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

@Service
public class MessageServiceImpl extends ServiceImpl<MessageMapper, Message> implements MessageService {

    @Override
    public Message sendMessage(Long roomId, Long userId, String content, String messageType) {
        Message message = new Message();
        message.setRoomId(roomId);
        message.setUserId(userId);
        message.setContent(content);
        message.setMessageType(messageType != null ? messageType : "TEXT");
        message.setCreatedAt(LocalDateTime.now());

        save(message);
        return message;
    }

    @Override
    public List<Message> getRoomMessage(Long roomId, Integer page, Integer size) {
        // 设置默认值
        if (page == null || page < 1) page = 1;
        if (size == null || size < 1) size = 20;

        // 计算偏移量
        int offset = (page - 1) * size;
        return lambdaQuery()
                .eq(Message::getRoomId, roomId)
                .orderByDesc(Message::getCreatedAt)  // 按创建时间倒序
                .last("LIMIT" + offset + ", " + size)  //分页
                .list();
    }
}
