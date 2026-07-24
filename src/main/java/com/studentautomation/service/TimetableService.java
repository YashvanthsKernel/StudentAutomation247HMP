package com.studentautomation.service;

import com.studentautomation.dto.request.TimetableSlotRequestDTO;
import com.studentautomation.dto.response.TimetableSlotResponseDTO;

import java.util.List;

public interface TimetableService {
    TimetableSlotResponseDTO createSlot(TimetableSlotRequestDTO request);
    List<TimetableSlotResponseDTO> createBulkSlots(List<TimetableSlotRequestDTO> requests);
    List<TimetableSlotResponseDTO> getAllSlots();
    TimetableSlotResponseDTO getSlotById(Long slotId);
    TimetableSlotResponseDTO updateSlot(Long slotId, TimetableSlotRequestDTO request);
    void deleteSlot(Long slotId);
    List<String> getConflicts();

    List<TimetableSlotResponseDTO> getTeacherTimetable(String teacherEmail);
    List<TimetableSlotResponseDTO> getTeacherTimetableToday(String teacherEmail);

    List<TimetableSlotResponseDTO> getStudentTimetable(String studentEmail);
    List<TimetableSlotResponseDTO> getStudentTimetableToday(String studentEmail);
}
