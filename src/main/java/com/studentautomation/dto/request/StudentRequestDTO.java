package com.studentautomation.dto.request;

import jakarta.validation.constraints.*;

/**
 * Request DTO for creating or updating student details.
 *
 * Purpose:
 * This DTO receives student academic/profile details from frontend/Postman.
 * It also receives userId to link the student profile with user login account.
 *
 * @param userId linked user account ID
 * @param regNo student register number
 * @param name student full name
 * @param phoneNo student phone number
 * @param department student department
 * @param semester current semester
 * @param section class section
 * @param academicYear academic year
 *
 * @author Yashvanth
 */
public record StudentRequestDTO(

        @NotNull(message = "User ID is required")
        Long userId,

        @NotBlank(message = "Register number is required")
        @Size(max = 50, message = "Register number cannot exceed 50 characters")
        String regNo,

        @NotBlank(message = "Student name is required")
        @Size(max = 100, message = "Student name cannot exceed 100 characters")
        String name,

        @Pattern(
                regexp = "^[0-9]{10}$",
                message = "Phone number must be 10 digits"
        )
        String phoneNo,

        @NotBlank(message = "Department is required")
        @Size(max = 80, message = "Department cannot exceed 80 characters")
        String department,

        @NotNull(message = "Semester is required")
        @Min(value = 1, message = "Semester must be at least 1")
        @Max(value = 8, message = "Semester cannot be greater than 8")
        Integer semester,

        @Size(max = 20, message = "Section cannot exceed 20 characters")
        String section,

        @Size(max = 20, message = "Academic year cannot exceed 20 characters")
        String academicYear
) {
}