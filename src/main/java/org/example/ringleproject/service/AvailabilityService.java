package org.example.ringleproject.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.example.ringleproject.domain.Availability;
import org.example.ringleproject.domain.Tutor;
import org.example.ringleproject.dto.AvailabilityRequest;
import org.example.ringleproject.dto.AvailabilityResponse;
import org.example.ringleproject.dto.AvailabilitySlotByDateResponse;
import org.example.ringleproject.dto.AvailabilitySlotByTimeResponse;
import org.example.ringleproject.repository.AvailabilityRepository;
import org.example.ringleproject.repository.TutorRepository;

import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AvailabilityService {
    private final TutorRepository tutorRepository;
    private final AvailabilityRepository availabilityRepository;

    @Transactional
    public AvailabilityResponse openAvailability(AvailabilityRequest availabilityRequest) {
        Long tutorId = availabilityRequest.getTutorId();
        LocalDateTime startTime = availabilityRequest.getStartTime();
        LocalDateTime endTime = availabilityRequest.getEndTime();

        if (!(startTime.getMinute() == 0 || startTime.getMinute() == 30)) {
            throw new IllegalArgumentException("Start time must be on the hour or half-hour.");
        }

        if (!endTime.equals(startTime.plusMinutes(30))) {
            throw new IllegalArgumentException("Lesson must be exactly 30 minutes.");
        }

        Tutor tutor = tutorRepository.findById(tutorId)
                .orElseThrow(() -> new IllegalArgumentException("Tutor does not exist."));

        Availability availability = new Availability();
        availability.setTutor(tutor);
        availability.setStartTime(startTime);
        availability.setEndTime(endTime);
        availability.setBooked(false);
        availabilityRepository.save(availability);

        return new AvailabilityResponse(tutorId, startTime, endTime);
    }

    @Transactional
    public void deleteAvailability(Long tutorId, LocalDateTime startTime) {
        boolean exists = availabilityRepository.existsByTutorIdAndStartTime(tutorId, startTime);
        if (!exists) {
            throw new IllegalArgumentException("The class available time zone does not exist.");
        }

        Availability availability = availabilityRepository.findByTutorIdAndStartTime(tutorId, startTime);
        Long availabilityId = availability.getId();

        availabilityRepository.deleteById(availabilityId);
    }

    @Transactional
    public List<AvailabilitySlotByDateResponse> searchByDateAvailableSlots(LocalDate startDate, LocalDate endDate, int lessonDuration) {
        List<Availability> allSlot = availabilityRepository.findAvailableWithTutor(
                startDate.atStartOfDay(),
                endDate.plusDays(1).atStartOfDay()
        );

        // tutor별 가능한 시간 분류
        Map<Long, List<Availability>> groupByTutor = allSlot.stream()
                .collect(Collectors.groupingBy(a -> a.getTutor().getId()));

        List<AvailabilitySlotByDateResponse> result = new ArrayList<>();

            int requiredSlotCount = lessonDuration / 30;

            for (Map.Entry<Long, List<Availability>> entry : groupByTutor.entrySet()) {
                Long tutorId = entry.getKey();

                // 시간순서대로 정렬
                List<Availability> slots = entry.getValue().stream()
                    .sorted(Comparator.comparing(Availability::getStartTime))
                    .toList();

            for (int i = 0; i <= slots.size() - requiredSlotCount; i++) {
                boolean isValid = true;
                LocalDateTime baseTime = slots.get(i).getStartTime();

                // 연속된 시간 확인
                for (int j = 1; j < requiredSlotCount; j++) {
                    if (!slots.get(i + j).getStartTime().equals(baseTime.plusMinutes(30L * j))) {
                        isValid = false;
                        break;
                    }
                } // lessonDuration의 확장성 때문에 for문으로 짰다.

                if (isValid) {
                    result.add(new AvailabilitySlotByDateResponse(tutorId, baseTime, lessonDuration));
                }
            }
        }
        return result;
    }

    @Transactional
    public List<AvailabilitySlotByTimeResponse> searchByTimeAvailableSlots(LocalDateTime startTime, int lessonDuration) {

        int requiredSlotCount = lessonDuration / 30;

        List<Availability> allSlot = availabilityRepository.findByStartTimeAndBookedIsFalseWithTutor(startTime);

        List<AvailabilitySlotByTimeResponse> result = new ArrayList<>();

        for (Availability slot : allSlot) {
            Tutor tutor = slot.getTutor();
            LocalDateTime nextTime = startTime;

            boolean isValid = true;

            for (int i = 1; i < requiredSlotCount; i++) {
                nextTime = nextTime.plusMinutes(30);
                boolean exists = availabilityRepository.existsByTutorIdAndStartTimeAndBookedIsFalse(tutor.getId(), nextTime);
                if (!exists) {
                    isValid = false;
                    break;
                }
            }

            if (isValid) {
                result.add(new AvailabilitySlotByTimeResponse(slot));
            }
        }

        return result;
    }

//    @Scheduled(cron = "0 0 3 * * *")
//    public void deleteExpiredAvailability() {
//        LocalDateTime now = LocalDateTime.now();
//        availabilityRepository.deleteByStartTimeBeforeAndBookedFalse(now);
//    }



}
