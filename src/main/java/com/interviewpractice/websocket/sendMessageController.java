package com.interviewpractice.websocket;

import com.interviewpractice.dto.*;
import com.interviewpractice.service.MessageService;
import org.springframework.beans.factory.annotation.Autowired;
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

    private static final Set<String> SIGNAL_TYPES = new HashSet<>(Arrays.asList(
            "OFFER", "ANSWER", "CANDIDATE", "VIDEO_STATUS"
    ));
    /**
     * 处理聊天消息
     */
    @MessageMapping("/chat/{roomId}")
    public void sendMessage(@RequestBody MessageDTO messageDTO) {
        // 广播消息给房间内所有用户
        messagingTemplate.convertAndSend("/topic/message/" + messageDTO.getRoomId(), messageDTO);

        String type = messageDTO.getMessageType();
        if (type == null || !SIGNAL_TYPES.contains(type)) {
            messageService.sendMessage(messageDTO);
        }
    }
}