package com.studentautomation.repository;

import com.studentautomation.entity.ExamSchedule;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface ExamScheduleRepository extends JpaRepository<ExamSchedule, Long> {
    List<ExamSchedule> findByExam_Id(Long examId);
    List<ExamSchedule> findBySubject_Id(Long subjectId);
}
