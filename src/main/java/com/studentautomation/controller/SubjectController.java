package com.studentautomation.controller;

import com.studentautomation.dto.request.SubjectRequestDTO;
import com.studentautomation.dto.response.ApiResponse;
import com.studentautomation.dto.response.SubjectResponseDTO;
import com.studentautomation.service.SubjectService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * REST controller for subject-related operations.
 *
 * Purpose:
 * Provides APIs for creating, updating, retrieving,
 * activating, and deactivating academic subjects.
 *
 * @author Yashvanth
 */
@RestController
@RequestMapping("/api/subjects")
@RequiredArgsConstructor
@Validated
public class SubjectController {

    /**
     * Service used to perform subject-related business operations.
     *
     * Lombok's @RequiredArgsConstructor automatically creates
     * constructor injection for this final field.
     */
    private final SubjectService subjectService;

    /**
     * Creates a new academic subject.
     *
     * Only ADMIN and SUPER_ADMIN users can create subjects.
     *
     * Endpoint:
     * POST /api/subjects
     *
     * @param requestDTO subject details received from the client
     * @return created subject information
     */
    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_ADMIN')")
    public ResponseEntity<ApiResponse<SubjectResponseDTO>> createSubject(
            @Valid @RequestBody SubjectRequestDTO requestDTO
    ) {

        SubjectResponseDTO createdSubject =
                subjectService.createSubject(requestDTO);

        ApiResponse<SubjectResponseDTO> response =
                new ApiResponse<>(
                        true,
                        "Subject created successfully",
                        createdSubject
                );

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(response);
    }

    /**
     * Updates an existing academic subject.
     *
     * Only ADMIN and SUPER_ADMIN users can update subjects.
     *
     * Endpoint:
     * PUT /api/subjects/{subjectId}
     *
     * @param subjectId subject ID
     * @param requestDTO updated subject details
     * @return updated subject information
     */
    @PutMapping("/{subjectId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_ADMIN')")
    public ResponseEntity<ApiResponse<SubjectResponseDTO>> updateSubject(
            @PathVariable
            @Positive(message = "Subject ID must be greater than zero")
            Long subjectId,

            @Valid
            @RequestBody
            SubjectRequestDTO requestDTO
    ) {

        SubjectResponseDTO updatedSubject =
                subjectService.updateSubject(
                        subjectId,
                        requestDTO
                );

        ApiResponse<SubjectResponseDTO> response =
                new ApiResponse<>(
                        true,
                        "Subject updated successfully",
                        updatedSubject
                );

        return ResponseEntity.ok(response);
    }

    /**
     * Retrieves a subject using its database ID.
     *
     * Endpoint:
     * GET /api/subjects/{subjectId}
     *
     * @param subjectId subject ID
     * @return matching subject information
     */
    @GetMapping("/{subjectId}")
    @PreAuthorize(
            "hasAnyRole('STUDENT', 'TEACHER', 'ADMIN', 'SUPER_ADMIN')"
    )
    public ResponseEntity<ApiResponse<SubjectResponseDTO>> getSubjectById(
            @PathVariable
            @Positive(message = "Subject ID must be greater than zero")
            Long subjectId
    ) {

        SubjectResponseDTO subject =
                subjectService.getSubjectById(subjectId);

        ApiResponse<SubjectResponseDTO> response =
                new ApiResponse<>(
                        true,
                        "Subject fetched successfully",
                        subject
                );

        return ResponseEntity.ok(response);
    }

    /**
     * Retrieves a subject using its unique subject code.
     *
     * Endpoint:
     * GET /api/subjects/code/{subjectCode}
     *
     * @param subjectCode unique subject code
     * @return matching subject information
     */
    @GetMapping("/code/{subjectCode}")
    @PreAuthorize(
            "hasAnyRole('STUDENT', 'TEACHER', 'ADMIN', 'SUPER_ADMIN')"
    )
    public ResponseEntity<ApiResponse<SubjectResponseDTO>> getSubjectByCode(
            @PathVariable
            @NotBlank(message = "Subject code is required")
            String subjectCode
    ) {

        SubjectResponseDTO subject =
                subjectService.getSubjectByCode(subjectCode);

        ApiResponse<SubjectResponseDTO> response =
                new ApiResponse<>(
                        true,
                        "Subject fetched successfully",
                        subject
                );

        return ResponseEntity.ok(response);
    }

