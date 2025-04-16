package org.example.ringleproject.repository;

import org.example.ringleproject.domain.Availability;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

public interface AvailabilityRepository extends JpaRepository<Availability, Long> {
    boolean existsByTutorIdAndStartTime(Long tutorId, LocalDateTime startTime);
    Availability findByTutorIdAndStartTime(Long tutorId, LocalDateTime startTime);
}
