package org.example.ringleproject.controller;

import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
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

    @Operation(summary = "시간대, 수업 길이, 튜터로 새로운 수업 신청", description = "학생이 시간대, 수업 길이, 튜터로 새로운 수업을 신청합니다.")
    @PostMapping("/new-lesson")
    public ResponseEntity<LessonResponse> createLesson(@RequestBody LessonRequest lessonRequest) {
        LessonResponse lessonResponse = lessonService.createLesson(lessonRequest);

        return ResponseEntity.ok(lessonResponse);
    }

    @Operation(summary = "신청한 수업 조회", description = "학생이 신청한 수업을조회합니다.")
    @GetMapping("/my-lesson")
    public ResponseEntity<List<StudentLessonResponse>> getMyLesson(@RequestParam Long studentId) {
        List<StudentLessonResponse> myLessons = lessonService.getStudentLesson(studentId);

        return ResponseEntity.ok(myLessons);
    }

}
