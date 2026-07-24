package com.studentautomation.service;

import com.studentautomation.dto.request.AcademicYearRequestDTO;
import com.studentautomation.dto.response.AcademicYearResponseDTO;
import java.util.List;

public interface AcademicYearService {
    AcademicYearResponseDTO createAcademicYear(AcademicYearRequestDTO request);
    List<AcademicYearResponseDTO> getAllAcademicYears();
    AcademicYearResponseDTO makeCurrent(Long id);
    AcademicYearResponseDTO closeAcademicYear(Long id);
}
