package com.studentautomation.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/**
 * Request DTO for updating an Admin account profile.
 *
 * Purpose:
 * Super Admin can update an Admin's name and email.
 *
 * @param name  admin's display name
 * @param email admin's login email
 * @author Yashvanth
 */
public record UpdateAdminRequestDTO(

        @NotBlank(message = "Name is required")
        @Size(max = 100, message = "Name must not exceed 100 characters")
        String name,

        @NotBlank(message = "Email is required")
        @Email(message = "Valid email is required")
        String email
) {
}
