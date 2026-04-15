package com.executionservice.service.impl;

import com.executionservice.dto.ExecutionResult;
import com.executionservice.entity.ExecutionJob;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.*;
import java.nio.file.*;
import java.util.*;
import java.util.concurrent.TimeUnit;

@Slf4j
@Service
public class DockerService {

    private static final long DEFAULT_TIMEOUT_SECONDS = 10;
    private static final String WORKSPACE = "/workspace";

    public ExecutionResult runCode(ExecutionJob job) {
        Path tempDir = null;
        String containerName = "exec_" + job.getJobId();

        try {
            tempDir = Files.createTempDirectory("sandbox_" + job.getJobId());

            // write code
            String filename = getSourceFileName(job.getLanguage());
            Path sourcePath = tempDir.resolve(filename);
            Files.writeString(sourcePath, job.getSourceCode());

            // stdin file
            if (job.getStdin() != null && !job.getStdin().isBlank()) {
                Files.writeString(tempDir.resolve("input.txt"), job.getStdin());
            }

            String image = getDockerImage(job.getLanguage());

            List<String> command = Arrays.asList(
                    "docker", "run", "--rm",
                    "--name", containerName,
                    "--network", "none",
                    "--cpus", "0.5",
                    "--memory", "256m",
                    "-v", tempDir.toAbsolutePath() + ":" + WORKSPACE,
                    image,
                    "sh", "-c",
                    "timeout " + DEFAULT_TIMEOUT_SECONDS + "s /run_code.sh " + job.getLanguage().toLowerCase() + " < input.txt"
            );

            long start = System.currentTimeMillis();

            ProcessBuilder pb = new ProcessBuilder(command);
            Process process = pb.start();

            boolean finished = process.waitFor(DEFAULT_TIMEOUT_SECONDS + 2, TimeUnit.SECONDS);

            if (!finished) {
                process.destroyForcibly();
            }

            long end = System.currentTimeMillis();

            String stdout = readStream(process.getInputStream());
            String stderr = readStream(process.getErrorStream());

            int exitCode = finished ? process.exitValue() : -1;

            return ExecutionResult.builder()
                    .success(exitCode == 0)
                    .stdout(stdout)
                    .stderr(stderr)
                    .exitCode(exitCode)
                    .executionTimeMs(end - start)
                    .errorMessage(!finished ? "Timeout exceeded" : null)
                    .build();

        } catch (Exception e) {
            log.error("Execution failed for job {}", job.getJobId(), e);
            return ExecutionResult.builder()
                    .success(false)
                    .stderr(e.getMessage())
                    .errorMessage(e.getMessage())
                    .build();
        } finally {
            if (tempDir != null) {
                deleteDirectory(tempDir);
            }
        }
    }

    private String getDockerImage(String language) {
        return switch (language.toLowerCase()) {
            case "java" -> "eclipse-temurin:21-jdk-alpine";
            case "python" -> "python:3.12-slim";
            case "javascript" -> "node:20-slim";
            case "c++" -> "gcc:13";
            case "go" -> "golang:1.22-alpine";
            case "rust" -> "rust:1.77-slim";
            default -> throw new IllegalArgumentException("Unsupported language: " + language);
        };
    }

    private String getSourceFileName(String language) {
        return switch (language.toLowerCase()) {
            case "java" -> "Main.java";
            case "python" -> "main.py";
            case "javascript" -> "main.js";
            case "c++" -> "main.cpp";
            case "go" -> "main.go";
            case "rust" -> "main.rs";
            default -> "code.txt";
        };
    }

    private String readStream(InputStream is) throws IOException {
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(is))) {
            StringBuilder sb = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                sb.append(line).append("\n");
            }
            return sb.toString().trim();
        }
    }

    private void deleteDirectory(Path path) {
        try {
            Files.walk(path)
                    .sorted(Comparator.reverseOrder())
                    .forEach(p -> {
                        try { Files.delete(p); } catch (IOException ignored) {}
                    });
        } catch (IOException ignored) {}
    }

    public void killContainer(String containerIdOrName) {
        try {
            new ProcessBuilder("docker", "kill", containerIdOrName).start();
            new ProcessBuilder("docker", "rm", "-f", containerIdOrName).start();
        } catch (Exception e) {
            log.warn("Failed to kill container {}", containerIdOrName, e);
        }
    }
}