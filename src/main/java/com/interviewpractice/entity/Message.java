// entity/Message.java
package com.interviewpractice.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@TableName("messages")
public class Message {
    @TableId(type = IdType.AUTO)
    private Long id;

    private Long roomId;
    private Long userId;
    private String content;
    private String messageType; // TEXT, IMAGE, SYSTEM

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createdAt;
}
