CREATE TABLE task_activities (
    id BIGSERIAL PRIMARY KEY,
    task_id BIGINT NOT NULL,
    actor_id BIGINT NOT NULL,
    type VARCHAR(32) NOT NULL,
    previous_value VARCHAR(255),
    new_value VARCHAR(255) NOT NULL,
    task_version BIGINT NOT NULL,
    created_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT fk_task_activities_task
        FOREIGN KEY (task_id)
        REFERENCES tasks (id)
        ON DELETE CASCADE,

    CONSTRAINT fk_task_activities_actor
        FOREIGN KEY (actor_id)
        REFERENCES accounts (id),

    CONSTRAINT chk_task_activities_type
        CHECK (type IN (
            'TASK_CREATED',
            'ASSIGNEE_CHANGED',
            'STATUS_CHANGED'
        )),

    CONSTRAINT chk_task_activities_version
        CHECK (task_version >= 0)
);

CREATE INDEX idx_task_activities_task_id_id
    ON task_activities (task_id, id DESC);
