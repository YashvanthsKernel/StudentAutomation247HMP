package com.studentautomation.dto.request;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record AcademicClassRequestDTO(
        @NotBlank(message = "Class name is required")
        String className,

        @NotBlank(message = "Department code is required")
        String departmentCode,

        @NotNull(message = "Semester is required")
        @Min(value = 1, message = "Semester must be at least 1")
        @Max(value = 8, message = "Semester must be at most 8")
        Integer semester,

        @NotBlank(message = "Section is required")
        String section,

        @NotBlank(message = "Academic year code is required")
        String academicYearCode
) {
}
