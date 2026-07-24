package com.studentautomation.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/**
 * Request DTO for resetting password using a reset token.
 *
 * Purpose:
 * User sends the reset token received by email
 * along with the new password to reset their account password.
 *
 * @param token           password reset token received via email
 * @param newPassword     new password to set
 * @param confirmPassword must match newPassword
 * @author Yashvanth
 */
public record ResetPasswordRequestDTO(

        @NotBlank(message = "Reset token is required")
        String token,

        @NotBlank(message = "New password is required")
        @Size(min = 6, message = "New password must be at least 6 characters")
        String newPassword,

        @NotBlank(message = "Confirm password is required")
        String confirmPassword
) {
}
