CREATE INDEX idx_tasks_author_id ON tasks (author_id);
CREATE INDEX idx_tasks_assignee_id ON tasks (assignee_id);
CREATE INDEX idx_comments_task_id ON comments (task_id);
CREATE INDEX idx_comments_author_id ON comments (author_id);
