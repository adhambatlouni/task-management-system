package com.adham.taskmanagement.common.openapi;

public final class OpenApiExamples {

    public static final String ETAG_ZERO = "\"0\"";
    public static final String ETAG_ONE = "\"1\"";
    public static final String ETAG_TWO = "\"2\"";

    public static final String IF_MATCH_ZERO = "\"\\\"0\\\"\"";
    public static final String IF_MATCH_ONE = "\"\\\"1\\\"\"";

    public static final String BAD_REQUEST = """
            {
              "title": "Invalid request",
              "status": 400,
              "detail": "Page must be zero or greater",
              "instance": "/api/tasks"
            }
            """;

    public static final String REGISTRATION_VALIDATION = """
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

    public static final String TASK_VALIDATION = """
            {
              "title": "Validation failed",
              "status": 400,
              "detail": "One or more request fields are invalid",
              "instance": "/api/tasks",
              "errors": {
                "title": "must not be blank"
              }
            }
            """;

    public static final String ASSIGNMENT_VALIDATION = """
            {
              "title": "Validation failed",
              "status": 400,
              "detail": "One or more request fields are invalid",
              "instance": "/api/tasks/42/assign",
              "errors": {
                "assignee": "must match the required email format or be 'none'"
              }
            }
            """;

    public static final String COMMENT_VALIDATION = """
            {
              "title": "Validation failed",
              "status": 400,
              "detail": "One or more request fields are invalid",
              "instance": "/api/tasks/42/comments",
              "errors": {
                "text": "must not be blank"
              }
            }
            """;

    public static final String MALFORMED_REQUEST_BODY = """
            {
              "title": "Invalid request",
              "status": 400,
              "detail": "Request body is malformed or contains an unsupported value",
              "instance": "/api/tasks/42/status"
            }
            """;

    public static final String ASSIGNMENT_FORBIDDEN = """
            {
              "title": "Forbidden operation",
              "status": 403,
              "detail": "Only the task author can assign it",
              "instance": "/api/tasks/42/assign"
            }
            """;

    public static final String STATUS_FORBIDDEN = """
            {
              "title": "Forbidden operation",
              "status": 403,
              "detail": "Only the author or assignee can change the task status",
              "instance": "/api/tasks/42/status"
            }
            """;

    public static final String ACCOUNT_NOT_FOUND = """
            {
              "title": "Resource not found",
              "status": 404,
              "detail": "Account not found",
              "instance": "/api/tasks"
            }
            """;

    public static final String ASSIGNEE_NOT_FOUND = """
            {
              "title": "Resource not found",
              "status": 404,
              "detail": "Assignee not found",
              "instance": "/api/tasks/42/assign"
            }
            """;

    public static final String COMMENT_TASK_NOT_FOUND = """
            {
              "title": "Resource not found",
              "status": 404,
              "detail": "Task not found",
              "instance": "/api/tasks/999999/comments"
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

    public static final String CONFLICT = """
            {
              "title": "Resource conflict",
              "status": 409,
              "detail": "Email already exists",
              "instance": "/api/accounts"
            }
            """;

    public static final String ASSIGNMENT_PRECONDITION_FAILED = """
            {
              "title": "Precondition failed",
              "status": 412,
              "detail": "Task was modified by another request. Refresh it and try again",
              "instance": "/api/tasks/42/assign"
            }
            """;

    public static final String ASSIGNMENT_PRECONDITION_REQUIRED = """
            {
              "title": "Precondition required",
              "status": 428,
              "detail": "If-Match header is required",
              "instance": "/api/tasks/42/assign"
            }
            """;

    public static final String STATUS_PRECONDITION_FAILED = """
            {
              "title": "Precondition failed",
              "status": 412,
              "detail": "Task was modified by another request. Refresh it and try again",
              "instance": "/api/tasks/42/status"
            }
            """;

    public static final String STATUS_PRECONDITION_REQUIRED = """
            {
              "title": "Precondition required",
              "status": 428,
              "detail": "If-Match header is required",
              "instance": "/api/tasks/42/status"
            }
            """;

    public static final String ACTIVITY_BAD_REQUEST = """
            {
              "title": "Invalid request",
              "status": 400,
              "detail": "Page must be zero or greater",
              "instance": "/api/tasks/42/activities"
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

    private OpenApiExamples() {
    }
}
