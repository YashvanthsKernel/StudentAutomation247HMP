package com.studentautomation.service;

import com.studentautomation.dto.request.CourseRequestDTO;
import com.studentautomation.dto.response.CourseResponseDTO;
import java.util.List;

public interface CourseService {
    CourseResponseDTO createCourse(CourseRequestDTO request);
    List<CourseResponseDTO> getAllCourses();
    CourseResponseDTO getCourseById(Long id);
    CourseResponseDTO updateCourse(Long id, CourseRequestDTO request);
    CourseResponseDTO activateCourse(Long id);
    CourseResponseDTO deactivateCourse(Long id);
}
