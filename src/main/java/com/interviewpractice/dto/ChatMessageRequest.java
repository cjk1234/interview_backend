package com.interviewpractice.dto;

import lombok.Data;

@Data
public class ChatMessageRequest {
    private Long roomId;
    private Long userId;
    private String content;
    private String messageType;
}
