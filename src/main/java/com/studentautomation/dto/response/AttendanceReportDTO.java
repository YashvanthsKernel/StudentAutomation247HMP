package com.studentautomation.dto.response;

public record AttendanceReportDTO(
        Integer totalRecords,
        Integer totalPresent,
        Integer totalAbsent,
        Integer totalLate,
        Integer totalExcused,
        Double overallAttendancePercentage,
        Integer totalStudentsWithShortage
) {
}
