package com.studentautomation.dto.request;

import com.studentautomation.enums.AttendanceStatus;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.time.LocalDate;

/**
 * Request DTO for marking student attendance.
 *
 * Purpose:
 * This record receives attendance data from the teacher
 * when marking attendance for a student.
 *
 * @param studentId ID of the student whose attendance is being marked
 * @param attendanceDate date for which attendance is being marked
 * @param subjectId ID of the subject for which attendance is being marked
 * @param periodNumber class period number
 * @param status attendance status such as PRESENT, ABSENT, LATE, or EXCUSED
 * @param remarks optional remarks added by the teacher
 *
 * @author Yashvanth
 */
public record AttendanceRequestDTO(

        /**
         * ID of the student whose attendance is being marked.
         */
        @NotNull(message = "Student ID is required")
        @Positive(message = "Student ID must be greater than zero")
        Long studentId,

        /**
         * Date for which attendance is being marked.
         */
        @NotNull(message = "Attendance date is required")
        LocalDate attendanceDate,

        /**
         * ID of the subject for which attendance is being marked.
         *
         * The backend will use this ID to fetch the Subject
         * entity from the subjects table.
         */
        @NotNull(message = "Subject ID is required")
        @Positive(message = "Subject ID must be greater than zero")
        Long subjectId,

        /**
         * Period number during which the class occurred.
         */
        @NotNull(message = "Period number is required")
        @Min(value = 1, message = "Period number must be at least 1")
        @Max(value = 10, message = "Period number cannot be more than 10")
        Integer periodNumber,

        /**
         * Attendance status of the student.
         */
        @NotNull(message = "Attendance status is required")
        AttendanceStatus status,

        /**
         * Optional remarks added by the teacher.
         */
        String remarks
) {
}