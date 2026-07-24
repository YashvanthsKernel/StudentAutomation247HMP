package com.studentautomation.controller;

import com.studentautomation.dto.request.BulkTeacherSubjectRequestDTO;
import com.studentautomation.dto.request.TeacherSubjectRequestDTO;
import com.studentautomation.dto.response.ApiResponse;
import com.studentautomation.dto.response.TeacherSubjectResponseDTO;
import com.studentautomation.service.TeacherSubjectService;
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
@RequestMapping("/api/admin/teacher-subjects")
@RequiredArgsConstructor
@Validated
@PreAuthorize("hasAnyRole('ADMIN', 'SUPER_ADMIN')")
public class TeacherSubjectController {

    private final TeacherSubjectService teacherSubjectService;

    @PostMapping
    public ResponseEntity<ApiResponse<TeacherSubjectResponseDTO>> assignSubjectToTeacher(
            @Valid @RequestBody TeacherSubjectRequestDTO requestDTO
    ) {
        TeacherSubjectResponseDTO assignedSubject = teacherSubjectService.assignSubjectToTeacher(requestDTO);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Subject assigned to teacher successfully", assignedSubject));
    }

    @PostMapping("/bulk")
    public ResponseEntity<ApiResponse<List<TeacherSubjectResponseDTO>>> bulkAssignTeacherSubjects(
            @Valid @RequestBody BulkTeacherSubjectRequestDTO request
    ) {
        List<TeacherSubjectResponseDTO> response = teacherSubjectService.bulkAssignTeacherSubjects(request);
        return ResponseEntity.ok(ApiResponse.success("Bulk teacher subjects assigned successfully", response));
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<TeacherSubjectResponseDTO>>> getAllActiveAssignments() {
        List<TeacherSubjectResponseDTO> assignments = teacherSubjectService.getAllActiveAssignments();
        return ResponseEntity.ok(ApiResponse.success("Active teacher-subject assignments fetched successfully", assignments));
    }

    @GetMapping("/{assignmentId}")
    public ResponseEntity<ApiResponse<TeacherSubjectResponseDTO>> getAssignmentById(
            @PathVariable @Positive Long assignmentId
    ) {
        TeacherSubjectResponseDTO response = teacherSubjectService.getAssignmentById(assignmentId);
        return ResponseEntity.ok(ApiResponse.success("Assignment fetched successfully", response));
    }

    @GetMapping("/teacher/{teacherId}")
    public ResponseEntity<ApiResponse<List<TeacherSubjectResponseDTO>>> getAssignmentsByTeacherId(
            @PathVariable @Positive Long teacherId
    ) {
        List<TeacherSubjectResponseDTO> assignments = teacherSubjectService.getAssignmentsByTeacherId(teacherId);
        return ResponseEntity.ok(ApiResponse.success("Teacher subject assignments fetched successfully", assignments));
    }

    @GetMapping("/subject/{subjectId}")
    public ResponseEntity<ApiResponse<List<TeacherSubjectResponseDTO>>> getAssignmentsBySubjectId(
            @PathVariable @Positive Long subjectId
    ) {
        List<TeacherSubjectResponseDTO> assignments = teacherSubjectService.getAssignmentsBySubjectId(subjectId);
        return ResponseEntity.ok(ApiResponse.success("Subject teacher assignments fetched successfully", assignments));
    }

    @PatchMapping("/{assignmentId}/activate")
    public ResponseEntity<ApiResponse<TeacherSubjectResponseDTO>> activateAssignment(
            @PathVariable @Positive Long assignmentId
    ) {
        TeacherSubjectResponseDTO activatedAssignment = teacherSubjectService.activateAssignment(assignmentId);
        return ResponseEntity.ok(ApiResponse.success("Teacher-subject assignment activated successfully", activatedAssignment));
    }

    @PatchMapping("/{assignmentId}/deactivate")
    public ResponseEntity<ApiResponse<TeacherSubjectResponseDTO>> deactivateAssignment(
            @PathVariable @Positive Long assignmentId
    ) {
        TeacherSubjectResponseDTO deactivatedAssignment = teacherSubjectService.deactivateAssignment(assignmentId);
        return ResponseEntity.ok(ApiResponse.success("Teacher-subject assignment deactivated successfully", deactivatedAssignment));
    }
}