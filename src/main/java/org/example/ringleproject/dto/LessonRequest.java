package org.example.ringleproject.dto;

import lombok.Getter;
import org.example.ringleproject.domain.LessonDuration;
import org.example.ringleproject.domain.Tutor;

import java.time.LocalDateTime;

@Getter
public class LessonRequest {
    private Long tutorId;
    private Long studentId;
    private LocalDateTime startTime;
    private LessonDuration lessonDuration;

    public LessonRequest(Long studentId, Long tutorId, LocalDateTime start, LessonDuration lessonDuration) {
        this.studentId = studentId;
        this.tutorId = tutorId;
        this.startTime = start;
        this.lessonDuration = lessonDuration;
    }
}
