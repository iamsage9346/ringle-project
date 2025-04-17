package org.example.ringleproject.dto;


import lombok.Getter;
import org.example.ringleproject.domain.Lesson;
import org.example.ringleproject.domain.LessonDuration;

import java.time.LocalDateTime;

@Getter
public class StudentLessonResponse {
    private Long lessonId;
    private String tutorName;
    private LocalDateTime startTime;
    private LessonDuration lessonDuration;

    public StudentLessonResponse(Lesson lesson) {
        this.lessonId = lesson.getId();
        this.tutorName = lesson.getTutor().getName();
        this.startTime = lesson.getStartTime();
        this.lessonDuration = lesson.getLessonDuration();
    }
}
