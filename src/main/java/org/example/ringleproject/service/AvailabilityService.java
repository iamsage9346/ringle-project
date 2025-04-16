package org.example.ringleproject.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.example.ringleproject.domain.Availability;
import org.example.ringleproject.domain.Tutor;
import org.example.ringleproject.dto.AvailabilityRequest;
import org.example.ringleproject.dto.AvailabilityResponse;
import org.example.ringleproject.repository.AvailabilityRepository;
import org.example.ringleproject.repository.TutorRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

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

}
