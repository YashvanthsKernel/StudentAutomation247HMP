package com.studentautomation.service.impl;

import com.studentautomation.dto.response.BulkUploadErrorDTO;
import com.studentautomation.dto.response.BulkUploadResponseDTO;
import com.studentautomation.entity.Student;
import com.studentautomation.entity.Teacher;
import com.studentautomation.entity.User;
import com.studentautomation.enums.AccountStatus;
import com.studentautomation.enums.Role;
import com.studentautomation.exception.InvalidRequestException;
import com.studentautomation.repository.StudentRepository;
import com.studentautomation.repository.TeacherRepository;
import com.studentautomation.repository.UserRepository;
import com.studentautomation.service.ExcelUploadService;
import org.apache.poi.ss.usermodel.DataFormatter;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.*;

/**
 * Service implementation for Excel upload operations.
 *
 * Purpose:
 * This class reads Excel files uploaded by ADMIN and creates
 * multiple Student or Teacher accounts and profiles in bulk.
 *
 * Important:
 * This implementation follows an all-or-nothing approach.
 * If even one row has an error, no data will be saved.
 *
 * @author Yashvanth
 */
@Service
public class ExcelUploadServiceImpl implements ExcelUploadService {

    private final UserRepository userRepository;
    private final StudentRepository studentRepository;
    private final TeacherRepository teacherRepository;
    private final PasswordEncoder passwordEncoder;

    public ExcelUploadServiceImpl(UserRepository userRepository,
                                  StudentRepository studentRepository,
                                  TeacherRepository teacherRepository,
                                  PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.studentRepository = studentRepository;
        this.teacherRepository = teacherRepository;
        this.passwordEncoder = passwordEncoder;
    }

