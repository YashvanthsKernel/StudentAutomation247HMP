package com.studentautomation.controller;

import com.studentautomation.dto.request.AcademicClassRequestDTO;
import com.studentautomation.dto.response.AcademicClassResponseDTO;
import com.studentautomation.dto.response.ApiResponse;
import com.studentautomation.dto.response.StudentResponseDTO;
import com.studentautomation.service.AcademicClassService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin/classes")
@PreAuthorize("hasAnyRole('ADMIN', 'SUPER_ADMIN')")
public class AcademicClassController {

    private final AcademicClassService academicClassService;

    public AcademicClassController(AcademicClassService academicClassService) {
        this.academicClassService = academicClassService;
    }

    @PostMapping
    public ResponseEntity<ApiResponse<AcademicClassResponseDTO>> createClass(
            @Valid @RequestBody AcademicClassRequestDTO request
    ) {
        AcademicClassResponseDTO response = academicClassService.createClass(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Class created successfully", response));
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<AcademicClassResponseDTO>>> getAllClasses() {
        List<AcademicClassResponseDTO> response = academicClassService.getAllClasses();
        return ResponseEntity.ok(ApiResponse.success("Classes fetched successfully", response));
    }

    @GetMapping("/{classId}")
    public ResponseEntity<ApiResponse<AcademicClassResponseDTO>> getClassById(
            @PathVariable Long classId
    ) {
        AcademicClassResponseDTO response = academicClassService.getClassById(classId);
        return ResponseEntity.ok(ApiResponse.success("Class fetched successfully", response));
    }

    @PutMapping("/{classId}")
    public ResponseEntity<ApiResponse<AcademicClassResponseDTO>> updateClass(
            @PathVariable Long classId,
            @Valid @RequestBody AcademicClassRequestDTO request
    ) {
        AcademicClassResponseDTO response = academicClassService.updateClass(classId, request);
        return ResponseEntity.ok(ApiResponse.success("Class updated successfully", response));
    }

    @PatchMapping("/{classId}/activate")
    public ResponseEntity<ApiResponse<AcademicClassResponseDTO>> activateClass(
            @PathVariable Long classId
    ) {
        AcademicClassResponseDTO response = academicClassService.activateClass(classId);
        return ResponseEntity.ok(ApiResponse.success("Class activated successfully", response));
    }

    @PatchMapping("/{classId}/deactivate")
    public ResponseEntity<ApiResponse<AcademicClassResponseDTO>> deactivateClass(
            @PathVariable Long classId
    ) {
        AcademicClassResponseDTO response = academicClassService.deactivateClass(classId);
        return ResponseEntity.ok(ApiResponse.success("Class deactivated successfully", response));
    }

    @GetMapping("/{classId}/students")
    public ResponseEntity<ApiResponse<List<StudentResponseDTO>>> getStudentsInClass(
            @PathVariable Long classId
    ) {
        List<StudentResponseDTO> response = academicClassService.getStudentsInClass(classId);
        return ResponseEntity.ok(ApiResponse.success("Class students fetched successfully", response));
    }
}
