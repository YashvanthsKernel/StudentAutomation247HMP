package com.studentautomation.dto.response;

import java.time.LocalDateTime;

public record AcademicClassResponseDTO(
        Long id,
        String className,
        String departmentCode,
        Integer semester,
        String section,
        String academicYearCode,
        Boolean active,
        LocalDateTime createdAt
) {
}
