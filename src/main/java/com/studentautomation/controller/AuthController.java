package com.studentautomation.controller;

import com.studentautomation.dto.request.LoginRequestDTO;
import com.studentautomation.dto.request.RegisterRequestDTO;
import com.studentautomation.dto.response.ApiResponse;
import com.studentautomation.dto.response.AuthTokenResponseDTO;
import com.studentautomation.dto.response.LoginResponseDTO;
import com.studentautomation.service.AuthService;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import java.time.Duration;

/**
 * Controller class for authentication-related APIs.
 *
 * Purpose:
 * This class receives student registration, teacher registration,
 * and login requests from frontend/Postman and sends them to
 * the AuthService layer.
 *
 * Important:
 * Role is not accepted from frontend during public registration.
 * Backend decides the role based on which API is called.
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
     * Registers a new student user account.
     *
     * Purpose:
     * This API creates a user login account with STUDENT role.
     * The frontend should not send role in request body.
     *
     * @param request student registration request data
     * @return registered student user response
     */
    @PostMapping("/register/student")
    public ResponseEntity<ApiResponse<LoginResponseDTO>> registerStudent(
            @Valid @RequestBody RegisterRequestDTO request) {

        LoginResponseDTO response = authService.registerStudent(request);

        return ResponseEntity.ok(
                ApiResponse.success("Student user registered successfully", response)
        );
    }

    /**
     * Registers a new teacher user account.
     *
     * Purpose:
     * This API creates a user login account with TEACHER role.
     * The frontend should not send role in request body.
     *
     * @param request teacher registration request data
     * @return registered teacher user response
     */
    @PostMapping("/register/teacher")
    public ResponseEntity<ApiResponse<LoginResponseDTO>> registerTeacher(
            @Valid @RequestBody RegisterRequestDTO request) {

        LoginResponseDTO response = authService.registerTeacher(request);

        return ResponseEntity.ok(
                ApiResponse.success("Teacher user registered successfully", response)
        );
    }

    /**
     * Logs in a user account.
     *
     * Purpose:
     * This API validates email and password.
     * If login is successful, it returns access token in response body
     * and stores refresh token inside HttpOnly cookie.
     *
     * @param request login request data
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
     * @param response HTTP response object
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
     * Refreshes access token using refresh token from cookie.
     *
     * Purpose:
     * This API reads the refresh token from HttpOnly cookie,
     * validates it, and returns a new access token.
     *
     * Note:
     * No request body is needed.
     * Refresh token is automatically sent by browser/Bruno as cookie.
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
}