package com.studentautomation.dto.response;

/**
 * Response DTO for login API.
 *
 * Purpose:
 * This DTO is returned to frontend after successful login.
 *
 * Note:
 * Refresh token is not included here because it is stored
 * securely in HttpOnly cookie.
 *
 * @param email user email
 * @param role user role
 * @param accessToken JWT access token
 *
 * @author Yashvanth
 */
public record LoginResponseDTO(
        String email,
        String role,
        String accessToken
) {
}