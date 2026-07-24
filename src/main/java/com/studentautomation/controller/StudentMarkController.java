package com.studentautomation.controller;

import com.studentautomation.dto.response.ApiResponse;
import com.studentautomation.dto.response.MarkResponseDTO;
import com.studentautomation.dto.response.StudentMarkSummaryResponseDTO;
import com.studentautomation.service.MarkService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/student/marks")
@PreAuthorize("hasRole('STUDENT')")
public class StudentMarkController {

    private final MarkService markService;

    public StudentMarkController(MarkService markService) {
        this.markService = markService;
    }

    @GetMapping("/me")
    public ResponseEntity<ApiResponse<List<MarkResponseDTO>>> getMyMarks(
            Authentication authentication
    ) {
        List<MarkResponseDTO> response = markService.getMyMarks(authentication.getName());
        return ResponseEntity.ok(ApiResponse.success("Student marks fetched successfully", response));
    }

    @GetMapping("/me/exams/{examId}")
    public ResponseEntity<ApiResponse<List<MarkResponseDTO>>> getMyMarksByExam(
            @PathVariable Long examId,
            Authentication authentication
    ) {
        List<MarkResponseDTO> response = markService.getMyMarksByExam(examId, authentication.getName());
        return ResponseEntity.ok(ApiResponse.success("Exam marks fetched successfully", response));
    }

    @GetMapping("/me/subjects/{subjectId}")
    public ResponseEntity<ApiResponse<List<MarkResponseDTO>>> getMyMarksBySubject(
            @PathVariable Long subjectId,
            Authentication authentication
    ) {
        List<MarkResponseDTO> response = markService.getMyMarksBySubject(subjectId, authentication.getName());
        return ResponseEntity.ok(ApiResponse.success("Subject marks fetched successfully", response));
    }

    @GetMapping("/me/summary")
    public ResponseEntity<ApiResponse<StudentMarkSummaryResponseDTO>> getMyMarksSummary(
            Authentication authentication
    ) {
        StudentMarkSummaryResponseDTO response = markService.getMyMarksSummary(authentication.getName());
        return ResponseEntity.ok(ApiResponse.success("Marks summary fetched successfully", response));
    }
}
