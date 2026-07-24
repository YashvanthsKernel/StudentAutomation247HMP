package com.studentautomation.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record CourseRequestDTO(
        @NotBlank(message = "Course code is required")
        @Size(max = 30, message = "Course code must not exceed 30 characters")
        String code,

        @NotBlank(message = "Course name is required")
        @Size(max = 120, message = "Course name must not exceed 120 characters")
        String name,

        Long departmentId,
        Integer durationYears,
        Integer totalSemesters
) {
}
