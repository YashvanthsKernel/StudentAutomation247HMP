package com.studentautomation.service.impl;

import com.studentautomation.dto.request.DepartmentRequestDTO;
import com.studentautomation.dto.response.DepartmentResponseDTO;
import com.studentautomation.entity.Department;
import com.studentautomation.exception.DuplicateResourceException;
import com.studentautomation.exception.ResourceNotFoundException;
import com.studentautomation.repository.DepartmentRepository;
import com.studentautomation.service.DepartmentService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class DepartmentServiceImpl implements DepartmentService {

    private final DepartmentRepository departmentRepository;

    public DepartmentServiceImpl(DepartmentRepository departmentRepository) {
        this.departmentRepository = departmentRepository;
    }

    @Override
    @Transactional
    public DepartmentResponseDTO createDepartment(DepartmentRequestDTO request) {
        String codeUpper = request.code().trim().toUpperCase();
        if (departmentRepository.existsByCodeIgnoreCase(codeUpper)) {
            throw new DuplicateResourceException("Department code already exists");
        }
        Department dept = new Department();
        dept.setCode(codeUpper);
        dept.setName(request.name().trim());
        dept.setActive(true);
        Department saved = departmentRepository.save(dept);
        return mapToDTO(saved);
    }

    @Override
    @Transactional(readOnly = true)
    public List<DepartmentResponseDTO> getAllDepartments() {
        return departmentRepository.findAll()
                .stream()
                .map(this::mapToDTO)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public DepartmentResponseDTO getDepartmentById(Long id) {
        Department dept = departmentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Department not found"));
        return mapToDTO(dept);
    }

    @Override
    @Transactional
    public DepartmentResponseDTO updateDepartment(Long id, DepartmentRequestDTO request) {
        Department dept = departmentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Department not found"));
        String codeUpper = request.code().trim().toUpperCase();
        if (departmentRepository.existsByCodeIgnoreCaseAndIdNot(codeUpper, id)) {
            throw new DuplicateResourceException("Department code already exists for another department");
        }
        dept.setCode(codeUpper);
        dept.setName(request.name().trim());
        Department saved = departmentRepository.save(dept);
        return mapToDTO(saved);
    }

    @Override
    @Transactional
    public DepartmentResponseDTO activateDepartment(Long id) {
        Department dept = departmentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Department not found"));
        dept.setActive(true);
        return mapToDTO(departmentRepository.save(dept));
    }

    @Override
    @Transactional
    public DepartmentResponseDTO deactivateDepartment(Long id) {
        Department dept = departmentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Department not found"));
        dept.setActive(false);
        return mapToDTO(departmentRepository.save(dept));
    }

    private DepartmentResponseDTO mapToDTO(Department dept) {
        return new DepartmentResponseDTO(
                dept.getId(),
                dept.getCode(),
                dept.getName(),
                dept.getActive(),
                dept.getCreatedAt()
        );
    }
}
