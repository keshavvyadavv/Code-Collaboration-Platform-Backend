package com.collabservice.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class CreateSessionRequest {

    @NotNull
    private Integer projectId;

    @NotNull
    private Integer fileId;

    @NotNull
    private Integer ownerId;

    private String language;
    private Integer maxParticipants;
    private Boolean isPasswordProtected;
    private String sessionPassword;
}