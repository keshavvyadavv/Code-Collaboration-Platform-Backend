package com.collabservice.service.impl;

import com.collabservice.dto.CollabSessionResponse;
import com.collabservice.dto.ParticipantResponse;
import com.collabservice.entity.CollabSession;
import com.collabservice.entity.Participant;
import com.collabservice.exception.ResourceNotFoundException;
import com.collabservice.mapper.CollabMapper;
import com.collabservice.repository.CollabSessionRepository;
import com.collabservice.repository.ParticipantRepository;
import com.collabservice.service.CollabService;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class CollabServiceImpl implements CollabService {

    private final CollabSessionRepository collabSessionRepository;
    private final ParticipantRepository participantRepository;
    private final SimpMessagingTemplate messagingTemplate;

    @Override
    public CollabSessionResponse createSession(CollabSession collabSession) {

        if (collabSession.getMaxParticipants() == null) {
            collabSession.setMaxParticipants(10);
        }

        CollabSession saved = collabSessionRepository.save(collabSession);

        Participant host = Participant.builder()
                .sessionId(saved.getSessionId())
                .userId(saved.getOwnerId())
                .role("HOST")
                .color(generateColor(1))
                .build();

        participantRepository.save(host);

        return CollabMapper.toSessionResponse(saved);
    }

    @Override
    public CollabSessionResponse getSessionById(String sessionId) {

        CollabSession session = collabSessionRepository.findBySessionId(sessionId)
                .orElseThrow(() -> new ResourceNotFoundException("Session not found"));

        return CollabMapper.toSessionResponse(session);
    }

    @Override
    public List<CollabSessionResponse> getSessionsByProject(Integer projectId) {

        return collabSessionRepository.findByProjectId(projectId)
                .stream()
                .map(CollabMapper::toSessionResponse)
                .toList();
    }

    @Override
    public ParticipantResponse joinSession(String sessionId, Integer userId, String role, String password) {

        CollabSession session = getSessionEntity(sessionId);

        if (!"ACTIVE".equalsIgnoreCase(session.getStatus())) {
            throw new IllegalArgumentException("Session is not active");
        }

        if (Boolean.TRUE.equals(session.getIsPasswordProtected())) {
            if (password == null || !password.equals(session.getSessionPassword())) {
                throw new IllegalArgumentException("Invalid session password");
            }
        }

        int activeCount = participantRepository.countBySessionIdAndLeftAtIsNull(sessionId);

        Participant participant = Participant.builder()
                .sessionId(sessionId)
                .userId(userId)
                .role(role)
                .color(generateColor(activeCount + 1))
                .build();

        Participant saved = participantRepository.save(participant);

        return CollabMapper.toParticipantResponse(saved);
    }

    @Override
    public void leaveSession(String sessionId, Integer userId) {
        Participant participant = participantRepository.findBySessionIdAndUserId(sessionId, userId)
                .orElseThrow(() -> new ResourceNotFoundException("Participant not found"));

        participant.setLeftAt(LocalDateTime.now());
        participantRepository.save(participant);

        messagingTemplate.convertAndSend("/topic/session/" + sessionId, "User left: " + userId);
    }

    @Override
    public void endSession(String sessionId) {

        CollabSession session = getSessionEntity(sessionId);
        session.setStatus("ENDED");
        session.setEndedAt(LocalDateTime.now());

        collabSessionRepository.save(session);

        messagingTemplate.convertAndSend("/topic/session/" + sessionId, "Session ended");
    }

    @Override
    public List<ParticipantResponse> getParticipants(String sessionId) {

        return participantRepository.findBySessionId(sessionId)
                .stream()
                .map(CollabMapper::toParticipantResponse)
                .toList();
    }

    @Override
    public void updateCursor(String sessionId, Integer userId, Integer line, Integer col) {
        Participant participant = participantRepository.findBySessionIdAndUserId(sessionId, userId)
                .orElseThrow(() -> new ResourceNotFoundException("Participant not found"));

        participant.setCursorLine(line);
        participant.setCursorCol(col);
        participantRepository.save(participant);

        messagingTemplate.convertAndSend(
                "/topic/session/" + sessionId + "/cursor",
                "User " + userId + " moved cursor to line " + line + ", col " + col
        );
    }

    @Override
    public void broadcastChange(String sessionId, String message) {
        messagingTemplate.convertAndSend("/topic/session/" + sessionId + "/changes", message);
    }

    @Override
    public void kickParticipant(String sessionId, Integer userId) {
        Participant participant = participantRepository.findBySessionIdAndUserId(sessionId, userId)
                .orElseThrow(() -> new ResourceNotFoundException("Participant not found"));

        participant.setLeftAt(LocalDateTime.now());
        participantRepository.save(participant);

        messagingTemplate.convertAndSend("/topic/session/" + sessionId, "User kicked: " + userId);
    }

    @Override
    public CollabSessionResponse getActiveSession(Integer fileId) {

        CollabSession session = collabSessionRepository.findByFileIdAndStatus(fileId, "ACTIVE")
                .orElseThrow(() -> new ResourceNotFoundException("No active session"));

        return CollabMapper.toSessionResponse(session);
    }
    private String generateColor(int index) {
        String[] colors = {"red", "blue", "green", "orange", "purple", "pink", "cyan", "yellow"};
        return colors[(index - 1) % colors.length];
    }

    private CollabSession getSessionEntity(String sessionId) {
        return collabSessionRepository.findBySessionId(sessionId)
                .orElseThrow(() -> new ResourceNotFoundException("Session not found"));
    }
}