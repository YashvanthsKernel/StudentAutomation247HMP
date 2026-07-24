package com.studentautomation.controller;

import com.studentautomation.dto.request.AttendanceRequestDTO;
import com.studentautomation.dto.request.BulkAttendanceRequestDTO;
import com.studentautomation.dto.response.ApiResponse;
import com.studentautomation.dto.response.AttendanceResponseDTO;
import com.studentautomation.dto.response.BulkAttendanceResponseDTO;
import com.studentautomation.dto.response.StudentResponseDTO;
import com.studentautomation.service.AttendanceService;
import jakarta.validation.Valid;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/teacher/attendance")
@PreAuthorize("hasRole('TEACHER')")
public class TeacherAttendanceController {

    private final AttendanceService attendanceService;

    public TeacherAttendanceController(AttendanceService attendanceService) {
        this.attendanceService = attendanceService;
    }

    @PostMapping
    public ResponseEntity<ApiResponse<AttendanceResponseDTO>> markAttendance(
            @Valid @RequestBody AttendanceRequestDTO request,
            Authentication authentication
    ) {
        AttendanceResponseDTO response = attendanceService.markAttendance(request, authentication.getName());
        return ResponseEntity.ok(ApiResponse.success("Attendance marked successfully", response));
    }

    @PostMapping("/bulk")
    public ResponseEntity<ApiResponse<BulkAttendanceResponseDTO>> markBulkAttendance(
            @Valid @RequestBody BulkAttendanceRequestDTO request,
            Authentication authentication
    ) {
        BulkAttendanceResponseDTO response = attendanceService.markBulkAttendance(request, authentication.getName());
        return ResponseEntity.ok(ApiResponse.success("Bulk attendance processed successfully", response));
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<AttendanceResponseDTO>>> getAttendanceWithFilters(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date,
            @RequestParam(required = false) Long subjectId,
            @RequestParam(required = false) String section,
            @RequestParam(required = false) Integer periodNumber,
            Authentication authentication
    ) {
        List<AttendanceResponseDTO> response = attendanceService.getAttendanceWithFilters(
                date, subjectId, section, periodNumber, authentication.getName()
        );
        return ResponseEntity.ok(ApiResponse.success("Attendance records fetched successfully", response));
    }

    @GetMapping("/roster")
    public ResponseEntity<ApiResponse<List<StudentResponseDTO>>> getAttendanceRoster(
            @RequestParam Long subjectId,
            @RequestParam(required = false) String section,
            @RequestParam(required = false) String academicYear,
            Authentication authentication
    ) {
        List<StudentResponseDTO> response = attendanceService.getAttendanceRoster(
                subjectId, section, academicYear, authentication.getName()
        );
        return ResponseEntity.ok(ApiResponse.success("Attendance roster fetched successfully", response));
    }

    @GetMapping("/{attendanceId}")
    public ResponseEntity<ApiResponse<AttendanceResponseDTO>> getAttendanceById(
            @PathVariable Long attendanceId
    ) {
        AttendanceResponseDTO response = attendanceService.getAttendanceById(attendanceId);
        return ResponseEntity.ok(ApiResponse.success("Attendance record fetched successfully", response));
    }

    @PutMapping("/{attendanceId}")
    public ResponseEntity<ApiResponse<AttendanceResponseDTO>> updateAttendance(
            @PathVariable Long attendanceId,
            @Valid @RequestBody AttendanceRequestDTO request,
            Authentication authentication
    ) {
        AttendanceResponseDTO response = attendanceService.updateAttendance(attendanceId, request, authentication.getName());
        return ResponseEntity.ok(ApiResponse.success("Attendance updated successfully", response));
    }

    @PatchMapping("/{attendanceId}/status")
    public ResponseEntity<ApiResponse<AttendanceResponseDTO>> updateAttendanceStatus(
            @PathVariable Long attendanceId,
            @RequestParam String status,
            Authentication authentication
    ) {
        AttendanceResponseDTO response = attendanceService.updateAttendanceStatus(attendanceId, status, authentication.getName());
        return ResponseEntity.ok(ApiResponse.success("Attendance status updated successfully", response));
    }

    @DeleteMapping("/{attendanceId}")
    public ResponseEntity<ApiResponse<Object>> deleteAttendance(
            @PathVariable Long attendanceId,
            Authentication authentication
    ) {
        attendanceService.deleteAttendance(attendanceId, authentication.getName());
        return ResponseEntity.ok(ApiResponse.success("Attendance deleted successfully", null));
    }
}