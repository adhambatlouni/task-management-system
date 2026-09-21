package com.adham.taskmanagement.activity;

import com.adham.taskmanagement.account.Account;
import com.adham.taskmanagement.task.Task;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import org.hibernate.annotations.Immutable;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.Instant;

@Entity
@Table(name = "task_activities")
@EntityListeners(AuditingEntityListener.class)
@Immutable
public class TaskActivity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "task_id", nullable = false, updatable = false)
    private Task task;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "actor_id", nullable = false, updatable = false)
    private Account actor;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, updatable = false, length = 32)
    private TaskActivityType type;

    @Column(name = "previous_value", updatable = false)
    private String previousValue;

    @Column(name = "new_value", nullable = false, updatable = false)
    private String newValue;

    @Column(name = "task_version", nullable = false, updatable = false)
    private Long taskVersion;

    @CreatedDate
    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    protected TaskActivity() {
    }

    public TaskActivity(
            Task task,
            Account actor,
            TaskActivityType type,
            String previousValue,
            String newValue,
            Long taskVersion
    ) {
        this.task = task;
        this.actor = actor;
        this.type = type;
        this.previousValue = previousValue;
        this.newValue = newValue;
        this.taskVersion = taskVersion;
    }

    public Long getId() {
        return id;
    }

    public Task getTask() {
        return task;
    }

    public Account getActor() {
        return actor;
    }

    public TaskActivityType getType() {
        return type;
    }

    public String getPreviousValue() {
        return previousValue;
    }

    public String getNewValue() {
        return newValue;
    }

    public Long getTaskVersion() {
        return taskVersion;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }
}
