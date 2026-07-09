package com.studentautomation.controller;

import com.studentautomation.dto.response.ApiResponse;
import com.studentautomation.dto.response.StudentResponseDTO;
import com.studentautomation.service.StudentService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

/**
 * Controller class for logged-in Student self APIs.
 *
 * Purpose:
 * This controller allows a student to access only his/her own data.
 *
 * @author Yashvanth
 */
@RestController
@RequestMapping("/api/student")
public class StudentSelfController {

    private final StudentService studentService;

    public StudentSelfController(StudentService studentService) {
        this.studentService = studentService;
    }

    /**
     * Gets the logged-in student's own profile.
     *
     * Purpose:
     * This API uses JWT authentication to identify the logged-in student.
     * Student does not send ID or email manually.
     *
     * @param authentication Spring Security authentication object
     * @return logged-in student profile details
     */
    @GetMapping("/me")
    public ResponseEntity<ApiResponse<StudentResponseDTO>> getMyProfile(
            Authentication authentication) {

        String email = authentication.getName();

        StudentResponseDTO response = studentService.getMyProfile(email);

        return ResponseEntity.ok(
                ApiResponse.success("Student profile fetched successfully", response)
        );
    }
}