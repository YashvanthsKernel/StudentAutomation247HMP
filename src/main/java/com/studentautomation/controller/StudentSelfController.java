package com.studentautomation.controller;

import com.studentautomation.dto.response.ApiResponse;
import com.studentautomation.dto.response.StudentResponseDTO;
import com.studentautomation.dto.response.StudentSubjectResponseDTO;
import com.studentautomation.service.StudentService;
import com.studentautomation.service.StudentSubjectService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

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
    private final StudentSubjectService studentSubjectService;

    public StudentSelfController(StudentService studentService, StudentSubjectService studentSubjectService) {
        this.studentService = studentService;
        this.studentSubjectService = studentSubjectService;
    }

    /**
     * Gets the logged-in student's own profile.
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

    /**
     * Gets the logged-in student's assigned subjects.
     */
    @GetMapping("/subjects/me")
    public ResponseEntity<ApiResponse<List<StudentSubjectResponseDTO>>> getMySubjects(
            Authentication authentication) {

        String email = authentication.getName();
        List<StudentSubjectResponseDTO> response = studentSubjectService.getAssignmentsByStudentEmail(email);

        return ResponseEntity.ok(
                ApiResponse.success("Student assigned subjects fetched successfully", response)
        );
    }
}