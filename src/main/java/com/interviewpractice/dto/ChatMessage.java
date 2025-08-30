package com.interviewpractice.dto;

import lombok.Data;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.NotBlank;

@Data
public class ChatMessage {
    @NotNull(message = "用户ID不能为空")
    private Long userId;

    @NotBlank(message = "消息内容不能为空")
    private String content;

    private String messageType = "TEXT"; // TEXT, IMAGE, SYSTEM
}