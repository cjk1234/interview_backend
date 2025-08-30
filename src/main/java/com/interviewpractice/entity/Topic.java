// entity/Topic.java
package com.interviewpractice.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@TableName("topics")
public class Topic {
    @TableId(type = IdType.AUTO)
    private Long id;

    private String title;
    private String content;
    private String category;
    private String difficulty; // EASY, MEDIUM, HARD

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createdAt;
}
