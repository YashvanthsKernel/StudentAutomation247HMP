package com.studentautomation.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

/**
 * Response DTO used to return subject information to the client.
 *
 * Purpose:
 * Prevents the Subject entity from being exposed directly
 * through the REST API.
 *
 * @author Yashvanth
 */
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SubjectResponseDTO {

    /**
     * Unique database ID of the subject.
     */
    private Long id;

    /**
     * Unique subject code.
     */
    private String subjectCode;

    /**
     * Name of the subject.
     */
    private String subjectName;

    /**
     * Department to which the subject belongs.
     */
    private String department;

    /**
     * Semester in which the subject is taught.
     */
    private Integer semester;

    /**
     * Number of academic credits assigned to the subject.
     */
    private Integer credits;

    /**
     * Indicates whether the subject is currently active.
     */
    private Boolean active;

    /**
     * Date and time when the subject was created.
     */
    private LocalDateTime createdAt;

    /**
     * Date and time when the subject was last updated.
     */
    private LocalDateTime updatedAt;
}