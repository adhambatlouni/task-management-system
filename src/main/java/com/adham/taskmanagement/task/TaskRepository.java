package com.adham.taskmanagement.task;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TaskRepository extends JpaRepository<Task, Long> {

    Page<Task> findAllByAuthor_EmailIgnoreCase(
            String authorEmail,
            Pageable pageable
    );

    Page<Task> findAllByAssignee_EmailIgnoreCase(
            String assigneeEmail,
            Pageable pageable
    );

    Page<Task> findAllByAuthor_EmailIgnoreCaseAndAssignee_EmailIgnoreCase(
            String authorEmail,
            String assigneeEmail,
            Pageable pageable
    );
}
