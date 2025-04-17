package org.example.ringleproject.repository;

import org.example.ringleproject.domain.Availability;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface AvailabilityRepository extends JpaRepository<Availability, Long> {
    boolean existsByTutorIdAndStartTime(Long tutorId, LocalDateTime startTime);

    Availability findByTutorIdAndStartTime(Long tutorId, LocalDateTime startTime);

    @Query("SELECT a FROM Availability a JOIN FETCH a.tutor WHERE a.startTime BETWEEN :start AND :end AND a.isBooked = false")
    List<Availability> findAvailableWithTutor(@Param("start") LocalDateTime start, @Param("end") LocalDateTime end);

    @Query("SELECT a FROM Availability a JOIN FETCH a.tutor WHERE a.startTime = :startTime AND a.isBooked = false")
    List<Availability> findByStartTimeAndBookedIsFalseWithTutor(@Param("startTime") LocalDateTime startTime);

    boolean existsByTutorIdAndStartTimeAndBookedIsFalse(Long id, LocalDateTime nextTime, boolean b);

    void deleteByStartTimeBeforeAndBookedFalse(LocalDateTime now);

}
