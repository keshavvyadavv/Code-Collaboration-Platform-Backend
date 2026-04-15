package com.executionservice.service;

import com.executionservice.dto.ExecutionStatsDto;
import com.executionservice.entity.ExecutionJob;

import java.util.List;
import java.util.Map;
import java.util.Optional;

public interface ExecutionService {
    ExecutionJob submitExecution(ExecutionJob excution);

    Optional<ExecutionJob> getJobById(String jobId);

    List<ExecutionJob> getExecutionsByUser(int userId);

    List<ExecutionJob> getExecutionsByProject(int projectId);

    void cancelExecution(String jobId);

    ExecutionJob getExecutionResult(String jobId);

    List<String> getSupportedLanguages();

    String getLanguageVersion(String language);

    ExecutionStatsDto getExecutionStats(int userId);

}
