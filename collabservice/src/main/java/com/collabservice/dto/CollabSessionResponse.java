package com.collabservice.dto;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;
//Hide sessionPassword

@Data
@Builder
public class CollabSessionResponse {

    private String sessionId;
    private Integer projectId;
    private Integer fileId;
    private Integer ownerId;
    private String status;
    private String language;
    private LocalDateTime createdAt;
    private LocalDateTime endedAt;
    private Integer maxParticipants;
    private Boolean isPasswordProtected;
}