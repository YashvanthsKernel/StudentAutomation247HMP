package com.studentautomation.controller;

import com.studentautomation.dto.request.DepartmentRequestDTO;
import com.studentautomation.dto.response.ApiResponse;
import com.studentautomation.dto.response.DepartmentResponseDTO;
import com.studentautomation.service.DepartmentService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin/departments")
@PreAuthorize("hasAnyRole('ADMIN', 'SUPER_ADMIN')")
public class DepartmentController {

    private final DepartmentService departmentService;

    public DepartmentController(DepartmentService departmentService) {
        this.departmentService = departmentService;
    }

    @PostMapping
    public ResponseEntity<ApiResponse<DepartmentResponseDTO>> createDepartment(
            @Valid @RequestBody DepartmentRequestDTO request
    ) {
        DepartmentResponseDTO response = departmentService.createDepartment(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Department created successfully", response));
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<DepartmentResponseDTO>>> getAllDepartments() {
        List<DepartmentResponseDTO> response = departmentService.getAllDepartments();
        return ResponseEntity.ok(ApiResponse.success("Departments fetched successfully", response));
    }

    @GetMapping("/{departmentId}")
    public ResponseEntity<ApiResponse<DepartmentResponseDTO>> getDepartmentById(
            @PathVariable Long departmentId
    ) {
        DepartmentResponseDTO response = departmentService.getDepartmentById(departmentId);
        return ResponseEntity.ok(ApiResponse.success("Department fetched successfully", response));
    }

    @PutMapping("/{departmentId}")
    public ResponseEntity<ApiResponse<DepartmentResponseDTO>> updateDepartment(
            @PathVariable Long departmentId,
            @Valid @RequestBody DepartmentRequestDTO request
    ) {
        DepartmentResponseDTO response = departmentService.updateDepartment(departmentId, request);
        return ResponseEntity.ok(ApiResponse.success("Department updated successfully", response));
    }

    @PatchMapping("/{departmentId}/activate")
    public ResponseEntity<ApiResponse<DepartmentResponseDTO>> activateDepartment(
            @PathVariable Long departmentId
    ) {
        DepartmentResponseDTO response = departmentService.activateDepartment(departmentId);
        return ResponseEntity.ok(ApiResponse.success("Department activated successfully", response));
    }

    @PatchMapping("/{departmentId}/deactivate")
    public ResponseEntity<ApiResponse<DepartmentResponseDTO>> deactivateDepartment(
            @PathVariable Long departmentId
    ) {
        DepartmentResponseDTO response = departmentService.deactivateDepartment(departmentId);
        return ResponseEntity.ok(ApiResponse.success("Department deactivated successfully", response));
    }
}
