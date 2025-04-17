package org.example.ringleproject.dto;

import lombok.Getter;
import org.aspectj.weaver.patterns.ConcreteCflowPointcut;
import org.example.ringleproject.domain.Availability;
import org.example.ringleproject.domain.LessonDuration;

import java.time.Duration;
import java.time.LocalDateTime;

@Getter
public class AvailabilitySlotByTimeResponse {
    private Long tutorId;
    private String tutorName;
    private LocalDateTime startTime;
    private LessonDuration lessonDuration;

    public AvailabilitySlotByTimeResponse(Availability slot) {
        this.tutorId = slot.getTutor().getId();
        this.tutorName = slot.getTutor().getName();
        this.startTime = slot.getStartTime();
        LocalDateTime endTime = slot.getEndTime();

        long minutes = Duration.between(startTime, endTime).toMinutes();

        if (minutes == 30) {
            this.lessonDuration = LessonDuration.MIN30;
        } else if (minutes == 60) {
            this.lessonDuration = LessonDuration.MIN60;
        } else {
            this.lessonDuration = null;
        }
    }

}
