package com.studentautomation.dto.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

public record MarkRequestDTO(
        @NotNull(message = "Student ID is required")
        Long studentId,

        @NotNull(message = "Subject ID is required")
        Long subjectId,

        @NotNull(message = "Exam ID is required")
        Long examId,

        @NotNull(message = "Marks obtained is required")
        @Min(0)
        Double marksObtained,

        Double maxMarks,
        String section,
        String academicYear
) {
}
