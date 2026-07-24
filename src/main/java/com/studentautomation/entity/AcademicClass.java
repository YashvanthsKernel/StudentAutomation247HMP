package com.studentautomation.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

/**
 * Entity class for storing Academic Class/Section details.
 * Represents a combination of Department + Semester + Section + AcademicYear.
 *
 * Example: CSE Semester 3 Section A (Academic Year 2026-2027)
 *
 * @author Yashvanth
 */
@Entity
@Table(
        name = "academic_classes",
        uniqueConstraints = {
                @UniqueConstraint(name = "uk_class_dept_sem_sec_year", columnNames = {"department_code", "semester", "section", "academic_year_code"})
        }
)
public class AcademicClass {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "class_name", nullable = false, length = 100)
    private String className;

    @Column(name = "department_code", nullable = false, length = 50)
    private String departmentCode;

    @Column(nullable = false)
    private Integer semester;

    @Column(nullable = false, length = 20)
    private String section;

    @Column(name = "academic_year_code", nullable = false, length = 30)
    private String academicYearCode;

    @Column(nullable = false)
    private Boolean active = true;

    @Column(nullable = false)
    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

    public AcademicClass() {
    }

    @PrePersist
    public void beforeSave() {
        this.createdAt = LocalDateTime.now();
    }

    @PreUpdate
    public void beforeUpdate() {
        this.updatedAt = LocalDateTime.now();
    }

    public Long getId() {
        return id;
    }

    public String getClassName() {
        return className;
    }

    public void setClassName(String className) {
        this.className = className;
    }

    public String getDepartmentCode() {
        return departmentCode;
    }

    public void setDepartmentCode(String departmentCode) {
        this.departmentCode = departmentCode;
    }

    public Integer getSemester() {
        return semester;
    }

    public void setSemester(Integer semester) {
        this.semester = semester;
    }

    public String getSection() {
        return section;
    }

    public void setSection(String section) {
        this.section = section;
    }

    public String getAcademicYearCode() {
        return academicYearCode;
    }

    public void setAcademicYearCode(String academicYearCode) {
        this.academicYearCode = academicYearCode;
    }

    public Boolean getActive() {
        return active;
    }

    public void setActive(Boolean active) {
        this.active = active;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }
}
