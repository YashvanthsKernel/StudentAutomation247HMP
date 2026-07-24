package com.studentautomation.controller;

import com.studentautomation.dto.response.ApiResponse;
import com.studentautomation.dto.response.MarkReportResponseDTO;
import com.studentautomation.dto.response.MarkResponseDTO;
import com.studentautomation.service.MarkService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin/marks")
@PreAuthorize("hasAnyRole('ADMIN', 'SUPER_ADMIN')")
public class AdminMarkController {

    private final MarkService markService;

    public AdminMarkController(MarkService markService) {
        this.markService = markService;
    }

    @GetMapping("/report")
    public ResponseEntity<ApiResponse<MarkReportResponseDTO>> getMarksReport() {
        MarkReportResponseDTO response = markService.getMarksReport();
        return ResponseEntity.ok(ApiResponse.success("Marks report fetched successfully", response));
    }

    @GetMapping("/students/{studentId}")
    public ResponseEntity<ApiResponse<List<MarkResponseDTO>>> getMarksByStudentId(
            @PathVariable Long studentId
    ) {
        List<MarkResponseDTO> response = markService.getMarksByStudentId(studentId);
        return ResponseEntity.ok(ApiResponse.success("Student marks fetched successfully", response));
    }

    @GetMapping("/classes/{classId}")
    public ResponseEntity<ApiResponse<List<MarkResponseDTO>>> getMarksByClassId(
            @PathVariable Long classId
    ) {
        List<MarkResponseDTO> response = markService.getMarksByClassId(classId);
        return ResponseEntity.ok(ApiResponse.success("Class marks fetched successfully", response));
    }

    @GetMapping("/subjects/{subjectId}")
    public ResponseEntity<ApiResponse<List<MarkResponseDTO>>> getMarksBySubjectId(
            @PathVariable Long subjectId
    ) {
        List<MarkResponseDTO> response = markService.getMarksBySubjectId(subjectId);
        return ResponseEntity.ok(ApiResponse.success("Subject marks fetched successfully", response));
    }

    @GetMapping("/failures")
    public ResponseEntity<ApiResponse<List<MarkResponseDTO>>> getFailures() {
        List<MarkResponseDTO> response = markService.getFailures();
        return ResponseEntity.ok(ApiResponse.success("Failed marks records fetched successfully", response));
    }

    @GetMapping("/top-performers")
    public ResponseEntity<ApiResponse<List<MarkResponseDTO>>> getTopPerformers() {
        List<MarkResponseDTO> response = markService.getTopPerformers();
        return ResponseEntity.ok(ApiResponse.success("Top performing marks records fetched successfully", response));
    }
}
