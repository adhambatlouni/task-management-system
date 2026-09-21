package com.adham.taskmanagement.activity;

import com.adham.taskmanagement.account.Account;
import com.adham.taskmanagement.common.exception.InvalidRequestException;
import com.adham.taskmanagement.common.exception.ResourceNotFoundException;
import com.adham.taskmanagement.common.web.PagedResponse;
import com.adham.taskmanagement.task.Task;
import com.adham.taskmanagement.task.TaskRepository;
import com.adham.taskmanagement.task.TaskStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.Locale;

@Service
@Transactional(readOnly = true)
public class TaskActivityService {

    private static final int MAX_PAGE_SIZE = 100;

    private final TaskActivityRepository activityRepository;
    private final TaskRepository taskRepository;

    public TaskActivityService(
            TaskActivityRepository activityRepository,
            TaskRepository taskRepository
    ) {
        this.activityRepository = activityRepository;
        this.taskRepository = taskRepository;
    }

    @Transactional(propagation = Propagation.MANDATORY)
    public void recordTaskCreated(Task task, Account actor) {
        record(
                task,
                actor,
                TaskActivityType.TASK_CREATED,
                null,
                task.getStatus().name()
        );
    }

    @Transactional(propagation = Propagation.MANDATORY)
    public void recordAssigneeChanged(
            Task task,
            Account actor,
            String previousAssignee,
            String newAssignee
    ) {
        record(
                task,
                actor,
                TaskActivityType.ASSIGNEE_CHANGED,
                previousAssignee,
                newAssignee
        );
    }

    @Transactional(propagation = Propagation.MANDATORY)
    public void recordStatusChanged(
            Task task,
            Account actor,
            TaskStatus previousStatus,
            TaskStatus newStatus
    ) {
        record(
                task,
                actor,
                TaskActivityType.STATUS_CHANGED,
                previousStatus.name(),
                newStatus.name()
        );
    }

    public PagedResponse<TaskActivityResponse> getTaskActivities(
            Long taskId,
            int page,
            int size
    ) {
        validatePage(page, size);

        if (!taskRepository.existsById(taskId)) {
            throw new ResourceNotFoundException("Task not found");
        }

        Page<TaskActivity> activities = activityRepository
                .findAllByTask_Id(
                        taskId,
                        PageRequest.of(
                                page,
                                size,
                                Sort.by(Sort.Direction.DESC, "id")
                        )
                );

        return PagedResponse.from(
                activities.map(this::toResponse)
        );
    }

    private void record(
            Task task,
            Account actor,
            TaskActivityType type,
            String previousValue,
            String newValue
    ) {
        activityRepository.save(
                new TaskActivity(
                        task,
                        actor,
                        type,
                        previousValue,
                        newValue,
                        task.getVersion()
                )
        );
    }

    private void validatePage(int page, int size) {
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
    }

    private TaskActivityResponse toResponse(TaskActivity activity) {
        return new TaskActivityResponse(
                activity.getId().toString(),
                activity.getTask().getId().toString(),
                activity.getType(),
                activity.getActor()
                        .getEmail()
                        .toLowerCase(Locale.ROOT),
                activity.getPreviousValue(),
                activity.getNewValue(),
                activity.getTaskVersion(),
                activity.getCreatedAt()
        );
    }
}
