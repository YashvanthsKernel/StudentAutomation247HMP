package com.studentautomation.controller;

import com.studentautomation.dto.response.ApiResponse;
import com.studentautomation.dto.response.TeacherResponseDTO;
import com.studentautomation.service.TeacherService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Controller class for teacher-related APIs.
 *
 * Purpose:
 * This class handles listing, fetching, updating and deactivating
 * teacher profiles. Teacher creation has been moved to AdminController
 * (POST /api/admin/teachers) as part of the production flow.
 *
 * Note:
 * POST (create) is intentionally removed from this controller.
 * Admin or Super Admin must use POST /api/admin/teachers to create teachers.
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
     * Gets all teacher profiles.
     *
     * Purpose:
     * This API returns all teachers from the database.
     * Admin and Super Admin can access this.
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
     * This API returns one teacher using their profile ID.
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
     * This API returns one teacher using their unique employee ID.
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
     * Deactivates a teacher profile.
     *
     * Purpose:
     * This API does not permanently delete the teacher.
     * It marks the teacher profile as inactive and blocks login.
     * Use PATCH /api/admin/teachers/{id}/deactivate for granular control.
     *
     * @param id teacher ID
     * @return success message
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Object>> deleteTeacher(
            @PathVariable Long id) {

        teacherService.deleteTeacher(id);

        return ResponseEntity.ok(
                ApiResponse.success("Teacher deactivated successfully", null)
        );
    }
}