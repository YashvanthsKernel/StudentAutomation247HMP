package com.studentautomation.controller;

import com.studentautomation.dto.request.AcademicYearRequestDTO;
import com.studentautomation.dto.response.AcademicYearResponseDTO;
import com.studentautomation.dto.response.ApiResponse;
import com.studentautomation.service.AcademicYearService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin/academic-years")
@PreAuthorize("hasAnyRole('ADMIN', 'SUPER_ADMIN')")
public class AcademicYearController {

    private final AcademicYearService academicYearService;

    public AcademicYearController(AcademicYearService academicYearService) {
        this.academicYearService = academicYearService;
    }

    @PostMapping
    public ResponseEntity<ApiResponse<AcademicYearResponseDTO>> createAcademicYear(
            @Valid @RequestBody AcademicYearRequestDTO request
    ) {
        AcademicYearResponseDTO response = academicYearService.createAcademicYear(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Academic year created successfully", response));
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<AcademicYearResponseDTO>>> getAllAcademicYears() {
        List<AcademicYearResponseDTO> response = academicYearService.getAllAcademicYears();
        return ResponseEntity.ok(ApiResponse.success("Academic years fetched successfully", response));
    }

    @PatchMapping("/{id}/make-current")
    public ResponseEntity<ApiResponse<AcademicYearResponseDTO>> makeCurrent(
            @PathVariable Long id
    ) {
        AcademicYearResponseDTO response = academicYearService.makeCurrent(id);
        return ResponseEntity.ok(ApiResponse.success("Academic year set as current successfully", response));
    }

    @PatchMapping("/{id}/close")
    public ResponseEntity<ApiResponse<AcademicYearResponseDTO>> closeAcademicYear(
            @PathVariable Long id
    ) {
        AcademicYearResponseDTO response = academicYearService.closeAcademicYear(id);
        return ResponseEntity.ok(ApiResponse.success("Academic year closed successfully", response));
    }
}
