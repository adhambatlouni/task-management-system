package com.adham.taskmanagement.task;

import com.adham.taskmanagement.account.Account;
import com.adham.taskmanagement.account.AccountRepository;
import com.adham.taskmanagement.activity.TaskActivityService;
import com.adham.taskmanagement.comment.CommentCountProjection;
import com.adham.taskmanagement.comment.CommentRepository;
import com.adham.taskmanagement.common.exception.ForbiddenOperationException;
import com.adham.taskmanagement.common.exception.InvalidRequestException;
import com.adham.taskmanagement.common.exception.PreconditionFailedException;
import com.adham.taskmanagement.common.exception.ResourceNotFoundException;
import com.adham.taskmanagement.common.web.PagedResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
public class TaskService {

    private static final int MAX_PAGE_SIZE = 100;

    private static final Map<String, String> SORT_PROPERTIES = Map.of(
            "id", "id",
            "title", "title",
            "status", "status",
            "created_at", "createdAt",
            "updated_at", "updatedAt"
    );

    private final TaskRepository taskRepository;
    private final AccountRepository accountRepository;
    private final CommentRepository commentRepository;
    private final TaskActivityService activityService;

    public TaskService(
            TaskRepository taskRepository,
            AccountRepository accountRepository,
            CommentRepository commentRepository,
            TaskActivityService activityService
    ) {
        this.taskRepository = taskRepository;
        this.accountRepository = accountRepository;
        this.commentRepository = commentRepository;
        this.activityService = activityService;
    }

    @Transactional
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

        Task savedTask = taskRepository.saveAndFlush(task);

        activityService.recordTaskCreated(savedTask, author);

        return toResponse(savedTask);
    }

    public PagedResponse<TaskListResponse> getTasks(
            String authorEmail,
            String assigneeEmail,
            int page,
            int size,
            String sort
    ) {
        Pageable pageable = createPageable(page, size, sort);

        boolean hasAuthor =
                authorEmail != null && !authorEmail.isBlank();

        boolean hasAssignee =
                assigneeEmail != null && !assigneeEmail.isBlank();

        Page<Task> tasks;

        if (hasAuthor && hasAssignee) {

            tasks = taskRepository
                    .findAllByAuthor_EmailIgnoreCaseAndAssignee_EmailIgnoreCase(
                            authorEmail,
                            assigneeEmail,
                            pageable
                    );

        } else if (hasAuthor) {

            tasks = taskRepository
                    .findAllByAuthor_EmailIgnoreCase(
                            authorEmail,
                            pageable
                    );

        } else if (hasAssignee) {

            tasks = taskRepository
                    .findAllByAssignee_EmailIgnoreCase(
                            assigneeEmail,
                            pageable
                    );

        } else {

            tasks = taskRepository.findAll(pageable);
        }

        Map<Long, Long> commentCounts =
                getCommentCounts(tasks.getContent());

        return PagedResponse.from(
                tasks.map(task -> toListResponse(
                                task,
                                commentCounts.getOrDefault(
                                        task.getId(),
                                        0L
                                )
                        )
                )
        );
    }

    public TaskResponse getTask(Long taskId) {
        Task task = taskRepository
                .findById(taskId)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Task not found")
                );

        return toResponse(task);
    }

    private Map<Long, Long> getCommentCounts(List<Task> tasks) {
        if (tasks.isEmpty()) {
            return Map.of();
        }

        List<Long> taskIds = tasks.stream()
                .map(Task::getId)
                .toList();

        return commentRepository
                .countCommentsByTaskIds(taskIds)
                .stream()
                .collect(Collectors.toMap(
                        CommentCountProjection::getTaskId,
                        CommentCountProjection::getTotalComments
                ));
    }

    private Pageable createPageable(
            int page,
            int size,
            String sort
    ) {
        if (page < 0) {
            throw new InvalidRequestException(
                    "Page must be zero or greater"
            );
        }

        if (size < 1 || size > MAX_PAGE_SIZE) {
            throw new InvalidRequestException(
                    "Size must be between 1 and " + MAX_PAGE_SIZE
            );
        }

        String[] sortParts = sort.split(",", -1);

        if (sortParts.length != 2) {
            throw new InvalidRequestException(
                    "Sort must use the format field,direction"
            );
        }

        String requestedField = sortParts[0].trim();
        String property = SORT_PROPERTIES.get(requestedField);

        if (property == null) {
            throw new InvalidRequestException(
                    "Unsupported sort field: " + requestedField
            );
        }

        Sort.Direction direction;

        try {
            direction = Sort.Direction.fromString(
                    sortParts[1].trim()
            );
        } catch (IllegalArgumentException exception) {
            throw new InvalidRequestException(
                    "Sort direction must be asc or desc"
            );
        }

        return PageRequest.of(
                page,
                size,
                Sort.by(direction, property)
        );
    }

    @Transactional
    public TaskResponse assignTask(
            Long taskId,
            AssignTaskRequest request,
            String currentUserEmail,
            long expectedVersion
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

        verifyVersion(task, expectedVersion);

        Account previousAssignee = task.getAssignee();
        Account newAssignee = request.assignee().equalsIgnoreCase("none")
                ? null
                : accountRepository
                .findByEmailIgnoreCase(request.assignee())
                .orElseThrow(() ->
                        new ResourceNotFoundException("Assignee not found")
                );

        if (sameAccount(previousAssignee, newAssignee)) {
            return toResponse(task);
        }

        task.setAssignee(newAssignee);

        Task savedTask = taskRepository.saveAndFlush(task);

        activityService.recordAssigneeChanged(
                savedTask,
                savedTask.getAuthor(),
                accountEmail(previousAssignee),
                accountEmail(newAssignee)
        );

        return toResponse(savedTask);
    }


    @Transactional
    public TaskResponse updateStatus(
            Long taskId,
            UpdateTaskStatusRequest request,
            String currentUserEmail,
            long expectedVersion
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

        verifyVersion(task, expectedVersion);

        if (task.getStatus() == request.status()) {
            return toResponse(task);
        }

        TaskStatus previousStatus = task.getStatus();
        Account actor = isAuthor
                ? task.getAuthor()
                : task.getAssignee();

        task.setStatus(request.status());

        Task savedTask = taskRepository.saveAndFlush(task);

        activityService.recordStatusChanged(
                savedTask,
                actor,
                previousStatus,
                savedTask.getStatus()
        );

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
                assigneeEmail,
                task.getVersion(),
                task.getCreatedAt(),
                task.getUpdatedAt()
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
                totalComments,
                task.getVersion(),
                task.getCreatedAt(),
                task.getUpdatedAt()
        );
    }

    private void verifyVersion(Task task, long expectedVersion) {
        if (!Objects.equals(task.getVersion(), expectedVersion)) {
            throw new PreconditionFailedException(
                    "Task was modified by another request. Refresh it and try again"
            );
        }
    }

    private boolean sameAccount(Account first, Account second) {
        if (first == null || second == null) {
            return first == second;
        }

        return Objects.equals(first.getId(), second.getId());
    }

    private String accountEmail(Account account) {
        return account == null
                ? "none"
                : account.getEmail().toLowerCase(Locale.ROOT);
    }
}
