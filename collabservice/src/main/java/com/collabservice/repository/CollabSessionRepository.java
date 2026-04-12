package com.collabservice.repository;

import com.collabservice.entity.CollabSession;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface CollabSessionRepository extends JpaRepository<CollabSession, String> {

    Optional<CollabSession> findBySessionId(String sessionId);

    List<CollabSession> findByProjectId(Integer projectId);

    List<CollabSession> findByFileId(Integer fileId);

    List<CollabSession> findByProjectIdAndStatus(Integer projectId, String status);

    List<CollabSession> findByOwnerId(Integer ownerId);

    Optional<CollabSession> findByFileIdAndStatus(Integer fileId, String status);
}