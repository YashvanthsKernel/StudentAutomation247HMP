package com.studentautomation.dto.response;

import java.time.LocalDateTime;

public record DepartmentResponseDTO(
        Long id,
        String code,
        String name,
        Boolean active,
        LocalDateTime createdAt
) {
}