    /**
     * Uploads an Excel file and creates multiple student accounts and profiles.
     *
     * Purpose:
     * Admin can upload one Excel file and create hundreds of students at once.
     *
     * Expected Excel column order:
     * name | email | password | confirmPassword | regNo | phoneNo | department | semester | section | academicYear
     *
     * @param file Excel file uploaded by admin
     * @return bulk upload response with success count and row-wise errors
     */
    @Override
    @Transactional
    public BulkUploadResponseDTO uploadStudents(MultipartFile file) {
        validateExcelFile(file);

        List<BulkUploadErrorDTO> errors = new ArrayList<>();
        List<StudentExcelRow> validRows = new ArrayList<>();

        Set<String> excelEmails = new HashSet<>();
        Set<String> excelRegNos = new HashSet<>();

        try (Workbook workbook = new XSSFWorkbook(file.getInputStream())) {
            Sheet sheet = getFirstSheet(workbook);
            DataFormatter formatter = new DataFormatter();

            int totalRows = 0;

            for (int rowIndex = 1; rowIndex <= sheet.getLastRowNum(); rowIndex++) {
                Row row = sheet.getRow(rowIndex);

                if (isRowEmpty(row, formatter)) {
                    continue;
                }

                totalRows++;

                int excelRowNumber = rowIndex + 1;

                String name = getCellValue(row, 0, formatter);
                String email = getCellValue(row, 1, formatter).toLowerCase();
                String password = getCellValue(row, 2, formatter);
                String confirmPassword = getCellValue(row, 3, formatter);
                String regNo = getCellValue(row, 4, formatter);
                String phoneNo = getCellValue(row, 5, formatter);
                String department = getCellValue(row, 6, formatter);
                String semesterText = getCellValue(row, 7, formatter);
                String section = getCellValue(row, 8, formatter);
                String academicYear = getCellValue(row, 9, formatter);

                validateRequiredField(errors, excelRowNumber, "name", name);
                validateRequiredField(errors, excelRowNumber, "email", email);
                validateRequiredField(errors, excelRowNumber, "password", password);
                validateRequiredField(errors, excelRowNumber, "confirmPassword", confirmPassword);
                validateRequiredField(errors, excelRowNumber, "regNo", regNo);
                validateRequiredField(errors, excelRowNumber, "department", department);
                validateRequiredField(errors, excelRowNumber, "semester", semesterText);
                validateRequiredField(errors, excelRowNumber, "section", section);
                validateRequiredField(errors, excelRowNumber, "academicYear", academicYear);

                Integer semester = parseInteger(errors, excelRowNumber, "semester", semesterText);

                validatePasswordMatch(errors, excelRowNumber, password, confirmPassword);
                validateEmailDuplicate(errors, excelRowNumber, email, excelEmails);
                validateRegNoDuplicate(errors, excelRowNumber, regNo, excelRegNos);

                if (hasText(email) && userRepository.existsByEmail(email)) {
                    errors.add(new BulkUploadErrorDTO(
                            excelRowNumber,
                            "email",
                            "Email already exists in database"
                    ));
                }

                if (hasText(regNo) && studentRepository.existsByRegNo(regNo)) {
                    errors.add(new BulkUploadErrorDTO(
                            excelRowNumber,
                            "regNo",
                            "Register number already exists in database"
                    ));
                }

                validRows.add(new StudentExcelRow(
                        excelRowNumber,
                        name,
                        email,
                        password,
                        regNo,
                        phoneNo,
                        department,
                        semester,
                        section,
                        academicYear
                ));
            }

            if (totalRows == 0) {
                throw new InvalidRequestException("Excel file does not contain student data");
            }

            if (!errors.isEmpty()) {
                return new BulkUploadResponseDTO(
                        totalRows,
                        0,
                        countFailedRows(errors),
                        errors
                );
            }

            for (StudentExcelRow studentRow : validRows) {
                User savedUser = createUser(
                        studentRow.name(),
                        studentRow.email(),
                        studentRow.password(),
                        Role.STUDENT
                );

                Student student = new Student();
                student.setUser(savedUser);
                student.setName(studentRow.name());
                student.setRegNo(studentRow.regNo());
                student.setPhoneNo(studentRow.phoneNo());
                student.setDepartment(studentRow.department());
                student.setSemester(studentRow.semester());
                student.setSection(studentRow.section());
                student.setAcademicYear(studentRow.academicYear());
                student.setActive(true);

                studentRepository.save(student);
            }

            return new BulkUploadResponseDTO(
                    totalRows,
                    validRows.size(),
                    0,
                    Collections.emptyList()
            );

        } catch (IOException exception) {
            throw new InvalidRequestException("Unable to read student Excel file");
        }
    }

