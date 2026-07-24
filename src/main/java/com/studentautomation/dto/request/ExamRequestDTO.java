package com.studentautomation.dto.request;

import jakarta.validation.constraints.NotBlank;
import java.time.LocalDate;

public record ExamRequestDTO(
        @NotBlank(message = "Exam name is required")
        String examName,

        @NotBlank(message = "Exam code is required")
        String examCode,

        @NotBlank(message = "Academic year is required")
        String academicYear,

        LocalDate startDate,
        LocalDate endDate
) {
}
