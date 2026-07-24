package com.studentautomation.service.impl;

import com.studentautomation.dto.request.CourseRequestDTO;
import com.studentautomation.dto.response.CourseResponseDTO;
import com.studentautomation.entity.Course;
import com.studentautomation.entity.Department;
import com.studentautomation.exception.DuplicateResourceException;
import com.studentautomation.exception.ResourceNotFoundException;
import com.studentautomation.repository.CourseRepository;
import com.studentautomation.repository.DepartmentRepository;
import com.studentautomation.service.CourseService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class CourseServiceImpl implements CourseService {

    private final CourseRepository courseRepository;
    private final DepartmentRepository departmentRepository;

    public CourseServiceImpl(CourseRepository courseRepository, DepartmentRepository departmentRepository) {
        this.courseRepository = courseRepository;
        this.departmentRepository = departmentRepository;
    }

    @Override
    @Transactional
    public CourseResponseDTO createCourse(CourseRequestDTO request) {
        String codeUpper = request.code().trim().toUpperCase();
        if (courseRepository.existsByCodeIgnoreCase(codeUpper)) {
            throw new DuplicateResourceException("Course code already exists");
        }

        Department dept = null;
        if (request.departmentId() != null) {
            dept = departmentRepository.findById(request.departmentId())
                    .orElseThrow(() -> new ResourceNotFoundException("Department not found"));
        }

        Course course = new Course();
        course.setCode(codeUpper);
        course.setName(request.name().trim());
        course.setDepartment(dept);
        course.setDurationYears(request.durationYears());
        course.setTotalSemesters(request.totalSemesters());
        course.setActive(true);

        Course saved = courseRepository.save(course);
        return mapToDTO(saved);
    }

    @Override
    @Transactional(readOnly = true)
    public List<CourseResponseDTO> getAllCourses() {
        return courseRepository.findAll()
                .stream()
                .map(this::mapToDTO)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public CourseResponseDTO getCourseById(Long id) {
        Course course = courseRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Course not found"));
        return mapToDTO(course);
    }

    @Override
    @Transactional
    public CourseResponseDTO updateCourse(Long id, CourseRequestDTO request) {
        Course course = courseRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Course not found"));

        String codeUpper = request.code().trim().toUpperCase();
        if (!course.getCode().equalsIgnoreCase(codeUpper) && courseRepository.existsByCodeIgnoreCase(codeUpper)) {
            throw new DuplicateResourceException("Course code already exists");
        }

        Department dept = null;
        if (request.departmentId() != null) {
            dept = departmentRepository.findById(request.departmentId())
                    .orElseThrow(() -> new ResourceNotFoundException("Department not found"));
        }

        course.setCode(codeUpper);
        course.setName(request.name().trim());
        course.setDepartment(dept);
        course.setDurationYears(request.durationYears());
        course.setTotalSemesters(request.totalSemesters());

        return mapToDTO(courseRepository.save(course));
    }

    @Override
    @Transactional
    public CourseResponseDTO activateCourse(Long id) {
        Course course = courseRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Course not found"));
        course.setActive(true);
        return mapToDTO(courseRepository.save(course));
    }

    @Override
    @Transactional
    public CourseResponseDTO deactivateCourse(Long id) {
        Course course = courseRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Course not found"));
        course.setActive(false);
        return mapToDTO(courseRepository.save(course));
    }

    private CourseResponseDTO mapToDTO(Course c) {
        return new CourseResponseDTO(
                c.getId(),
                c.getCode(),
                c.getName(),
                c.getDepartment() != null ? c.getDepartment().getId() : null,
                c.getDepartment() != null ? c.getDepartment().getCode() : null,
                c.getDurationYears(),
                c.getTotalSemesters(),
                c.getActive(),
                c.getCreatedAt()
        );
    }
}
