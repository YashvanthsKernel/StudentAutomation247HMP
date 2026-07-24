package com.studentautomation.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import java.time.LocalDate;

public record AcademicYearRequestDTO(
        @NotBlank(message = "Year code is required (e.g. 2026-2027)")
        @Size(max = 30, message = "Year code must not exceed 30 characters")
        String yearCode,

        LocalDate startDate,
        LocalDate endDate
) {
}
