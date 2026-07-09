package com.studentautomation.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

/**
 * Request DTO for creating an Admin account.
 *
 * Purpose:
 * This DTO receives admin account details from Super Admin.
 * Only Super Admin is allowed to create Admin accounts.
 *
 * @param name admin full name
 * @param email admin email address
 * @param password admin password
 * @param confirmPassword password confirmation
 *
 * @author Yashvanth
 */
public record CreateAdminRequestDTO(

        @NotBlank(message = "Name is required")
        @Size(min = 2, max = 100, message = "Name must be between 2 and 100 characters")
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
        String confirmPassword

) {
}