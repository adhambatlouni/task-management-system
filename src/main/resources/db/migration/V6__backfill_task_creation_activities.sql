INSERT INTO task_activities (
    task_id,
    actor_id,
    type,
    previous_value,
    new_value,
    task_version,
    created_at
)
SELECT
    task.id,
    task.author_id,
    'TASK_CREATED',
    NULL,
    'CREATED',
    0,
    task.created_at
FROM tasks task
WHERE NOT EXISTS (
    SELECT 1
    FROM task_activities activity
    WHERE activity.task_id = task.id
      AND activity.type = 'TASK_CREATED'
);
