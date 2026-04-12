package com.collabservice.repository;

import com.collabservice.entity.Participant;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ParticipantRepository extends JpaRepository<Participant, Integer> {

    List<Participant> findBySessionId(String sessionId);

    Optional<Participant> findBySessionIdAndUserId(String sessionId, Integer userId);

    int countBySessionIdAndLeftAtIsNull(String sessionId);
}