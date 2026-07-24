package com.studentautomation.dto.request;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record TimetableSlotRequestDTO(
        @NotBlank(message = "Department code is required")
        String departmentCode,

        @NotNull(message = "Semester is required")
        @Min(1) @Max(8)
        Integer semester,

        @NotBlank(message = "Section is required")
        String section,

        @NotNull(message = "Subject ID is required")
        Long subjectId,

        @NotNull(message = "Teacher ID is required")
        Long teacherId,

        @NotBlank(message = "Day of week is required")
        String dayOfWeek,

        @NotNull(message = "Period number is required")
        @Min(1) @Max(10)
        Integer periodNumber,

        String roomNumber,

        @NotBlank(message = "Academic year is required")
        String academicYear
) {
}
