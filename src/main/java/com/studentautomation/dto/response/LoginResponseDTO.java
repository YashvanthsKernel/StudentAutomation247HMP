package com.studentautomation.dto.response;

import com.studentautomation.enums.Role;

/**
 * Response DTO for login API.
 *
 * Purpose:
 * This DTO sends login success details back to frontend.
 * Later, when JWT is added, accessToken and refreshToken will be sent here.
 *
 * @param email logged-in user email
 * @param role logged-in user role
 * @param accessToken JWT access token
 * @param refreshToken JWT refresh token
 *
 * @author Yashvanth
 */
public record LoginResponseDTO(
        String email,
        Role role,
        String accessToken,
        String refreshToken
) {
}