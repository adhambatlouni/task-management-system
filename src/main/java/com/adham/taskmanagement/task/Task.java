package com.adham.taskmanagement.task;

import com.adham.taskmanagement.account.Account;
import com.adham.taskmanagement.common.persistence.AuditableEntity;
import jakarta.persistence.*;

@Entity
@Table(name = "tasks")
public class Task extends AuditableEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String title;

    @Column(nullable = false)
    private String description;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TaskStatus status;

    @ManyToOne(optional = false)
    @JoinColumn(name = "author_id", nullable = false)
    private Account author;

    @ManyToOne
    @JoinColumn(name = "assignee_id")
    private Account assignee;

    @Version
    @Column(nullable = false)
    private Long version;

    public Task() {
    }

    public Task(
            String title,
            String description,
            TaskStatus status,
            Account author
    ) {
        this.title = title;
        this.description = description;
        this.status = status;
        this.author = author;
    }

    public Long getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public TaskStatus getStatus() {
        return status;
    }

    public Account getAuthor() {
        return author;
    }

    public Account getAssignee() {
        return assignee;
    }

    public Long getVersion() {
        return version;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setStatus(TaskStatus status) {
        this.status = status;
    }

    public void setAuthor(Account author) {
        this.author = author;
    }

    public void setAssignee(Account assignee) {
        this.assignee = assignee;
    }

}
