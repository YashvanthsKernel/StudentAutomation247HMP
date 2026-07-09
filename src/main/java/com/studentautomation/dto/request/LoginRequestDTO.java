package com.studentautomation.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

/**
 * Request DTO for login API.
 *
 * Purpose:
 * This DTO receives login details from frontend/Postman.
 *
 * @param email user email address
 * @param password user password
 *
 * @author Yashvanth
 */
public record LoginRequestDTO(

        @NotBlank(message = "Email is required")
        @Email(message = "Invalid email format")
        String email,

        @NotBlank(message = "Password is required")
        String password
) {
}