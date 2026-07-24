package com.studentautomation.dto.response;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

public record ExamScheduleResponseDTO(
        Long id,
        Long examId,
        String examName,
        String examCode,
        Long subjectId,
        String subjectCode,
        String subjectName,
        LocalDate examDate,
        LocalTime startTime,
        LocalTime endTime,
        String roomNumber,
        String academicYear,
        LocalDateTime createdAt
) {
}
