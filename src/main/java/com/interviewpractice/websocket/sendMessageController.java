package com.interviewpractice.websocket;

import com.interviewpractice.dto.*;
import com.interviewpractice.service.MessageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;

@Controller
public class sendMessageController {

    @Autowired
    private MessageService messageService;

    @Autowired
    private SimpMessagingTemplate messagingTemplate;
    /**
     * 处理聊天消息
     */
    @MessageMapping("/chat/{roomId}")
    public void sendMessage(@RequestBody MessageDTO messageDTO) {
        System.out.println("收到聊天消息: " + messageDTO.getContent());

        // 广播消息给房间内所有用户
        messagingTemplate.convertAndSend("/topic/message/" + messageDTO.getRoomId(), messageDTO);
        messageService.sendMessage(messageDTO);
    }
}