package com.adham.taskmanagement.task;

import com.adham.taskmanagement.common.web.PagedResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
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
