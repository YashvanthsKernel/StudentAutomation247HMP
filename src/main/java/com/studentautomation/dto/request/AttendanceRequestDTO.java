package com.studentautomation.dto.request;

import com.studentautomation.enums.AttendanceStatus;
import jakarta.validation.constraints.*;

import java.time.LocalDate;

/**
 * Request DTO for marking student attendance.
 *
 * Purpose:
 * This class receives attendance data from the teacher when marking
 * attendance for a student.
 *
 * @param studentId ID of the student whose attendance is being marked
 * @param attendanceDate date of attendance
 * @param subjectName subject name for which attendance is marked
 * @param periodNumber class period number
 * @param status attendance status such as PRESENT, ABSENT, or LATE
 * @param remarks optional remarks from teacher
 *
 * @author Yashvanth
 */
public record AttendanceRequestDTO(

        @NotNull(message = "Student ID is required")
        Long studentId,

        @NotNull(message = "Attendance date is required")
        LocalDate attendanceDate,

        @NotBlank(message = "Subject name is required")
        String subjectName,

        @NotNull(message = "Period number is required")
        @Min(value = 1, message = "Period number must be at least 1")
        @Max(value = 10, message = "Period number cannot be more than 10")
        Integer periodNumber,

        @NotNull(message = "Attendance status is required")
        AttendanceStatus status,

        String remarks
) {
}