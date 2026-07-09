package com.studentautomation.dto.request;

import jakarta.validation.constraints.*;

/**
 * Request DTO for creating or updating teacher details.
 *
 * Purpose:
 * This DTO receives teacher profile/professional details from frontend/Postman.
 * It also receives userId to link the teacher profile with user login account.
 *
 * @param userId linked user account ID
 * @param employeeId teacher employee ID
 * @param name teacher full name
 * @param phoneNo teacher phone number
 * @param department teacher department
 * @param designation teacher designation
 * @param qualification teacher qualification
 * @param experienceYears teacher experience in years
 *
 * @author Yashvanth
 */
public record TeacherRequestDTO(

        @NotNull(message = "User ID is required")
        Long userId,

        @NotBlank(message = "Employee ID is required")
        @Size(max = 50, message = "Employee ID cannot exceed 50 characters")
        String employeeId,

        @NotBlank(message = "Teacher name is required")
        @Size(max = 100, message = "Teacher name cannot exceed 100 characters")
        String name,

        @Pattern(
                regexp = "^[0-9]{10}$",
                message = "Phone number must be 10 digits"
        )
        String phoneNo,

        @NotBlank(message = "Department is required")
        @Size(max = 80, message = "Department cannot exceed 80 characters")
        String department,

        @Size(max = 80, message = "Designation cannot exceed 80 characters")
        String designation,

        @Size(max = 120, message = "Qualification cannot exceed 120 characters")
        String qualification,

        @Min(value = 0, message = "Experience years cannot be negative")
        @Max(value = 60, message = "Experience years cannot be greater than 60")
        Integer experienceYears
) {
}