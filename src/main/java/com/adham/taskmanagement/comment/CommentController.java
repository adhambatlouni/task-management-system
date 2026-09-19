package com.adham.taskmanagement.comment;

import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/tasks/{taskId}/comments")
public class CommentController {

    private final CommentService commentService;

    public CommentController(CommentService commentService) {
        this.commentService = commentService;
    }

    @PostMapping
    public ResponseEntity<Void> createComment(
            @PathVariable Long taskId,
            @Valid @RequestBody CreateCommentRequest request,
            Authentication authentication
    ) {

        commentService.createComment(
                taskId,
                request,
                authentication.getName()
        );

        return ResponseEntity.ok().build();
    }

    @GetMapping
    public ResponseEntity<List<CommentResponse>> getComments(
            @PathVariable Long taskId
    ) {

        return ResponseEntity.ok(
                commentService.getComments(taskId)
        );
    }
}
