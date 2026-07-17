package com.studentautomation.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Request DTO used to assign a subject to a teacher.
 *
 * Purpose:
 * Accepts the teacher ID, subject ID, section, and academic year
 * required to create a teacher-subject assignment.
 *
 * @author Yashvanth
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TeacherSubjectRequestDTO {

    /**
     * Database ID of the teacher receiving the subject assignment.
     */
    @NotNull(message = "Teacher ID is required")
    @Positive(message = "Teacher ID must be greater than zero")
    private Long teacherId;

    /**
     * Database ID of the subject being assigned.
     */
    @NotNull(message = "Subject ID is required")
    @Positive(message = "Subject ID must be greater than zero")
    private Long subjectId;

    /**
     * Class section handled by the teacher.
     *
     * Examples:
     * A, B, C, A1
     */
    @NotBlank(message = "Section is required")
    @Size(
            max = 20,
            message = "Section cannot contain more than 20 characters"
    )
    private String section;

    /**
     * Academic year for which the subject is assigned.
     *
     * Expected format:
     * 2026-2027
     */
    @NotBlank(message = "Academic year is required")
    @Pattern(
            regexp = "^\\d{4}-\\d{4}$",
            message = "Academic year must use the format YYYY-YYYY"
    )
    private String academicYear;
}