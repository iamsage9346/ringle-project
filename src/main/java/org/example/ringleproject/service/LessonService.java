package org.example.ringleproject.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.example.ringleproject.domain.Availability;
import org.example.ringleproject.domain.Lesson;
import org.example.ringleproject.domain.LessonAvailability;
import org.example.ringleproject.domain.LessonDuration;
import org.example.ringleproject.domain.Student;
import org.example.ringleproject.domain.Tutor;
import org.example.ringleproject.dto.LessonRequest;
import org.example.ringleproject.dto.LessonResponse;
import org.example.ringleproject.dto.StudentLessonResponse;
import org.example.ringleproject.repository.AvailabilityRepository;
import org.example.ringleproject.repository.LessonAvailabilityRepository;
import org.example.ringleproject.repository.LessonRepository;
import org.example.ringleproject.repository.StudentRepository;
import org.example.ringleproject.repository.TutorRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class LessonService {

    private final LessonRepository lessonRepository;
    private final AvailabilityRepository availabilityRepository;
    private final StudentRepository studentRepository;
    private final TutorRepository tutorRepository;
    private final LessonAvailabilityRepository lessonAvailabilityRepository;

    @Transactional
    public LessonResponse createLesson(LessonRequest lessonRequest) {
        Student student = studentRepository.findById(lessonRequest.getStudentId())
                .orElseThrow(() -> new IllegalArgumentException("Student not found"));

        Tutor tutor = tutorRepository.findById(lessonRequest.getTutorId())
                .orElseThrow(() -> new IllegalArgumentException("Tutor not found"));

        LocalDateTime startTime = lessonRequest.getStartTime();
        LessonDuration durationEnum = lessonRequest.getLessonDuration();
        int minutes = durationEnum.getMinutes();

        int requiredSlots = minutes / 30;

        for (int i = 0; i < requiredSlots; i++) {
            LocalDateTime time = startTime.plusMinutes(30L * i);
            boolean available = availabilityRepository.existsByTutorIdAndStartTimeAndBookedIsFalse(tutor.getId(), time);
            if (!available) {
                throw new IllegalArgumentException("Tutor is not available at this time");
            }
        }

        for (int i = 0; i < requiredSlots; i++) {
            LocalDateTime time = startTime.plusMinutes(30L * i);
            Availability slot = availabilityRepository.findByTutorIdAndStartTime(tutor.getId(), time);
            slot.setBooked(true);
        }

        Lesson lesson = Lesson.builder()
                .student(student)
                .tutor(tutor)
                .startTime(startTime)
                .lessonDuration(durationEnum)
                .build();

        lessonRepository.save(lesson);

        List<LessonAvailability> links = new ArrayList<>();
        for (int i = 0; i < requiredSlots; i++) {
            LocalDateTime time = startTime.plusMinutes(30L * i);
            Availability slot = availabilityRepository.findByTutorIdAndStartTime(tutor.getId(), time);

            // 연결 엔티티 생성
            LessonAvailability link = new LessonAvailability(lesson, slot);
            links.add(link);
        }

        lessonAvailabilityRepository.saveAll(links);

        return new LessonResponse(lesson);
    }

    public List<StudentLessonResponse> getStudentLesson(Long studentId) {
        List<Lesson> lessons = lessonRepository.findByStudentIdWithTutor(studentId);

        return lessons.stream()
                .map(StudentLessonResponse::new)
                .toList();
    }

}
