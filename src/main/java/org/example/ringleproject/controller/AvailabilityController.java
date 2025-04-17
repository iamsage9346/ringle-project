package org.example.ringleproject.controller;

import lombok.RequiredArgsConstructor;
import org.example.ringleproject.dto.AvailabilityRequest;
import org.example.ringleproject.dto.AvailabilityResponse;
import org.example.ringleproject.dto.AvailabilitySlotByDateResponse;
import org.example.ringleproject.dto.AvailabilitySlotByTimeResponse;
import org.example.ringleproject.service.AvailabilityService;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequiredArgsConstructor
public class AvailabilityController {

    private final AvailabilityService availabilityService;

    @PostMapping("/availability")
    public ResponseEntity<AvailabilityResponse> registerAvailability(@RequestBody AvailabilityRequest availabilityRequest) {
        AvailabilityResponse availabilityResponse = availabilityService.openAvailability(availabilityRequest);

        return ResponseEntity.ok(availabilityResponse);
    }

    @DeleteMapping("/availability")
    public ResponseEntity<Void> deleteAvailability(@RequestParam Long tutorId,
                                                   @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startTime) {
        availabilityService.deleteAvailability(tutorId, startTime);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/search-by-date")
    public ResponseEntity<List<AvailabilitySlotByDateResponse>> findAvailabilityByDate(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
            @RequestParam int duration
    ) {
        List<AvailabilitySlotByDateResponse> result = availabilityService.searchByDateAvailableSlots(startDate, endDate, duration);

        return ResponseEntity.ok(result);
    }

    @GetMapping("/search-by-time")
    public ResponseEntity<List<AvailabilitySlotByTimeResponse>> findAvailabilityByTime(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
            @RequestParam int duration
    ) {
        List<AvailabilitySlotByTimeResponse> result = availabilityService.searchByTimeAvailableSlots(startDate, duration);
        return ResponseEntity.ok(result);
    }

}
