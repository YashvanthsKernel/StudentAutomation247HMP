package com.studentautomation.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/**
 * Request DTO for Admin to reset a user's password.
 *
 * Purpose:
 * Admin can set a new temporary password for a student or teacher.
 *
 * @param newPassword     new temporary password
 * @param confirmPassword must match newPassword
 * @author Yashvanth
 */
public record AdminResetPasswordRequestDTO(

        @NotBlank(message = "New password is required")
        @Size(min = 6, message = "Password must be at least 6 characters")
        String newPassword,

        @NotBlank(message = "Confirm password is required")
        String confirmPassword
) {
}
