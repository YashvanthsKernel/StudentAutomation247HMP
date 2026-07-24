package com.studentautomation.dto.response;

import java.util.List;

/**
 * Generic paginated response wrapper.
 *
 * Purpose:
 * Wraps a list of items with pagination metadata
 * so frontend can implement pagination correctly.
 *
 * @param content       list of items on current page
 * @param page          current page number (0-indexed)
 * @param size          items per page
 * @param totalElements total number of records in database
 * @param totalPages    total number of pages
 * @param last          true if this is the last page
 * @param <T>           type of items in the list
 * @author Yashvanth
 */
public record PagedResponseDTO<T>(
        List<T> content,
        int page,
        int size,
        long totalElements,
        int totalPages,
        boolean last
) {
}
