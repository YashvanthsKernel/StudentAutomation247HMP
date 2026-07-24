package com.studentautomation.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/**
 * Request DTO for changing password.
 *
 * Purpose:
 * Logged-in user sends old password and new password
 * to update their account password.
 *
 * @param oldPassword   current password of the user
 * @param newPassword   new password to set
 * @param confirmPassword must match newPassword
 * @author Yashvanth
 */
public record ChangePasswordRequestDTO(

        @NotBlank(message = "Old password is required")
        String oldPassword,

        @NotBlank(message = "New password is required")
        @Size(min = 6, message = "New password must be at least 6 characters")
        String newPassword,

        @NotBlank(message = "Confirm password is required")
        String confirmPassword
) {
}
