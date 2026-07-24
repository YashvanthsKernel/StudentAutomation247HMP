package com.studentautomation.dto.response;

import java.util.List;

public record StudentMarkSummaryResponseDTO(
        Long studentId,
        String studentRegNo,
        String studentName,
        Double totalMarksObtained,
        Double totalMaxMarks,
        Double overallPercentage,
        String overallGrade,
        Integer totalSubjects,
        Integer passedSubjects,
        Integer failedSubjects,
        List<MarkResponseDTO> subjectMarks
) {
}
