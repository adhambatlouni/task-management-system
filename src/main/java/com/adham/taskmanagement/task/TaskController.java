package com.adham.taskmanagement.task;

import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/tasks")
public class TaskController {

    private final TaskService taskService;

    public TaskController(TaskService taskService) {
        this.taskService = taskService;
    }

    @PostMapping
    public ResponseEntity<TaskResponse> createTask(
            @Valid @RequestBody CreateTaskRequest request,
            Authentication authentication
    ) {

        TaskResponse response =
                taskService.createTask(
                        request,
                        authentication.getName()
                );

        return ResponseEntity.ok(response);
    }

    @PutMapping("/{taskId}/assign")
    public ResponseEntity<TaskResponse> assignTask(
            @PathVariable Long taskId,
            @Valid @RequestBody AssignTaskRequest request,
            Authentication authentication
    ) {

        TaskResponse response = taskService.assignTask(
                taskId,
                request,
                authentication.getName()
        );

        return ResponseEntity.ok(response);
    }

    @PutMapping("/{taskId}/status")
    public ResponseEntity<TaskResponse> updateStatus(
            @PathVariable Long taskId,
            @Valid @RequestBody UpdateTaskStatusRequest request,
            Authentication authentication
    ) {

        TaskResponse response = taskService.updateStatus(
                taskId,
                request,
                authentication.getName()
        );

        return ResponseEntity.ok(response);
    }

    @GetMapping
    public ResponseEntity<List<TaskListResponse>> getTasks(
            @RequestParam(required = false) String author,
            @RequestParam(required = false) String assignee
    ) {
        return ResponseEntity.ok(
                taskService.getTasks(author, assignee)
        );
    }
}
