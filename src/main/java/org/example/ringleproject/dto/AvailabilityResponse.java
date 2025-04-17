package org.example.ringleproject.dto;

import lombok.Getter;

import java.time.LocalDateTime;

public class AvailabilityResponse {
    private Long tutorId;
    private LocalDateTime startTime;
    private LocalDateTime endTime;

    public AvailabilityResponse(Long tutorId, LocalDateTime startTime, LocalDateTime endTime) {
        this.tutorId = tutorId;
        this.startTime = startTime;
        this.endTime = endTime;
    }
}
