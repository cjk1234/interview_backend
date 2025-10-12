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
    private String userName;
    private String role; // LEADER, MEMBER, OBSERVER

    @TableField("joined_at")
    private LocalDateTime joinedAt;

    @TableField(value = "left_at", updateStrategy = FieldStrategy.ALWAYS)
    private LocalDateTime leftAt;
}
