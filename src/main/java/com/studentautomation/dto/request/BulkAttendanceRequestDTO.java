package com.studentautomation.dto.request;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;
import java.util.List;

/**
 * DTO used for marking attendance for multiple students.
 *
 * Purpose:
 * This DTO contains common attendance details like date,
 * subject and period, along with a list of student attendance records.
 *
 * @param attendanceDate attendance date
 * @param subjectName subject name
 * @param periodNumber period number
 * @param records list of student attendance records
 *
 * @author Yashvanth
 */
public record BulkAttendanceRequestDTO(

        @NotNull(message = "Attendance date is required")
        LocalDate attendanceDate,

        @NotBlank(message = "Subject name is required")
        String subjectName,

        @NotNull(message = "Period number is required")
        @Min(value = 1, message = "Period number must be at least 1")
        Integer periodNumber,

        @Valid
        @NotEmpty(message = "Attendance records are required")
        List<BulkAttendanceRecordRequestDTO> records
) {
}