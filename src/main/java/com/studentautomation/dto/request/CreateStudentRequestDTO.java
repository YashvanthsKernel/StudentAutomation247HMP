package com.studentautomation.dto.request;

import jakarta.validation.constraints.*;

/**
 * Request DTO for Admin creating a Student account and profile.
 *
 * Purpose:
 * This DTO receives both login details and student profile details.
 * Admin should create the student account; student should not self-register.
 *
 * @param name student full name
 * @param email student login email
 * @param password student login password
 * @param confirmPassword password confirmation
 * @param regNo student register number
 * @param phoneNo student phone number
 * @param department student department
 * @param semester student semester
 * @param section student section
 * @param academicYear academic year
 *
 * @author Yashvanth
 */
public record CreateStudentRequestDTO(

        @NotBlank(message = "Student name is required")
        @Size(max = 100, message = "Student name cannot exceed 100 characters")
        String name,

        @NotBlank(message = "Email is required")
        @Email(message = "Invalid email format")
        String email,

        @NotBlank(message = "Password is required")
        @Pattern(
                regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@#$%^&+=!]).{6,}$",
                message = "Password must contain at least 6 characters, one uppercase letter, one lowercase letter, one number, and one special character"
        )
        String password,

        @NotBlank(message = "Confirm password is required")
        String confirmPassword,

        @NotBlank(message = "Register number is required")
        @Size(max = 50, message = "Register number cannot exceed 50 characters")
        String regNo,

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