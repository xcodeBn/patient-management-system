package io.xcodebn.common.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * Generic paginated response wrapper.
 * Use this for endpoints that return paginated data.
 *
 * @param <T> The type of items in the page
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class PagedResponse<T> {

    /**
     * The list of items in the current page
     */
    private List<T> content;

    /**
     * Pagination metadata
     */
    private PageMetadata pagination;

    @Data
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class PageMetadata {
        /**
         * Current page number (0-indexed)
         */
        private int page;

        /**
         * Number of items per page
         */
        private int size;

        /**
         * Total number of items across all pages
         */
        private long totalElements;

        /**
         * Total number of pages
         */
        private int totalPages;

        /**
         * Whether this is the first page
         */
        private boolean first;

        /**
         * Whether this is the last page
         */
        private boolean last;

        /**
         * Whether there is a next page
         */
        private boolean hasNext;

        /**
         * Whether there is a previous page
         */
        private boolean hasPrevious;
    }

    // Factory methods

    public static <T> PagedResponse<T> of(List<T> content, int page, int size, long totalElements) {
        int totalPages = (int) Math.ceil((double) totalElements / size);

        PageMetadata metadata = PageMetadata.builder()
                .page(page)
                .size(size)
                .totalElements(totalElements)
                .totalPages(totalPages)
                .first(page == 0)
                .last(page >= totalPages - 1)
                .hasNext(page < totalPages - 1)
                .hasPrevious(page > 0)
                .build();

        return PagedResponse.<T>builder()
                .content(content)
                .pagination(metadata)
                .build();
    }

    public static <T> PagedResponse<T> empty(int page, int size) {
        return of(List.of(), page, size, 0);
    }
}
