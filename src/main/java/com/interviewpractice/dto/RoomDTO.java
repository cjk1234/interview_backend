package com.interviewpractice.dto;

import lombok.Data;
import java.time.LocalDateTime;
import java.util.List;

@Data
public class RoomDTO {
    private Long id;
    private String topic;
    private String description;
    private Integer maxParticipants;
    private Integer currentParticipants;
    private String status;
    private LocalDateTime createdAt;
    private LocalDateTime startedAt;
    private LocalDateTime endedAt;
    private Long creatorId;
    private List<ParticipantDTO> participants;
}

@Data
class ParticipantDTO {
    private Long userId;
    private String username;
    private String avatarUrl;
    private String role;
    private LocalDateTime joinedAt;
}
