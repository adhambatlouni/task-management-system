package com.adham.taskmanagement.task;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TaskRepository extends JpaRepository<Task, Long> {

    List<Task> findAllByOrderByIdDesc();

    List<Task> findAllByAuthor_EmailIgnoreCaseOrderByIdDesc(
            String authorEmail
    );

    List<Task> findAllByAssignee_EmailIgnoreCaseOrderByIdDesc(
            String assigneeEmail
    );

    List<Task> findAllByAuthor_EmailIgnoreCaseAndAssignee_EmailIgnoreCaseOrderByIdDesc(
            String authorEmail,
            String assigneeEmail
    );
}
