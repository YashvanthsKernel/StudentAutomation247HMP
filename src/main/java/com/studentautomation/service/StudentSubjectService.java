package com.studentautomation.service;

import com.studentautomation.dto.request.AssignClassSubjectRequestDTO;
import com.studentautomation.dto.request.BulkStudentSubjectRequestDTO;
import com.studentautomation.dto.request.StudentSubjectRequestDTO;
import com.studentautomation.dto.response.StudentSubjectResponseDTO;

import java.util.List;

public interface StudentSubjectService {

    StudentSubjectResponseDTO assignSubjectToStudent(StudentSubjectRequestDTO requestDTO);

    List<StudentSubjectResponseDTO> getAllActiveAssignments();

    List<StudentSubjectResponseDTO> getAssignmentsByStudentId(Long studentId);

    List<StudentSubjectResponseDTO> getAssignmentsByStudentEmail(String email);

    List<StudentSubjectResponseDTO> getAssignmentsBySubjectId(Long subjectId);

    StudentSubjectResponseDTO getAssignmentById(Long assignmentId);

    StudentSubjectResponseDTO activateAssignment(Long assignmentId);

    StudentSubjectResponseDTO deactivateAssignment(Long assignmentId);

    List<StudentSubjectResponseDTO> bulkAssignStudentSubjects(BulkStudentSubjectRequestDTO request);

    List<StudentSubjectResponseDTO> assignClassToSubjects(AssignClassSubjectRequestDTO request);
}