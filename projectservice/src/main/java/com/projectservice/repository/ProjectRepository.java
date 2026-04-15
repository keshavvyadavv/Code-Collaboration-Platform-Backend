package com.projectservice.repository;

import com.projectservice.entity.Project;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ProjectRepository extends JpaRepository<Project, Integer> {

    List<Project> findByOwnerId(Integer ownerId);

    Optional<Project> findByProjectId(Integer projectId);

    List<Project> findByVisibility(String visibility);

    List<Project> findByLanguage(String language);

    List<Project> findByIsArchived(Boolean isArchived);

    // Search by name (case insensitive)
    List<Project> findByNameContainingIgnoreCase(String name);

    // Find projects where user is member (you can extend later with Member entity)
    // For now, returning public + own projects as placeholder
    @Query("SELECT p FROM Project p WHERE p.visibility = 'PUBLIC' OR p.ownerId = :userId")
    List<Project> findByMemberUserId(Integer userId);

    long countByOwnerId(Integer ownerId);

    // For discovery feed - popular projects
    List<Project> findTop10ByVisibilityOrderByStarCountDesc(String visibility);
}