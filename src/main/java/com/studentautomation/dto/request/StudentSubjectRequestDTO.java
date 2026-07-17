package com.studentautomation.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Request DTO used to assign an academic subject to a student.
 *
 * Purpose:
 * Accepts the student ID, subject ID, and academic year
 * required to create a student-subject assignment.
 *
 * @author Yashvanth
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class StudentSubjectRequestDTO {

    /**
     * Database ID of the student receiving the subject assignment.
     */
    @NotNull(message = "Student ID is required")
    @Positive(message = "Student ID must be greater than zero")
    private Long studentId;

    /**
     * Database ID of the subject being assigned.
     */
    @NotNull(message = "Subject ID is required")
    @Positive(message = "Subject ID must be greater than zero")
    private Long subjectId;

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