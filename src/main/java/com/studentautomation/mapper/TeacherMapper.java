package com.studentautomation.mapper;

import com.studentautomation.dto.response.TeacherResponseDTO;
import com.studentautomation.entity.Teacher;

/**
 * Mapper class for Teacher entity.
 *
 * Purpose:
 * This class converts Teacher entity objects into TeacherResponseDTO objects.
 * It helps us avoid sending full entity data directly to frontend.
 *
 * @author Yashvanth
 */
public class TeacherMapper {

    /**
     * Converts Teacher entity to TeacherResponseDTO.
     *
     * Purpose:
     * This method controls what teacher data should be sent to frontend/Postman.
     *
     * @param teacher teacher entity object from database
     * @return teacher response DTO
     */
    public static TeacherResponseDTO toResponseDTO(Teacher teacher) {

        return new TeacherResponseDTO(
                teacher.getId(),
                teacher.getEmployeeId(),
                teacher.getName(),
                teacher.getUser().getEmail(),
                teacher.getPhoneNo(),
                teacher.getDepartment(),
                teacher.getDesignation(),
                teacher.getQualification(),
                teacher.getExperienceYears(),
                teacher.getActive(),
                teacher.getCreatedAt(),
                teacher.getUpdatedAt()
        );
    }
}