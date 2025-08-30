// entity/RoomParticipant.java
package com.interviewpractice.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@TableName("room_participants")
public class RoomParticipant {
    @TableId(type = IdType.AUTO)
    private Long id;

    private Long roomId;
    private Long userId;
    private String role; // LEADER, MEMBER, OBSERVER

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime joinedAt;

    private LocalDateTime leftAt;
}
