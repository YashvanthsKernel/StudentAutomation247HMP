package com.studentautomation.repository;

import com.studentautomation.entity.AcademicClass;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface AcademicClassRepository extends JpaRepository<AcademicClass, Long> {
    boolean existsByDepartmentCodeAndSemesterAndSectionAndAcademicYearCode(
            String departmentCode, Integer semester, String section, String academicYearCode
    );
    Optional<AcademicClass> findByDepartmentCodeAndSemesterAndSectionAndAcademicYearCode(
            String departmentCode, Integer semester, String section, String academicYearCode
    );
}
