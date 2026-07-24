package com.studentautomation.service.impl;

import com.studentautomation.dto.request.AcademicClassRequestDTO;
import com.studentautomation.dto.response.AcademicClassResponseDTO;
import com.studentautomation.dto.response.StudentResponseDTO;
import com.studentautomation.entity.AcademicClass;
import com.studentautomation.entity.Student;
import com.studentautomation.exception.DuplicateResourceException;
import com.studentautomation.exception.ResourceNotFoundException;
import com.studentautomation.mapper.StudentMapper;
import com.studentautomation.repository.AcademicClassRepository;
import com.studentautomation.repository.StudentRepository;
import com.studentautomation.service.AcademicClassService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class AcademicClassServiceImpl implements AcademicClassService {

    private final AcademicClassRepository academicClassRepository;
    private final StudentRepository studentRepository;

    public AcademicClassServiceImpl(AcademicClassRepository academicClassRepository, StudentRepository studentRepository) {
        this.academicClassRepository = academicClassRepository;
        this.studentRepository = studentRepository;
    }

    @Override
    @Transactional
    public AcademicClassResponseDTO createClass(AcademicClassRequestDTO request) {
        String deptCode = request.departmentCode().trim().toUpperCase();
        String yearCode = request.academicYearCode().trim();
        String sectionUpper = request.section().trim().toUpperCase();

        if (academicClassRepository.existsByDepartmentCodeAndSemesterAndSectionAndAcademicYearCode(
                deptCode, request.semester(), sectionUpper, yearCode
        )) {
            throw new DuplicateResourceException("Class already exists for this department, semester, section and academic year");
        }

        AcademicClass ac = new AcademicClass();
        ac.setClassName(request.className().trim());
        ac.setDepartmentCode(deptCode);
        ac.setSemester(request.semester());
        ac.setSection(sectionUpper);
        ac.setAcademicYearCode(yearCode);
        ac.setActive(true);

        AcademicClass saved = academicClassRepository.save(ac);
        return mapToDTO(saved);
    }

    @Override
    @Transactional(readOnly = true)
    public List<AcademicClassResponseDTO> getAllClasses() {
        return academicClassRepository.findAll()
                .stream()
                .map(this::mapToDTO)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public AcademicClassResponseDTO getClassById(Long id) {
        AcademicClass ac = academicClassRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Class not found"));
        return mapToDTO(ac);
    }

    @Override
    @Transactional
    public AcademicClassResponseDTO updateClass(Long id, AcademicClassRequestDTO request) {
        AcademicClass ac = academicClassRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Class not found"));

        String deptCode = request.departmentCode().trim().toUpperCase();
        String yearCode = request.academicYearCode().trim();
        String sectionUpper = request.section().trim().toUpperCase();

        ac.setClassName(request.className().trim());
        ac.setDepartmentCode(deptCode);
        ac.setSemester(request.semester());
        ac.setSection(sectionUpper);
        ac.setAcademicYearCode(yearCode);

        return mapToDTO(academicClassRepository.save(ac));
    }

    @Override
    @Transactional
    public AcademicClassResponseDTO activateClass(Long id) {
        AcademicClass ac = academicClassRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Class not found"));
        ac.setActive(true);
        return mapToDTO(academicClassRepository.save(ac));
    }

    @Override
    @Transactional
    public AcademicClassResponseDTO deactivateClass(Long id) {
        AcademicClass ac = academicClassRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Class not found"));
        ac.setActive(false);
        return mapToDTO(academicClassRepository.save(ac));
    }

    @Override
    @Transactional(readOnly = true)
    public List<StudentResponseDTO> getStudentsInClass(Long classId) {
        AcademicClass ac = academicClassRepository.findById(classId)
                .orElseThrow(() -> new ResourceNotFoundException("Class not found"));

        // Match students by department, semester, section
        List<Student> students = studentRepository.findByDepartmentAndSemester(
                ac.getDepartmentCode(), ac.getSemester()
        );

        return students.stream()
                .filter(s -> s.getSection() != null && s.getSection().equalsIgnoreCase(ac.getSection()))
                .map(StudentMapper::toResponseDTO)
                .toList();
    }

    private AcademicClassResponseDTO mapToDTO(AcademicClass ac) {
        return new AcademicClassResponseDTO(
                ac.getId(),
                ac.getClassName(),
                ac.getDepartmentCode(),
                ac.getSemester(),
                ac.getSection(),
                ac.getAcademicYearCode(),
                ac.getActive(),
                ac.getCreatedAt()
        );
    }
}
