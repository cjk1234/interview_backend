package com.interviewpractice.websocket;

import com.interviewpractice.dto.*;
import com.interviewpractice.service.MessageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

@Controller
public class sendMessageController {

    @Autowired
    private MessageService messageService;

    @Autowired
    private SimpMessagingTemplate messagingTemplate;
    /**
     * 处理聊天消息及信令消息
     */
    @MessageMapping("/chat/{roomId}")
    public void sendMessage(@RequestBody MessageDTO messageDTO) {
        // 判断是否为WebRTC信令消息
        if ("SIGNALING".equals(messageDTO.getMessageType())) {
            // 信令消息只需转发给目标用户 (toUserId)
            // 假设前端订阅的个人信令地址为 /queue/signaling/{userId}
            if (messageDTO.getToUserId() != null) {
                messagingTemplate.convertAndSend(
                        "/queue/signaling/" + messageDTO.getToUserId(),
                        messageDTO
                );
            }
        } else {
            messagingTemplate.convertAndSend("/topic/message/" + messageDTO.getRoomId(), messageDTO);
            if ("TEXT".equals(messageDTO.getMessageType())) {
                messageService.sendMessage(messageDTO);
            }
        }
    }
}