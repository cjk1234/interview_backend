package com.interviewpractice.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.interviewpractice.dto.MessageDTO;
import com.interviewpractice.entity.Message;

import java.util.List;

public interface MessageService extends IService<Message> {
    void sendMessage(MessageDTO messageDTO);
    List<Message> getRoomMessage(Long roomId, Integer page, Integer size);
}
