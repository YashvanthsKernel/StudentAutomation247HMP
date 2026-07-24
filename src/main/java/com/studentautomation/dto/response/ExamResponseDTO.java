package com.studentautomation.dto.response;

import java.time.LocalDate;
import java.time.LocalDateTime;

public record ExamResponseDTO(
        Long id,
        String examName,
        String examCode,
        String academicYear,
        LocalDate startDate,
        LocalDate endDate,
        String status,
        Boolean active,
        LocalDateTime createdAt
) {
}