    /**
     * Uploads an Excel file and creates multiple teacher accounts and profiles.
     *
     * Purpose:
     * Admin can upload one Excel file and create many teachers at once.
     *
     * Expected Excel column order:
     * name | email | password | confirmPassword | employeeId | phoneNo | department | designation | qualification | experienceYears
     *
     * @param file Excel file uploaded by admin
     * @return bulk upload response with success count and row-wise errors
     */
    @Override
    @Transactional
    public BulkUploadResponseDTO uploadTeachers(MultipartFile file) {
        validateExcelFile(file);

        List<BulkUploadErrorDTO> errors = new ArrayList<>();
        List<TeacherExcelRow> validRows = new ArrayList<>();

        Set<String> excelEmails = new HashSet<>();
        Set<String> excelEmployeeIds = new HashSet<>();

        try (Workbook workbook = new XSSFWorkbook(file.getInputStream())) {
            Sheet sheet = getFirstSheet(workbook);
            DataFormatter formatter = new DataFormatter();

            int totalRows = 0;

            for (int rowIndex = 1; rowIndex <= sheet.getLastRowNum(); rowIndex++) {
                Row row = sheet.getRow(rowIndex);

                if (isRowEmpty(row, formatter)) {
                    continue;
                }

                totalRows++;

                int excelRowNumber = rowIndex + 1;

                String name = getCellValue(row, 0, formatter);
                String email = getCellValue(row, 1, formatter).toLowerCase();
                String password = getCellValue(row, 2, formatter);
                String confirmPassword = getCellValue(row, 3, formatter);
                String employeeId = getCellValue(row, 4, formatter);
                String phoneNo = getCellValue(row, 5, formatter);
                String department = getCellValue(row, 6, formatter);
                String designation = getCellValue(row, 7, formatter);
                String qualification = getCellValue(row, 8, formatter);
                String experienceYearsText = getCellValue(row, 9, formatter);

                validateRequiredField(errors, excelRowNumber, "name", name);
                validateRequiredField(errors, excelRowNumber, "email", email);
                validateRequiredField(errors, excelRowNumber, "password", password);
                validateRequiredField(errors, excelRowNumber, "confirmPassword", confirmPassword);
                validateRequiredField(errors, excelRowNumber, "employeeId", employeeId);
                validateRequiredField(errors, excelRowNumber, "department", department);
                validateRequiredField(errors, excelRowNumber, "designation", designation);
                validateRequiredField(errors, excelRowNumber, "qualification", qualification);
                validateRequiredField(errors, excelRowNumber, "experienceYears", experienceYearsText);

                Integer experienceYears = parseInteger(
                        errors,
                        excelRowNumber,
                        "experienceYears",
                        experienceYearsText
                );

                validatePasswordMatch(errors, excelRowNumber, password, confirmPassword);
                validateEmailDuplicate(errors, excelRowNumber, email, excelEmails);
                validateEmployeeIdDuplicate(errors, excelRowNumber, employeeId, excelEmployeeIds);

                if (hasText(email) && userRepository.existsByEmail(email)) {
                    errors.add(new BulkUploadErrorDTO(
                            excelRowNumber,
                            "email",
                            "Email already exists in database"
                    ));
                }

                if (hasText(employeeId) && teacherRepository.existsByEmployeeId(employeeId)) {
                    errors.add(new BulkUploadErrorDTO(
                            excelRowNumber,
                            "employeeId",
                            "Employee ID already exists in database"
                    ));
                }

                validRows.add(new TeacherExcelRow(
                        excelRowNumber,
                        name,
                        email,
                        password,
                        employeeId,
                        phoneNo,
                        department,
                        designation,
                        qualification,
                        experienceYears
                ));
            }

            if (totalRows == 0) {
                throw new InvalidRequestException("Excel file does not contain teacher data");
            }

            if (!errors.isEmpty()) {
                return new BulkUploadResponseDTO(
                        totalRows,
                        0,
                        countFailedRows(errors),
                        errors
                );
            }

            for (TeacherExcelRow teacherRow : validRows) {
                User savedUser = createUser(
                        teacherRow.name(),
                        teacherRow.email(),
                        teacherRow.password(),
                        Role.TEACHER
                );

                Teacher teacher = new Teacher();
                teacher.setUser(savedUser);
                teacher.setName(teacherRow.name());
                teacher.setEmployeeId(teacherRow.employeeId());
                teacher.setPhoneNo(teacherRow.phoneNo());
                teacher.setDepartment(teacherRow.department());
                teacher.setDesignation(teacherRow.designation());
                teacher.setQualification(teacherRow.qualification());
                teacher.setExperienceYears(teacherRow.experienceYears());
                teacher.setActive(true);

                teacherRepository.save(teacher);
            }

            return new BulkUploadResponseDTO(
                    totalRows,
                    validRows.size(),
                    0,
                    Collections.emptyList()
            );

        } catch (IOException exception) {
            throw new InvalidRequestException("Unable to read teacher Excel file");
        }
    }

    /**
     * Creates a user account for student or teacher.
     *
     * Purpose:
     * This private method avoids repeated user creation logic
     * inside student and teacher Excel upload methods.
     *
     * @param name user full name
     * @param email user email
     * @param rawPassword plain password from Excel
     * @param role role to assign for the user
     * @return saved user entity
     */
    private User createUser(String name, String email, String rawPassword, Role role) {
        User user = new User();
        user.setName(name);
        user.setEmail(email);
        user.setPassword(passwordEncoder.encode(rawPassword));
        user.setRole(role);
        user.setAccountStatus(AccountStatus.ACTIVE);

        return userRepository.save(user);
    }

