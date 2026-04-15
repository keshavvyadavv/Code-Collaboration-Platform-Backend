package com.executionservice.controller;

import com.executionservice.dto.ApiResponse;
import com.executionservice.dto.ExecutionRequestDto;
import com.executionservice.dto.ExecutionResponseDto;
import com.executionservice.dto.ExecutionStatsDto;
import com.executionservice.entity.ExecutionJob;
import com.executionservice.mapper.ExecutionMapper;
import com.executionservice.service.ExecutionService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.coyote.Response;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.accept.ApiVersionResolver;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RequiredArgsConstructor
@Slf4j
@RequestMapping("/api/executions")
@RestController
public class ExecutionController {




    private  final ExecutionService executionService;

    //submit execution
    @PostMapping("/submit")
    public ResponseEntity<ApiResponse<ExecutionResponseDto>> submitExecution(
            @Valid @RequestBody ExecutionRequestDto dto
            ){
        ExecutionJob job = ExecutionMapper.toEntity(dto);
        ExecutionJob saved = executionService.submitExecution(job);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(ApiResponse.success("Execution job submitted successfully", ExecutionMapper.toResponse(saved)));
        
    }

    //get job by id
    @GetMapping("/{jobId}")
    public ResponseEntity<ApiResponse<ExecutionResponseDto>> getJobById(
            @PathVariable String jobId
    ){

        ExecutionResponseDto dto = executionService.getJobById(jobId)
                .map(ExecutionMapper::toResponse)
                .orElseThrow(() -> new com.executionservice.exception.ResourceNotFoundException("Job not found: " + jobId));


        return ResponseEntity.ok(ApiResponse.success("Job fetched successfully", dto));
    }
    // get executions by user
    @GetMapping("/user/{userId}")
    public ResponseEntity<ApiResponse<List<ExecutionResponseDto>>> getExecutionsByUser(@PathVariable int userId){

        List<ExecutionJob> list = executionService.getExecutionsByUser(userId);

        List<ExecutionResponseDto> res = list.stream().map(ExecutionMapper::toResponse).toList();

        return ResponseEntity.ok(ApiResponse.success("Executions fetched for user " + userId, res));

    }

    //get executions by project
    @GetMapping("/project/{projectId}")
    public ResponseEntity<ApiResponse<List<ExecutionResponseDto>>> byExecutionByProject(@PathVariable int projectId){

        List<ExecutionJob> list = executionService.getExecutionsByProject(projectId);

        List<ExecutionResponseDto> res = list.stream().map(ExecutionMapper::toResponse).toList();

        return ResponseEntity.ok(ApiResponse.success("Executions fetched for project " + projectId , res));



    }

    // cancel execution
    @PostMapping("/{jobId}/cancel")
    public ResponseEntity<ApiResponse<Void>> cancelExecution(
            @PathVariable String jobId) {
        // Verify job exists first
        executionService.getJobById(jobId)
                .orElseThrow(() -> new com.executionservice.exception.ResourceNotFoundException("Job not found: " + jobId));
        executionService.cancelExecution(jobId);
        return ResponseEntity.ok(ApiResponse.success("Execution job " + jobId + " cancelled successfully"));
    }

    //get execution result
    @GetMapping("/{jobId}/result")
    public ResponseEntity<ApiResponse<ExecutionResponseDto>> getExecutionResult(
            @PathVariable String jobId){

        ExecutionResponseDto dto = ExecutionMapper.toResponse(executionService.getExecutionResult(jobId));
        return ResponseEntity.ok(ApiResponse.success("Execution result fetched", dto));
    }

    //get supported languages
    @GetMapping("/languages")
    public ResponseEntity<ApiResponse<List<String>>> getSupportedLanguages() {

        return ResponseEntity.ok(ApiResponse.success("Supported languages fetched", executionService.getSupportedLanguages()));
    }

    //get execution stats
    @GetMapping("/stats/user/{userId}")
    public ResponseEntity<ApiResponse<ExecutionStatsDto>> getExecutionStats(
            @PathVariable int userId) {

        return ResponseEntity.ok(
                ApiResponse.success(
                        "Execution stats fetched",
                        executionService.getExecutionStats(userId)
                )
        );
    }
}

