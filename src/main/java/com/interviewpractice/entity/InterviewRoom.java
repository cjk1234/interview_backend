package com.interviewpractice.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("interview_rooms")
public class InterviewRoom {
    @TableId(type = IdType.AUTO)
    private Long id;

    private String topic;
    private String description;
    private Integer maxParticipants;
    private Integer currentParticipants;
    private String status; // WAITING, ONGOING, COMPLETED

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createdAt;

    private LocalDateTime startedAt;
    private LocalDateTime endedAt;
}
