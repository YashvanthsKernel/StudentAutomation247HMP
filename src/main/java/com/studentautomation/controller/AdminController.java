package com.studentautomation.controller;

import com.studentautomation.dto.request.CreateStudentRequestDTO;
import com.studentautomation.dto.request.CreateTeacherRequestDTO;
import com.studentautomation.dto.response.ApiResponse;
import com.studentautomation.dto.response.StudentResponseDTO;
import com.studentautomation.dto.response.TeacherResponseDTO;
import com.studentautomation.service.AdminService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * Controller class for Admin APIs.
 *
 * Purpose:
 * This controller receives requests that Admin and Super Admin can perform.
 *
 * @author Yashvanth
 */
@RestController
@RequestMapping("/api/admin")
public class AdminController {

    private final AdminService adminService;

    public AdminController(AdminService adminService) {
        this.adminService = adminService;
    }

    /**
     * Creates a new Student account and profile.
     *
     * Purpose:
     * This API is used by Admin or Super Admin to create students.
     * Student cannot self-register.
     *
     * @param request student account and profile creation request
     * @return created student details
     */
    @PostMapping("/students")
    public ResponseEntity<ApiResponse<StudentResponseDTO>> createStudent(
            @Valid @RequestBody CreateStudentRequestDTO request) {

        StudentResponseDTO response = adminService.createStudent(request);

        return ResponseEntity.ok(
                ApiResponse.success("Student created successfully", response)
        );
    }

    /**
     * Creates a new Teacher account and profile.
     *
     * Purpose:
     * This API is used by Admin or Super Admin to create teachers.
     * Teacher cannot self-register.
     *
     * @param request teacher account and profile creation request
     * @return created teacher details
     */
    @PostMapping("/teachers")
    public ResponseEntity<ApiResponse<TeacherResponseDTO>> createTeacher(
            @Valid @RequestBody CreateTeacherRequestDTO request) {

        TeacherResponseDTO response = adminService.createTeacher(request);

        return ResponseEntity.ok(
                ApiResponse.success("Teacher created successfully", response)
        );
    }
}