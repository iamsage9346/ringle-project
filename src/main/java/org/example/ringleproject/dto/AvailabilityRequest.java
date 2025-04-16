package org.example.ringleproject.dto;

import lombok.Getter;
import org.example.ringleproject.domain.Availability;

import java.time.LocalDateTime;

@Getter
public class AvailabilityRequest {
    private Long tutorId;
    private LocalDateTime startTime;
    private LocalDateTime endTime;

}
