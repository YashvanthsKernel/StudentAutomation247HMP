package com.studentautomation.service;

import com.studentautomation.dto.request.BulkTeacherSubjectRequestDTO;
import com.studentautomation.dto.request.TeacherSubjectRequestDTO;
import com.studentautomation.dto.response.StudentResponseDTO;
import com.studentautomation.dto.response.TeacherSubjectResponseDTO;

import java.util.List;

public interface TeacherSubjectService {

    TeacherSubjectResponseDTO assignSubjectToTeacher(TeacherSubjectRequestDTO requestDTO);

    List<TeacherSubjectResponseDTO> getAllActiveAssignments();

    List<TeacherSubjectResponseDTO> getAssignmentsByTeacherId(Long teacherId);

    List<TeacherSubjectResponseDTO> getAssignmentsByTeacherEmail(String email);

    List<String> getSectionsForTeacherSubject(String email, Long subjectId);

    List<StudentResponseDTO> getStudentsForTeacherSubject(String email, Long subjectId);

    List<TeacherSubjectResponseDTO> getAssignmentsBySubjectId(Long subjectId);

    TeacherSubjectResponseDTO getAssignmentById(Long assignmentId);

    TeacherSubjectResponseDTO activateAssignment(Long assignmentId);

    TeacherSubjectResponseDTO deactivateAssignment(Long assignmentId);

    List<TeacherSubjectResponseDTO> bulkAssignTeacherSubjects(BulkTeacherSubjectRequestDTO request);
}