package com.studentautomation.service;

import com.studentautomation.dto.request.ChangePasswordRequestDTO;
import com.studentautomation.dto.request.ForgotPasswordRequestDTO;
import com.studentautomation.dto.request.LoginRequestDTO;
import com.studentautomation.dto.request.ResetPasswordRequestDTO;
import com.studentautomation.dto.response.AuthTokenResponseDTO;
import com.studentautomation.dto.response.LoginResponseDTO;
import com.studentautomation.dto.response.UserMeResponseDTO;

/**
 * Service interface for authentication-related operations.
 *
 * Purpose:
 * Defines contract for login, token refresh, logout,
 * password management, and user identity APIs.
 *
 * @author Yashvanth
 */
public interface AuthService {

    /**
     * Logs in a user and returns access token with refresh token.
     *
     * @param request login request data
     * @return auth token response containing access and refresh tokens
     */
    AuthTokenResponseDTO login(LoginRequestDTO request);

    /**
     * Generates a new access token using a valid refresh token.
     *
     * @param refreshToken JWT refresh token from cookie
     * @return new login response with fresh access token
     */
    LoginResponseDTO refreshAccessToken(String refreshToken);

    /**
     * Logs out a user by clearing the refresh token cookie.
     * Stateless logout; token blacklisting can be added later.
     */
    void logout();

    /**
     * Returns the profile of the currently logged-in user.
     *
     * @param email email extracted from JWT token
     * @return authenticated user details
     */
    UserMeResponseDTO getMe(String email);

    /**
     * Changes the password of the currently logged-in user.
     *
     * @param email     email extracted from JWT token
     * @param request   contains old password, new password and confirm password
     */
    void changePassword(String email, ChangePasswordRequestDTO request);

    /**
     * Initiates forgot password flow by generating a reset token.
     *
     * @param request contains user email
     */
    void forgotPassword(ForgotPasswordRequestDTO request);

    /**
     * Resets the password using a valid reset token.
     *
     * @param request contains reset token and new password
     */
    void resetPassword(ResetPasswordRequestDTO request);
}