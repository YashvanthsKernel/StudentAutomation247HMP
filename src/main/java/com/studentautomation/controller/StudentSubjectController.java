package com.studentautomation.controller;

import com.studentautomation.dto.request.AssignClassSubjectRequestDTO;
import com.studentautomation.dto.request.BulkStudentSubjectRequestDTO;
import com.studentautomation.dto.request.StudentSubjectRequestDTO;
import com.studentautomation.dto.response.ApiResponse;
import com.studentautomation.dto.response.StudentSubjectResponseDTO;
import com.studentautomation.service.StudentSubjectService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin/student-subjects")
@RequiredArgsConstructor
@Validated
@PreAuthorize("hasAnyRole('ADMIN', 'SUPER_ADMIN')")
public class StudentSubjectController {

    private final StudentSubjectService studentSubjectService;

    @PostMapping
    public ResponseEntity<ApiResponse<StudentSubjectResponseDTO>> assignSubjectToStudent(
            @Valid @RequestBody StudentSubjectRequestDTO requestDTO
    ) {
        StudentSubjectResponseDTO assignedSubject = studentSubjectService.assignSubjectToStudent(requestDTO);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Subject assigned to student successfully", assignedSubject));
    }

    @PostMapping("/bulk")
    public ResponseEntity<ApiResponse<List<StudentSubjectResponseDTO>>> bulkAssignStudentSubjects(
            @Valid @RequestBody BulkStudentSubjectRequestDTO request
    ) {
        List<StudentSubjectResponseDTO> response = studentSubjectService.bulkAssignStudentSubjects(request);
        return ResponseEntity.ok(ApiResponse.success("Bulk student subjects assigned successfully", response));
    }

    @PostMapping("/assign-class")
    public ResponseEntity<ApiResponse<List<StudentSubjectResponseDTO>>> assignClassToSubjects(
            @Valid @RequestBody AssignClassSubjectRequestDTO request
    ) {
        List<StudentSubjectResponseDTO> response = studentSubjectService.assignClassToSubjects(request);
        return ResponseEntity.ok(ApiResponse.success("Class assigned to subjects successfully", response));
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<StudentSubjectResponseDTO>>> getAllActiveAssignments() {
        List<StudentSubjectResponseDTO> assignments = studentSubjectService.getAllActiveAssignments();
        return ResponseEntity.ok(ApiResponse.success("Active student-subject assignments fetched successfully", assignments));
    }

    @GetMapping("/{assignmentId}")
    public ResponseEntity<ApiResponse<StudentSubjectResponseDTO>> getAssignmentById(
            @PathVariable @Positive Long assignmentId
    ) {
        StudentSubjectResponseDTO response = studentSubjectService.getAssignmentById(assignmentId);
        return ResponseEntity.ok(ApiResponse.success("Assignment fetched successfully", response));
    }

    @GetMapping("/student/{studentId}")
    public ResponseEntity<ApiResponse<List<StudentSubjectResponseDTO>>> getAssignmentsByStudentId(
            @PathVariable @Positive Long studentId
    ) {
        List<StudentSubjectResponseDTO> assignments = studentSubjectService.getAssignmentsByStudentId(studentId);
        return ResponseEntity.ok(ApiResponse.success("Student subject assignments fetched successfully", assignments));
    }

    @GetMapping("/subject/{subjectId}")
    public ResponseEntity<ApiResponse<List<StudentSubjectResponseDTO>>> getAssignmentsBySubjectId(
            @PathVariable @Positive Long subjectId
    ) {
        List<StudentSubjectResponseDTO> assignments = studentSubjectService.getAssignmentsBySubjectId(subjectId);
        return ResponseEntity.ok(ApiResponse.success("Subject student assignments fetched successfully", assignments));
    }

    @PatchMapping("/{assignmentId}/activate")
    public ResponseEntity<ApiResponse<StudentSubjectResponseDTO>> activateAssignment(
            @PathVariable @Positive Long assignmentId
    ) {
        StudentSubjectResponseDTO activatedAssignment = studentSubjectService.activateAssignment(assignmentId);
        return ResponseEntity.ok(ApiResponse.success("Student-subject assignment activated successfully", activatedAssignment));
    }

    @PatchMapping("/{assignmentId}/deactivate")
    public ResponseEntity<ApiResponse<StudentSubjectResponseDTO>> deactivateAssignment(
            @PathVariable @Positive Long assignmentId
    ) {
        StudentSubjectResponseDTO deactivatedAssignment = studentSubjectService.deactivateAssignment(assignmentId);
        return ResponseEntity.ok(ApiResponse.success("Student-subject assignment deactivated successfully", deactivatedAssignment));
    }
}