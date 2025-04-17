package org.example.ringleproject.dto;

import lombok.Getter;
import org.example.ringleproject.domain.Lesson;
import org.example.ringleproject.domain.LessonDuration;

import java.time.LocalDateTime;

public class LessonResponse {
    private Long studentId;
    private Long tutorId;
    private LocalDateTime startTime;
    private LessonDuration lessonDuration;

    public LessonResponse(Lesson lesson) {
        this.studentId = lesson.getStudent().getId();
        this.tutorId = lesson.getTutor().getId();
        this.startTime = lesson.getStartTime();
        this.lessonDuration = lesson.getLessonDuration();
    }

}
