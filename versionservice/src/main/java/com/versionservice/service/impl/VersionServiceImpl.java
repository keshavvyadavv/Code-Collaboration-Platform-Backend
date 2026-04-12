package com.versionservice.service.impl;

import com.editorservice.entity.CodeFile;
import com.versionservice.dto.SnapshotResponseDto;
import com.versionservice.entity.Snapshot;
import com.versionservice.repository.SnapshotRepository;
import com.versionservice.service.VersionService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class VersionServiceImpl implements VersionService {

    private final SnapshotRepository snapshotRepository;

    @Override
    public Snapshot createSnapshot(Snapshot snapshot) {
        if (snapshot.getSnapshotId() != null && snapshotRepository.existsById(snapshot.getSnapshotId())) {
            throw new IllegalArgumentException("Snapshot with id " + snapshot.getSnapshotId() + " already exists.");
        }

        snapshot.setCreatedAt(LocalDateTime.now());
        // TODO: snapshot.setHash(HashUtil.sha256(snapshot.getContent()));

        Snapshot saved = snapshotRepository.save(snapshot);
        log.info("Created snapshot ID: {} for file ID: {} in project: {}",
                saved.getSnapshotId(), saved.getFileId(), saved.getProjectId());
        return saved;
    }

    @Override
    public Optional<Snapshot> getSnapshotById(int snapshotId) {
        return snapshotRepository.findById(snapshotId);
    }

    @Override
    public List<Snapshot> getSnapshotsByFile(int fileId) {
        return snapshotRepository.findByFileIdOrderByCreatedAtDesc(fileId);
    }

    @Override
    public List<Snapshot> getSnapshotsByProject(int projectId) {
        return snapshotRepository.findByProjectIdOrderByCreatedAtDesc(projectId);
    }

    @Override
    public List<Snapshot> getSnapshotsByBranch(int projectId, String branchName) {
        return snapshotRepository.findByProjectIdAndBranchOrderByCreatedAtDesc(projectId, branchName);
    }

    @Override
    public Optional<Snapshot> getLatestSnapshot(int fileId) {
        return snapshotRepository.findTopByFileIdOrderByCreatedAtDesc(fileId);
    }

    @Override
    public CodeFile restoreSnapshot(int snapshotId) {
        Snapshot snapshot = snapshotRepository.findById(snapshotId)
                .orElseThrow(() -> new IllegalArgumentException("Snapshot not found with id: " + snapshotId));

        Snapshot restored = Snapshot.builder()
                .projectId(snapshot.getProjectId())
                .fileId(snapshot.getFileId())
                .authorId(snapshot.getAuthorId())
                .message("Restored from snapshot " + snapshotId)
                .content(snapshot.getContent())
                .hash(snapshot.getHash())
                .parentSnapshotId(snapshotId)
                .branch(snapshot.getBranch())
                .createdAt(LocalDateTime.now())
                .build();

        Snapshot saved = snapshotRepository.save(restored);
        log.info("Restored snapshot {} as new snapshot {}", snapshotId, saved.getSnapshotId());

        CodeFile codeFile = new CodeFile();
        codeFile.setContent(snapshot.getContent());
        // TODO: map other fields from CodeFile entity if needed
        return codeFile;
    }

    @Override
    public String diffSnapshots(int snapshotId1, int snapshotId2) {
        Snapshot s1 = snapshotRepository.findById(snapshotId1)
                .orElseThrow(() -> new IllegalArgumentException("Snapshot not found: " + snapshotId1));

        Snapshot s2 = snapshotRepository.findById(snapshotId2)
                .orElseThrow(() -> new IllegalArgumentException("Snapshot not found: " + snapshotId2));

        // Variables s1 and s2 are now used → error fixed
        return String.format(
                "Diff between snapshot %d and %d\n" +
                        "Content length difference: %d characters\n" +
                        "(Myers diff algorithm to be implemented)",
                snapshotId1, snapshotId2,
                s1.getContent().length() - s2.getContent().length()
        );
    }

    @Override
    public void createBranch(int projectId, String branchName) {
        log.info("Branch '{}' created for project {}", branchName, projectId);
    }

    @Override
    public void tagSnapshot(int snapshotId, String tag) {
        Snapshot snapshot = snapshotRepository.findById(snapshotId)
                .orElseThrow(() -> new IllegalArgumentException("Snapshot not found: " + snapshotId));
        snapshot.setTag(tag);
        snapshotRepository.save(snapshot);
        log.info("Tagged snapshot {} with '{}'", snapshotId, tag);
    }

    @Override
    public List<Snapshot> getFileHistory(int fileId) {
        return snapshotRepository.findByFileIdOrderByCreatedAtDesc(fileId);
    }

    @Override
    public List<SnapshotResponseDto> getFileHistoryDto(int fileId) {
        return getFileHistory(fileId).stream()
                .map(this::convertToDto)
                .toList();
    }

    private SnapshotResponseDto convertToDto(Snapshot s) {
        return SnapshotResponseDto.builder()
                .snapshotId(s.getSnapshotId())
                .projectId(s.getProjectId())
                .fileId(s.getFileId())
                .authorId(s.getAuthorId())
                .message(s.getMessage())
                .hash(s.getHash())
                .branch(s.getBranch())
                .tag(s.getTag())
                .createdAt(s.getCreatedAt())
                .content(s.getContent())
                .build();
    }
}