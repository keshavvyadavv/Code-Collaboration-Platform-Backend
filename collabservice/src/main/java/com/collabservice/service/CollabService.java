package com.collabservice.service;

import com.collabservice.dto.CollabSessionResponse;
import com.collabservice.dto.ParticipantResponse;
import com.collabservice.entity.CollabSession;

import java.util.List;

public interface CollabService {

    CollabSessionResponse createSession(CollabSession collabSession);

    CollabSessionResponse getSessionById(String sessionId);

    List<CollabSessionResponse> getSessionsByProject(Integer projectId);

    ParticipantResponse joinSession(String sessionId, Integer userId, String role, String password);

    void leaveSession(String sessionId, Integer userId);

    void endSession(String sessionId);

    List<ParticipantResponse> getParticipants(String sessionId);

    void updateCursor(String sessionId, Integer userId, Integer line, Integer col);

    void broadcastChange(String sessionId, String message);

    void kickParticipant(String sessionId, Integer userId);

    CollabSessionResponse getActiveSession(Integer fileId);
}