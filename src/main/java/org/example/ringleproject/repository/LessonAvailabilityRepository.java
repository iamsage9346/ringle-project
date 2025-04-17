package org.example.ringleproject.repository;

import org.example.ringleproject.domain.Availability;
import org.example.ringleproject.domain.Lesson;
import org.example.ringleproject.domain.LessonAvailability;
import org.example.ringleproject.domain.LessonAvailabilityId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.Repository;

import java.util.List;

public interface LessonAvailabilityRepository extends JpaRepository<LessonAvailability, LessonAvailabilityId> {
    List<LessonAvailability> findByLesson(Lesson lesson);
    List<LessonAvailability> findByAvailability(Availability a);

}