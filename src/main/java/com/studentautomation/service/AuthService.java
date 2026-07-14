package com.studentautomation.service;

import com.studentautomation.dto.request.LoginRequestDTO;
import com.studentautomation.dto.request.RegisterRequestDTO;
import com.studentautomation.dto.response.AuthTokenResponseDTO;
import com.studentautomation.dto.response.LoginResponseDTO;

/**
 * Service interface for authentication-related operations.
 *
 * Purpose:
 * This interface defines authentication operations such as
 * student registration, teacher registration, and login.
 *
 * @author Yashvanth
 */
public interface AuthService {

    /**
     * Registers a student user account.
     *
     * Purpose:
     * Creates a new user with STUDENT role.
     *
     * @param request student registration request data
     * @return registered student login response
     */
    LoginResponseDTO registerStudent(RegisterRequestDTO request);

    /**
     * Registers a teacher user account.
     *
     * Purpose:
     * Creates a new user with TEACHER role.
     *
     * @param request teacher registration request data
     * @return registered teacher login response
     */
    LoginResponseDTO registerTeacher(RegisterRequestDTO request);

    /**
     * Logs in a user.
     *
     * Purpose:
     * Validates email and password, then returns access token
     * and refresh token internally.
     *
     * Note:
     * Refresh token should not be directly returned to frontend.
     * Controller will store it inside HttpOnly cookie.
     *
     * @param request login request data
     * @return authentication token response containing access and refresh token
     */
    AuthTokenResponseDTO login(LoginRequestDTO request);

    /**
     * Refreshes access token using refresh token.
     *
     * Purpose:
     * This method validates the refresh token and generates
     * a new access token for the same user.
     *
     * @param refreshToken JWT refresh token from HttpOnly cookie
     * @return login response containing new access token
     */
    LoginResponseDTO refreshAccessToken(String refreshToken);
}