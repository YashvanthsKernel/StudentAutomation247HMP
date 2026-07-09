package com.studentautomation.controller;

import com.studentautomation.dto.request.TeacherRequestDTO;
import com.studentautomation.dto.response.ApiResponse;
import com.studentautomation.dto.response.TeacherResponseDTO;
import com.studentautomation.service.TeacherService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Controller class for teacher-related APIs.
 *
 * Purpose:
 * This class receives HTTP requests from frontend/Postman
 * and sends them to the TeacherService layer.
 *
 * @author Yashvanth
 */
@RestController
@RequestMapping("/api/teachers")
public class TeacherController {

    private final TeacherService teacherService;

    public TeacherController(TeacherService teacherService) {
        this.teacherService = teacherService;
    }

    /**
     * Creates a new teacher profile.
     *
     * Purpose:
     * This API receives teacher details from frontend/Postman
     * and creates a teacher profile linked with a user account.
     *
     * @param request teacher request data
     * @return created teacher details
     */
    @PostMapping
    public ResponseEntity<ApiResponse<TeacherResponseDTO>> createTeacher(
            @Valid @RequestBody TeacherRequestDTO request) {

        TeacherResponseDTO response = teacherService.createTeacher(request);

        return ResponseEntity.ok(
                ApiResponse.success("Teacher created successfully", response)
        );
    }

    /**
     * Gets all teacher profiles.
     *
     * Purpose:
     * This API returns all teachers from the database.
     *
     * @return list of teacher details
     */
    @GetMapping
    public ResponseEntity<ApiResponse<List<TeacherResponseDTO>>> getAllTeachers() {

        List<TeacherResponseDTO> response = teacherService.getAllTeachers();

        return ResponseEntity.ok(
                ApiResponse.success("Teachers fetched successfully", response)
        );
    }

    /**
     * Gets a teacher by ID.
     *
     * Purpose:
     * This API returns one teacher using teacher ID.
     *
     * @param id teacher ID
     * @return teacher details
     */
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<TeacherResponseDTO>> getTeacherById(
            @PathVariable Long id) {

        TeacherResponseDTO response = teacherService.getTeacherById(id);

        return ResponseEntity.ok(
                ApiResponse.success("Teacher fetched successfully", response)
        );
    }

    /**
     * Gets a teacher by employee ID.
     *
     * Purpose:
     * This API returns one teacher using employee ID.
     *
     * @param employeeId teacher employee ID
     * @return teacher details
     */
    @GetMapping("/employee-id/{employeeId}")
    public ResponseEntity<ApiResponse<TeacherResponseDTO>> getTeacherByEmployeeId(
            @PathVariable String employeeId) {

        TeacherResponseDTO response = teacherService.getTeacherByEmployeeId(employeeId);

        return ResponseEntity.ok(
                ApiResponse.success("Teacher fetched successfully", response)
        );
    }

    /**
     * Updates an existing teacher profile.
     *
     * Purpose:
     * This API updates teacher profile/professional details.
     *
     * @param id teacher ID
     * @param request updated teacher request data
     * @return updated teacher details
     */
    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<TeacherResponseDTO>> updateTeacher(
            @PathVariable Long id,
            @Valid @RequestBody TeacherRequestDTO request) {

        TeacherResponseDTO response = teacherService.updateTeacher(id, request);

        return ResponseEntity.ok(
                ApiResponse.success("Teacher updated successfully", response)
        );
    }

    /**
     * Deactivates a teacher profile.
     *
     * Purpose:
     * This API does not permanently delete the teacher.
     * It marks the teacher profile as inactive.
     *
     * @param id teacher ID
     * @return success message
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Object>> deleteTeacher(
            @PathVariable Long id) {

        teacherService.deleteTeacher(id);

        return ResponseEntity.ok(
                ApiResponse.success("Teacher deleted successfully", null)
        );
    }
}