package com.interviewpractice.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.interviewpractice.entity.Message;

import java.util.List;

public interface MessageService extends IService<Message> {
    Message sendMessage(Long roomId, Long userId, String content, String messageType);
    List<Message> getRoomMessage(Long roomId, Integer page, Integer size);
}
