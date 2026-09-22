package com.adham.taskmanagement.activity;

import com.adham.taskmanagement.common.openapi.ApiProblemResponse;
import com.adham.taskmanagement.common.openapi.OpenApiExamples;
import com.adham.taskmanagement.common.web.PagedResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(
        value = "/api/tasks/{taskId}/activities",
        produces = MediaType.APPLICATION_JSON_VALUE
)
@Tag(
        name = "Task activities",
        description = "Read append-only, actor-attributed task change history"
)
@SecurityRequirement(name = "bearerAuth")
public class TaskActivityController {

    private final TaskActivityService activityService;

    public TaskActivityController(TaskActivityService activityService) {
        this.activityService = activityService;
    }

    @GetMapping
    @Operation(
            summary = "List a task's activity history",
            description = "Returns append-only assignment and status events in newest-first order."
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Task activities retrieved"
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Invalid pagination parameters",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_PROBLEM_JSON_VALUE,
                            schema = @Schema(implementation = ApiProblemResponse.class),
                            examples = @ExampleObject(
                                    value = OpenApiExamples.ACTIVITY_BAD_REQUEST
                            )
                    )
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "Authentication required",
                    content = @Content
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Task not found",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_PROBLEM_JSON_VALUE,
                            schema = @Schema(implementation = ApiProblemResponse.class),
                            examples = @ExampleObject(
                                    value = OpenApiExamples.ACTIVITY_NOT_FOUND
                            )
                    )
            )
    })
    public ResponseEntity<PagedResponse<TaskActivityResponse>>
    getTaskActivities(
            @Parameter(description = "Task identifier", example = "42")
            @PathVariable Long taskId,
            @Parameter(description = "Zero-based page index", example = "0")
            @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Items per page from 1 to 100", example = "20")
            @RequestParam(defaultValue = "20") int size
    ) {
        return ResponseEntity.ok(
                activityService.getTaskActivities(taskId, page, size)
        );
    }
}
