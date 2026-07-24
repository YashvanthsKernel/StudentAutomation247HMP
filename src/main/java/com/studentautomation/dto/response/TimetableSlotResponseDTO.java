package com.studentautomation.dto.response;

import java.time.LocalDateTime;

public record TimetableSlotResponseDTO(
        Long id,
        String departmentCode,
        Integer semester,
        String section,
        Long subjectId,
        String subjectCode,
        String subjectName,
        Long teacherId,
        String teacherName,
        String dayOfWeek,
        Integer periodNumber,
        String roomNumber,
        String academicYear,
        Boolean active,
        LocalDateTime createdAt
) {
}
