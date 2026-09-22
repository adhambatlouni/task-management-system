package com.adham.taskmanagement.comment;

import com.adham.taskmanagement.common.openapi.ApiProblemResponse;
import com.adham.taskmanagement.common.openapi.OpenApiExamples;
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

import java.util.List;

@RestController
@RequestMapping(
        value = "/api/tasks/{taskId}/comments",
        produces = MediaType.APPLICATION_JSON_VALUE
)
@Tag(
        name = "Comments",
        description = "Add and retrieve comments attached to tasks"
)
@SecurityRequirement(name = "bearerAuth")
public class CommentController {

    private final CommentService commentService;

    public CommentController(CommentService commentService) {
        this.commentService = commentService;
    }

    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
    @Operation(
            summary = "Add a comment to a task",
            description = "Adds a comment attributed to the authenticated account."
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Comment added",
                    content = @Content
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Comment text is invalid",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_PROBLEM_JSON_VALUE,
                            schema = @Schema(implementation = ApiProblemResponse.class),
                            examples = @ExampleObject(
                                    value = OpenApiExamples.COMMENT_VALIDATION
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
                    description = "Task or authenticated account not found",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_PROBLEM_JSON_VALUE,
                            schema = @Schema(implementation = ApiProblemResponse.class),
                            examples = @ExampleObject(
                                    value = OpenApiExamples.COMMENT_TASK_NOT_FOUND
                            )
                    )
            )
    })
    public ResponseEntity<Void> createComment(
            @Parameter(description = "Task identifier", example = "42")
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
    @Operation(
            summary = "List comments for a task",
            description = "Returns the task's comments in newest-first order."
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Comments retrieved"
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
                                    value = OpenApiExamples.COMMENT_TASK_NOT_FOUND
                            )
                    )
            )
    })
    public ResponseEntity<List<CommentResponse>> getComments(
            @Parameter(description = "Task identifier", example = "42")
            @PathVariable Long taskId
    ) {

        return ResponseEntity.ok(
                commentService.getComments(taskId)
        );
    }
}
