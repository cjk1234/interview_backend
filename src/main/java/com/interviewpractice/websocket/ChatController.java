package com.interviewpractice.websocket;

import com.interviewpractice.dto.*;
import com.interviewpractice.entity.Message;
import com.interviewpractice.service.MessageService;
import com.interviewpractice.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;

import java.time.LocalDateTime;

@Controller
public class ChatController {

    @Autowired
    private MessageService messageService;

    @Autowired
    private UserService userService;

    @Autowired
    private SimpMessagingTemplate messagingTemplate;
    /**
     * 处理聊天消息
     */
    @MessageMapping("/chat/{roomId}")
    public void handleChatMessage(@RequestBody MessageDTO chatMessage) {
        System.out.println("收到聊天消息: " + chatMessage.getContent());

        // 广播消息给房间内所有用户
        messagingTemplate.convertAndSend("/topic/message/" + chatMessage.getRoomId(), chatMessage);
    }
}