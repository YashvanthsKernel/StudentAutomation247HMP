package com.studentautomation.controller;

import com.studentautomation.dto.request.ExamScheduleRequestDTO;
import com.studentautomation.dto.response.ApiResponse;
import com.studentautomation.dto.response.ExamScheduleResponseDTO;
import com.studentautomation.service.ExamScheduleService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class ExamScheduleController {

    private final ExamScheduleService examScheduleService;

    public ExamScheduleController(ExamScheduleService examScheduleService) {
        this.examScheduleService = examScheduleService;
    }

    @PostMapping("/api/admin/exam-schedules")
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_ADMIN')")
    public ResponseEntity<ApiResponse<ExamScheduleResponseDTO>> createExamSchedule(
            @Valid @RequestBody ExamScheduleRequestDTO request
    ) {
        ExamScheduleResponseDTO response = examScheduleService.createExamSchedule(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Exam schedule created successfully", response));
    }

    @GetMapping("/api/admin/exam-schedules")
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_ADMIN')")
    public ResponseEntity<ApiResponse<List<ExamScheduleResponseDTO>>> getAllExamSchedules() {
        List<ExamScheduleResponseDTO> response = examScheduleService.getAllExamSchedules();
        return ResponseEntity.ok(ApiResponse.success("Exam schedules fetched successfully", response));
    }

    @PutMapping("/api/admin/exam-schedules/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_ADMIN')")
    public ResponseEntity<ApiResponse<ExamScheduleResponseDTO>> updateExamSchedule(
            @PathVariable Long id,
            @Valid @RequestBody ExamScheduleRequestDTO request
    ) {
        ExamScheduleResponseDTO response = examScheduleService.updateExamSchedule(id, request);
        return ResponseEntity.ok(ApiResponse.success("Exam schedule updated successfully", response));
    }

    @DeleteMapping("/api/admin/exam-schedules/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_ADMIN')")
    public ResponseEntity<ApiResponse<Object>> deleteExamSchedule(
            @PathVariable Long id
    ) {
        examScheduleService.deleteExamSchedule(id);
        return ResponseEntity.ok(ApiResponse.success("Exam schedule deleted successfully", null));
    }

    @GetMapping("/api/student/exam-schedules/me")
    @PreAuthorize("hasRole('STUDENT')")
    public ResponseEntity<ApiResponse<List<ExamScheduleResponseDTO>>> getMyStudentExamSchedules(
            Authentication authentication
    ) {
        List<ExamScheduleResponseDTO> response = examScheduleService.getMyStudentExamSchedules(authentication.getName());
        return ResponseEntity.ok(ApiResponse.success("Student exam schedules fetched successfully", response));
    }

    @GetMapping("/api/teacher/exam-schedules/me")
    @PreAuthorize("hasRole('TEACHER')")
    public ResponseEntity<ApiResponse<List<ExamScheduleResponseDTO>>> getMyTeacherExamSchedules(
            Authentication authentication
    ) {
        List<ExamScheduleResponseDTO> response = examScheduleService.getMyTeacherExamSchedules(authentication.getName());
        return ResponseEntity.ok(ApiResponse.success("Teacher exam schedules fetched successfully", response));
    }
}
