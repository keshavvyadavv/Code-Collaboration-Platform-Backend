package com.collabservice.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "participants")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Participant {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer participantId;

    private String sessionId;
    private Integer userId;
    private String role;
    private LocalDateTime joinedAt;
    private LocalDateTime leftAt;
    private Integer cursorLine;
    private Integer cursorCol;
    private String color;

    @PrePersist
    public void prePersist() {
        if (this.joinedAt == null) {
            this.joinedAt = LocalDateTime.now();
        }
        if (this.cursorLine == null) {
            this.cursorLine = 0;
        }
        if (this.cursorCol == null) {
            this.cursorCol = 0;
        }
    }
}