package com.studentautomation.dto.request;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import java.util.List;

public record BulkStudentSubjectRequestDTO(
        @NotEmpty(message = "Student IDs list cannot be empty")
        List<Long> studentIds,

        @NotEmpty(message = "Subject IDs list cannot be empty")
        List<Long> subjectIds,

        @NotNull(message = "Academic year is required")
        String academicYear
) {
}
