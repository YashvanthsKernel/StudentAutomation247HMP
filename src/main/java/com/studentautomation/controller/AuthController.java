package com.studentautomation.controller;

import com.studentautomation.dto.request.ChangePasswordRequestDTO;
import com.studentautomation.dto.request.ForgotPasswordRequestDTO;
import com.studentautomation.dto.request.LoginRequestDTO;
import com.studentautomation.dto.request.ResetPasswordRequestDTO;
import com.studentautomation.dto.response.ApiResponse;
import com.studentautomation.dto.response.AuthTokenResponseDTO;
import com.studentautomation.dto.response.LoginResponseDTO;
import com.studentautomation.dto.response.UserMeResponseDTO;
import com.studentautomation.service.AuthService;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.time.Duration;

/**
 * Controller class for authentication-related APIs.
 *
 * Purpose:
 * Handles login, token refresh, logout, user identity,
 * and password management APIs.
 *
 * Note:
 * Public student and teacher registration APIs have been removed.
 * Students and teachers are created by Admin only via
 * POST /api/admin/students and POST /api/admin/teachers.
 *
 * @author Yashvanth
 */
@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    /**
     * Logs in a user account.
     *
     * Purpose:
     * This API validates email and password.
     * If login is successful, it returns access token in response body
     * and stores refresh token inside HttpOnly cookie.
     *
     * @param request  login request data
     * @param response HTTP response used to add refresh token cookie
     * @return login response with access token only
     */
    @PostMapping("/login")
    public ResponseEntity<ApiResponse<LoginResponseDTO>> login(
            @Valid @RequestBody LoginRequestDTO request,
            HttpServletResponse response
    ) {
        AuthTokenResponseDTO authTokenResponse = authService.login(request);

        addRefreshTokenCookie(response, authTokenResponse.refreshToken());

        LoginResponseDTO loginResponse = new LoginResponseDTO(
                authTokenResponse.email(),
                authTokenResponse.role(),
                authTokenResponse.accessToken()
        );

        return ResponseEntity.ok(
                ApiResponse.success("Login successful", loginResponse)
        );
    }

    /**
     * Refreshes access token using refresh token from cookie.
     *
     * Purpose:
     * This API reads the refresh token from HttpOnly cookie,
     * validates it, and returns a new access token.
     *
     * Note:
     * No request body is needed.
     * Refresh token is automatically sent by browser as cookie.
     *
     * @param refreshToken refresh token stored in HttpOnly cookie
     * @return new access token response
     */
    @PostMapping("/refresh-token")
    public ResponseEntity<ApiResponse<LoginResponseDTO>> refreshAccessToken(
            @CookieValue(name = "refreshToken", required = false) String refreshToken
    ) {
        LoginResponseDTO loginResponse = authService.refreshAccessToken(refreshToken);

        return ResponseEntity.ok(
                ApiResponse.success("Access token refreshed successfully", loginResponse)
        );
    }

    /**
     * Logs out the currently logged-in user.
     *
     * Purpose:
     * Clears the refresh token cookie so the user cannot
     * obtain new access tokens without logging in again.
     * This API is accessible to any authenticated user.
     *
     * @param response HTTP response used to clear the cookie
     * @return logout success message
     */
    @PostMapping("/logout")
    public ResponseEntity<ApiResponse<Object>> logout(HttpServletResponse response) {

        authService.logout();
        clearRefreshTokenCookie(response);

        return ResponseEntity.ok(
                ApiResponse.success("Logged out successfully", null)
        );
    }

    /**
     * Returns the profile of the currently logged-in user.
     *
     * Purpose:
     * Frontend can call this API to get the logged-in user's
     * role, name, email and account status using their JWT token.
     *
     * @param userDetails Spring Security loaded user details
     * @return current user profile
     */
    @GetMapping("/me")
    public ResponseEntity<ApiResponse<UserMeResponseDTO>> getMe(
            @AuthenticationPrincipal UserDetails userDetails
    ) {
        UserMeResponseDTO response = authService.getMe(userDetails.getUsername());

        return ResponseEntity.ok(
                ApiResponse.success("User profile fetched successfully", response)
        );
    }

    /**
     * Changes the password of the currently logged-in user.
     *
     * Purpose:
     * User provides old password for verification and new password to set.
     * All roles can use this API.
     *
     * @param userDetails Spring Security loaded user details
     * @param request     change password request
     * @return success message
     */
    @PutMapping("/change-password")
    public ResponseEntity<ApiResponse<Object>> changePassword(
            @AuthenticationPrincipal UserDetails userDetails,
            @Valid @RequestBody ChangePasswordRequestDTO request
    ) {
        authService.changePassword(userDetails.getUsername(), request);

        return ResponseEntity.ok(
                ApiResponse.success("Password changed successfully", null)
        );
    }

    /**
     * Initiates forgot password flow.
     *
     * Purpose:
     * User provides their registered email.
     * System will send a password reset token or OTP.
     * Full implementation with email sending will be added in Phase 6.
     *
     * @param request forgot password request with email
     * @return success message (does not reveal if email exists)
     */
    @PostMapping("/forgot-password")
    public ResponseEntity<ApiResponse<Object>> forgotPassword(
            @Valid @RequestBody ForgotPasswordRequestDTO request
    ) {
        authService.forgotPassword(request);

        return ResponseEntity.ok(
                ApiResponse.success(
                        "If this email is registered, a reset link will be sent.",
                        null
                )
        );
    }

    /**
     * Resets password using a valid reset token.
     *
     * Purpose:
     * User provides the reset token received by email
     * and the new password to set.
     * Full implementation with token storage will be added in Phase 6.
     *
     * @param request reset password request with token and new password
     * @return success message
     */
    @PostMapping("/reset-password")
    public ResponseEntity<ApiResponse<Object>> resetPassword(
            @Valid @RequestBody ResetPasswordRequestDTO request
    ) {
        authService.resetPassword(request);

        return ResponseEntity.ok(
                ApiResponse.success("Password reset successfully", null)
        );
    }

    /**
     * Adds refresh token into HttpOnly cookie.
     *
     * Purpose:
     * Refresh token should not be exposed in JSON response.
     * So this method stores the refresh token inside a secure HttpOnly cookie.
     *
     * Note:
     * secure(false) is used for localhost development.
     * In production with HTTPS, change secure(false) to secure(true).
     *
     * @param response     HTTP response object
     * @param refreshToken JWT refresh token
     */
    private void addRefreshTokenCookie(HttpServletResponse response, String refreshToken) {

        ResponseCookie cookie = ResponseCookie.from("refreshToken", refreshToken)
                .httpOnly(true)
                .secure(false)
                .path("/api/auth/refresh-token")
                .maxAge(Duration.ofDays(7))
                .sameSite("Lax")
                .build();

        response.addHeader(HttpHeaders.SET_COOKIE, cookie.toString());
    }

    /**
     * Clears the refresh token cookie on logout.
     *
     * Purpose:
     * Sets cookie maxAge to 0 so browser deletes the cookie immediately.
     *
     * @param response HTTP response object
     */
    private void clearRefreshTokenCookie(HttpServletResponse response) {

        ResponseCookie cookie = ResponseCookie.from("refreshToken", "")
                .httpOnly(true)
                .secure(false)
                .path("/api/auth/refresh-token")
                .maxAge(Duration.ZERO)
                .sameSite("Lax")
                .build();

        response.addHeader(HttpHeaders.SET_COOKIE, cookie.toString());
    }
}