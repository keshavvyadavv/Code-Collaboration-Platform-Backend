package com.projectservice.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProjectResponse {

    private Integer projectId;
    private Integer ownerId;
    private String name;
    private String description;
    private String language;
    private String visibility;
    private Integer templateId;
    private Boolean isArchived;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private Integer starCount;
    private Integer forkCount;
}