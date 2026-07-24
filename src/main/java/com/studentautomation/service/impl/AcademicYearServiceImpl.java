package com.studentautomation.service.impl;

import com.studentautomation.dto.request.AcademicYearRequestDTO;
import com.studentautomation.dto.response.AcademicYearResponseDTO;
import com.studentautomation.entity.AcademicYear;
import com.studentautomation.exception.DuplicateResourceException;
import com.studentautomation.exception.ResourceNotFoundException;
import com.studentautomation.repository.AcademicYearRepository;
import com.studentautomation.service.AcademicYearService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class AcademicYearServiceImpl implements AcademicYearService {

    private final AcademicYearRepository academicYearRepository;

    public AcademicYearServiceImpl(AcademicYearRepository academicYearRepository) {
        this.academicYearRepository = academicYearRepository;
    }

    @Override
    @Transactional
    public AcademicYearResponseDTO createAcademicYear(AcademicYearRequestDTO request) {
        String code = request.yearCode().trim();
        if (academicYearRepository.existsByYearCodeIgnoreCase(code)) {
            throw new DuplicateResourceException("Academic year code already exists");
        }

        AcademicYear ay = new AcademicYear();
        ay.setYearCode(code);
        ay.setStartDate(request.startDate());
        ay.setEndDate(request.endDate());
        ay.setIsCurrent(false);
        ay.setStatus("ACTIVE");

        AcademicYear saved = academicYearRepository.save(ay);
        return mapToDTO(saved);
    }

    @Override
    @Transactional(readOnly = true)
    public List<AcademicYearResponseDTO> getAllAcademicYears() {
        return academicYearRepository.findAll()
                .stream()
                .map(this::mapToDTO)
                .toList();
    }

    @Override
    @Transactional
    public AcademicYearResponseDTO makeCurrent(Long id) {
        AcademicYear target = academicYearRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Academic year not found"));

        // Unset current for all other academic years
        List<AcademicYear> allYears = academicYearRepository.findAll();
        for (AcademicYear ay : allYears) {
            if (ay.getId().equals(id)) {
                ay.setIsCurrent(true);
                ay.setStatus("ACTIVE");
            } else {
                ay.setIsCurrent(false);
            }
        }
        academicYearRepository.saveAll(allYears);

        return mapToDTO(target);
    }

    @Override
    @Transactional
    public AcademicYearResponseDTO closeAcademicYear(Long id) {
        AcademicYear ay = academicYearRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Academic year not found"));
        ay.setStatus("CLOSED");
        ay.setIsCurrent(false);
        return mapToDTO(academicYearRepository.save(ay));
    }

    private AcademicYearResponseDTO mapToDTO(AcademicYear ay) {
        return new AcademicYearResponseDTO(
                ay.getId(),
                ay.getYearCode(),
                ay.getStartDate(),
                ay.getEndDate(),
                ay.getIsCurrent(),
                ay.getStatus(),
                ay.getCreatedAt()
        );
    }
}
