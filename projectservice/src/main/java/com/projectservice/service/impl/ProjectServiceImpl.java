package com.projectservice.service.impl;

import com.projectservice.dto.ProjectRequest;
import com.projectservice.dto.ProjectResponse;
import com.projectservice.entity.Project;
import com.projectservice.exception.ResourceNotFoundException;
import com.projectservice.mapper.ProjectMapper;
import com.projectservice.repository.ProjectRepository;
import com.projectservice.service.ProjectService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@RequiredArgsConstructor
@Transactional
@Service
public class ProjectServiceImpl implements ProjectService {

    private final ProjectRepository projectRepository;
    private final ProjectMapper projectMapper;

    @Override
    public ProjectResponse createProject(ProjectRequest request, Integer ownerId) {
        Project project = projectMapper.toEntity(request);
        project.setOwnerId(ownerId);

        Project savedProject = projectRepository.save(project);
        log.info("Project created: {} by user {}", savedProject.getName(), ownerId);

        return projectMapper.toResponse(savedProject);
    }

    @Override
    public ProjectResponse getProjectById(Integer projectId) {
        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new ResourceNotFoundException("Project not found with id: " + projectId));
        return projectMapper.toResponse(project);
    }

    @Override
    public List<ProjectResponse> getProjectsByOwner(Integer ownerId) {
        List<Project> projects = projectRepository.findByOwnerId(ownerId);
        return projectMapper.toResponseList(projects);
    }

    @Override
    public List<ProjectResponse> getPublicProjects() {
        List<Project> projects = projectRepository.findByVisibility("PUBLIC");
        return projectMapper.toResponseList(projects);
    }

    @Override
    public List<ProjectResponse> searchProjects(String query) {
        List<Project> projects = projectRepository.findByNameContainingIgnoreCase(query);
        return projectMapper.toResponseList(projects);
    }

    @Override
    public List<ProjectResponse> getProjectsByMember(Integer userId) {
        List<Project> projects = projectRepository.findByMemberUserId(userId);
        return projectMapper.toResponseList(projects);
    }

    @Override
    public ProjectResponse updateProject(Integer projectId, ProjectRequest request) {
        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new ResourceNotFoundException("Project not found with id: " + projectId));

        projectMapper.updateEntityFromRequest(project, request);

        Project updated = projectRepository.save(project);
        return projectMapper.toResponse(updated);
    }

    @Override
    public void archiveProject(Integer projectId) {
        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new ResourceNotFoundException("Project not found with id: " + projectId));

        project.setIsArchived(true);
        projectRepository.save(project);
        log.info("Project archived: {}", projectId);
    }

    @Override
    public void deleteProject(Integer projectId) {
        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new ResourceNotFoundException("Project not found with id: " + projectId));

        if (Boolean.TRUE.equals(project.getIsArchived())) {
            projectRepository.deleteById(projectId);
            log.info("Project permanently deleted: {}", projectId);
        } else {
            throw new IllegalArgumentException("Only archived projects can be permanently deleted");
        }
    }

    @Override
    public ProjectResponse forkProject(Integer projectId, Integer newOwnerId) {
        Project original = projectRepository.findById(projectId)
                .orElseThrow(() -> new ResourceNotFoundException("Project not found with id: " + projectId));

        Project forked = Project.builder()
                .ownerId(newOwnerId)
                .name(original.getName() + " (Fork)")
                .description(original.getDescription())
                .language(original.getLanguage())
                .visibility("PRIVATE")
                .templateId(original.getProjectId())
                .isArchived(false)
                .starCount(0)
                .forkCount(0)
                .build();

        Project savedFork = projectRepository.save(forked);

        // fileServiceClient.copyProjectFiles(original.getProjectId(), savedFork.getProjectId());

        // Increment fork count on original
        original.setForkCount(original.getForkCount() + 1);
        projectRepository.save(original);

        log.info("Project {} forked by user {} → new project {}", projectId, newOwnerId, savedFork.getProjectId());

        return projectMapper.toResponse(savedFork);
    }

    @Override
    public ProjectResponse starProject(Integer projectId) {
        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new ResourceNotFoundException("Project not found with id: " + projectId));

        project.setStarCount(project.getStarCount() + 1);
        Project updated = projectRepository.save(project);

        return projectMapper.toResponse(updated);
    }

    @Override
    public List<ProjectResponse> getProjectsByLanguage(String language) {
        List<Project> projects = projectRepository.findByLanguage(language);
        return projectMapper.toResponseList(projects);
    }
}