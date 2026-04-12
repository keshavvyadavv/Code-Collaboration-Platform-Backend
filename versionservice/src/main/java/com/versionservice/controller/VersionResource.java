package com.versionservice.controller;

import com.editorservice.entity.CodeFile;
import com.versionservice.dto.*;
import com.versionservice.entity.Snapshot;
import com.versionservice.mapper.SnapshotMapper;
import com.versionservice.service.VersionService;
import com.versionservice.util.HashUtil;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.swing.*;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("api/versions")
@RequiredArgsConstructor
public class VersionResource {

    private final VersionService versionService;

    @PostMapping("/snapshot")
    public ResponseEntity<ApiResponse<SnapshotResponseDto>>  create(@Valid @RequestBody SnapshotRequestDto dto){
        Snapshot snapshot = SnapshotMapper.toEntity(dto);

        snapshot.setHash(HashUtil.sha256(dto.getContent()));

        Snapshot saved = versionService.createSnapshot(snapshot);

        SnapshotResponseDto response = SnapshotMapper.toResponse(saved);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(ApiResponse.success("Snapshot created successfully", response));
    }

    @GetMapping("/snapshot/{snapshotId}")
    public ResponseEntity<ApiResponse<SnapshotResponseDto>> getSnapshotById(
            @PathVariable int  snapshotId){

        SnapshotResponseDto dto = versionService.getSnapshotById(snapshotId)
                .map(SnapshotMapper::toResponse)
                .orElseThrow(() -> new IllegalArgumentException("Snapshot not found with id: " + snapshotId));

        return ResponseEntity.ok(ApiResponse.success("Snapshot fetched successfully", dto));
    }
//get snapshots by file
//No throw used
//Even if empty → returns [] (empty list)
    @GetMapping("/file/{fileId}")
    public ResponseEntity<ApiResponse<List<SnapshotResponseDto>>> getSnapshotsByFile(@PathVariable int fileId){

        List<SnapshotResponseDto> list = versionService.getSnapshotsByFile(fileId).stream().map(SnapshotMapper::toResponse).toList();

        return ResponseEntity.ok(ApiResponse.success("Success fatched for file " + fileId,list));
    }

    //get snapshots by project
    @GetMapping("/project/{projectId}")
    public ResponseEntity<ApiResponse<List<SnapshotResponseDto>>> getSnapshotsByProject(@PathVariable int projectId){

        List<SnapshotResponseDto> list = versionService.getSnapshotsByProject(projectId)
                .stream().map(SnapshotMapper::toResponse).toList();
        return ResponseEntity.ok(ApiResponse.success("Snapshots fetched for project " + projectId, list));
    }

    //get snapshots by branch
    @GetMapping("/project/{projectId}/branch/{branchName}")
    public ResponseEntity<ApiResponse<List<SnapshotResponseDto>>> getByBranch(@PathVariable int projectId , @PathVariable String branchName){

        List<SnapshotResponseDto> list = versionService.getSnapshotsByBranch(projectId, branchName)
                .stream().map(SnapshotMapper::toResponse).toList();

        return ResponseEntity.ok(ApiResponse.success("Snapshots fetched for branch: " + branchName , list));
    }

    //get latest snapshot
    @GetMapping("/file/{fileId}/latest")
    public ResponseEntity<ApiResponse<SnapshotResponseDto>> getLatestSnapshot(
            @PathVariable int fileId){

        SnapshotResponseDto dto = versionService.getLatestSnapshot(fileId)
                .map(SnapshotMapper::toResponse)
                .orElseThrow(() -> new IllegalArgumentException("No snapshots found for file: " + fileId));
        return ResponseEntity.ok(ApiResponse.success("Latest snapshot fetched", dto));
    }

    //get file history
    @GetMapping("/file/{fileId}/history")
    public ResponseEntity<ApiResponse<List<SnapshotResponseDto>>> getFileHistory(
            @PathVariable int fileId){

        List<SnapshotResponseDto> history = versionService.getFileHistoryDto(fileId);

        return ResponseEntity.ok(ApiResponse.success("File history fetched", history));
    }

    //restore snapshot
    @PostMapping("/snapshot/{snapshotId}/restore")
    public ResponseEntity<ApiResponse<RestoreResponseDto>> restoreSnapshot(
            @PathVariable int snapshotId){

        CodeFile restoredFile = versionService.restoreSnapshot(snapshotId);

        RestoreResponseDto response = RestoreResponseDto.builder()
                .restoredFromSnapshotId(snapshotId)
                .content(restoredFile.getContent())
                .message("Snapshot " + snapshotId + " restored as a new snapshot (non-destructive)")
                .build();

        return  ResponseEntity
                .status(HttpStatus.CREATED)
                .body(ApiResponse.success("Snapshot restored successfully", response));


    }

    //diff two snapshots
    @GetMapping("/diff")
    public ResponseEntity<ApiResponse<DiffResponseDto>> diffSnapshots(
            @RequestParam int snapshotId1 ,
            @RequestParam int snapshotId2){

        String diff = versionService.diffSnapshots(snapshotId1, snapshotId2);

        DiffResponseDto response = DiffResponseDto
                .builder()
                .snapshotId1(snapshotId1)
                .snapshotId2(snapshotId2)
                .diffResult(diff)
                .build();

        return ResponseEntity.ok(ApiResponse.success("Diff computed successfully", response));
    }

    //create branch
    @PostMapping("/branch")
    public ResponseEntity<ApiResponse<Void>> createBranch(
            @Valid @RequestBody BranchRequestDto dto
    ){
        versionService.createBranch(dto.getProjectId(), dto.getBranchName());

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(ApiResponse.success("Branch '" + dto.getBranchName() + "' created successfully"));
    }

    //tag snapshot
    @PutMapping("/snapshot/{snapshotId}/tag")
    public ResponseEntity<ApiResponse<Void>> tagSnapshot(
            @PathVariable int snapshotId,
            @Valid @RequestBody TagRequestDto dto
    ){

        versionService.tagSnapshot(snapshotId, dto.getTag());

        return ResponseEntity.ok(ApiResponse.success("Snapshot tagged with '" + dto.getTag() + "' successfully"));

    }


}
