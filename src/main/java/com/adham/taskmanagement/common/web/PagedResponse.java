package com.adham.taskmanagement.common.web;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import org.springframework.data.domain.Page;

import java.util.List;

@Schema(description = "One page of results with navigation metadata")
public record PagedResponse<T>(
        @Schema(
                description = "Results on the current page",
                requiredMode = Schema.RequiredMode.REQUIRED
        )
        List<T> content,

        @Schema(
                description = "Zero-based current page index",
                example = "0",
                requiredMode = Schema.RequiredMode.REQUIRED
        )
        int page,

        @Schema(
                description = "Maximum number of results per page",
                example = "20",
                requiredMode = Schema.RequiredMode.REQUIRED
        )
        int size,

        @JsonProperty("total_elements")
        @Schema(
                description = "Total number of matching results",
                example = "1",
                requiredMode = Schema.RequiredMode.REQUIRED
        )
        long totalElements,

        @JsonProperty("total_pages")
        @Schema(
                description = "Total number of available pages",
                example = "1",
                requiredMode = Schema.RequiredMode.REQUIRED
        )
        int totalPages,

        @Schema(
                description = "Whether this is the first page",
                example = "true",
                requiredMode = Schema.RequiredMode.REQUIRED
        )
        boolean first,

        @Schema(
                description = "Whether this is the last page",
                example = "true",
                requiredMode = Schema.RequiredMode.REQUIRED
        )
        boolean last
) {

    public static <T> PagedResponse<T> from(Page<T> page) {
        return new PagedResponse<>(
                page.getContent(),
                page.getNumber(),
                page.getSize(),
                page.getTotalElements(),
                page.getTotalPages(),
                page.isFirst(),
                page.isLast()
        );
    }
}
