package com.adham.taskmanagement.activity;

import jakarta.persistence.EntityListeners;
import org.hibernate.annotations.Immutable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TaskActivityRepository
        extends JpaRepository<TaskActivity, Long> {

    @EntityGraph(attributePaths = {"task", "actor"})
    Page<TaskActivity> findAllByTask_Id(
            Long taskId,
            Pageable pageable
    );
}
