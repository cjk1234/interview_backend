package com.interviewpractice.dto;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class ChatMessageResponse {
    private Long id;
    private Long roomId;
    private Long userId;
    private String username;
    private String avatarUrl;
    private String content;
    private String messageType;
    private LocalDateTime createdAt;
}
