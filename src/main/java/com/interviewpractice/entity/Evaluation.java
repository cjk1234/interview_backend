// entity/Evaluation.java
package com.interviewpractice.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@TableName("evaluations")
public class Evaluation {
    @TableId(type = IdType.AUTO)
    private Long id;

    private Long roomId;
    private Long evaluatorId;
    private Long evaluatedUserId;
    private Integer leadershipScore;
    private Integer communicationScore;
    private Integer logicScore;
    private Integer cooperationScore;
    private String comment;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createdAt;
}