package org.example.ringleproject.controller;

import io.swagger.v3.oas.annotations.Operation;
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
import org.springframework.web.bind.annotation.PathVariable;
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

    @Operation(summary = "수업 가능한 시간 생성", description = "튜터가 수업 가능한 시간을 생성합니다.")
    @PostMapping("/create-availability")
    public ResponseEntity<AvailabilityResponse> registerAvailability(@RequestBody AvailabilityRequest availabilityRequest) {
        AvailabilityResponse availabilityResponse = availabilityService.openAvailability(availabilityRequest);

        return ResponseEntity.ok(availabilityResponse);
    }

    @Operation(summary = "수업 가능한 시간 삭제", description = "튜터가 수업 가능한 시간을 삭제합니다.")
    @DeleteMapping("/delete-availability/{tutorId}")
    public ResponseEntity<Void> deleteAvailability(@PathVariable Long tutorId,
                                                   @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startTime) {
        availabilityService.deleteAvailability(tutorId, startTime);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "기간 & 수업 길이로 수업 가능한 시간 조회", description = "학생이 기간 & 수업 길이로 현재 수업 가능한 시간대를 조회합니다.")
    @GetMapping("/search-by-date")
    public ResponseEntity<List<AvailabilitySlotByDateResponse>> findAvailabilityByDate(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
            @RequestParam int duration
    ) {
        List<AvailabilitySlotByDateResponse> result = availabilityService.searchByDateAvailableSlots(startDate, endDate, duration);

        return ResponseEntity.ok(result);
    }

    @Operation(summary = "시간대 & 수업 길이로 수업 가능한 튜터 조회", description = "학생이 시간대 & 수업 길이로 수업 가능한 튜터를 조회합니다.")
    @GetMapping("/search-by-time")
    public ResponseEntity<List<AvailabilitySlotByTimeResponse>> findAvailabilityByTime(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
            @RequestParam int duration
    ) {
        List<AvailabilitySlotByTimeResponse> result = availabilityService.searchByTimeAvailableSlots(startDate, duration);
        return ResponseEntity.ok(result);
    }

}
