package com.studentautomation.controller;

import com.studentautomation.dto.response.ApiResponse;
import com.studentautomation.dto.response.AttendanceReportDTO;
import com.studentautomation.dto.response.AttendanceResponseDTO;
import com.studentautomation.dto.response.StudentAttendanceSummaryDTO;
import com.studentautomation.service.AttendanceService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin/attendance")
@PreAuthorize("hasAnyRole('ADMIN', 'SUPER_ADMIN')")
public class AdminAttendanceController {

    private final AttendanceService attendanceService;

    public AdminAttendanceController(AttendanceService attendanceService) {
        this.attendanceService = attendanceService;
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<AttendanceResponseDTO>>> getAllAttendance() {
        List<AttendanceResponseDTO> response = attendanceService.getAllAttendance();
        return ResponseEntity.ok(ApiResponse.success("All attendance records fetched successfully", response));
    }

    @GetMapping("/students/{studentId}")
    public ResponseEntity<ApiResponse<List<AttendanceResponseDTO>>> getAttendanceByStudentId(
            @PathVariable Long studentId
    ) {
        List<AttendanceResponseDTO> response = attendanceService.getAttendanceByStudentId(studentId);
        return ResponseEntity.ok(ApiResponse.success("Student attendance records fetched successfully", response));
    }

    @GetMapping("/classes/{classId}")
    public ResponseEntity<ApiResponse<List<AttendanceResponseDTO>>> getAttendanceByClassId(
            @PathVariable Long classId
    ) {
        List<AttendanceResponseDTO> response = attendanceService.getAttendanceByClassId(classId);
        return ResponseEntity.ok(ApiResponse.success("Class attendance records fetched successfully", response));
    }

    @GetMapping("/subjects/{subjectId}")
    public ResponseEntity<ApiResponse<List<AttendanceResponseDTO>>> getAttendanceBySubjectId(
            @PathVariable Long subjectId
    ) {
        List<AttendanceResponseDTO> response = attendanceService.getAttendanceBySubjectId(subjectId);
        return ResponseEntity.ok(ApiResponse.success("Subject attendance records fetched successfully", response));
    }

    @GetMapping("/shortage")
    public ResponseEntity<ApiResponse<List<StudentAttendanceSummaryDTO>>> getAttendanceShortage() {
        List<StudentAttendanceSummaryDTO> response = attendanceService.getAttendanceShortage();
        return ResponseEntity.ok(ApiResponse.success("Students with attendance shortage (<75%) fetched successfully", response));
    }

    @GetMapping("/report")
    public ResponseEntity<ApiResponse<AttendanceReportDTO>> getAttendanceReport() {
        AttendanceReportDTO response = attendanceService.getAttendanceReport();
        return ResponseEntity.ok(ApiResponse.success("Attendance report fetched successfully", response));
    }
}
