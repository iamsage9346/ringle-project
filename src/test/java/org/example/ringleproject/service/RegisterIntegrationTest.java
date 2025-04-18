package org.example.ringleproject.service;

import org.example.ringleproject.domain.LessonDuration;
import org.example.ringleproject.domain.Student;
import org.example.ringleproject.domain.Tutor;
import org.example.ringleproject.dto.AvailabilityRequest;
import org.example.ringleproject.dto.LessonRequest;
import org.example.ringleproject.dto.StudentLessonResponse;
import org.example.ringleproject.repository.LessonAvailabilityRepository;
import org.example.ringleproject.repository.LessonRepository;
import org.example.ringleproject.repository.StudentRepository;
import org.example.ringleproject.repository.TutorRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.IntStream;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
public class RegisterIntegrationTest {

    @Autowired
    StudentRepository studentRepository;
    @Autowired
    TutorRepository tutorRepository;
    @Autowired
    AvailabilityService availabilityService;
    @Autowired
    LessonService lessonService;

    List<Student> students;
    List<Tutor> tutors;
    @Autowired
    private LessonRepository lessonRepository;
    @Autowired
    private LessonAvailabilityRepository lessonAvailabilityRepository;

    @BeforeEach
    void setUp() {
        tutors = IntStream.range(0, 100)
                .mapToObj(i -> tutorRepository.save(new Tutor("튜터" + i)))
                .toList();

        Tutor tutor1 = tutors.get(0);
        for (int i = 0; i < 10; i++) {
            LocalDateTime start = LocalDateTime.of(2025, 4, 18, 9 + i, 0);
            LocalDateTime end = start.plusMinutes(30);
            availabilityService.openAvailability(new AvailabilityRequest(tutor1.getId(), start, end));
        }

        students = IntStream.range(0, 100)
                .mapToObj(i -> studentRepository.save(new Student("학생" + i)))
                .toList();
    }

    @Test
    void testStudentReservations() {
        Student student = students.get(0);
        Tutor tutor = tutors.get(0);

        for (int i = 0; i < 10; i++) {
            LocalDateTime start = LocalDateTime.of(2025, 4, 18, 9 + i, 0);
            LessonRequest request = new LessonRequest(student.getId(), tutor.getId(), start, LessonDuration.MIN30);
            lessonService.createLesson(request);
        }

        List<StudentLessonResponse> lessons = lessonService.getStudentLesson(student.getId());
        assertEquals(10, lessons.size());
    }
}
