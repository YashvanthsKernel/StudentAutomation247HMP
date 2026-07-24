package com.studentautomation.dto.response;

import java.time.LocalDateTime;

public record CourseResponseDTO(
        Long id,
        String code,
        String name,
        Long departmentId,
        String departmentCode,
        Integer durationYears,
        Integer totalSemesters,
        Boolean active,
        LocalDateTime createdAt
) {
}
