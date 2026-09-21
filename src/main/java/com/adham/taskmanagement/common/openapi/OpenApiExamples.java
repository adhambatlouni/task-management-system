package com.adham.taskmanagement.common.openapi;

public final class OpenApiExamples {

    public static final String BAD_REQUEST = """
            {
              "title": "Invalid request",
              "status": 400,
              "detail": "Page must be zero or greater",
              "instance": "/api/tasks"
            }
            """;

    public static final String VALIDATION_ERROR = """
            {
              "title": "Validation failed",
              "status": 400,
              "detail": "One or more request fields are invalid",
              "instance": "/api/accounts",
              "errors": {
                "email": "must be a well-formed email address"
              }
            }
            """;

    public static final String ASSIGNMENT_FORBIDDEN = """
            {
              "title": "Forbidden operation",
              "status": 403,
              "detail": "Only the task author can assign it",
              "instance": "/api/tasks/1/assign"
            }
            """;

    public static final String NOT_FOUND = """
            {
              "title": "Resource not found",
              "status": 404,
              "detail": "Task not found",
              "instance": "/api/tasks/999999/comments"
            }
            """;

    public static final String CONFLICT = """
            {
              "title": "Resource conflict",
              "status": 409,
              "detail": "Email already exists",
              "instance": "/api/accounts"
            }
            """;

    public static final String STATUS_FORBIDDEN = """
            {
              "title": "Forbidden operation",
              "status": 403,
              "detail": "Only the author or assignee can change the task status",
              "instance": "/api/tasks/1/status"
            }
            """;

    public static final String ASSIGNMENT_PRECONDITION_FAILED = """
            {
              "title": "Precondition failed",
              "status": 412,
              "detail": "Task was modified by another request. Refresh it and try again",
              "instance": "/api/tasks/1/assign"
            }
            """;

    public static final String ASSIGNMENT_PRECONDITION_REQUIRED = """
            {
              "title": "Precondition required",
              "status": 428,
              "detail": "If-Match header is required",
              "instance": "/api/tasks/1/assign"
            }
            """;

    public static final String STATUS_PRECONDITION_FAILED = """
            {
              "title": "Precondition failed",
              "status": 412,
              "detail": "Task was modified by another request. Refresh it and try again",
              "instance": "/api/tasks/1/status"
            }
            """;

    public static final String STATUS_PRECONDITION_REQUIRED = """
            {
              "title": "Precondition required",
              "status": 428,
              "detail": "If-Match header is required",
              "instance": "/api/tasks/1/status"
            }
            """;

    public static final String ACTIVITY_BAD_REQUEST = """
            {
              "title": "Invalid request",
              "status": 400,
              "detail": "Page must be zero or greater",
              "instance": "/api/tasks/1/activities"
            }
            """;

    public static final String ACTIVITY_NOT_FOUND = """
            {
              "title": "Resource not found",
              "status": 404,
              "detail": "Task not found",
              "instance": "/api/tasks/999999/activities"
            }
            """;

    public static final String TASK_NOT_FOUND = """
            {
              "title": "Resource not found",
              "status": 404,
              "detail": "Task not found",
              "instance": "/api/tasks/999999"
            }
            """;

    private OpenApiExamples() {
    }
}
