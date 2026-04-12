package com.versionservice.repository;

import com.versionservice.entity.Snapshot;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface SnapshotRepository extends JpaRepository<Snapshot, Integer> {

    List<Snapshot> findByProjectId(Integer projectId);

    List<Snapshot> findByFileId(Integer fileId);

    List<Snapshot> findByAuthorId(Integer authorId);

    List<Snapshot> findByBranch(String branch);

    List<Snapshot> findByFileIdOrderByCreatedAtDesc(Integer fileId);

    List<Snapshot> findByProjectIdOrderByCreatedAtDesc(Integer projectId);

    List<Snapshot> findByProjectIdAndBranchOrderByCreatedAtDesc(Integer projectId, String branch);

    Optional<Snapshot> findTopByFileIdOrderByCreatedAtDesc(Integer fileId);

    // Optional additional methods from diagram
    Optional<Snapshot> findByHash(String hash);
    List<Snapshot> findByTag(String tag);
    Optional<Snapshot> findBySnapshotId(Integer snapshotId);
}