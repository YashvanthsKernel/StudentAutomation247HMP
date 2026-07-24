package com.studentautomation.controller;

import com.studentautomation.dto.request.BulkMarkRequestDTO;
import com.studentautomation.dto.request.MarkRequestDTO;
import com.studentautomation.dto.response.ApiResponse;
import com.studentautomation.dto.response.MarkResponseDTO;
import com.studentautomation.service.MarkService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/teacher/marks")
@PreAuthorize("hasRole('TEACHER')")
public class TeacherMarkController {

    private final MarkService markService;

    public TeacherMarkController(MarkService markService) {
        this.markService = markService;
    }

    @PostMapping
    public ResponseEntity<ApiResponse<MarkResponseDTO>> recordMark(
            @Valid @RequestBody MarkRequestDTO request,
            Authentication authentication
    ) {
        MarkResponseDTO response = markService.recordMark(request, authentication.getName());
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Mark recorded successfully", response));
    }

    @PostMapping("/bulk")
    public ResponseEntity<ApiResponse<List<MarkResponseDTO>>> recordBulkMarks(
            @Valid @RequestBody BulkMarkRequestDTO request,
            Authentication authentication
    ) {
        List<MarkResponseDTO> response = markService.recordBulkMarks(request, authentication.getName());
        return ResponseEntity.ok(ApiResponse.success("Bulk marks recorded successfully", response));
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<MarkResponseDTO>>> getMarksForTeacher(
            @RequestParam Long examId,
            @RequestParam Long subjectId,
            @RequestParam(required = false) String section,
            Authentication authentication
    ) {
        List<MarkResponseDTO> response = markService.getMarksForTeacher(examId, subjectId, section, authentication.getName());
        return ResponseEntity.ok(ApiResponse.success("Marks fetched successfully", response));
    }

    @GetMapping("/{markId}")
    public ResponseEntity<ApiResponse<MarkResponseDTO>> getMarkById(
            @PathVariable Long markId
    ) {
        MarkResponseDTO response = markService.getMarkById(markId);
        return ResponseEntity.ok(ApiResponse.success("Mark fetched successfully", response));
    }

    @PutMapping("/{markId}")
    public ResponseEntity<ApiResponse<MarkResponseDTO>> updateMark(
            @PathVariable Long markId,
            @Valid @RequestBody MarkRequestDTO request,
            Authentication authentication
    ) {
        MarkResponseDTO response = markService.updateMark(markId, request, authentication.getName());
        return ResponseEntity.ok(ApiResponse.success("Mark updated successfully", response));
    }

    @DeleteMapping("/{markId}")
    public ResponseEntity<ApiResponse<Object>> deleteMark(
            @PathVariable Long markId,
            Authentication authentication
    ) {
        markService.deleteMark(markId, authentication.getName());
        return ResponseEntity.ok(ApiResponse.success("Mark deleted successfully", null));
    }

    @PostMapping("/publish")
    public ResponseEntity<ApiResponse<Object>> publishMarks(
            @RequestParam Long examId,
            @RequestParam Long subjectId,
            Authentication authentication
    ) {
        markService.publishMarksForExamAndSubject(examId, subjectId, authentication.getName());
        return ResponseEntity.ok(ApiResponse.success("Marks published successfully", null));
    }
}
