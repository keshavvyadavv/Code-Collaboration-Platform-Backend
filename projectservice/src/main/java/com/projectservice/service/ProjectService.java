package com.projectservice.service;

import com.projectservice.dto.ProjectRequest;
import com.projectservice.dto.ProjectResponse;

import java.util.List;

public interface ProjectService {

    ProjectResponse createProject(ProjectRequest request, Integer ownerId);

    ProjectResponse getProjectById(Integer projectId);

    List<ProjectResponse> getProjectsByOwner(Integer ownerId);

    List<ProjectResponse> getPublicProjects();

    List<ProjectResponse> searchProjects(String query);

    List<ProjectResponse> getProjectsByMember(Integer userId);

    ProjectResponse updateProject(Integer projectId, ProjectRequest request);

    void archiveProject(Integer projectId);

    void deleteProject(Integer projectId);

    ProjectResponse forkProject(Integer projectId, Integer newOwnerId);

    ProjectResponse starProject(Integer projectId);

    List<ProjectResponse> getProjectsByLanguage(String language);
}