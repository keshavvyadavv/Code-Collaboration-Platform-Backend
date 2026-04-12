package com.collabservice.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import lombok.*;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "collab_sessions")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CollabSession {

    @Id
    private String sessionId;

    private Integer projectId;
    private Integer fileId;
    private Integer ownerId;
    private String status;
    private String language;
    private LocalDateTime createdAt;
    private LocalDateTime endedAt;
    private Integer maxParticipants;
    private Boolean isPasswordProtected;
    private String sessionPassword;

    @PrePersist
    public void prePersist() {
        if (this.sessionId == null || this.sessionId.isBlank()) {
            this.sessionId = UUID.randomUUID().toString();
        }
        if (this.createdAt == null) {
            this.createdAt = LocalDateTime.now();
        }
        if (this.status == null) {
            this.status = "ACTIVE";
        }
        if (this.isPasswordProtected == null) {
            this.isPasswordProtected = false;
        }
    }
}