package com.studentautomation.dto.response;

public record DashboardSummaryDTO(
        long totalStudents,
        long totalTeachers,
        long totalSubjects,
        long totalDepartments,
        long totalClasses,
        long totalExams,
        double overallAttendanceRate,
        double overallPassRate
) {
}
