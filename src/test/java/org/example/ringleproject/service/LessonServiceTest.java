
package org.example.ringleproject.service;

import org.example.ringleproject.domain.*;
import org.example.ringleproject.dto.LessonRequest;
import org.example.ringleproject.dto.LessonResponse;
import org.example.ringleproject.dto.StudentLessonResponse;
import org.example.ringleproject.repository.AvailabilityRepository;
import org.example.ringleproject.repository.LessonAvailabilityRepository;
import org.example.ringleproject.repository.LessonRepository;
import org.example.ringleproject.repository.StudentRepository;
import org.example.ringleproject.repository.TutorRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class LessonServiceTest {

    @InjectMocks
    private LessonService lessonService;

    @Mock
    private LessonRepository lessonRepository;
    @Mock
    private AvailabilityRepository availabilityRepository;
    @Mock
    private StudentRepository studentRepository;
    @Mock
    private TutorRepository tutorRepository;
    @Mock
    private LessonAvailabilityRepository lessonAvailabilityRepository;

    @Test
    void createLesson_success_60MIN() {
        // given
        Long studentId = 1L;
        Long tutorId = 2L;
        LocalDateTime start = LocalDateTime.of(2025, 4, 20, 10, 0);
        LocalDateTime end = start.plusMinutes(30);

        Student student = new Student("Jay");
        student.setId(studentId);

        Tutor tutor = new Tutor("Anna");
        tutor.setId(tutorId);

        LessonRequest request = new LessonRequest(studentId, tutorId, start, LessonDuration.MIN60);

        Availability slot1 = new Availability();
        slot1.setStartTime(start);
        slot1.setEndTime(end);
        slot1.setTutor(tutor);
        slot1.setBooked(false);

        Availability slot2 = new Availability();
        slot2.setStartTime(start.plusMinutes(30));
        slot2.setEndTime(end.plusMinutes(30));
        slot2.setTutor(tutor);
        slot2.setBooked(false);

        when(studentRepository.findById(studentId)).thenReturn(Optional.of(student));
        when(tutorRepository.findById(tutorId)).thenReturn(Optional.of(tutor));
        when(availabilityRepository.existsByTutorIdAndStartTimeAndBookedIsFalse(eq(tutorId), any()))
                .thenReturn(true);
        when(availabilityRepository.findByTutorIdAndStartTime(eq(tutorId), any()))
                .thenReturn(slot1, slot2);

        // when
        LessonResponse response = lessonService.createLesson(request);

        // then
        assertThat(response.getStudentId()).isEqualTo(1L);
        assertThat(response.getStartTime()).isEqualTo(start);
        assertThat(response.getLessonDuration()).isEqualTo(LessonDuration.MIN60);

        verify(lessonRepository, times(1)).save(any());
        verify(lessonAvailabilityRepository).saveAll(argThat(list ->
                list instanceof List<?> && ((List<?>) list).size() == 2
        ));
    }

    @Test
    void createLesson_success_30MIN() {
        // given
        Long studentId = 1L;
        Long tutorId = 2L;
        LocalDateTime start = LocalDateTime.of(2025, 4, 20, 10, 0);

        Student student = new Student("Jay");
        student.setId(studentId);

        Tutor tutor = new Tutor("Anna");
        tutor.setId(tutorId);

        LessonRequest request = new LessonRequest(studentId, tutorId, start, LessonDuration.MIN30);

        Availability slot = new Availability();
        slot.setStartTime(start);
        slot.setEndTime(start.plusMinutes(30));
        slot.setTutor(tutor);
        slot.setBooked(false);

        when(studentRepository.findById(studentId)).thenReturn(Optional.of(student));
        when(tutorRepository.findById(tutorId)).thenReturn(Optional.of(tutor));
        when(availabilityRepository.existsByTutorIdAndStartTimeAndBookedIsFalse(tutorId, start)).thenReturn(true);
        when(availabilityRepository.findByTutorIdAndStartTime(tutorId, start)).thenReturn(slot);

        // when
        LessonResponse response = lessonService.createLesson(request);

        // then
        assertThat(response.getStudentId()).isEqualTo(1L);
        assertThat(response.getStartTime()).isEqualTo(start);
        assertThat(response.getLessonDuration()).isEqualTo(LessonDuration.MIN30);

        verify(lessonRepository).save(any());
        verify(lessonAvailabilityRepository).saveAll(argThat(list ->
                list instanceof List<?> && ((List<?>) list).size() == 1
        ));
    }

    @Test
    void createLesson_fail_ifStudentNotFound() {
        // given
        Long studentId = 1L;
        Long tutorId = 2L;
        LessonRequest request = new LessonRequest(studentId, tutorId, LocalDateTime.now(), LessonDuration.MIN30);

        when(studentRepository.findById(studentId)).thenReturn(Optional.empty());

        // when, then
        assertThatThrownBy(() -> lessonService.createLesson(request))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Student not found");
    }

    @Test
    void createLesson_fail_ifTutorNotFound() {
        // given
        Long studentId = 1L;
        Long tutorId = 2L;
        Student student = new Student("Jay"); student.setId(studentId);
        LessonRequest request = new LessonRequest(studentId, tutorId, LocalDateTime.now(), LessonDuration.MIN30);

        when(studentRepository.findById(studentId)).thenReturn(Optional.of(student));
        when(tutorRepository.findById(tutorId)).thenReturn(Optional.empty());

        // when, then
        assertThatThrownBy(() -> lessonService.createLesson(request))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Tutor not found");
    }

    @Test
    void createLesson_fail_ifAvailabilityMissing() {
        // given
        Long studentId = 1L;
        Long tutorId = 2L;
        LocalDateTime start = LocalDateTime.of(2025, 4, 20, 10, 0);
        LessonRequest request = new LessonRequest(studentId, tutorId, start, LessonDuration.MIN60);

        Student student = new Student("Jay"); student.setId(studentId);
        Tutor tutor = new Tutor("Anna"); tutor.setId(tutorId);

        when(studentRepository.findById(studentId)).thenReturn(Optional.of(student));
        when(tutorRepository.findById(tutorId)).thenReturn(Optional.of(tutor));
        when(availabilityRepository.existsByTutorIdAndStartTimeAndBookedIsFalse(eq(tutorId), any()))
                .thenReturn(true, false);

        // when, then
        assertThatThrownBy(() -> lessonService.createLesson(request))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Tutor is not available at this time");
    }

    @Test
    void getStudentLesson_success() {
        // given
        Long studentId = 1L;
        Student student = new Student("Jay"); student.setId(studentId);
        Tutor tutor = new Tutor("Anna"); tutor.setId(2L);

        Lesson lesson1 = Lesson.builder()
                .student(student)
                .tutor(tutor)
                .startTime(LocalDateTime.of(2025, 4, 20, 10, 0))
                .lessonDuration(LessonDuration.MIN30)
                .build();

        Lesson lesson2 = Lesson.builder()
                .student(student)
                .tutor(tutor)
                .startTime(LocalDateTime.of(2025, 4, 21, 12, 0))
                .lessonDuration(LessonDuration.MIN60)
                .build();

        when(lessonRepository.findByStudentIdWithTutor(studentId))
                .thenReturn(List.of(lesson1, lesson2));

        // when
        List<StudentLessonResponse> result = lessonService.getStudentLesson(studentId);

        // then
        assertThat(result).hasSize(2);
        assertThat(result.get(0).getTutorName()).isEqualTo("Anna");
        assertThat(result.get(1).getLessonDuration()).isEqualTo(LessonDuration.MIN60);
    }
}
