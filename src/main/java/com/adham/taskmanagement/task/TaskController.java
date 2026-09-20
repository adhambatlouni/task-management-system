package com.adham.taskmanagement.task;

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
import jakarta.validation.Valid;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/tasks")
@Tag(name = "Tasks", description = "Task creation, assignment, status, and listing")
@SecurityRequirement(name = "bearerAuth")
public class TaskController {

    private final TaskService taskService;

    public TaskController(TaskService taskService) {
        this.taskService = taskService;
    }

    @PostMapping
    @Operation(summary = "Create a task")
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Task created"
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Task details are invalid",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_PROBLEM_JSON_VALUE,
                            schema = @Schema(implementation = ApiProblemResponse.class),
                            examples = @ExampleObject(
                                    value = OpenApiExamples.VALIDATION_ERROR
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
                    description = "Authenticated account not found",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_PROBLEM_JSON_VALUE,
                            schema = @Schema(implementation = ApiProblemResponse.class),
                            examples = @ExampleObject(
                                    value = OpenApiExamples.NOT_FOUND
                            )
                    )
            )
    })
    public ResponseEntity<TaskResponse> createTask(
            @Valid @RequestBody CreateTaskRequest request,
            @Parameter(hidden = true) Authentication authentication
    ) {

        TaskResponse response =
                taskService.createTask(
                        request,
                        authentication.getName()
                );

        return ResponseEntity.ok(response);
    }

    @PutMapping("/{taskId}/assign")
    @Operation(summary = "Assign or unassign a task")
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Task assignment updated"
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Assignee value is invalid",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_PROBLEM_JSON_VALUE,
                            schema = @Schema(implementation = ApiProblemResponse.class),
                            examples = @ExampleObject(
                                    value = OpenApiExamples.VALIDATION_ERROR
                            )
                    )
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "Authentication required",
                    content = @Content
            ),
            @ApiResponse(
                    responseCode = "403",
                    description = "Only the task author can assign it",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_PROBLEM_JSON_VALUE,
                            schema = @Schema(implementation = ApiProblemResponse.class),
                            examples = @ExampleObject(
                                    value = OpenApiExamples.FORBIDDEN
                            )
                    )
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Task or assignee not found",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_PROBLEM_JSON_VALUE,
                            schema = @Schema(implementation = ApiProblemResponse.class),
                            examples = @ExampleObject(
                                    value = OpenApiExamples.NOT_FOUND
                            )
                    )
            )
    })
    public ResponseEntity<TaskResponse> assignTask(
            @PathVariable Long taskId,
            @Valid @RequestBody AssignTaskRequest request,
            @Parameter(hidden = true) Authentication authentication
    ) {

        TaskResponse response = taskService.assignTask(
                taskId,
                request,
                authentication.getName()
        );

        return ResponseEntity.ok(response);
    }

    @PutMapping("/{taskId}/status")
    @Operation(summary = "Update a task status")
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Task status updated"
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Task status is invalid",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_PROBLEM_JSON_VALUE,
                            schema = @Schema(implementation = ApiProblemResponse.class),
                            examples = @ExampleObject(
                                    value = OpenApiExamples.VALIDATION_ERROR
                            )
                    )
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "Authentication required",
                    content = @Content
            ),
            @ApiResponse(
                    responseCode = "403",
                    description = "Only the author or assignee can change the status",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_PROBLEM_JSON_VALUE,
                            schema = @Schema(implementation = ApiProblemResponse.class),
                            examples = @ExampleObject(
                                    value = OpenApiExamples.FORBIDDEN
                            )
                    )
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Task not found",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_PROBLEM_JSON_VALUE,
                            schema = @Schema(implementation = ApiProblemResponse.class),
                            examples = @ExampleObject(
                                    value = OpenApiExamples.NOT_FOUND
                            )
                    )
            )
    })
    public ResponseEntity<TaskResponse> updateStatus(
            @PathVariable Long taskId,
            @Valid @RequestBody UpdateTaskStatusRequest request,
            @Parameter(hidden = true) Authentication authentication
    ) {

        TaskResponse response = taskService.updateStatus(
                taskId,
                request,
                authentication.getName()
        );

        return ResponseEntity.ok(response);
    }

    @GetMapping
    @Operation(summary = "List, filter, paginate, and sort tasks")
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Tasks retrieved"
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Invalid pagination or sorting parameters",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_PROBLEM_JSON_VALUE,
                            schema = @Schema(implementation = ApiProblemResponse.class),
                            examples = @ExampleObject(
                                    value = OpenApiExamples.BAD_REQUEST
                            )
                    )
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "Authentication required",
                    content = @Content
            )
    })
    public ResponseEntity<PagedResponse<TaskListResponse>> getTasks(
            @RequestParam(required = false) String author,
            @RequestParam(required = false) String assignee,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(defaultValue = "created_at,desc") String sort
    ) {
        return ResponseEntity.ok(
                taskService.getTasks(
                        author,
                        assignee,
                        page,
                        size,
                        sort
                )
        );
    }
}
