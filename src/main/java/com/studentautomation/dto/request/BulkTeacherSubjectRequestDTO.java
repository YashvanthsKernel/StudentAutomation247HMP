package com.studentautomation.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import java.util.List;

public record BulkTeacherSubjectRequestDTO(
        @NotNull(message = "Teacher ID is required")
        Long teacherId,

        @NotEmpty(message = "Subject IDs list cannot be empty")
        List<Long> subjectIds,

        @NotBlank(message = "Section is required")
        String section,

        @NotBlank(message = "Academic year is required")
        String academicYear
) {
}
