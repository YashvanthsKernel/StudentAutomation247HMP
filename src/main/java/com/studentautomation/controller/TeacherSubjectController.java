package com.studentautomation.controller;

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

/**
 * REST controller for teacher-subject assignment operations.
 *
 * Purpose:
 * Provides APIs for assigning subjects to teachers,
 * retrieving active assignments, and deactivating assignments.
 *
 * Only ADMIN and SUPER_ADMIN users are allowed to manage
 * teacher-subject assignments.
 *
 * @author Yashvanth
 */
@RestController
@RequestMapping("/api/admin/teacher-subjects")
@RequiredArgsConstructor
@Validated
@PreAuthorize("hasAnyRole('ADMIN', 'SUPER_ADMIN')")
public class TeacherSubjectController {

    /**
     * Service used to perform teacher-subject assignment operations.
     *
     * Lombok's @RequiredArgsConstructor automatically creates
     * constructor injection for this final field.
     */
    private final TeacherSubjectService teacherSubjectService;

    /**
     * Assigns a subject to a teacher for a particular section
     * and academic year.
     *
     * Endpoint:
     * POST /api/admin/teacher-subjects
     *
     * Example request:
     * {
     *   "teacherId": 1,
     *   "subjectId": 1,
     *   "section": "A",
     *   "academicYear": "2026-2027"
     * }
     *
     * @param requestDTO teacher-subject assignment information
     * @return created or reactivated assignment information
     */
    @PostMapping
    public ResponseEntity<ApiResponse<TeacherSubjectResponseDTO>>
    assignSubjectToTeacher(
            @Valid
            @RequestBody
            TeacherSubjectRequestDTO requestDTO
    ) {

        TeacherSubjectResponseDTO assignedSubject =
                teacherSubjectService.assignSubjectToTeacher(
                        requestDTO
                );

        ApiResponse<TeacherSubjectResponseDTO> response =
                new ApiResponse<>(
                        true,
                        "Subject assigned to teacher successfully",
                        assignedSubject
                );

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(response);
    }

    /**
     * Retrieves all currently active teacher-subject assignments.
     *
     * Endpoint:
     * GET /api/admin/teacher-subjects
     *
     * @return list of active teacher-subject assignments
     */
    @GetMapping
    public ResponseEntity<
            ApiResponse<List<TeacherSubjectResponseDTO>>
            > getAllActiveAssignments() {

        List<TeacherSubjectResponseDTO> assignments =
                teacherSubjectService.getAllActiveAssignments();

        ApiResponse<List<TeacherSubjectResponseDTO>> response =
                new ApiResponse<>(
                        true,
                        "Active teacher-subject assignments fetched successfully",
                        assignments
                );

        return ResponseEntity.ok(response);
    }

    /**
     * Retrieves all active subject assignments belonging
     * to a particular teacher.
     *
     * Endpoint:
     * GET /api/admin/teacher-subjects/teacher/{teacherId}
     *
     * Example:
     * GET /api/admin/teacher-subjects/teacher/1
     *
     * @param teacherId database ID of the teacher
     * @return active assignments belonging to the teacher
     */
    @GetMapping("/teacher/{teacherId}")
    public ResponseEntity<
            ApiResponse<List<TeacherSubjectResponseDTO>>
            > getAssignmentsByTeacherId(
            @PathVariable
            @Positive(message = "Teacher ID must be greater than zero")
            Long teacherId
    ) {

        List<TeacherSubjectResponseDTO> assignments =
                teacherSubjectService.getAssignmentsByTeacherId(
                        teacherId
                );

        ApiResponse<List<TeacherSubjectResponseDTO>> response =
                new ApiResponse<>(
                        true,
                        "Teacher subject assignments fetched successfully",
                        assignments
                );

        return ResponseEntity.ok(response);
    }

    /**
     * Retrieves all active teacher assignments belonging
     * to a particular subject.
     *
     * Endpoint:
     * GET /api/admin/teacher-subjects/subject/{subjectId}
     *
     * Example:
     * GET /api/admin/teacher-subjects/subject/1
     *
     * @param subjectId database ID of the subject
     * @return active assignments belonging to the subject
     */
    @GetMapping("/subject/{subjectId}")
    public ResponseEntity<
            ApiResponse<List<TeacherSubjectResponseDTO>>
            > getAssignmentsBySubjectId(
            @PathVariable
            @Positive(message = "Subject ID must be greater than zero")
            Long subjectId
    ) {

        List<TeacherSubjectResponseDTO> assignments =
                teacherSubjectService.getAssignmentsBySubjectId(
                        subjectId
                );

        ApiResponse<List<TeacherSubjectResponseDTO>> response =
                new ApiResponse<>(
                        true,
                        "Subject teacher assignments fetched successfully",
                        assignments
                );

        return ResponseEntity.ok(response);
    }

    /**
     * Deactivates an existing teacher-subject assignment.
     *
     * This performs a soft delete. The database row remains,
     * but the assignment cannot be used for attendance,
     * marks, or timetable operations.
     *
     * Endpoint:
     * PATCH /api/admin/teacher-subjects/{assignmentId}/deactivate
     *
     * @param assignmentId database ID of the assignment
     * @return deactivated assignment information
     */
    @PatchMapping("/{assignmentId}/deactivate")
    public ResponseEntity<ApiResponse<TeacherSubjectResponseDTO>>
    deactivateAssignment(
            @PathVariable
            @Positive(message = "Assignment ID must be greater than zero")
            Long assignmentId
    ) {

        TeacherSubjectResponseDTO deactivatedAssignment =
                teacherSubjectService.deactivateAssignment(
                        assignmentId
                );

        ApiResponse<TeacherSubjectResponseDTO> response =
                new ApiResponse<>(
                        true,
                        "Teacher-subject assignment deactivated successfully",
                        deactivatedAssignment
                );

        return ResponseEntity.ok(response);
    }
}