    /**
     * Validates uploaded Excel file.
     *
     * Purpose:
     * This method checks whether uploaded file is empty
     * and whether it is an .xlsx file.
     *
     * @param file uploaded Excel file
     */
    private void validateExcelFile(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new InvalidRequestException("Excel file is required");
        }

        String fileName = file.getOriginalFilename();

        if (fileName == null || !fileName.toLowerCase().endsWith(".xlsx")) {
            throw new InvalidRequestException("Only .xlsx Excel files are allowed");
        }
    }

    /**
     * Gets the first sheet from workbook.
     *
     * Purpose:
     * This method ensures the uploaded Excel contains at least one sheet.
     *
     * @param workbook uploaded Excel workbook
     * @return first sheet from workbook
     */
    private Sheet getFirstSheet(Workbook workbook) {
        if (workbook.getNumberOfSheets() == 0) {
            throw new InvalidRequestException("Excel file does not contain any sheet");
        }

        return workbook.getSheetAt(0);
    }

    /**
     * Reads cell value as text.
     *
     * Purpose:
     * Excel cells can contain string, number, or date values.
     * DataFormatter helps read them safely as display text.
     *
     * @param row Excel row
     * @param cellIndex Excel cell index
     * @param formatter Apache POI data formatter
     * @return cell value as trimmed text
     */
    private String getCellValue(Row row, int cellIndex, DataFormatter formatter) {
        if (row == null || row.getCell(cellIndex) == null) {
            return "";
        }

        return formatter.formatCellValue(row.getCell(cellIndex)).trim();
    }

    /**
     * Checks whether an Excel row is empty.
     *
     * Purpose:
     * This helps skip blank rows in uploaded Excel files.
     *
     * @param row Excel row
     * @param formatter Apache POI data formatter
     * @return true if row is empty, otherwise false
     */
    private boolean isRowEmpty(Row row, DataFormatter formatter) {
        if (row == null) {
            return true;
        }

        for (int cellIndex = 0; cellIndex < row.getLastCellNum(); cellIndex++) {
            String value = getCellValue(row, cellIndex, formatter);

            if (hasText(value)) {
                return false;
            }
        }

        return true;
    }

    /**
     * Validates required Excel field.
     *
     * Purpose:
     * This method adds a row-wise error if a required value is missing.
     *
     * @param errors error list
     * @param rowNumber Excel row number
     * @param fieldName field name
     * @param value cell value
     */
    private void validateRequiredField(List<BulkUploadErrorDTO> errors,
                                       int rowNumber,
                                       String fieldName,
                                       String value) {
        if (!hasText(value)) {
            errors.add(new BulkUploadErrorDTO(
                    rowNumber,
                    fieldName,
                    fieldName + " is required"
            ));
        }
    }

    /**
     * Validates password and confirm password.
     *
     * Purpose:
     * This method ensures both password fields match before creating accounts.
     *
     * @param errors error list
     * @param rowNumber Excel row number
     * @param password password value
     * @param confirmPassword confirm password value
     */
    private void validatePasswordMatch(List<BulkUploadErrorDTO> errors,
                                       int rowNumber,
                                       String password,
                                       String confirmPassword) {
        if (hasText(password) && hasText(confirmPassword) && !password.equals(confirmPassword)) {
            errors.add(new BulkUploadErrorDTO(
                    rowNumber,
                    "confirmPassword",
                    "Password and confirm password do not match"
            ));
        }
    }

    /**
     * Validates duplicate email inside same Excel file.
     *
     * Purpose:
     * This prevents two rows in the same Excel from using the same email.
     *
     * @param errors error list
     * @param rowNumber Excel row number
     * @param email email value
     * @param excelEmails set of emails already found in Excel
     */
    private void validateEmailDuplicate(List<BulkUploadErrorDTO> errors,
                                        int rowNumber,
                                        String email,
                                        Set<String> excelEmails) {
        if (hasText(email) && !excelEmails.add(email.toLowerCase())) {
            errors.add(new BulkUploadErrorDTO(
                    rowNumber,
                    "email",
                    "Duplicate email found inside Excel file"
            ));
        }
    }

    /**
     * Validates duplicate register number inside same Excel file.
     *
     * Purpose:
     * This prevents two student rows from using the same register number.
     *
     * @param errors error list
     * @param rowNumber Excel row number
     * @param regNo register number
     * @param excelRegNos set of register numbers already found in Excel
     */
    private void validateRegNoDuplicate(List<BulkUploadErrorDTO> errors,
                                        int rowNumber,
                                        String regNo,
                                        Set<String> excelRegNos) {
        if (hasText(regNo) && !excelRegNos.add(regNo.toUpperCase())) {
            errors.add(new BulkUploadErrorDTO(
                    rowNumber,
                    "regNo",
                    "Duplicate register number found inside Excel file"
            ));
        }
    }

    /**
     * Validates duplicate employee ID inside same Excel file.
     *
     * Purpose:
     * This prevents two teacher rows from using the same employee ID.
     *
     * @param errors error list
     * @param rowNumber Excel row number
     * @param employeeId employee ID
     * @param excelEmployeeIds set of employee IDs already found in Excel
     */
    private void validateEmployeeIdDuplicate(List<BulkUploadErrorDTO> errors,
                                             int rowNumber,
                                             String employeeId,
                                             Set<String> excelEmployeeIds) {
        if (hasText(employeeId) && !excelEmployeeIds.add(employeeId.toUpperCase())) {
            errors.add(new BulkUploadErrorDTO(
                    rowNumber,
                    "employeeId",
                    "Duplicate employee ID found inside Excel file"
            ));
        }
    }

    /**
     * Converts Excel text value into integer.
     *
     * Purpose:
     * This method safely converts semester and experienceYears values.
     *
     * @param errors error list
     * @param rowNumber Excel row number
     * @param fieldName field name
     * @param value Excel cell value
     * @return integer value if valid, otherwise null
     */
    private Integer parseInteger(List<BulkUploadErrorDTO> errors,
                                 int rowNumber,
                                 String fieldName,
                                 String value) {
        if (!hasText(value)) {
            return null;
        }

        try {
            return Integer.parseInt(value);
        } catch (NumberFormatException exception) {
            errors.add(new BulkUploadErrorDTO(
                    rowNumber,
                    fieldName,
                    fieldName + " must be a valid number"
            ));

            return null;
        }
    }

    /**
     * Counts failed rows from error list.
     *
     * Purpose:
     * One row can have multiple errors, but failure count should count
     * unique failed rows, not total error messages.
     *
     * @param errors row-wise error list
     * @return unique failed row count
     */
    private int countFailedRows(List<BulkUploadErrorDTO> errors) {
        Set<Integer> failedRows = new HashSet<>();

        for (BulkUploadErrorDTO error : errors) {
            failedRows.add(error.rowNumber());
        }

        return failedRows.size();
    }

    /**
     * Checks whether text has real value.
     *
     * Purpose:
     * This avoids repeated null and blank checks.
     *
     * @param value text value
     * @return true if value is not null and not blank
     */
    private boolean hasText(String value) {
        return value != null && !value.trim().isEmpty();
    }

    /**
     * Internal record for holding one valid student Excel row.
     *
     * Purpose:
     * This record temporarily stores parsed student data before saving.
     */
    private record StudentExcelRow(
            int rowNumber,
            String name,
            String email,
            String password,
            String regNo,
            String phoneNo,
            String department,
            Integer semester,
            String section,
            String academicYear
    ) {
    }

    /**
     * Internal record for holding one valid teacher Excel row.
     *
     * Purpose:
     * This record temporarily stores parsed teacher data before saving.
     */
    private record TeacherExcelRow(
            int rowNumber,
            String name,
            String email,
            String password,
            String employeeId,
            String phoneNo,
            String department,
            String designation,
            String qualification,
            Integer experienceYears
    ) {
    }
}