package org.example.ringleproject.dto;

import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class AvailabilitySlotByDateResponse {
    private Long tutorId;
    private LocalDateTime startTime;
    private int duration;

    public AvailabilitySlotByDateResponse(Long tutorId, LocalDateTime startTime, int duration) {
        this.tutorId = tutorId;
        this.startTime = startTime;
        this.duration = duration;
    }

}
