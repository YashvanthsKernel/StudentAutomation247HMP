package com.studentautomation.service.impl;

import com.studentautomation.dto.request.TimetableSlotRequestDTO;
import com.studentautomation.dto.response.TimetableSlotResponseDTO;
import com.studentautomation.entity.*;
import com.studentautomation.exception.DuplicateResourceException;
import com.studentautomation.exception.InvalidRequestException;
import com.studentautomation.exception.ResourceNotFoundException;
import com.studentautomation.repository.*;
import com.studentautomation.service.TimetableService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Service
public class TimetableServiceImpl implements TimetableService {

    private final TimetableSlotRepository timetableSlotRepository;
    private final SubjectRepository subjectRepository;
    private final TeacherRepository teacherRepository;
    private final StudentRepository studentRepository;

    public TimetableServiceImpl(
            TimetableSlotRepository timetableSlotRepository,
            SubjectRepository subjectRepository,
            TeacherRepository teacherRepository,
            StudentRepository studentRepository
    ) {
        this.timetableSlotRepository = timetableSlotRepository;
        this.subjectRepository = subjectRepository;
        this.teacherRepository = teacherRepository;
        this.studentRepository = studentRepository;
    }

    @Override
    @Transactional
    public TimetableSlotResponseDTO createSlot(TimetableSlotRequestDTO request) {
        Subject subject = subjectRepository.findById(request.subjectId())
                .orElseThrow(() -> new ResourceNotFoundException("Subject not found"));
        Teacher teacher = teacherRepository.findById(request.teacherId())
                .orElseThrow(() -> new ResourceNotFoundException("Teacher not found"));

        String dayUpper = request.dayOfWeek().trim().toUpperCase();
        String deptUpper = request.departmentCode().trim().toUpperCase();
        String secUpper = request.section().trim().toUpperCase();
        String yearUpper = request.academicYear().trim();

        // 1. Conflict check: Teacher already has a period at this time
        if (timetableSlotRepository.existsByTeacher_IdAndDayOfWeekIgnoreCaseAndPeriodNumberAndAcademicYear(
                teacher.getId(), dayUpper, request.periodNumber(), yearUpper
        )) {
            throw new DuplicateResourceException("Teacher " + teacher.getName() + " already has a class assigned on " + dayUpper + " period " + request.periodNumber());
        }

        // 2. Conflict check: Class already has a subject at this period
        if (timetableSlotRepository.existsByDepartmentCodeAndSemesterAndSectionAndDayOfWeekIgnoreCaseAndPeriodNumberAndAcademicYear(
                deptUpper, request.semester(), secUpper, dayUpper, request.periodNumber(), yearUpper
        )) {
            throw new DuplicateResourceException("Class " + deptUpper + "-" + request.semester() + secUpper + " already has a subject assigned on " + dayUpper + " period " + request.periodNumber());
        }

        TimetableSlot slot = new TimetableSlot();
        slot.setDepartmentCode(deptUpper);
        slot.setSemester(request.semester());
        slot.setSection(secUpper);
        slot.setSubject(subject);
        slot.setTeacher(teacher);
        slot.setDayOfWeek(dayUpper);
        slot.setPeriodNumber(request.periodNumber());
        slot.setRoomNumber(request.roomNumber());
        slot.setAcademicYear(yearUpper);
        slot.setActive(true);

        return mapToDTO(timetableSlotRepository.save(slot));
    }

    @Override
    @Transactional
    public List<TimetableSlotResponseDTO> createBulkSlots(List<TimetableSlotRequestDTO> requests) {
        List<TimetableSlotResponseDTO> list = new ArrayList<>();
        for (TimetableSlotRequestDTO req : requests) {
            list.add(createSlot(req));
        }
        return list;
    }

    @Override
    @Transactional(readOnly = true)
    public List<TimetableSlotResponseDTO> getAllSlots() {
        return timetableSlotRepository.findAll().stream().map(this::mapToDTO).toList();
    }

    @Override
    @Transactional(readOnly = true)
    public TimetableSlotResponseDTO getSlotById(Long slotId) {
        TimetableSlot slot = timetableSlotRepository.findById(slotId)
                .orElseThrow(() -> new ResourceNotFoundException("Timetable slot not found"));
        return mapToDTO(slot);
    }

    @Override
    @Transactional
    public TimetableSlotResponseDTO updateSlot(Long slotId, TimetableSlotRequestDTO request) {
        TimetableSlot slot = timetableSlotRepository.findById(slotId)
                .orElseThrow(() -> new ResourceNotFoundException("Timetable slot not found"));

        Subject subject = subjectRepository.findById(request.subjectId())
                .orElseThrow(() -> new ResourceNotFoundException("Subject not found"));
        Teacher teacher = teacherRepository.findById(request.teacherId())
                .orElseThrow(() -> new ResourceNotFoundException("Teacher not found"));

        String dayUpper = request.dayOfWeek().trim().toUpperCase();
        String deptUpper = request.departmentCode().trim().toUpperCase();
        String secUpper = request.section().trim().toUpperCase();
        String yearUpper = request.academicYear().trim();

        // Conflict check for other slots
        for (TimetableSlot other : timetableSlotRepository.findAll()) {
            if (!other.getId().equals(slotId) &&
                    other.getDayOfWeek().equalsIgnoreCase(dayUpper) &&
                    other.getPeriodNumber().equals(request.periodNumber()) &&
                    other.getAcademicYear().equalsIgnoreCase(yearUpper)) {
                if (other.getTeacher().getId().equals(teacher.getId())) {
                    throw new DuplicateResourceException("Teacher " + teacher.getName() + " already assigned elsewhere on " + dayUpper + " period " + request.periodNumber());
                }
            }
        }

        slot.setDepartmentCode(deptUpper);
        slot.setSemester(request.semester());
        slot.setSection(secUpper);
        slot.setSubject(subject);
        slot.setTeacher(teacher);
        slot.setDayOfWeek(dayUpper);
        slot.setPeriodNumber(request.periodNumber());
        slot.setRoomNumber(request.roomNumber());
        slot.setAcademicYear(yearUpper);

        return mapToDTO(timetableSlotRepository.save(slot));
    }

