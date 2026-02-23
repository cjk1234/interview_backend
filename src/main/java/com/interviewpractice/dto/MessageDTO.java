package com.interviewpractice.dto;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class MessageDTO {
    private Long id;
    private Long roomId;
    private Long userId;
    // 新增：指定接收消息的用户ID，用于WebRTC点对点信令
    private Long toUserId;
    private String username;
    private String avatarUrl;
    private String content;
    private String messageType;
    private LocalDateTime createdAt;
}
