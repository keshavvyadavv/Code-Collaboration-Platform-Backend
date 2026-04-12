package com.collabservice.controller;

import com.collabservice.dto.*;
import com.collabservice.entity.CollabSession;
import com.collabservice.entity.Participant;
import com.collabservice.service.CollabService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "Session API", description = "Collaboration session operations")
@RestController
@RequestMapping("/sessions")
@RequiredArgsConstructor
public class CollabResource {

    private final CollabService collabService;

    @Operation(summary = "Create a new session")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Session created successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid request"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @PostMapping
    public ResponseEntity<CollabSessionResponse> create(@Valid @RequestBody CreateSessionRequest request) {
        CollabSession session = CollabSession.builder()
                .projectId(request.getProjectId())
                .fileId(request.getFileId())
                .ownerId(request.getOwnerId())
                .language(request.getLanguage())
                .maxParticipants(request.getMaxParticipants())
                .isPasswordProtected(request.getIsPasswordProtected())
                .sessionPassword(request.getSessionPassword())
                .build();

        return ResponseEntity.ok(collabService.createSession(session));
    }

    @Operation(summary = "Get session by ID")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Session fetched successfully"),
            @ApiResponse(responseCode = "404", description = "Session not found"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @GetMapping("/{sessionId}")
    public ResponseEntity<CollabSessionResponse> getById(@PathVariable String sessionId) {
        return ResponseEntity.ok(collabService.getSessionById(sessionId));
    }

    @Operation(summary = "Get sessions by project")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Sessions fetched successfully"),
            @ApiResponse(responseCode = "404", description = "Project not found"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @GetMapping("/project/{projectId}")
    public ResponseEntity<List<CollabSessionResponse>> getByProject(@PathVariable Integer projectId) {
        return ResponseEntity.ok(collabService.getSessionsByProject(projectId));
    }

    @Operation(summary = "Join session")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Joined session successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid request / session full"),
            @ApiResponse(responseCode = "404", description = "Session not found"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @PostMapping("/{sessionId}/join")
    public ResponseEntity<ParticipantResponse> join(@PathVariable String sessionId,
                                            @Valid @RequestBody JoinSessionRequest request) {
        return ResponseEntity.ok(
                collabService.joinSession(
                        sessionId,
                        request.getUserId(),
                        request.getRole(),
                        request.getSessionPassword()
                )
        );
    }

    @Operation(summary = "Leave session")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Left session successfully"),
            @ApiResponse(responseCode = "404", description = "Participant not found"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @PostMapping("/{sessionId}/leave/{userId}")
    public ResponseEntity<String> leave(@PathVariable String sessionId, @PathVariable Integer userId) {
        collabService.leaveSession(sessionId, userId);
        return ResponseEntity.ok("Participant left session successfully");
    }

    @Operation(summary = "End session")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Session ended successfully"),
            @ApiResponse(responseCode = "404", description = "Session not found"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @PostMapping("/{sessionId}/end")
    public ResponseEntity<String> end(@PathVariable String sessionId) {
        collabService.endSession(sessionId);
        return ResponseEntity.ok("Session ended successfully");
    }

    @Operation(summary = "Get participants")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Participants fetched successfully"),
            @ApiResponse(responseCode = "404", description = "Session not found"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @GetMapping("/{sessionId}/participants")
    public ResponseEntity<List<ParticipantResponse>> getParticipants(@PathVariable String sessionId) {
        return ResponseEntity.ok(collabService.getParticipants(sessionId));
    }

    @Operation(summary = "Update cursor position")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Cursor updated successfully"),
            @ApiResponse(responseCode = "404", description = "Participant not found"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @PutMapping("/{sessionId}/cursor")
    public ResponseEntity<String> updateCursor(@PathVariable String sessionId,
                                               @Valid @RequestBody UpdateCursorRequest request) {
        collabService.updateCursor(
                sessionId,
                request.getUserId(),
                request.getCursorLine(),
                request.getCursorCol()
        );
        return ResponseEntity.ok("Cursor updated successfully");
    }

    @Operation(summary = "Kick participant")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Participant kicked successfully"),
            @ApiResponse(responseCode = "404", description = "Participant not found"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @PostMapping("/{sessionId}/kick")
    public ResponseEntity<String> kick(@PathVariable String sessionId,
                                       @Valid @RequestBody KickParticipantRequest request) {
        collabService.kickParticipant(sessionId, request.getUserId());
        return ResponseEntity.ok("Participant kicked successfully");
    }

    @Operation(summary = "Get active session by file ID")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Active session fetched successfully"),
            @ApiResponse(responseCode = "404", description = "No active session found"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @GetMapping("/active/{fileId}")
    public ResponseEntity<CollabSessionResponse> getActive(@PathVariable Integer fileId) {
        return ResponseEntity.ok(collabService.getActiveSession(fileId));
    }
}