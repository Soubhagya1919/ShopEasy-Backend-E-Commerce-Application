package com.soubhagya.electronic.store.dtos;

import lombok.*;

import java.util.List;

/**
 * Represents a response type that supports pagination.
 *
 * This class is a generic data structure designed to encapsulate
 * paginated results from a query, including the list of items of type T,
 * along with metadata about the pagination state such as current page number,
 * page size, total number of elements, total number of pages, and a flag
 * indicating if the current page is the last one.
 *
 * @param <T> the type of the content within the paginated response
 */
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class PageableResponse<T> {

    private List<T> content;
    private int pageNumber;
    private int pageSize;
    private long totalElements;
    private int totalPages;
    private boolean lastPage;
}
