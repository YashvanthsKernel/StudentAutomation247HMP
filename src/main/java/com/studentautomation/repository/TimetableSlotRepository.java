package com.studentautomation.repository;

import com.studentautomation.entity.TimetableSlot;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface TimetableSlotRepository extends JpaRepository<TimetableSlot, Long> {
    List<TimetableSlot> findByTeacher_IdAndActiveTrue(Long teacherId);
    List<TimetableSlot> findByTeacher_IdAndDayOfWeekIgnoreCaseAndActiveTrue(Long teacherId, String dayOfWeek);
    List<TimetableSlot> findByDepartmentCodeAndSemesterAndSectionAndActiveTrue(String departmentCode, Integer semester, String section);
    List<TimetableSlot> findByDepartmentCodeAndSemesterAndSectionAndDayOfWeekIgnoreCaseAndActiveTrue(String departmentCode, Integer semester, String section, String dayOfWeek);
    boolean existsByTeacher_IdAndDayOfWeekIgnoreCaseAndPeriodNumberAndAcademicYear(Long teacherId, String dayOfWeek, Integer periodNumber, String academicYear);
    boolean existsByDepartmentCodeAndSemesterAndSectionAndDayOfWeekIgnoreCaseAndPeriodNumberAndAcademicYear(String dept, Integer sem, String sec, String day, Integer period, String year);
}
