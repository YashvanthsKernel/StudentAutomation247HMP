package com.studentautomation.dto.request;

import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;
import java.time.LocalTime;

public record ExamScheduleRequestDTO(
        @NotNull(message = "Exam ID is required")
        Long examId,

        @NotNull(message = "Subject ID is required")
        Long subjectId,

        @NotNull(message = "Exam date is required")
        LocalDate examDate,

        LocalTime startTime,
        LocalTime endTime,
        String roomNumber,
        String academicYear
) {
}
