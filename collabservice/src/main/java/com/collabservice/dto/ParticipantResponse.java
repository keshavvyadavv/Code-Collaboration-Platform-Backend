package com.collabservice.dto;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class ParticipantResponse {

    private Integer id;
    private String sessionId;
    private Integer userId;
    private String role;
    private String color;
    private Integer cursorLine;
    private Integer cursorCol;
    private LocalDateTime joinedAt;
    private LocalDateTime leftAt;
}