package com.adham.taskmanagement.comment;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/tasks/{taskId}/comments")
@Tag(name = "Comments", description = "Task comments")
@SecurityRequirement(name = "bearerAuth")
public class CommentController {

    private final CommentService commentService;

    public CommentController(CommentService commentService) {
        this.commentService = commentService;
    }

    @PostMapping
    @Operation(summary = "Add a comment to a task")
    public ResponseEntity<Void> createComment(
            @PathVariable Long taskId,
            @Valid @RequestBody CreateCommentRequest request,
            @Parameter(hidden = true) Authentication authentication
    ) {

        commentService.createComment(
                taskId,
                request,
                authentication.getName()
        );

        return ResponseEntity.ok().build();
    }

    @GetMapping
    @Operation(summary = "List comments for a task")
    public ResponseEntity<List<CommentResponse>> getComments(
            @PathVariable Long taskId
    ) {

        return ResponseEntity.ok(
                commentService.getComments(taskId)
        );
    }
}
