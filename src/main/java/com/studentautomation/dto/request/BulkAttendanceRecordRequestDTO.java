package com.studentautomation.dto.request;

import com.studentautomation.enums.AttendanceStatus;
import jakarta.validation.constraints.NotNull;

/**
 * DTO used for one student's attendance record in bulk attendance.
 *
 * Purpose:
 * This DTO represents one student's attendance status
 * inside the bulk attendance request.
 *
 * @param studentId student profile ID from students table
 * @param status attendance status like PRESENT, ABSENT, or LATE
 * @param remarks optional remarks
 *
 * @author Yashvanth
 */
public record BulkAttendanceRecordRequestDTO(

        @NotNull(message = "Student ID is required")
        Long studentId,

        @NotNull(message = "Attendance status is required")
        AttendanceStatus status,

        String remarks
) {
}