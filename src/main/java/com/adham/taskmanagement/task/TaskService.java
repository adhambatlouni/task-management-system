package com.adham.taskmanagement.task;

import com.adham.taskmanagement.account.Account;
import com.adham.taskmanagement.account.AccountRepository;
import com.adham.taskmanagement.comment.CommentRepository;
import com.adham.taskmanagement.common.exception.ForbiddenOperationException;
import com.adham.taskmanagement.common.exception.ResourceNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class TaskService {

    private final TaskRepository taskRepository;
    private final AccountRepository accountRepository;
    private final CommentRepository commentRepository;

    public TaskService(
            TaskRepository taskRepository,
            AccountRepository accountRepository,
            CommentRepository commentRepository
    ) {
        this.taskRepository = taskRepository;
        this.accountRepository = accountRepository;
        this.commentRepository = commentRepository;
    }

    public TaskResponse createTask(
            CreateTaskRequest request,
            String authorEmail
    ) {

        Account author = accountRepository
                .findByEmailIgnoreCase(authorEmail)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Account not found")
                );

        Task task = new Task(
                request.title(),
                request.description(),
                TaskStatus.CREATED,
                author
        );

        Task savedTask = taskRepository.save(task);

        return toResponse(savedTask);
    }

    public List<TaskListResponse> getTasks(
            String authorEmail,
            String assigneeEmail
    ) {

        boolean hasAuthor =
                authorEmail != null && !authorEmail.isBlank();

        boolean hasAssignee =
                assigneeEmail != null && !assigneeEmail.isBlank();

        List<Task> tasks;

        if (hasAuthor && hasAssignee) {

            tasks = taskRepository
                    .findAllByAuthor_EmailIgnoreCaseAndAssignee_EmailIgnoreCaseOrderByIdDesc(
                            authorEmail,
                            assigneeEmail
                    );

        } else if (hasAuthor) {

            tasks = taskRepository
                    .findAllByAuthor_EmailIgnoreCaseOrderByIdDesc(
                            authorEmail
                    );

        } else if (hasAssignee) {

            tasks = taskRepository
                    .findAllByAssignee_EmailIgnoreCaseOrderByIdDesc(
                            assigneeEmail
                    );

        } else {

            tasks = taskRepository
                    .findAllByOrderByIdDesc();
        }

        if (tasks.isEmpty()) {
            return List.of();
        }

        List<Long> taskIds = tasks.stream()
                .map(Task::getId)
                .toList();

        Map<Long, Long> commentCounts =
                commentRepository
                        .countCommentsByTaskIds(taskIds)
                        .stream()
                        .collect(Collectors.toMap(
                                row -> (Long) row[0],
                                row -> (Long) row[1]
                        ));

        return tasks.stream()
                .map(task -> toListResponse(
                        task,
                        commentCounts.getOrDefault(
                                task.getId(),
                                0L
                        )
                ))
                .toList();
    }

    public TaskResponse assignTask(
            Long taskId,
            AssignTaskRequest request,
            String currentUserEmail
    ) {

        Task task = taskRepository
                .findById(taskId)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Task not found")
                );

        if (!task.getAuthor()
                .getEmail()
                .equalsIgnoreCase(currentUserEmail)) {

            throw new ForbiddenOperationException(
                    "Only the task author can assign it"
            );
        }

        if (request.assignee().equalsIgnoreCase("none")) {

            task.setAssignee(null);

            Task savedTask = taskRepository.save(task);

            return toResponse(savedTask);
        }

        Account assignee = accountRepository
                .findByEmailIgnoreCase(request.assignee())
                .orElseThrow(() ->
                        new ResourceNotFoundException("Assignee not found")
                );

        task.setAssignee(assignee);

        Task savedTask = taskRepository.save(task);

        return toResponse(savedTask);
    }


    public TaskResponse updateStatus(
            Long taskId,
            UpdateTaskStatusRequest request,
            String currentUserEmail
    ) {

        Task task = taskRepository
                .findById(taskId)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Task not found")
                );

        boolean isAuthor = task.getAuthor()
                .getEmail()
                .equalsIgnoreCase(currentUserEmail);

        boolean isAssignee =
                task.getAssignee() != null
                        && task.getAssignee()
                        .getEmail()
                        .equalsIgnoreCase(currentUserEmail);

        if (!isAuthor && !isAssignee) {
            throw new ForbiddenOperationException(
                    "Only the author or assignee can change the task status"
            );
        }

        task.setStatus(request.status());

        Task savedTask = taskRepository.save(task);

        return toResponse(savedTask);
    }

    private TaskResponse toResponse(Task task) {

        String assigneeEmail =
                task.getAssignee() == null
                        ? "none"
                        : task.getAssignee()
                        .getEmail()
                        .toLowerCase(Locale.ROOT);

        return new TaskResponse(
                task.getId().toString(),
                task.getTitle(),
                task.getDescription(),
                task.getStatus(),
                task.getAuthor()
                        .getEmail()
                        .toLowerCase(Locale.ROOT),
                assigneeEmail
        );
    }

    private TaskListResponse toListResponse(
            Task task,
            long totalComments
    ) {

        String assigneeEmail =
                task.getAssignee() == null
                        ? "none"
                        : task.getAssignee()
                        .getEmail()
                        .toLowerCase(Locale.ROOT);

        return new TaskListResponse(
                task.getId().toString(),
                task.getTitle(),
                task.getDescription(),
                task.getStatus(),
                task.getAuthor()
                        .getEmail()
                        .toLowerCase(Locale.ROOT),
                assigneeEmail,
                totalComments
        );
    }
}
