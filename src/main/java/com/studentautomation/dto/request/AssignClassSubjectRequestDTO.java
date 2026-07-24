package com.studentautomation.dto.request;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import java.util.List;

public record AssignClassSubjectRequestDTO(
        @NotBlank(message = "Department is required")
        String department,

        @NotNull(message = "Semester is required")
        @Min(1) @Max(8)
        Integer semester,

        @NotBlank(message = "Section is required")
        String section,

        @NotBlank(message = "Academic year is required")
        String academicYear,

        @NotEmpty(message = "Subject IDs list cannot be empty")
        List<Long> subjectIds
) {
}
