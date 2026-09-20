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

    public static final String FORBIDDEN = """
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

    private OpenApiExamples() {
    }
}
