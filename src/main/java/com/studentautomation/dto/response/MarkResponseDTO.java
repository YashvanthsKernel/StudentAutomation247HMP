package com.studentautomation.dto.response;

import java.time.LocalDateTime;

public record MarkResponseDTO(
        Long id,
        Long studentId,
        String studentRegNo,
        String studentName,
        Long subjectId,
        String subjectCode,
        String subjectName,
        Long examId,
        String examName,
        String examCode,
        Double marksObtained,
        Double maxMarks,
        Double percentage,
        String grade,
        Boolean isPass,
        String section,
        String academicYear,
        Boolean isPublished,
        LocalDateTime createdAt
) {
}
