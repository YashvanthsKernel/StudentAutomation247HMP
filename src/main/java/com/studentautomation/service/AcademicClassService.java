package com.studentautomation.service;

import com.studentautomation.dto.request.AcademicClassRequestDTO;
import com.studentautomation.dto.response.AcademicClassResponseDTO;
import com.studentautomation.dto.response.StudentResponseDTO;
import java.util.List;

public interface AcademicClassService {
    AcademicClassResponseDTO createClass(AcademicClassRequestDTO request);
    List<AcademicClassResponseDTO> getAllClasses();
    AcademicClassResponseDTO getClassById(Long id);
    AcademicClassResponseDTO updateClass(Long id, AcademicClassRequestDTO request);
    AcademicClassResponseDTO activateClass(Long id);
    AcademicClassResponseDTO deactivateClass(Long id);
    List<StudentResponseDTO> getStudentsInClass(Long classId);
}
