package com.adham.taskmanagement.comment;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import com.adham.taskmanagement.account.Account;
import com.adham.taskmanagement.account.AccountRepository;
import com.adham.taskmanagement.task.Task;
import com.adham.taskmanagement.task.TaskRepository;

import java.util.List;
import java.util.Locale;

@Service
public class CommentService {

    private final CommentRepository commentRepository;
    private final TaskRepository taskRepository;
    private final AccountRepository accountRepository;

    public CommentService(
            CommentRepository commentRepository,
            TaskRepository taskRepository,
            AccountRepository accountRepository
    ) {
        this.commentRepository = commentRepository;
        this.taskRepository = taskRepository;
        this.accountRepository = accountRepository;
    }

    public void createComment(
            Long taskId,
            CreateCommentRequest request,
            String currentUserEmail
    ) {

        Task task = taskRepository
                .findById(taskId)
                .orElseThrow(() ->
                        new ResponseStatusException(
                                HttpStatus.NOT_FOUND,
                                "Task not found"
                        )
                );

        Account author = accountRepository
                .findByEmailIgnoreCase(currentUserEmail)
                .orElseThrow(() ->
                        new ResponseStatusException(
                                HttpStatus.NOT_FOUND,
                                "Account not found"
                        )
                );

        Comment comment = new Comment(
                request.text(),
                task,
                author
        );

        commentRepository.save(comment);
    }

    public List<CommentResponse> getComments(Long taskId) {

        if (!taskRepository.existsById(taskId)) {
            throw new ResponseStatusException(
                    HttpStatus.NOT_FOUND,
                    "Task not found"
            );
        }

        return commentRepository
                .findAllByTask_IdOrderByIdDesc(taskId)
                .stream()
                .map(this::toResponse)
                .toList();
    }

    private CommentResponse toResponse(Comment comment) {

        return new CommentResponse(
                comment.getId().toString(),
                comment.getTask().getId().toString(),
                comment.getText(),
                comment.getAuthor()
                        .getEmail()
                        .toLowerCase(Locale.ROOT)
        );
    }
}
