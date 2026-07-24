package com.studentautomation.controller;

import com.studentautomation.dto.response.ApiResponse;
import com.studentautomation.dto.response.AttendanceResponseDTO;
import com.studentautomation.dto.response.StudentAttendanceSummaryDTO;
import com.studentautomation.service.AttendanceService;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/student/attendance")
@PreAuthorize("hasRole('STUDENT')")
public class StudentAttendanceController {

    private final AttendanceService attendanceService;

    public StudentAttendanceController(AttendanceService attendanceService) {
        this.attendanceService = attendanceService;
    }

    @GetMapping("/me")
    public ResponseEntity<ApiResponse<List<AttendanceResponseDTO>>> getMyAttendance(
            @RequestParam(required = false) Long subjectId,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fromDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate toDate,
            Authentication authentication
    ) {
        List<AttendanceResponseDTO> response = attendanceService.getMyAttendanceFiltered(
                subjectId, fromDate, toDate, authentication.getName()
        );
        return ResponseEntity.ok(ApiResponse.success("Student attendance fetched successfully", response));
    }

    @GetMapping("/me/summary")
    public ResponseEntity<ApiResponse<StudentAttendanceSummaryDTO>> getMyAttendanceSummary(
            Authentication authentication
    ) {
        StudentAttendanceSummaryDTO response = attendanceService.getMyAttendanceSummary(authentication.getName());
        return ResponseEntity.ok(ApiResponse.success("Student overall attendance summary fetched successfully", response));
    }

    @GetMapping("/me/subjects/{subjectId}/summary")
    public ResponseEntity<ApiResponse<StudentAttendanceSummaryDTO>> getMySubjectAttendanceSummary(
            @PathVariable Long subjectId,
            Authentication authentication
    ) {
        StudentAttendanceSummaryDTO response = attendanceService.getMySubjectAttendanceSummary(subjectId, authentication.getName());
        return ResponseEntity.ok(ApiResponse.success("Subject attendance summary fetched successfully", response));
    }
}