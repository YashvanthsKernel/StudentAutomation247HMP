package com.studentautomation.controller;

import com.studentautomation.dto.request.AttendanceRequestDTO;
import com.studentautomation.dto.request.BulkAttendanceRequestDTO;
import com.studentautomation.dto.response.ApiResponse;
import com.studentautomation.dto.response.AttendanceResponseDTO;
import com.studentautomation.dto.response.BulkAttendanceResponseDTO;
import com.studentautomation.service.AttendanceService;
import jakarta.validation.Valid;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

/**
 * Controller class for teacher attendance APIs.
 *
 * Purpose:
 * This controller allows teachers to mark single attendance,
 * mark bulk attendance, and view attendance records marked by them.
 *
 * @author Yashvanth
 */
@RestController
@RequestMapping("/api/teacher/attendance")
public class TeacherAttendanceController {

    private final AttendanceService attendanceService;

    /**
     * Constructor used to inject AttendanceService.
     *
     * @param attendanceService service containing attendance business logic
     */
    public TeacherAttendanceController(AttendanceService attendanceService) {
        this.attendanceService = attendanceService;
    }

    /**
     * Marks attendance for one student.
     *
     * Purpose:
     * This API allows a logged-in teacher to mark attendance
     * for one student for a specific date, subject, and period.
     *
     * API:
     * POST /api/teacher/attendance
     *
     * @param request attendance request data
     * @param authentication logged-in teacher authentication object
     * @return saved attendance response
     */
    @PostMapping
    public ResponseEntity<ApiResponse<AttendanceResponseDTO>> markAttendance(
            @Valid @RequestBody AttendanceRequestDTO request,
            Authentication authentication
    ) {

        String teacherEmail = authentication.getName();

        AttendanceResponseDTO response =
                attendanceService.markAttendance(request, teacherEmail);

        return ResponseEntity.ok(
                new ApiResponse<>(
                        true,
                        "Attendance marked successfully",
                        response
                )
        );
    }

    /**
     * Marks attendance for multiple students.
     *
     * Purpose:
     * This API allows a logged-in teacher to submit attendance
     * for multiple students in a single request.
     *
     * The teacher email is taken from the authenticated JWT token.
     * Therefore, teacherId must not be accepted from the request body.
     *
     * API:
     * POST /api/teacher/attendance/bulk
     *
     * @param request bulk attendance request containing multiple student records
     * @param authentication logged-in teacher authentication object
     * @return bulk attendance processing result
     */
    @PostMapping("/bulk")
    public ResponseEntity<ApiResponse<BulkAttendanceResponseDTO>> markBulkAttendance(
            @Valid @RequestBody BulkAttendanceRequestDTO request,
            Authentication authentication
    ) {

        String teacherEmail = authentication.getName();

        BulkAttendanceResponseDTO response =
                attendanceService.markBulkAttendance(request, teacherEmail);

        return ResponseEntity.ok(
                new ApiResponse<>(
                        true,
                        "Bulk attendance processed successfully",
                        response
                )
        );
    }

    /**
     * Gets attendance records marked by the logged-in teacher on a date.
     *
     * Purpose:
     * This API allows a teacher to view attendance records marked by them
     * for a specific date.
     *
     * API:
     * GET /api/teacher/attendance?date=2026-07-16
     *
     * @param date attendance date
     * @param authentication logged-in teacher authentication object
     * @return list of attendance records
     */
    @GetMapping
    public ResponseEntity<ApiResponse<List<AttendanceResponseDTO>>> getAttendanceByDate(
            @RequestParam("date")
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
            LocalDate date,
            Authentication authentication
    ) {

        String teacherEmail = authentication.getName();

        List<AttendanceResponseDTO> response =
                attendanceService.getAttendanceByDateForTeacher(
                        date,
                        teacherEmail
                );

        return ResponseEntity.ok(
                new ApiResponse<>(
                        true,
                        "Attendance records fetched successfully",
                        response
                )
        );
    }
}