package com.studentautomation.controller;

import com.studentautomation.dto.request.LoginRequestDTO;
import com.studentautomation.dto.request.RegisterRequestDTO;
import com.studentautomation.dto.response.ApiResponse;
import com.studentautomation.dto.response.LoginResponseDTO;
import com.studentautomation.service.AuthService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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
     * Logs in an existing user.
     *
     * Purpose:
     * This API checks email and password.
     * Later, JWT access token and refresh token will be returned here.
     *
     * @param request login request data
     * @return logged-in user response
     */
    @PostMapping("/login")
    public ResponseEntity<ApiResponse<LoginResponseDTO>> login(
            @Valid @RequestBody LoginRequestDTO request) {

        LoginResponseDTO response = authService.login(request);

        return ResponseEntity.ok(
                ApiResponse.success("Login successful", response)
        );
    }
}