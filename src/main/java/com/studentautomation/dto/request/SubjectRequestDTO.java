package com.studentautomation.dto.request;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Request DTO used to create or update an academic subject.
 *
 * Purpose:
 * Accepts and validates subject information received from
 * the client before it is processed by the service layer.
 *
 * @author Yashvanth
 */
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SubjectRequestDTO {

    /**
     * Unique code assigned to the subject.
     *
     * Example:
     * CS301, CS302, MA201
     */
    @NotBlank(message = "Subject code is required")
    @Size(
            min = 2,
            max = 30,
            message = "Subject code must contain between 2 and 30 characters"
    )
    private String subjectCode;

    /**
     * Name of the academic subject.
     */
    @NotBlank(message = "Subject name is required")
    @Size(
            min = 2,
            max = 150,
            message = "Subject name must contain between 2 and 150 characters"
    )
    private String subjectName;

    /**
     * Department to which the subject belongs.
     *
     * Example:
     * CSE, ECE, MECH
     */
    @NotBlank(message = "Department is required")
    @Size(
            min = 2,
            max = 100,
            message = "Department must contain between 2 and 100 characters"
    )
    private String department;

    /**
     * Semester in which the subject is taught.
     *
     * The accepted semester range is 1 to 8.
     */
    @NotNull(message = "Semester is required")
    @Min(
            value = 1,
            message = "Semester must be at least 1"
    )
    @Max(
            value = 8,
            message = "Semester cannot be greater than 8"
    )
    private Integer semester;

    /**
     * Number of credits assigned to the subject.
     */
    @NotNull(message = "Credits are required")
    @Min(
            value = 1,
            message = "Credits must be at least 1"
    )
    @Max(
            value = 10,
            message = "Credits cannot be greater than 10"
    )
    private Integer credits;
}