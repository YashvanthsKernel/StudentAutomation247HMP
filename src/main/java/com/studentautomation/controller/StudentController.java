package com.studentautomation.controller;

import com.studentautomation.dto.request.StudentRequestDTO;
import com.studentautomation.dto.response.ApiResponse;
import com.studentautomation.dto.response.StudentResponseDTO;
import com.studentautomation.service.StudentService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Controller class for student-related APIs.
 *
 * Purpose:
 * This class receives HTTP requests from frontend/Postman
 * and sends them to the StudentService layer.
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
     * Creates a new student profile.
     *
     * Purpose:
     * This API receives student details from frontend/Postman
     * and creates a student profile linked with a user account.
     *
     * @param request student request data
     * @return created student details
     */
    @PostMapping
    public ResponseEntity<ApiResponse<StudentResponseDTO>> createStudent(
            @Valid @RequestBody StudentRequestDTO request) {

        StudentResponseDTO response = studentService.createStudent(request);

        return ResponseEntity.ok(
                ApiResponse.success("Student created successfully", response)
        );
    }

    /**
     * Gets all student profiles.
     *
     * Purpose:
     * This API returns all students from the database.
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
     * This API returns one student using student ID.
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
     * This API returns one student using register number.
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
     * Updates an existing student profile.
     *
     * Purpose:
     * This API updates student academic/profile details.
     *
     * @param id student ID
     * @param request updated student request data
     * @return updated student details
     */
    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<StudentResponseDTO>> updateStudent(
            @PathVariable Long id,
            @Valid @RequestBody StudentRequestDTO request) {

        StudentResponseDTO response = studentService.updateStudent(id, request);

        return ResponseEntity.ok(
                ApiResponse.success("Student updated successfully", response)
        );
    }

    /**
     * Deactivates a student profile.
     *
     * Purpose:
     * This API does not permanently delete the student.
     * It marks the student profile as inactive.
     *
     * @param id student ID
     * @return success message
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Object>> deleteStudent(
            @PathVariable Long id) {

        studentService.deleteStudent(id);

        return ResponseEntity.ok(
                ApiResponse.success("Student deleted successfully", null)
        );
    }
}