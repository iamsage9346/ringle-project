package org.example.ringleproject.repository;

import org.example.ringleproject.domain.LessonAvailability;
import org.example.ringleproject.domain.LessonAvailabilityId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.Repository;

public interface LessonAvailabilityRepository extends JpaRepository<LessonAvailability, LessonAvailabilityId> {

}