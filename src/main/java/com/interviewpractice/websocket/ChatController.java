package com.interviewpractice.websocket;

import com.interviewpractice.dto.ChatMessage;
import com.interviewpractice.dto.MessageDTO;
import com.interviewpractice.entity.Message;
import com.interviewpractice.service.MessageService;
import com.interviewpractice.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

import java.time.LocalDateTime;

@Controller
public class ChatController {

    @Autowired
    private MessageService messageService;

    @Autowired
    private UserService userService;

    @Autowired
    private SimpMessagingTemplate messagingTemplate;

    @MessageMapping("/chat/{roomId}")
    @SendTo("/topic/room/{roomId}")
    public MessageDTO sendMessage(@DestinationVariable Long roomId,
                                  ChatMessage chatMessage) {
        // 保存消息到数据库
        Message message = messageService.sendMessage(
                roomId,
                chatMessage.getUserId(),
                chatMessage.getContent(),
                chatMessage.getMessageType()
        );

        // 获取用户信息
        com.interviewpractice.entity.User user = userService.getUserInfo(chatMessage.getUserId());

        // 创建DTO返回给前端
        MessageDTO messageDTO = new MessageDTO();
        messageDTO.setId(message.getId());
        messageDTO.setRoomId(roomId);
        messageDTO.setUserId(chatMessage.getUserId());
        messageDTO.setUsername(user.getUsername());
        messageDTO.setAvatarUrl(user.getAvatarUrl());
        messageDTO.setContent(chatMessage.getContent());
        messageDTO.setMessageType(chatMessage.getMessageType());
        messageDTO.setCreatedAt(message.getCreatedAt());

        return messageDTO;
    }
}