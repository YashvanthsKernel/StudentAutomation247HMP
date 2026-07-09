package com.studentautomation.controller;

import com.studentautomation.dto.response.ApiResponse;
import com.studentautomation.dto.response.TeacherResponseDTO;
import com.studentautomation.service.TeacherService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

/**
 * Controller class for logged-in Teacher self APIs.
 *
 * Purpose:
 * This controller allows a teacher to access only his/her own data.
 *
 * @author Yashvanth
 */
@RestController
@RequestMapping("/api/teacher")
public class TeacherSelfController {

    private final TeacherService teacherService;

    public TeacherSelfController(TeacherService teacherService) {
        this.teacherService = teacherService;
    }

    /**
     * Gets the logged-in teacher's own profile.
     *
     * Purpose:
     * This API uses JWT authentication to identify the logged-in teacher.
     * Teacher does not send ID or email manually.
     *
     * @param authentication Spring Security authentication object
     * @return logged-in teacher profile details
     */
    @GetMapping("/me")
    public ResponseEntity<ApiResponse<TeacherResponseDTO>> getMyProfile(
            Authentication authentication) {

        String email = authentication.getName();

        TeacherResponseDTO response = teacherService.getMyProfile(email);

        return ResponseEntity.ok(
                ApiResponse.success("Teacher profile fetched successfully", response)
        );
    }
}