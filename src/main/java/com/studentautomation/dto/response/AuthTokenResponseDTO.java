package com.studentautomation.dto.response;

/**
 * Internal response DTO for authentication tokens.
 *
 * Purpose:
 * This DTO carries both access token and refresh token
 * from service layer to controller layer.
 *
 * Important:
 * This DTO should not be directly returned to frontend,
 * because refresh token must be stored inside HttpOnly cookie.
 *
 * @param email user email
 * @param role user role
 * @param accessToken JWT access token
 * @param refreshToken JWT refresh token
 *
 * @author Yashvanth
 */
public record AuthTokenResponseDTO(
        String email,
        String role,
        String accessToken,
        String refreshToken
) {
}