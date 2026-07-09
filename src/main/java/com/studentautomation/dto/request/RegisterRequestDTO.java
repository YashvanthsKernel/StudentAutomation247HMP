package com.studentautomation.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

/**
 * Request DTO for public user registration.
 *
 * Purpose:
 * This DTO receives only email and password details.
 * Role should not come from frontend for public registration.
 *
 * @param email user email address
 * @param password user password
 * @param confirmPassword password confirmation
 *
 * @author Yashvanth
 */
public record RegisterRequestDTO(

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
        String confirmPassword

) {
}