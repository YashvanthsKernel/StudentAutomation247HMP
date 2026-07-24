package com.studentautomation.dto.response;

import java.time.LocalDate;
import java.time.LocalDateTime;

public record AcademicYearResponseDTO(
        Long id,
        String yearCode,
        LocalDate startDate,
        LocalDate endDate,
        Boolean isCurrent,
        String status,
        LocalDateTime createdAt
) {
}
