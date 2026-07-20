package com.studentautomation.dto.request;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.time.LocalDate;
import java.util.List;

/**
 * DTO used for marking attendance for multiple students.
 *
 * Purpose:
 * This record contains common attendance details such as the
 * attendance date, subject, and period, together with a list
 * of individual student attendance records.
 *
 * @param attendanceDate date for which attendance is being marked
 * @param subjectId ID of the subject for which attendance is being marked
 * @param periodNumber class period number
 * @param records list containing attendance details for multiple students
 *
 * @author Yashvanth
 */
public record BulkAttendanceRequestDTO(

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
         * List containing attendance details for each student.
         *
         * @Valid ensures validation is also applied to every
         * BulkAttendanceRecordRequestDTO inside the list.
         */
        @Valid
        @NotEmpty(message = "Attendance records are required")
        List<BulkAttendanceRecordRequestDTO> records
) {
}