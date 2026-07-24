package com.studentautomation.repository;

import com.studentautomation.entity.AcademicYear;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface AcademicYearRepository extends JpaRepository<AcademicYear, Long> {
    Optional<AcademicYear> findByYearCodeIgnoreCase(String yearCode);
    boolean existsByYearCodeIgnoreCase(String yearCode);
    Optional<AcademicYear> findByIsCurrentTrue();
}
