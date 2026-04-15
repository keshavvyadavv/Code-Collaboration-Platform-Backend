package com.executionservice.service.impl;

import com.executionservice.dto.ExecutionResult;
import com.executionservice.dto.ExecutionStatsDto;
import com.executionservice.entity.ExecutionJob;
import com.executionservice.exception.ResourceNotFoundException;
import com.executionservice.repository.ExecutionRepository;
import com.executionservice.service.ExecutionService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.task.TaskExecutor;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.*;

@Slf4j
@RequiredArgsConstructor
@Transactional
@Service
public class ExecutionServiceImpl implements ExecutionService {

    private final ExecutionRepository executionRepository;
    private final DockerService dockerService;
    private final TaskExecutor taskExecutor;

    //submit
    @Override
    public ExecutionJob submitExecution(ExecutionJob executionJob) {

        executionJob.setStatus("PENDING");
        executionJob.setCreatedAt(LocalDateTime.now());
        executionJob.setCompletedAt(null);

        executionJob.setStderr("");
        executionJob.setStdout("");
        executionJob.setStdin("");

        ExecutionJob saved = executionRepository.saveAndFlush(executionJob);

        log.info("Submitted execution job ID: {} for user: {} language: {}",
                saved.getJobId(), saved.getUserId(), saved.getLanguage());

        taskExecutor.execute(() -> executeJobAsync(saved));

        return saved;
    }

    @Async
    public void executeJobAsync(ExecutionJob job) {
        try {
            job.setStatus("RUNNING");
            executionRepository.saveAndFlush(job);

            ExecutionResult result = dockerService.runCode(job);

            job.setStatus(result.isSuccess() ? "SUCCESS" : "FAILED");
            job.setStdout(result.getStdout());
            job.setStderr(result.getStderr());
            job.setExitCode(result.getExitCode());
            job.setExecutionTimeMs(result.getExecutionTimeMs());
            job.setErrorMessage(result.getErrorMessage());
            job.setCompletedAt(LocalDateTime.now());

            executionRepository.saveAndFlush(job);

        } catch (Exception e) {
            job.setStatus("FAILED");
            job.setErrorMessage(e.getMessage());
            job.setCompletedAt(LocalDateTime.now());

            executionRepository.saveAndFlush(job);

            log.error("Async execution failed for job {}", job.getJobId(), e);
        }
    }

    @Override
    public void cancelExecution(String jobId) {
        ExecutionJob job = executionRepository.findByJobId(jobId)
                .orElseThrow(() -> new ResourceNotFoundException("Execution job not found: " + jobId));

        if (!"CANCELLED".equals(job.getStatus())) {

            if ("RUNNING".equals(job.getStatus()) && job.getContainerId() != null) {
                dockerService.killContainer(job.getContainerId());
            }

            job.setStatus("CANCELLED");
            job.setCompletedAt(LocalDateTime.now());
            executionRepository.save(job);

            log.info("Cancelled execution job: {}", jobId);

        } else {
            throw new IllegalArgumentException("Job already cancelled");
        }
    }
    // get by id
    @Override
    public Optional<ExecutionJob> getJobById(String jobId) {
        return executionRepository.findByJobId(jobId);
    }
    // get by user
    @Override
    public List<ExecutionJob> getExecutionsByUser(int userId) {
        return executionRepository.findByUserId(userId);
    }
    // get by project
    @Override
    public List<ExecutionJob> getExecutionsByProject(int projectId) {
        return executionRepository.findByProjectId(projectId);
    }

    //get result
    @Override
    public ExecutionJob getExecutionResult(String jobId) {
        return executionRepository.findByJobId(jobId)
                .orElseThrow(() -> new ResourceNotFoundException("Execution job not found: " + jobId));
    }

    //supported languages
    @Override
    public List<String> getSupportedLanguages() {
        //get from dockerservice
        return List.of("Java", "Python" , "JavaScript", "C++", "Go", "Rust");
    }

    // language version
    @Override
    public String getLanguageVersion(String language) {
        //get from docker
        Map<String, String> versions = new HashMap<>();
        versions.put("Java", "21");
        versions.put("Python", "3.12");
        versions.put("JavaScript", "Node 20");
        versions.put("C++", "GCC 13");
        versions.put("Go", "1.22");
        versions.put("Rust", "1.77");

        return versions.getOrDefault(language, "Unknown");
    }

    @Override
    public ExecutionStatsDto getExecutionStats(int userId) {
        long totalJobs  = executionRepository.countByUserId(userId);
        long successful = executionRepository.countByUserIdAndStatus(userId, "SUCCESS");
        long failed     = executionRepository.countByUserIdAndStatus(userId, "FAILED");
        long pending    = executionRepository.countByUserIdAndStatus(userId, "PENDING");
        long cancelled  = executionRepository.countByUserIdAndStatus(userId, "CANCELLED");

        log.info("Fetched execution stats for user: {}", userId);

        return new ExecutionStatsDto(totalJobs, successful, failed, pending, cancelled);
    }


}
