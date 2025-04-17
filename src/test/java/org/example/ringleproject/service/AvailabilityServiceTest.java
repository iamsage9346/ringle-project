
package org.example.ringleproject.service;

import org.example.ringleproject.domain.Availability;
import org.example.ringleproject.domain.Tutor;
import org.example.ringleproject.dto.AvailabilityRequest;
import org.example.ringleproject.dto.AvailabilityResponse;
import org.example.ringleproject.dto.AvailabilitySlotByDateResponse;
import org.example.ringleproject.dto.AvailabilitySlotByTimeResponse;
import org.example.ringleproject.repository.AvailabilityRepository;
import org.example.ringleproject.repository.TutorRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AvailabilityServiceTest {

    @InjectMocks
    private AvailabilityService availabilityService;

    @Mock
    private TutorRepository tutorRepository;

    @Mock
    private AvailabilityRepository availabilityRepository;

    @Test
    void openAvailability_success() {
        Long tutorId = 1L;
        LocalDateTime start = LocalDateTime.of(2025, 4, 18, 10, 0);
        LocalDateTime end = start.plusMinutes(30);
        AvailabilityRequest request = new AvailabilityRequest(tutorId, start, end);

        Tutor tutor = new Tutor("Jane");
        tutor.setId(tutorId);
        when(tutorRepository.findById(tutorId)).thenReturn(Optional.of(tutor));

        AvailabilityResponse response = availabilityService.openAvailability(request);

        assertThat(response.getTutorId()).isEqualTo(tutorId);
        assertThat(response.getStartTime()).isEqualTo(start);
        verify(availabilityRepository).save(any());
    }

    @Test
    void openAvailability_startTime_error() {
        AvailabilityRequest request = new AvailabilityRequest(
                1L,
                LocalDateTime.of(2025, 4, 18, 10, 15),
                LocalDateTime.of(2025, 4, 18, 10, 45)
        );

        assertThatThrownBy(() -> availabilityService.openAvailability(request))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Start time must be on the hour or half-hour.");
    }

    @Test
    void openAvailability_endTime_error() {
        AvailabilityRequest request = new AvailabilityRequest(
                1L,
                LocalDateTime.of(2025, 4, 18, 10, 0),
                LocalDateTime.of(2025, 4, 18, 10, 45)
        );

        assertThatThrownBy(() -> availabilityService.openAvailability(request))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Lesson must be exactly 30 minutes.");
    }

    @Test
    void openAvailability_notExistTutor() {
        Long tutorId = 99L;
        LocalDateTime start = LocalDateTime.of(2025, 4, 18, 11, 0);
        LocalDateTime end = start.plusMinutes(30);
        AvailabilityRequest request = new AvailabilityRequest(tutorId, start, end);

        when(tutorRepository.findById(tutorId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> availabilityService.openAvailability(request))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Tutor does not exist.");
    }

    @Test
    void deleteAvailability_success() {
        Long tutorId = 1L;
        LocalDateTime startTime = LocalDateTime.of(2025, 4, 19, 10, 0);

        Availability availability = new Availability();
        availability.setId(100L);

        when(availabilityRepository.existsByTutorIdAndStartTime(tutorId, startTime)).thenReturn(true);
        when(availabilityRepository.findByTutorIdAndStartTime(tutorId, startTime)).thenReturn(availability);

        availabilityService.deleteAvailability(tutorId, startTime);

        verify(availabilityRepository).deleteById(100L);
    }

    @Test
    void deleteAvailability_fail_if_not_exist() {
        Long tutorId = 2L;
        LocalDateTime startTime = LocalDateTime.of(2025, 4, 19, 12, 30);

        when(availabilityRepository.existsByTutorIdAndStartTime(tutorId, startTime)).thenReturn(false);

        assertThatThrownBy(() -> availabilityService.deleteAvailability(tutorId, startTime))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("The class available time zone does not exist.");
    }


    @Test
    void searchByDateAvailableSlots_success() {
        Tutor tutor = new Tutor("Amy");
        tutor.setId(1L);

        LocalDateTime t1 = LocalDateTime.of(2025, 4, 20, 10, 0);
        LocalDateTime t2 = t1.plusMinutes(30);
        Availability a1 = new Availability(); a1.setTutor(tutor); a1.setStartTime(t1); a1.setEndTime(t2);
        Availability a2 = new Availability(); a2.setTutor(tutor); a2.setStartTime(t2); a2.setEndTime(t2.plusMinutes(30));

        when(availabilityRepository.findAvailableWithTutor(t1.toLocalDate().atStartOfDay(), t1.toLocalDate().plusDays(1).atStartOfDay()))
                .thenReturn(Arrays.asList(a1, a2));

        List<AvailabilitySlotByDateResponse> result = availabilityService.searchByDateAvailableSlots(t1.toLocalDate(), t1.toLocalDate(), 60);

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getStartTime()).isEqualTo(t1);
        assertThat(result.get(0).getTutorId()).isEqualTo(tutor.getId());
    }

    @Test
    void searchByDateAvailableSlots_ignoreNonContinuousSlots() {
        Tutor tutor = new Tutor("Amy");
        tutor.setId(1L);

        LocalDateTime t1 = LocalDateTime.of(2025, 4, 20, 10, 0);
        LocalDateTime t2 = LocalDateTime.of(2025, 4, 20, 11, 0);
        Availability a1 = new Availability(); a1.setTutor(tutor); a1.setStartTime(t1); a1.setEndTime(t1.plusMinutes(30));
        Availability a2 = new Availability(); a2.setTutor(tutor); a2.setStartTime(t2); a2.setEndTime(t2.plusMinutes(30));

        when(availabilityRepository.findAvailableWithTutor(t1.toLocalDate().atStartOfDay(), t1.toLocalDate().plusDays(1).atStartOfDay()))
                .thenReturn(Arrays.asList(a1, a2));

        List<AvailabilitySlotByDateResponse> result = availabilityService.searchByDateAvailableSlots(t1.toLocalDate(), t1.toLocalDate(), 60);

        assertThat(result).isEmpty();
    }

    @Test
    void searchByTimeAvailableSlots_success() {
        Tutor tutor = new Tutor("Ben");
        tutor.setId(2L);

        LocalDateTime base = LocalDateTime.of(2025, 4, 21, 9, 0);
        Availability a1 = new Availability(); a1.setTutor(tutor); a1.setStartTime(base); a1.setEndTime(base.plusMinutes(30));

        when(availabilityRepository.findByStartTimeAndBookedIsFalseWithTutor(base))
                .thenReturn(Collections.singletonList(a1));
        when(availabilityRepository.existsByTutorIdAndStartTimeAndBookedIsFalse(tutor.getId(), base.plusMinutes(30)))
                .thenReturn(true);

        List<AvailabilitySlotByTimeResponse> result = availabilityService.searchByTimeAvailableSlots(base, 60);

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getTutorId()).isEqualTo(tutor.getId());
    }

    @Test
    void searchByTimeAvailableSlots_ignorePartialAvailability() {
        Tutor tutor = new Tutor("Ben");
        tutor.setId(2L);

        LocalDateTime base = LocalDateTime.of(2025, 4, 21, 9, 0);
        Availability a1 = new Availability(); a1.setTutor(tutor); a1.setStartTime(base); a1.setEndTime(base.plusMinutes(30));

        when(availabilityRepository.findByStartTimeAndBookedIsFalseWithTutor(base))
                .thenReturn(Collections.singletonList(a1));
        when(availabilityRepository.existsByTutorIdAndStartTimeAndBookedIsFalse(tutor.getId(), base.plusMinutes(30)))
                .thenReturn(false);

        List<AvailabilitySlotByTimeResponse> result = availabilityService.searchByTimeAvailableSlots(base, 60);

        assertThat(result).isEmpty();
    }
}
