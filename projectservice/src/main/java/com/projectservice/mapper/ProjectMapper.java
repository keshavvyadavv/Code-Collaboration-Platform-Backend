package com.projectservice.mapper;

import com.projectservice.dto.ProjectRequest;
import com.projectservice.dto.ProjectResponse;
import com.projectservice.entity.Project;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class ProjectMapper {

    public Project toEntity(ProjectRequest request) {
        return Project.builder()
                .name(request.getName())
                .description(request.getDescription())
                .language(request.getLanguage())
                .visibility(request.getVisibility() != null ? request.getVisibility().toUpperCase() : "PRIVATE")
                .templateId(request.getTemplateId())
                .isArchived(false)
                .starCount(0)
                .forkCount(0)
                .build();
    }

    public ProjectResponse toResponse(Project project) {
        return ProjectResponse.builder()
                .projectId(project.getProjectId())
                .ownerId(project.getOwnerId())
                .name(project.getName())
                .description(project.getDescription())
                .language(project.getLanguage())
                .visibility(project.getVisibility())
                .templateId(project.getTemplateId())
                .isArchived(project.getIsArchived())
                .createdAt(project.getCreatedAt())
                .updatedAt(project.getUpdatedAt())
                .starCount(project.getStarCount())
                .forkCount(project.getForkCount())
                .build();
    }

    public List<ProjectResponse> toResponseList(List<Project> projects) {
        return projects.stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    public void updateEntityFromRequest(Project project, ProjectRequest request) {
        project.setName(request.getName());
        project.setDescription(request.getDescription());
        project.setLanguage(request.getLanguage());
        if (request.getVisibility() != null) {
            project.setVisibility(request.getVisibility().toUpperCase());
        }
    }
}