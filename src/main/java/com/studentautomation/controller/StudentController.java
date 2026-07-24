package com.studentautomation.controller;

import com.studentautomation.dto.response.ApiResponse;
import com.studentautomation.dto.response.StudentResponseDTO;
import com.studentautomation.service.StudentService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Controller class for student-related APIs.
 *
 * Purpose:
 * This class handles listing, fetching, updating and deactivating
 * student profiles. Student creation has been moved to AdminController
 * (POST /api/admin/students) as part of the production flow.
 *
 * Note:
 * POST (create) is intentionally removed from this controller.
 * Admin or Super Admin must use POST /api/admin/students to create students.
 *
 * @author Yashvanth
 */
@RestController
@RequestMapping("/api/students")
public class StudentController {

    private final StudentService studentService;

    public StudentController(StudentService studentService) {
        this.studentService = studentService;
    }

    /**
     * Gets all student profiles.
     *
     * Purpose:
     * This API returns all students from the database.
     * Admin and Super Admin can access this.
     *
     * @return list of student details
     */
    @GetMapping
    public ResponseEntity<ApiResponse<List<StudentResponseDTO>>> getAllStudents() {

        List<StudentResponseDTO> response = studentService.getAllStudents();

        return ResponseEntity.ok(
                ApiResponse.success("Students fetched successfully", response)
        );
    }

    /**
     * Gets a student by ID.
     *
     * Purpose:
     * This API returns one student using student profile ID.
     *
     * @param id student ID
     * @return student details
     */
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<StudentResponseDTO>> getStudentById(
            @PathVariable Long id) {

        StudentResponseDTO response = studentService.getStudentById(id);

        return ResponseEntity.ok(
                ApiResponse.success("Student fetched successfully", response)
        );
    }

    /**
     * Gets a student by register number.
     *
     * Purpose:
     * This API returns one student using their unique register number.
     *
     * @param regNo student register number
     * @return student details
     */
    @GetMapping("/reg-no/{regNo}")
    public ResponseEntity<ApiResponse<StudentResponseDTO>> getStudentByRegNo(
            @PathVariable String regNo) {

        StudentResponseDTO response = studentService.getStudentByRegNo(regNo);

        return ResponseEntity.ok(
                ApiResponse.success("Student fetched successfully", response)
        );
    }

    /**
     * Deactivates a student profile.
     *
     * Purpose:
     * This API does not permanently delete the student.
     * It marks the student profile as inactive and blocks login.
     * Use PATCH /api/admin/students/{id}/deactivate for granular control.
     *
     * @param id student ID
     * @return success message
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Object>> deleteStudent(
            @PathVariable Long id) {

        studentService.deleteStudent(id);

        return ResponseEntity.ok(
                ApiResponse.success("Student deactivated successfully", null)
        );
    }
}