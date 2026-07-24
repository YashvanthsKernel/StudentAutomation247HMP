package com.studentautomation.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

/**
 * Request DTO for forgot password.
 *
 * Purpose:
 * User sends their registered email to receive a password reset token.
 *
 * @param email registered user email address
 * @author Yashvanth
 */
public record ForgotPasswordRequestDTO(

        @NotBlank(message = "Email is required")
        @Email(message = "Valid email is required")
        String email
) {
}
