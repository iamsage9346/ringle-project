package org.example.ringleproject.controller;

import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.example.ringleproject.domain.Lesson;
import org.example.ringleproject.dto.LessonRequest;
import org.example.ringleproject.dto.LessonResponse;
import org.example.ringleproject.dto.StudentLessonResponse;
import org.example.ringleproject.service.LessonService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class LessonController {

    private final LessonService lessonService;

    @PostMapping("/new-lesson")
    public ResponseEntity<LessonResponse> createLesson(@RequestBody LessonRequest lessonRequest) {
        LessonResponse lessonResponse = lessonService.createLesson(lessonRequest);

        return ResponseEntity.ok(lessonResponse);
    }

    @GetMapping("/my-lesson")
    public ResponseEntity<List<StudentLessonResponse>> getMyLesson(@RequestParam Long studentId) {
        List<StudentLessonResponse> myLessons = lessonService.getStudentLesson(studentId);

        return ResponseEntity.ok(myLessons);
    }
}
