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
}