    @Override
    @Transactional
    public void deleteSlot(Long slotId) {
        TimetableSlot slot = timetableSlotRepository.findById(slotId)
                .orElseThrow(() -> new ResourceNotFoundException("Timetable slot not found"));
        timetableSlotRepository.delete(slot);
    }

    @Override
    @Transactional(readOnly = true)
    public List<String> getConflicts() {
        List<String> conflicts = new ArrayList<>();
        List<TimetableSlot> slots = timetableSlotRepository.findAll();
        for (int i = 0; i < slots.size(); i++) {
            for (int j = i + 1; j < slots.size(); j++) {
                TimetableSlot s1 = slots.get(i);
                TimetableSlot s2 = slots.get(j);

                if (s1.getDayOfWeek() != null && s1.getDayOfWeek().equalsIgnoreCase(s2.getDayOfWeek()) &&
                        s1.getPeriodNumber() != null && s1.getPeriodNumber().equals(s2.getPeriodNumber()) &&
                        s1.getAcademicYear() != null && s1.getAcademicYear().equalsIgnoreCase(s2.getAcademicYear())) {

                    if (s1.getTeacher() != null && s2.getTeacher() != null && s1.getTeacher().getId().equals(s2.getTeacher().getId())) {
                        conflicts.add("Teacher Collision: " + s1.getTeacher().getName() + " double-booked on " + s1.getDayOfWeek() + " period " + s1.getPeriodNumber());
                    }
                    if (s1.getDepartmentCode() != null && s1.getDepartmentCode().equalsIgnoreCase(s2.getDepartmentCode()) &&
                            s1.getSemester() != null && s1.getSemester().equals(s2.getSemester()) &&
                            s1.getSection() != null && s1.getSection().equalsIgnoreCase(s2.getSection())) {
                        conflicts.add("Class Collision: " + s1.getDepartmentCode() + "-" + s1.getSemester() + s1.getSection() + " double-booked on " + s1.getDayOfWeek() + " period " + s1.getPeriodNumber());
                    }
                }
            }
        }
        return conflicts;
    }

    @Override
    @Transactional(readOnly = true)
    public List<TimetableSlotResponseDTO> getTeacherTimetable(String teacherEmail) {
        Teacher teacher = teacherRepository.findByUser_Email(teacherEmail)
                .orElseThrow(() -> new ResourceNotFoundException("Teacher profile not found"));
        return timetableSlotRepository.findByTeacher_IdAndActiveTrue(teacher.getId())
                .stream().map(this::mapToDTO).toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<TimetableSlotResponseDTO> getTeacherTimetableToday(String teacherEmail) {
        Teacher teacher = teacherRepository.findByUser_Email(teacherEmail)
                .orElseThrow(() -> new ResourceNotFoundException("Teacher profile not found"));
        String today = LocalDate.now().getDayOfWeek().name();
        return timetableSlotRepository.findByTeacher_IdAndDayOfWeekIgnoreCaseAndActiveTrue(teacher.getId(), today)
                .stream().map(this::mapToDTO).toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<TimetableSlotResponseDTO> getStudentTimetable(String studentEmail) {
        Student student = studentRepository.findByUser_Email(studentEmail)
                .orElseThrow(() -> new ResourceNotFoundException("Student profile not found"));
        return timetableSlotRepository.findByDepartmentCodeAndSemesterAndSectionAndActiveTrue(
                student.getDepartment(), student.getSemester(), student.getSection()
        ).stream().map(this::mapToDTO).toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<TimetableSlotResponseDTO> getStudentTimetableToday(String studentEmail) {
        Student student = studentRepository.findByUser_Email(studentEmail)
                .orElseThrow(() -> new ResourceNotFoundException("Student profile not found"));
        String today = LocalDate.now().getDayOfWeek().name();
        return timetableSlotRepository.findByDepartmentCodeAndSemesterAndSectionAndDayOfWeekIgnoreCaseAndActiveTrue(
                student.getDepartment(), student.getSemester(), student.getSection(), today
        ).stream().map(this::mapToDTO).toList();
    }

    private TimetableSlotResponseDTO mapToDTO(TimetableSlot s) {
        return new TimetableSlotResponseDTO(
                s.getId(),
                s.getDepartmentCode(),
                s.getSemester(),
                s.getSection(),
                s.getSubject().getId(),
                s.getSubject().getSubjectCode(),
                s.getSubject().getSubjectName(),
                s.getTeacher().getId(),
                s.getTeacher().getName(),
                s.getDayOfWeek(),
                s.getPeriodNumber(),
                s.getRoomNumber(),
                s.getAcademicYear(),
                s.getActive(),
                s.getCreatedAt()
        );
    }
}
