package com.studentautomation.dto.request;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import java.util.List;

public record BulkMarkRequestDTO(
        @NotNull(message = "Exam ID is required")
        Long examId,

        @NotNull(message = "Subject ID is required")
        Long subjectId,

        String section,
        String academicYear,
        Double maxMarks,

        @NotEmpty(message = "Student marks list cannot be empty")
        List<StudentMarkEntry> marks
) {
    public record StudentMarkEntry(
            Long studentId,
            Double marksObtained
    ) {}
}
