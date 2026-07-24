package com.studentautomation.service;

import com.studentautomation.dto.request.DepartmentRequestDTO;
import com.studentautomation.dto.response.DepartmentResponseDTO;
import java.util.List;

public interface DepartmentService {
    DepartmentResponseDTO createDepartment(DepartmentRequestDTO request);
    List<DepartmentResponseDTO> getAllDepartments();
    DepartmentResponseDTO getDepartmentById(Long id);
    DepartmentResponseDTO updateDepartment(Long id, DepartmentRequestDTO request);
    DepartmentResponseDTO activateDepartment(Long id);
    DepartmentResponseDTO deactivateDepartment(Long id);
}
