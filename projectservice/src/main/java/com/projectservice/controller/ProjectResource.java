package com.projectservice.controller;

import com.projectservice.dto.ApiResponse;
import com.projectservice.dto.ProjectRequest;
import com.projectservice.dto.ProjectResponse;
import com.projectservice.service.ProjectService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/projects")
@RequiredArgsConstructor
public class ProjectResource {

    private final ProjectService projectService;

    @PostMapping
    public ResponseEntity<ApiResponse<ProjectResponse>> createProject(
            @Valid @RequestBody ProjectRequest request) {

        // TODO: Replace 1 with actual userId from JWT/SecurityContext
        Integer ownerId = 1;

        ProjectResponse response = projectService.createProject(request, ownerId);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Project created successfully", response));
    }

    @GetMapping("/{projectId}")
    public ResponseEntity<ApiResponse<ProjectResponse>> getProjectById(@PathVariable Integer projectId) {
        ProjectResponse response = projectService.getProjectById(projectId);
        return ResponseEntity.ok(ApiResponse.success("Project fetched successfully", response));
    }

    @GetMapping("/owner/{ownerId}")
    public ResponseEntity<ApiResponse<List<ProjectResponse>>> getProjectsByOwner(@PathVariable Integer ownerId) {
        List<ProjectResponse> responses = projectService.getProjectsByOwner(ownerId);
        return ResponseEntity.ok(ApiResponse.success("Projects fetched for owner", responses));
    }

    @GetMapping("/public")
    public ResponseEntity<ApiResponse<List<ProjectResponse>>> getPublicProjects() {
        List<ProjectResponse> responses = projectService.getPublicProjects();
        return ResponseEntity.ok(ApiResponse.success("Public projects fetched", responses));
    }

    @GetMapping("/search")
    public ResponseEntity<ApiResponse<List<ProjectResponse>>> searchProjects(@RequestParam String query) {
        List<ProjectResponse> responses = projectService.searchProjects(query);
        return ResponseEntity.ok(ApiResponse.success("Search results", responses));
    }

    @GetMapping("/member/{userId}")
    public ResponseEntity<ApiResponse<List<ProjectResponse>>> getProjectsByMember(@PathVariable Integer userId) {
        List<ProjectResponse> responses = projectService.getProjectsByMember(userId);
        return ResponseEntity.ok(ApiResponse.success("Projects for member", responses));
    }

    @PutMapping("/{projectId}")
    public ResponseEntity<ApiResponse<ProjectResponse>> updateProject(
            @PathVariable Integer projectId,
            @Valid @RequestBody ProjectRequest request) {

        ProjectResponse response = projectService.updateProject(projectId, request);
        return ResponseEntity.ok(ApiResponse.success("Project updated successfully", response));
    }

    @PutMapping("/{projectId}/archive")
    public ResponseEntity<ApiResponse<Void>> archiveProject(@PathVariable Integer projectId) {
        projectService.archiveProject(projectId);
        return ResponseEntity.ok(ApiResponse.success("Project archived successfully"));
    }

    @PostMapping("/{projectId}/fork")
    public ResponseEntity<ApiResponse<ProjectResponse>> forkProject(
            @PathVariable Integer projectId,
            @RequestParam Integer newOwnerId) {

        ProjectResponse response = projectService.forkProject(projectId, newOwnerId);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Project forked successfully", response));
    }

    @PostMapping("/{projectId}/star")
    public ResponseEntity<ApiResponse<ProjectResponse>> starProject(@PathVariable Integer projectId) {
        ProjectResponse response = projectService.starProject(projectId);
        return ResponseEntity.ok(ApiResponse.success("Project starred successfully", response));
    }

    @GetMapping("/language/{language}")
    public ResponseEntity<ApiResponse<List<ProjectResponse>>> getProjectsByLanguage(@PathVariable String language) {
        List<ProjectResponse> responses = projectService.getProjectsByLanguage(language);
        return ResponseEntity.ok(ApiResponse.success("Projects by language fetched", responses));
    }

    @DeleteMapping("/{projectId}")
    public ResponseEntity<ApiResponse<Void>> deleteProject(@PathVariable Integer projectId) {
        projectService.deleteProject(projectId);
        return ResponseEntity.ok(ApiResponse.success("Project deleted successfully"));
    }
}