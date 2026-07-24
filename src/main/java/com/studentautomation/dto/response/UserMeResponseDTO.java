package com.studentautomation.dto.response;

import com.studentautomation.enums.AccountStatus;
import com.studentautomation.enums.Role;

import java.time.LocalDateTime;

/**
 * Response DTO for the GET /api/auth/me API.
 *
 * Purpose:
 * Returns the profile of the currently logged-in user.
 * Includes basic user identity and account status.
 *
 * @param id            user ID
 * @param email         user email
 * @param name          user display name
 * @param role          user role (STUDENT, TEACHER, ADMIN, SUPER_ADMIN)
 * @param accountStatus current account status
 * @param createdAt     account creation timestamp
 * @author Yashvanth
 */
public record UserMeResponseDTO(
        Long id,
        String email,
        String name,
        Role role,
        AccountStatus accountStatus,
        LocalDateTime createdAt
) {
}
