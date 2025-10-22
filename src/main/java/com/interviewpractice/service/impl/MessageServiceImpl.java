package com.interviewpractice.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.interviewpractice.dto.MessageDTO;
import com.interviewpractice.entity.Message;
import com.interviewpractice.mapper.MessageMapper;
import com.interviewpractice.service.MessageService;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class MessageServiceImpl extends ServiceImpl<MessageMapper, Message> implements MessageService {

    @Override
    public void sendMessage(MessageDTO messageDTO) {
        Message message = new Message();
        message.setRoomId(messageDTO.getRoomId());
        message.setUserId(messageDTO.getUserId());
        message.setContent(messageDTO.getContent());
        message.setMessageType(messageDTO.getMessageType() != null ? messageDTO.getMessageType() : "TEXT");
        message.setCreatedAt(LocalDateTime.now());
        save(message);
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
