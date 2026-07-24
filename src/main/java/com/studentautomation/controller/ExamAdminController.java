package com.studentautomation.controller;

import com.studentautomation.dto.request.ExamRequestDTO;
import com.studentautomation.dto.response.ApiResponse;
import com.studentautomation.dto.response.ExamResponseDTO;
import com.studentautomation.service.ExamService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin/exams")
@PreAuthorize("hasAnyRole('ADMIN', 'SUPER_ADMIN')")
public class ExamAdminController {

    private final ExamService examService;

    public ExamAdminController(ExamService examService) {
        this.examService = examService;
    }

    @PostMapping
    public ResponseEntity<ApiResponse<ExamResponseDTO>> createExam(
            @Valid @RequestBody ExamRequestDTO request
    ) {
        ExamResponseDTO response = examService.createExam(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Exam created successfully", response));
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<ExamResponseDTO>>> getAllExams() {
        List<ExamResponseDTO> response = examService.getAllExams();
        return ResponseEntity.ok(ApiResponse.success("Exams fetched successfully", response));
    }

    @GetMapping("/{examId}")
    public ResponseEntity<ApiResponse<ExamResponseDTO>> getExamById(
            @PathVariable Long examId
    ) {
        ExamResponseDTO response = examService.getExamById(examId);
        return ResponseEntity.ok(ApiResponse.success("Exam fetched successfully", response));
    }

    @PutMapping("/{examId}")
    public ResponseEntity<ApiResponse<ExamResponseDTO>> updateExam(
            @PathVariable Long examId,
            @Valid @RequestBody ExamRequestDTO request
    ) {
        ExamResponseDTO response = examService.updateExam(examId, request);
        return ResponseEntity.ok(ApiResponse.success("Exam updated successfully", response));
    }

    @PatchMapping("/{examId}/publish")
    public ResponseEntity<ApiResponse<ExamResponseDTO>> publishExam(
            @PathVariable Long examId
    ) {
        ExamResponseDTO response = examService.publishExam(examId);
        return ResponseEntity.ok(ApiResponse.success("Exam published successfully", response));
    }

    @PatchMapping("/{examId}/close")
    public ResponseEntity<ApiResponse<ExamResponseDTO>> closeExam(
            @PathVariable Long examId
    ) {
        ExamResponseDTO response = examService.closeExam(examId);
        return ResponseEntity.ok(ApiResponse.success("Exam closed successfully", response));
    }
}
