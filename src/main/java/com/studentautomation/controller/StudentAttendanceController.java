package com.studentautomation.controller;

import com.studentautomation.dto.response.ApiResponse;
import com.studentautomation.dto.response.AttendanceResponseDTO;
import com.studentautomation.service.AttendanceService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Controller class for student attendance APIs.
 *
 * Purpose:
 * This controller allows students to view only their own attendance records.
 *
 * @author Yashvanth
 */
@RestController
@RequestMapping("/api/student/attendance")
public class StudentAttendanceController {

    private final AttendanceService attendanceService;

    public StudentAttendanceController(AttendanceService attendanceService) {
        this.attendanceService = attendanceService;
    }

    /**
     * Gets attendance records of the logged-in student.
     *
     * Purpose:
     * This API allows a student to view only their own attendance.
     * The student email is extracted from the JWT token.
     *
     * @param authentication logged-in student authentication object
     * @return student's attendance records
     */
    @GetMapping("/me")
    public ResponseEntity<ApiResponse<List<AttendanceResponseDTO>>> getMyAttendance(
            Authentication authentication
    ) {
        String studentEmail = authentication.getName();

        List<AttendanceResponseDTO> response = attendanceService.getMyAttendance(studentEmail);

        return ResponseEntity.ok(
                new ApiResponse<>(true, "Student attendance fetched successfully", response)
        );
    }
}