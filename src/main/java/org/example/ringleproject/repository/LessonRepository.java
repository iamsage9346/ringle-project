package org.example.ringleproject.repository;

import org.example.ringleproject.domain.Lesson;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface LessonRepository extends JpaRepository<Lesson, Long> {
    @Query("SELECT l FROM Lesson l JOIN FETCH l.tutor WHERE l.student.id = :studentId")
    List<Lesson> findByStudentIdWithTutor(@Param("studentId") Long studentId);
}
