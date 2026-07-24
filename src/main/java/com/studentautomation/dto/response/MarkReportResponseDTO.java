package com.studentautomation.dto.response;

public record MarkReportResponseDTO(
        Integer totalEntries,
        Double averagePercentage,
        Double highestMarks,
        Double lowestMarks,
        Integer totalPassed,
        Integer totalFailed,
        Double passPercentage
) {
}
