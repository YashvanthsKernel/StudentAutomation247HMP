package com.studentautomation.controller;

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

/**
 * REST controller for student-subject assignment operations.
 *
 * Purpose:
 * Provides APIs for assigning subjects to students,
 * retrieving active assignments, and deactivating assignments.
 *
 * Only ADMIN and SUPER_ADMIN users can manage
 * student-subject assignments.
 *
 * @author Yashvanth
 */
@RestController
@RequestMapping("/api/admin/student-subjects")
@RequiredArgsConstructor
@Validated
@PreAuthorize("hasAnyRole('ADMIN', 'SUPER_ADMIN')")
public class StudentSubjectController {

    /**
     * Service used to perform student-subject assignment operations.
     *
     * Lombok's @RequiredArgsConstructor creates constructor injection
     * for this final field.
     */
    private final StudentSubjectService studentSubjectService;

    /**
     * Assigns a subject to a student for a particular academic year.
     *
     * Endpoint:
     * POST /api/admin/student-subjects
     *
     * @param requestDTO student-subject assignment information
     * @return created or reactivated assignment information
     */
    @PostMapping
    public ResponseEntity<ApiResponse<StudentSubjectResponseDTO>>
    assignSubjectToStudent(
            @Valid
            @RequestBody
            StudentSubjectRequestDTO requestDTO
    ) {

        StudentSubjectResponseDTO assignedSubject =
                studentSubjectService.assignSubjectToStudent(
                        requestDTO
                );

        ApiResponse<StudentSubjectResponseDTO> response =
                new ApiResponse<>(
                        true,
                        "Subject assigned to student successfully",
                        assignedSubject
                );

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(response);
    }

    /**
     * Retrieves all currently active student-subject assignments.
     *
     * Endpoint:
     * GET /api/admin/student-subjects
     *
     * @return list of active student-subject assignments
     */
    @GetMapping
    public ResponseEntity<
            ApiResponse<List<StudentSubjectResponseDTO>>
            > getAllActiveAssignments() {

        List<StudentSubjectResponseDTO> assignments =
                studentSubjectService.getAllActiveAssignments();

        ApiResponse<List<StudentSubjectResponseDTO>> response =
                new ApiResponse<>(
                        true,
                        "Active student-subject assignments fetched successfully",
                        assignments
                );

        return ResponseEntity.ok(response);
    }

    /**
     * Retrieves all active subject assignments belonging
     * to a particular student.
     *
     * Endpoint:
     * GET /api/admin/student-subjects/student/{studentId}
     *
     * @param studentId database ID of the student
     * @return active assignments belonging to the student
     */
    @GetMapping("/student/{studentId}")
    public ResponseEntity<
            ApiResponse<List<StudentSubjectResponseDTO>>
            > getAssignmentsByStudentId(
            @PathVariable
            @Positive(message = "Student ID must be greater than zero")
            Long studentId
    ) {

        List<StudentSubjectResponseDTO> assignments =
                studentSubjectService.getAssignmentsByStudentId(
                        studentId
                );

        ApiResponse<List<StudentSubjectResponseDTO>> response =
                new ApiResponse<>(
                        true,
                        "Student subject assignments fetched successfully",
                        assignments
                );

        return ResponseEntity.ok(response);
    }

    /**
     * Retrieves all active student assignments belonging
     * to a particular subject.
     *
     * Endpoint:
     * GET /api/admin/student-subjects/subject/{subjectId}
     *
     * @param subjectId database ID of the subject
     * @return active assignments belonging to the subject
     */
    @GetMapping("/subject/{subjectId}")
    public ResponseEntity<
            ApiResponse<List<StudentSubjectResponseDTO>>
            > getAssignmentsBySubjectId(
            @PathVariable
            @Positive(message = "Subject ID must be greater than zero")
            Long subjectId
    ) {

        List<StudentSubjectResponseDTO> assignments =
                studentSubjectService.getAssignmentsBySubjectId(
                        subjectId
                );

        ApiResponse<List<StudentSubjectResponseDTO>> response =
                new ApiResponse<>(
                        true,
                        "Subject student assignments fetched successfully",
                        assignments
                );

        return ResponseEntity.ok(response);
    }

    /**
     * Deactivates an existing student-subject assignment.
     *
     * This performs a soft delete. The assignment remains
     * stored in the database for academic history.
     *
     * Endpoint:
     * PATCH /api/admin/student-subjects/{assignmentId}/deactivate
     *
     * @param assignmentId database ID of the assignment
     * @return deactivated assignment information
     */
    @PatchMapping("/{assignmentId}/deactivate")
    public ResponseEntity<ApiResponse<StudentSubjectResponseDTO>>
    deactivateAssignment(
            @PathVariable
            @Positive(message = "Assignment ID must be greater than zero")
            Long assignmentId
    ) {

        StudentSubjectResponseDTO deactivatedAssignment =
                studentSubjectService.deactivateAssignment(
                        assignmentId
                );

        ApiResponse<StudentSubjectResponseDTO> response =
                new ApiResponse<>(
                        true,
                        "Student-subject assignment deactivated successfully",
                        deactivatedAssignment
                );

        return ResponseEntity.ok(response);
    }
}