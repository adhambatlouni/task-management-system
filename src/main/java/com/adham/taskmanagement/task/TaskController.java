package com.adham.taskmanagement.task;

import com.adham.taskmanagement.common.openapi.ApiProblemResponse;
import com.adham.taskmanagement.common.openapi.OpenApiExamples;
import com.adham.taskmanagement.common.web.EntityTagVersion;
import com.adham.taskmanagement.common.web.PagedResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.headers.Header;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(
        value = "/api/tasks",
        produces = MediaType.APPLICATION_JSON_VALUE
)
@Tag(
        name = "Tasks",
        description = "Create, retrieve, filter, assign, and transition versioned tasks"
)
@SecurityRequirement(name = "bearerAuth")
public class TaskController {

    private final TaskService taskService;

    public TaskController(TaskService taskService) {
        this.taskService = taskService;
    }

    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
    @Operation(
            summary = "Create a task",
            description = "Creates an unassigned task in CREATED status for the authenticated account. The response ETag contains version 0."
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Task created",
                    headers = @Header(
                            name = HttpHeaders.ETAG,
                            description = "Current task version",
                            schema = @Schema(
                                    type = "string",
                                    example = OpenApiExamples.ETAG_ZERO
                            )
                    )
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Task details are invalid",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_PROBLEM_JSON_VALUE,
                            schema = @Schema(implementation = ApiProblemResponse.class),
                            examples = @ExampleObject(
                                    value = OpenApiExamples.TASK_VALIDATION
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
                                    value = OpenApiExamples.ACCOUNT_NOT_FOUND
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

        return versioned(response);
    }

    @PutMapping(
            value = "/{taskId}/assign",
            consumes = MediaType.APPLICATION_JSON_VALUE
    )
    @Operation(
            summary = "Assign or unassign a task",
            description = "Only the task author may assign a registered account or send 'none' to remove the current assignee. Supply the latest ETag through If-Match."
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Task assignment updated",
                    headers = @Header(
                            name = HttpHeaders.ETAG,
                            description = "Updated task version",
                            schema = @Schema(
                                    type = "string",
                                    example = OpenApiExamples.ETAG_ONE
                            )
                    )
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Assignee value is invalid",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_PROBLEM_JSON_VALUE,
                            schema = @Schema(implementation = ApiProblemResponse.class),
                            examples = @ExampleObject(
                                    value = OpenApiExamples.ASSIGNMENT_VALIDATION
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
                                    value = OpenApiExamples.ASSIGNMENT_FORBIDDEN
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
                                    value = OpenApiExamples.ASSIGNEE_NOT_FOUND
                            )
                    )
            ),
            @ApiResponse(
                    responseCode = "412",
                    description = "The supplied task version is stale",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_PROBLEM_JSON_VALUE,
                            schema = @Schema(implementation = ApiProblemResponse.class),
                            examples = @ExampleObject(
                                    value = OpenApiExamples.ASSIGNMENT_PRECONDITION_FAILED
                            )
                    )
            ),
            @ApiResponse(
                    responseCode = "428",
                    description = "If-Match header is required",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_PROBLEM_JSON_VALUE,
                            schema = @Schema(implementation = ApiProblemResponse.class),
                            examples = @ExampleObject(
                                    value = OpenApiExamples.ASSIGNMENT_PRECONDITION_REQUIRED
                            )
                    )
            )
    })
    public ResponseEntity<TaskResponse> assignTask(
            @Parameter(description = "Task identifier", example = "42")
            @PathVariable Long taskId,
            @Valid @RequestBody AssignTaskRequest request,
            @Parameter(
                    description = "Quoted task version from the latest response",
                    required = true,
                    example = OpenApiExamples.IF_MATCH_ZERO
            )
            @RequestHeader(value = HttpHeaders.IF_MATCH, required = false)
            String ifMatch,
            @Parameter(hidden = true) Authentication authentication
    ) {

        TaskResponse response = taskService.assignTask(
                taskId,
                request,
                authentication.getName(),
                EntityTagVersion.parseRequired(ifMatch)
        );

        return versioned(response);
    }

    @PutMapping(
            value = "/{taskId}/status",
            consumes = MediaType.APPLICATION_JSON_VALUE
    )
    @Operation(
            summary = "Update a task status",
            description = "The task author or current assignee may transition its status. Supply the latest ETag through If-Match to prevent stale writes."
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Task status updated",
                    headers = @Header(
                            name = HttpHeaders.ETAG,
                            description = "Updated task version",
                            schema = @Schema(
                                    type = "string",
                                    example = OpenApiExamples.ETAG_TWO
                            )
                    )
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Task status is invalid",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_PROBLEM_JSON_VALUE,
                            schema = @Schema(implementation = ApiProblemResponse.class),
                            examples = @ExampleObject(
                                    value = OpenApiExamples.MALFORMED_REQUEST_BODY
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
                                    value = OpenApiExamples.STATUS_FORBIDDEN
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
                                    value = OpenApiExamples.TASK_NOT_FOUND
                            )
                    )
            ),
            @ApiResponse(
                    responseCode = "412",
                    description = "The supplied task version is stale",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_PROBLEM_JSON_VALUE,
                            schema = @Schema(implementation = ApiProblemResponse.class),
                            examples = @ExampleObject(
                                    value = OpenApiExamples.STATUS_PRECONDITION_FAILED
                            )
                    )
            ),
            @ApiResponse(
                    responseCode = "428",
                    description = "If-Match header is required",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_PROBLEM_JSON_VALUE,
                            schema = @Schema(implementation = ApiProblemResponse.class),
                            examples = @ExampleObject(
                                    value = OpenApiExamples.STATUS_PRECONDITION_REQUIRED
                            )
                    )
            )
    })
    public ResponseEntity<TaskResponse> updateStatus(
            @Parameter(description = "Task identifier", example = "42")
            @PathVariable Long taskId,
            @Valid @RequestBody UpdateTaskStatusRequest request,
            @Parameter(
                    description = "Quoted task version from the latest response",
                    required = true,
                    example = OpenApiExamples.IF_MATCH_ONE
            )
            @RequestHeader(value = HttpHeaders.IF_MATCH, required = false)
            String ifMatch,
            @Parameter(hidden = true) Authentication authentication
    ) {

        TaskResponse response = taskService.updateStatus(
                taskId,
                request,
                authentication.getName(),
                EntityTagVersion.parseRequired(ifMatch)
        );

        return versioned(response);
    }

    @GetMapping("/{taskId}")
    @Operation(
            summary = "Get a task with its current version",
            description = "Returns the complete task and exposes its optimistic-lock version in the ETag response header."
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Task retrieved",
                    headers = @Header(
                            name = HttpHeaders.ETAG,
                            description = "Current task version",
                            schema = @Schema(
                                    type = "string",
                                    example = OpenApiExamples.ETAG_TWO
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
                                    value = OpenApiExamples.TASK_NOT_FOUND
                            )
                    )
            )
    })
    public ResponseEntity<TaskResponse> getTask(
            @Parameter(description = "Task identifier", example = "42")
            @PathVariable Long taskId
    ) {
        return versioned(taskService.getTask(taskId));
    }

    @GetMapping
    @Operation(
            summary = "List, filter, paginate, and sort tasks",
            description = "Returns all tasks or filters them by author and assignee email. Results use zero-based pagination and controlled sorting."
    )
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
            @Parameter(
                    description = "Filter by author email",
                    example = "owner@example.com"
            )
            @RequestParam(required = false) String author,
            @Parameter(
                    description = "Filter by assignee email",
                    example = "assignee@example.com"
            )
            @RequestParam(required = false) String assignee,
            @Parameter(
                    description = "Zero-based page index",
                    example = "0"
            )
            @RequestParam(defaultValue = "0") int page,
            @Parameter(
                    description = "Items per page from 1 to 100",
                    example = "20"
            )
            @RequestParam(defaultValue = "20") int size,
            @Parameter(
                    description = "Sort as field,direction. Fields: id, title, status, created_at, updated_at. Directions: asc, desc",
                    example = "created_at,desc"
            )
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

    private ResponseEntity<TaskResponse> versioned(TaskResponse response) {
        return ResponseEntity
                .ok()
                .eTag(EntityTagVersion.format(response.version()))
                .body(response);
    }
}
