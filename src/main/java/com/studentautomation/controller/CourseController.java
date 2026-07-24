package com.studentautomation.controller;

import com.studentautomation.dto.request.CourseRequestDTO;
import com.studentautomation.dto.response.ApiResponse;
import com.studentautomation.dto.response.CourseResponseDTO;
import com.studentautomation.service.CourseService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin/courses")
@PreAuthorize("hasAnyRole('ADMIN', 'SUPER_ADMIN')")
public class CourseController {

    private final CourseService courseService;

    public CourseController(CourseService courseService) {
        this.courseService = courseService;
    }

    @PostMapping
    public ResponseEntity<ApiResponse<CourseResponseDTO>> createCourse(
            @Valid @RequestBody CourseRequestDTO request
    ) {
        CourseResponseDTO response = courseService.createCourse(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Course created successfully", response));
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<CourseResponseDTO>>> getAllCourses() {
        List<CourseResponseDTO> response = courseService.getAllCourses();
        return ResponseEntity.ok(ApiResponse.success("Courses fetched successfully", response));
    }

    @GetMapping("/{courseId}")
    public ResponseEntity<ApiResponse<CourseResponseDTO>> getCourseById(
            @PathVariable Long courseId
    ) {
        CourseResponseDTO response = courseService.getCourseById(courseId);
        return ResponseEntity.ok(ApiResponse.success("Course fetched successfully", response));
    }

    @PutMapping("/{courseId}")
    public ResponseEntity<ApiResponse<CourseResponseDTO>> updateCourse(
            @PathVariable Long courseId,
            @Valid @RequestBody CourseRequestDTO request
    ) {
        CourseResponseDTO response = courseService.updateCourse(courseId, request);
        return ResponseEntity.ok(ApiResponse.success("Course updated successfully", response));
    }

    @PatchMapping("/{courseId}/activate")
    public ResponseEntity<ApiResponse<CourseResponseDTO>> activateCourse(
            @PathVariable Long courseId
    ) {
        CourseResponseDTO response = courseService.activateCourse(courseId);
        return ResponseEntity.ok(ApiResponse.success("Course activated successfully", response));
    }

    @PatchMapping("/{courseId}/deactivate")
    public ResponseEntity<ApiResponse<CourseResponseDTO>> deactivateCourse(
            @PathVariable Long courseId
    ) {
        CourseResponseDTO response = courseService.deactivateCourse(courseId);
        return ResponseEntity.ok(ApiResponse.success("Course deactivated successfully", response));
    }
}