    /**
     * Retrieves all currently active academic subjects.
     *
     * Endpoint:
     * GET /api/subjects
     *
     * @return list of active subjects
     */
    @GetMapping
    @PreAuthorize(
            "hasAnyRole('STUDENT', 'TEACHER', 'ADMIN', 'SUPER_ADMIN')"
    )
    public ResponseEntity<ApiResponse<List<SubjectResponseDTO>>>
    getAllActiveSubjects() {

        List<SubjectResponseDTO> subjects =
                subjectService.getAllActiveSubjects();

        ApiResponse<List<SubjectResponseDTO>> response =
                new ApiResponse<>(
                        true,
                        "Active subjects fetched successfully",
                        subjects
                );

        return ResponseEntity.ok(response);
    }

    /**
     * Retrieves active subjects using department and semester.
     *
     * Example:
     * GET /api/subjects/filter?department=CSE&semester=3
     *
     * @param department department name
     * @param semester semester number
     * @return matching active subjects
     */
    @GetMapping("/filter")
    @PreAuthorize(
            "hasAnyRole('STUDENT', 'TEACHER', 'ADMIN', 'SUPER_ADMIN')"
    )
    public ResponseEntity<ApiResponse<List<SubjectResponseDTO>>>
    getSubjectsByDepartmentAndSemester(
            @RequestParam
            @NotBlank(message = "Department is required")
            String department,

            @RequestParam
            @Min(
                    value = 1,
                    message = "Semester must be at least 1"
            )
            @Max(
                    value = 8,
                    message = "Semester cannot be greater than 8"
            )
            Integer semester
    ) {

        List<SubjectResponseDTO> subjects =
                subjectService.getSubjectsByDepartmentAndSemester(
                        department,
                        semester
                );

        ApiResponse<List<SubjectResponseDTO>> response =
                new ApiResponse<>(
                        true,
                        "Subjects fetched successfully",
                        subjects
                );

        return ResponseEntity.ok(response);
    }

    /**
     * Activates an existing academic subject.
     *
     * Endpoint:
     * PATCH /api/subjects/{subjectId}/activate
     *
     * @param subjectId subject ID
     * @return activated subject information
     */
    @PatchMapping("/{subjectId}/activate")
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_ADMIN')")
    public ResponseEntity<ApiResponse<SubjectResponseDTO>> activateSubject(
            @PathVariable
            @Positive(message = "Subject ID must be greater than zero")
            Long subjectId
    ) {

        SubjectResponseDTO activatedSubject =
                subjectService.activateSubject(subjectId);

        ApiResponse<SubjectResponseDTO> response =
                new ApiResponse<>(
                        true,
                        "Subject activated successfully",
                        activatedSubject
                );

        return ResponseEntity.ok(response);
    }

    /**
     * Deactivates an existing academic subject.
     *
     * This is a soft-delete operation.
     *
     * Endpoint:
     * PATCH /api/subjects/{subjectId}/deactivate
     *
     * @param subjectId subject ID
     * @return deactivated subject information
     */
    @PatchMapping("/{subjectId}/deactivate")
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_ADMIN')")
    public ResponseEntity<ApiResponse<SubjectResponseDTO>> deactivateSubject(
            @PathVariable
            @Positive(message = "Subject ID must be greater than zero")
            Long subjectId
    ) {

        SubjectResponseDTO deactivatedSubject =
                subjectService.deactivateSubject(subjectId);

        ApiResponse<SubjectResponseDTO> response =
                new ApiResponse<>(
                        true,
                        "Subject deactivated successfully",
                        deactivatedSubject
                );

        return ResponseEntity.ok(response);
    }
}