package com.studentautomation.dto.request;

import jakarta.validation.constraints.*;

/**
 * Request DTO for Admin creating a Teacher account and profile.
 *
 * Purpose:
 * This DTO receives both login details and teacher profile details.
 * Admin should create the teacher account; teacher should not self-register.
 *
 * @param name teacher full name
 * @param email teacher login email
 * @param password teacher login password
 * @param confirmPassword password confirmation
 * @param employeeId teacher employee ID
 * @param phoneNo teacher phone number
 * @param department teacher department
 * @param designation teacher designation
 * @param qualification teacher qualification
 * @param experienceYears teacher experience in years
 *
 * @author Yashvanth
 */
public record CreateTeacherRequestDTO(

        @NotBlank(message = "Teacher name is required")
        @Size(max = 100, message = "Teacher name cannot exceed 100 characters")
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

        @NotBlank(message = "Employee ID is required")
        @Size(max = 50, message = "Employee ID cannot exceed 50 characters")
        String employeeId,

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