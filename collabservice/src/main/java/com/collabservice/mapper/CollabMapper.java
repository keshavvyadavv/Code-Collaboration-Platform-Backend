package com.collabservice.mapper;

import com.collabservice.dto.CollabSessionResponse;
import com.collabservice.dto.ParticipantResponse;
import com.collabservice.entity.CollabSession;
import com.collabservice.entity.Participant;

public class CollabMapper {

    public static CollabSessionResponse toSessionResponse(CollabSession session) {
        return CollabSessionResponse.builder()
                .sessionId(session.getSessionId())
                .projectId(session.getProjectId())
                .fileId(session.getFileId())
                .ownerId(session.getOwnerId())
                .status(session.getStatus())
                .language(session.getLanguage())
                .createdAt(session.getCreatedAt())
                .endedAt(session.getEndedAt())
                .maxParticipants(session.getMaxParticipants())
                .isPasswordProtected(session.getIsPasswordProtected())
                .build();
    }

    public static ParticipantResponse toParticipantResponse(Participant p) {
        return ParticipantResponse.builder()
                .id(p.getId())
                .sessionId(p.getSessionId())
                .userId(p.getUserId())
                .role(p.getRole())
                .color(p.getColor())
                .cursorLine(p.getCursorLine())
                .cursorCol(p.getCursorCol())
                .joinedAt(p.getJoinedAt())
                .leftAt(p.getLeftAt())
                .build();
    }
}