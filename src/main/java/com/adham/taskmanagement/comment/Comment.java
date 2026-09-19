package com.adham.taskmanagement.comment;

import jakarta.persistence.*;
import com.adham.taskmanagement.account.Account;
import com.adham.taskmanagement.task.Task;

@Entity
@Table(name = "comments")
public class Comment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String text;

    @ManyToOne(optional = false)
    @JoinColumn(name = "task_id", nullable = false)
    private Task task;

    @ManyToOne(optional = false)
    @JoinColumn(name = "author_id", nullable = false)
    private Account author;

    public Comment() {
    }

    public Comment(
            String text,
            Task task,
            Account author
    ) {
        this.text = text;
        this.task = task;
        this.author = author;
    }

    public Long getId() {
        return id;
    }

    public String getText() {
        return text;
    }

    public Task getTask() {
        return task;
    }

    public Account getAuthor() {
        return author;
    }

    public void setText(String text) {
        this.text = text;
    }

    public void setTask(Task task) {
        this.task = task;
    }

    public void setAuthor(Account author) {
        this.author = author;
    }
}
