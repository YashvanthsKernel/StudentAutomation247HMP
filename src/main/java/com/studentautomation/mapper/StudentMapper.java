package com.studentautomation.mapper;

import com.studentautomation.dto.response.StudentResponseDTO;
import com.studentautomation.entity.Student;

/**
 * Mapper class for Student entity.
 *
 * Purpose:
 * This class converts Student entity objects into StudentResponseDTO objects.
 * It helps us avoid sending full entity data directly to frontend.
 *
 * @author Yashvanth
 */
public class StudentMapper {

    /**
     * Converts Student entity to StudentResponseDTO.
     *
     * Purpose:
     * This method controls what student data should be sent to frontend/Postman.
     *
     * @param student student entity object from database
     * @return student response DTO
     */
    public static StudentResponseDTO toResponseDTO(Student student) {

        return new StudentResponseDTO(
                student.getId(),
                student.getRegNo(),
                student.getName(),
                student.getUser().getEmail(),
                student.getPhoneNo(),
                student.getDepartment(),
                student.getSemester(),
                student.getSection(),
                student.getAcademicYear(),
                student.getActive(),
                student.getCreatedAt(),
                student.getUpdatedAt()
        );
    }
}