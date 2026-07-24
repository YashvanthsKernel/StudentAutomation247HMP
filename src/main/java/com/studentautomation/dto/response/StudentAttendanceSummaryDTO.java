package com.studentautomation.dto.response;

public record StudentAttendanceSummaryDTO(
        Long studentId,
        String studentRegNo,
        String studentName,
        Long subjectId,
        String subjectCode,
        String subjectName,
        Integer totalClasses,
        Integer presentCount,
        Integer absentCount,
        Integer lateCount,
        Integer excusedCount,
        Double attendancePercentage,
        Integer classesNeededFor75Percent
) {
}
