package com.adham.taskmanagement;

import com.adham.taskmanagement.task.Task;
import com.adham.taskmanagement.task.TaskStatus;
import com.jayway.jsonpath.JsonPath;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.EntityTransaction;
import jakarta.persistence.OptimisticLockException;
import jakarta.persistence.RollbackException;
import org.junit.jupiter.api.Test;
import org.flywaydb.core.Flyway;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.postgresql.PostgreSQLContainer;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.httpBasic;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@Testcontainers
@AutoConfigureMockMvc
@SpringBootTest(properties = {
        "spring.datasource.password=test",
        "app.security.jwt.secret-base64="
                + "MDEyMzQ1Njc4OTAxMjM0NTY3ODkwMTIzNDU2Nzg5MDE=",
        "app.security.jwt.access-token-ttl=1h",
        "app.openapi.server-url=https://api.example.com"
})
class TaskManagementApiIntegrationTests {

    private static final String PASSWORD = "password123";

    @Container
    @ServiceConnection
    static final PostgreSQLContainer postgres =
            new PostgreSQLContainer("postgres:18-alpine");

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private Flyway flyway;

    @Autowired
    private EntityManagerFactory entityManagerFactory;

    @Test
    void appliesAllDatabaseMigrations() {
        assertThat(flyway.info().current().getVersion().getVersion())
                .isEqualTo("6");
    }

    @Test
    void exposesPublicHealthStatusForPlatformMonitoring() throws Exception {
        mockMvc.perform(get("/actuator/health"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("UP"));
    }

    @Test
    void redirectsPublicRootToSwaggerUi() throws Exception {
        mockMvc.perform(get("/"))
                .andExpect(status().isFound())
                .andExpect(header().string(
                        HttpHeaders.LOCATION,
                        "/swagger-ui.html"
                ));
    }

    @Test
    void publishesOpenApiDocumentation() throws Exception {
        mockMvc.perform(get("/v3/api-docs"))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(
                        MediaType.APPLICATION_JSON
                ))
                .andExpect(jsonPath("$.info.title")
                        .value("Task Management API"))
                .andExpect(jsonPath("$.info.version")
                        .value("1.0.0"))
                .andExpect(jsonPath("$.info.description")
                        .value(org.hamcrest.Matchers.containsString(
                                "JWT authentication"
                        )))
                .andExpect(jsonPath("$.info.description")
                        .value(org.hamcrest.Matchers.containsString(
                                "optimistic concurrency"
                        )))
                .andExpect(jsonPath("$.info.description")
                        .value(org.hamcrest.Matchers.containsString(
                                "append-only activity history"
                        )))
                .andExpect(jsonPath("$.info.contact.name")
                        .value("Adham Batlouni"))
                .andExpect(jsonPath("$.externalDocs.url").value(
                        "https://github.com/adhambatlouni/"
                                + "task-management-system"
                ))
                .andExpect(jsonPath("$.servers[0].url")
                        .value("https://api.example.com"))
                .andExpect(jsonPath("$.servers[0].description")
                        .value("Production API"))
                .andExpect(jsonPath("$.tags[0].name")
                        .value("Accounts"))
                .andExpect(jsonPath("$.tags[1].name")
                        .value("Authentication"))
                .andExpect(jsonPath("$.tags[2].name")
                        .value("Tasks"))
                .andExpect(jsonPath("$.tags[3].name")
                        .value("Comments"))
                .andExpect(jsonPath("$.tags[4].name")
                        .value("Task activities"))
                .andExpect(jsonPath("$.tags[4].description")
                        .value(org.hamcrest.Matchers.containsString(
                                "append-only"
                        )))
                .andExpect(jsonPath("$['paths']['/']")
                        .doesNotExist())
                .andExpect(jsonPath("$['paths']['/api/tasks']")
                        .exists())
                .andExpect(jsonPath(
                        "$['paths']['/api/tasks'].get.responses['400']"
                ).exists())
                .andExpect(jsonPath(
                        "$['paths']['/api/accounts'].post.responses['409']"
                ).exists())
                .andExpect(jsonPath(
                        "$['paths']['/api/auth/token'].post.responses['401']"
                ).exists())
                .andExpect(jsonPath(
                        "$['paths']['/api/tasks'].post.responses['400']"
                ).exists())
                .andExpect(jsonPath(
                        "$['paths']['/api/tasks/{taskId}']"
                                + ".get.responses['200'].headers.ETag"
                ).exists())
                .andExpect(jsonPath(
                        "$['paths']['/api/tasks/{taskId}']"
                                + ".get.responses['200'].headers.ETag"
                                + ".schema.example"
                ).value("\"2\""))
                .andExpect(jsonPath(
                        "$['paths']['/api/tasks/{taskId}']"
                                + ".get.responses['200'].content"
                                + "['application/json']"
                ).exists())
                .andExpect(jsonPath(
                        "$['paths']['/api/tasks/{taskId}']"
                                + ".get.responses['404']"
                ).exists())
                .andExpect(jsonPath(
                        "$['paths']['/api/tasks/{taskId}/assign']"
                                + ".put.responses['403']"
                ).exists())
                .andExpect(jsonPath(
                        "$['paths']['/api/tasks/{taskId}/assign']"
                                + ".put.responses['412']"
                ).exists())
                .andExpect(jsonPath(
                        "$['paths']['/api/tasks/{taskId}/status']"
                                + ".put.responses['428']"
                ).exists())
                .andExpect(jsonPath(
                        "$['paths']['/api/tasks/{taskId}/status']"
                                + ".put.responses['200'].headers.ETag"
                ).exists())
                .andExpect(jsonPath(
                        "$['paths']['/api/tasks/{taskId}/status']"
                                + ".put.parameters[?(@.name == 'If-Match')]"
                ).isNotEmpty())
                .andExpect(jsonPath(
                        "$['paths']['/api/tasks/{taskId}/status']"
                                + ".put.parameters[?(@.name == 'If-Match')]"
                                + ".example"
                ).value("\"1\""))
                .andExpect(jsonPath(
                        "$['paths']['/api/tasks/{taskId}/status']"
                                + ".put.responses['404']"
                ).exists())
                .andExpect(jsonPath(
                        "$['paths']['/api/tasks/{taskId}/comments']"
                                + ".post.responses['400']"
                ).exists())
                .andExpect(jsonPath(
                        "$['paths']['/api/tasks/{taskId}/comments']"
                                + ".get.responses['404']"
                ).exists())
                .andExpect(jsonPath(
                        "$['paths']['/api/tasks/{taskId}/activities']"
                                + ".get.responses['200']"
                ).exists())
                .andExpect(jsonPath(
                        "$['paths']['/api/tasks/{taskId}/activities']"
                                + ".get.responses['400']"
                ).exists())
                .andExpect(jsonPath(
                        "$['paths']['/api/tasks/{taskId}/activities']"
                                + ".get.responses['404']"
                ).exists())
                .andExpect(jsonPath(
                        "$.components.securitySchemes.bearerAuth"
                ).exists())
                .andExpect(jsonPath(
                        "$.components.securitySchemes.bearerAuth.description"
                ).value(org.hamcrest.Matchers.containsString(
                        "POST /api/auth/token"
                )))
                .andExpect(jsonPath(
                        "$.components.securitySchemes.basicAuth"
                ).exists())
                .andExpect(jsonPath(
                        "$.components.securitySchemes.basicAuth.description"
                ).value(org.hamcrest.Matchers.containsString(
                        "registered email and password"
                )))
                .andExpect(jsonPath(
                        "$.components.schemas.RegisterAccountRequest"
                                + ".properties.password.format"
                ).value("password"))
                .andExpect(jsonPath(
                        "$.components.schemas.RegisterAccountRequest"
                                + ".properties.password.writeOnly"
                ).value(true))
                .andExpect(jsonPath(
                        "$.components.schemas.TaskResponse"
                                + ".properties.version.description"
                ).value(org.hamcrest.Matchers.containsString("ETag")))
                .andExpect(jsonPath(
                        "$.components.schemas.ApiProblem.properties.title"
                ).exists())
                .andExpect(jsonPath(
                        "$.components.schemas.ApiProblem.properties.errors"
                ).exists())
                .andExpect(jsonPath(
                        "$.components.schemas.ApiProblem.properties.properties"
                ).doesNotExist());
    }

    @Test
    void returnsStableProblemDetailsForUnsupportedRequestValues()
            throws Exception {
        String owner = "invalid-status-owner@example.com";

        register(owner);
        String token = obtainToken(owner);
        CreatedTask task = createTask(token);

        mockMvc.perform(put("/api/tasks/{taskId}/status", task.id())
                        .header(HttpHeaders.AUTHORIZATION, bearer(token))
                        .header(
                                HttpHeaders.IF_MATCH,
                                entityTag(task.version())
                        )
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "status": "NOT_A_STATUS"
                                }
                                """))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentTypeCompatibleWith(
                        MediaType.APPLICATION_PROBLEM_JSON
                ))
                .andExpect(jsonPath("$.title")
                        .value("Invalid request"))
                .andExpect(jsonPath("$.detail").value(
                        "Request body is malformed or contains an unsupported value"
                ))
                .andExpect(jsonPath("$.instance").value(
                        "/api/tasks/" + task.id() + "/status"
                ))
                .andExpect(jsonPath("$.trace").doesNotExist());
    }

    @Test
    void supportsTheCompleteTaskWorkflow() throws Exception {
        String owner = "workflow-owner@example.com";
        String assignee = "workflow-assignee@example.com";

        register(owner);
        register(assignee);

        String ownerToken = obtainToken(owner);
        String assigneeToken = obtainToken(assignee);

        CreatedTask task = createTask(ownerToken);

        mockMvc.perform(put("/api/tasks/{taskId}/assign", task.id())
                        .header(HttpHeaders.AUTHORIZATION, bearer(ownerToken))
                        .header(HttpHeaders.IF_MATCH, entityTag(task.version()))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "assignee": "%s"
                                }
                                """.formatted(assignee)))
                .andExpect(status().isOk())
                .andExpect(header().string(HttpHeaders.ETAG, entityTag(1)))
                .andExpect(jsonPath("$.assignee").value(assignee))
                .andExpect(jsonPath("$.version").value(1));

        mockMvc.perform(put("/api/tasks/{taskId}/status", task.id())
                        .header(HttpHeaders.AUTHORIZATION, bearer(assigneeToken))
                        .header(HttpHeaders.IF_MATCH, entityTag(1))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "status": "IN_PROGRESS"
                                }
                                """))
                .andExpect(status().isOk())
                .andExpect(header().string(HttpHeaders.ETAG, entityTag(2)))
                .andExpect(jsonPath("$.status").value("IN_PROGRESS"))
                .andExpect(jsonPath("$.version").value(2));

        mockMvc.perform(post("/api/tasks/{taskId}/comments", task.id())
                        .header(HttpHeaders.AUTHORIZATION, bearer(assigneeToken))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "text": "Authentication has been verified"
                                }
                                """))
                .andExpect(status().isOk());

        mockMvc.perform(get("/api/tasks/{taskId}/comments", task.id())
                        .header(HttpHeaders.AUTHORIZATION, bearer(ownerToken)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].task_id").value(task.id()))
                .andExpect(jsonPath("$[0].text")
                        .value("Authentication has been verified"))
                .andExpect(jsonPath("$[0].author").value(assignee))
                .andExpect(jsonPath("$[0].created_at").isNotEmpty())
                .andExpect(jsonPath("$[0].updated_at").isNotEmpty());

        mockMvc.perform(get("/api/tasks")
                        .param("author", owner)
                        .param("assignee", assignee)
                        .header(HttpHeaders.AUTHORIZATION, bearer(ownerToken)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].id").value(task.id()))
                .andExpect(jsonPath("$.content[0].status")
                        .value("IN_PROGRESS"))
                .andExpect(jsonPath("$.content[0].author").value(owner))
                .andExpect(jsonPath("$.content[0].assignee")
                        .value(assignee))
                .andExpect(jsonPath("$.content[0].total_comments")
                        .value(1))
                .andExpect(jsonPath("$.content[0].version").value(2))
                .andExpect(jsonPath("$.content[0].created_at")
                        .isNotEmpty())
                .andExpect(jsonPath("$.content[0].updated_at")
                        .isNotEmpty())
                .andExpect(jsonPath("$.total_elements").value(1));
    }

    @Test
    void recordsAndPaginatesTaskActivityHistory() throws Exception {
        String owner = "activity-owner@example.com";
        String assignee = "activity-assignee@example.com";

        register(owner);
        register(assignee);

        String ownerToken = obtainToken(owner);
        String assigneeToken = obtainToken(assignee);
        CreatedTask task = createTask(ownerToken);

        mockMvc.perform(get("/api/tasks/{taskId}", task.id())
                        .header(HttpHeaders.AUTHORIZATION, bearer(ownerToken)))
                .andExpect(status().isOk())
                .andExpect(header().string(
                        HttpHeaders.ETAG,
                        entityTag(task.version())
                ))
                .andExpect(jsonPath("$.id").value(task.id()))
                .andExpect(jsonPath("$.version").value(task.version()));

        mockMvc.perform(get("/api/tasks/{taskId}/activities", task.id()))
                .andExpect(status().isUnauthorized());

        mockMvc.perform(put("/api/tasks/{taskId}/assign", task.id())
                        .header(HttpHeaders.AUTHORIZATION, bearer(ownerToken))
                        .header(HttpHeaders.IF_MATCH, entityTag(task.version()))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "assignee": "%s"
                                }
                                """.formatted(assignee)))
                .andExpect(status().isOk())
                .andExpect(header().string(HttpHeaders.ETAG, entityTag(1)));

        mockMvc.perform(put("/api/tasks/{taskId}/status", task.id())
                        .header(HttpHeaders.AUTHORIZATION, bearer(assigneeToken))
                        .header(HttpHeaders.IF_MATCH, entityTag(1))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "status": "IN_PROGRESS"
                                }
                                """))
                .andExpect(status().isOk())
                .andExpect(header().string(HttpHeaders.ETAG, entityTag(2)));

        mockMvc.perform(put("/api/tasks/{taskId}/status", task.id())
                        .header(HttpHeaders.AUTHORIZATION, bearer(assigneeToken))
                        .header(HttpHeaders.IF_MATCH, entityTag(2))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "status": "IN_PROGRESS"
                                }
                                """))
                .andExpect(status().isOk())
                .andExpect(header().string(HttpHeaders.ETAG, entityTag(2)))
                .andExpect(jsonPath("$.version").value(2));

        mockMvc.perform(put("/api/tasks/{taskId}/status", task.id())
                        .header(HttpHeaders.AUTHORIZATION, bearer(assigneeToken))
                        .header(HttpHeaders.IF_MATCH, entityTag(1))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "status": "COMPLETED"
                                }
                                """))
                .andExpect(status().isPreconditionFailed());

        mockMvc.perform(get("/api/tasks/{taskId}/activities", task.id())
                        .param("page", "0")
                        .param("size", "2")
                        .header(HttpHeaders.AUTHORIZATION, bearer(ownerToken)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content.length()").value(2))
                .andExpect(jsonPath("$.content[0].task_id")
                        .value(task.id()))
                .andExpect(jsonPath("$.content[0].type")
                        .value("STATUS_CHANGED"))
                .andExpect(jsonPath("$.content[0].actor")
                        .value(assignee))
                .andExpect(jsonPath("$.content[0].previous_value")
                        .value("CREATED"))
                .andExpect(jsonPath("$.content[0].new_value")
                        .value("IN_PROGRESS"))
                .andExpect(jsonPath("$.content[0].task_version")
                        .value(2))
                .andExpect(jsonPath("$.content[0].created_at")
                        .isNotEmpty())
                .andExpect(jsonPath("$.content[1].type")
                        .value("ASSIGNEE_CHANGED"))
                .andExpect(jsonPath("$.content[1].actor")
                        .value(owner))
                .andExpect(jsonPath("$.content[1].previous_value")
                        .value("none"))
                .andExpect(jsonPath("$.content[1].new_value")
                        .value(assignee))
                .andExpect(jsonPath("$.content[1].task_version")
                        .value(1))
                .andExpect(jsonPath("$.page").value(0))
                .andExpect(jsonPath("$.size").value(2))
                .andExpect(jsonPath("$.total_elements").value(3))
                .andExpect(jsonPath("$.total_pages").value(2))
                .andExpect(jsonPath("$.first").value(true))
                .andExpect(jsonPath("$.last").value(false));

        mockMvc.perform(get("/api/tasks/{taskId}/activities", task.id())
                        .param("page", "1")
                        .param("size", "2")
                        .header(HttpHeaders.AUTHORIZATION, bearer(ownerToken)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content.length()").value(1))
                .andExpect(jsonPath("$.content[0].type")
                        .value("TASK_CREATED"))
                .andExpect(jsonPath("$.content[0].actor")
                        .value(owner))
                .andExpect(jsonPath("$.content[0].new_value")
                        .value("CREATED"))
                .andExpect(jsonPath("$.content[0].task_version")
                        .value(0))
                .andExpect(jsonPath("$.last").value(true));

        mockMvc.perform(get("/api/tasks/{taskId}/activities", task.id())
                        .param("page", "-1")
                        .header(HttpHeaders.AUTHORIZATION, bearer(ownerToken)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.detail")
                        .value("Page must be zero or greater"));

        mockMvc.perform(get("/api/tasks/{taskId}/activities", 999999)
                        .header(HttpHeaders.AUTHORIZATION, bearer(ownerToken)))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.detail").value("Task not found"));
    }

    @Test
    void paginatesAndSortsTasks() throws Exception {
        String owner = "pagination-owner@example.com";

        register(owner);
        String token = obtainToken(owner);

        String firstTaskId = createTask(token).id();
        String secondTaskId = createTask(token).id();
        String thirdTaskId = createTask(token).id();

        mockMvc.perform(get("/api/tasks")
                        .param("author", owner)
                        .param("page", "0")
                        .param("size", "2")
                        .param("sort", "id,desc")
                        .header(HttpHeaders.AUTHORIZATION, bearer(token)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content.length()").value(2))
                .andExpect(jsonPath("$.content[0].id").value(thirdTaskId))
                .andExpect(jsonPath("$.content[1].id").value(secondTaskId))
                .andExpect(jsonPath("$.page").value(0))
                .andExpect(jsonPath("$.size").value(2))
                .andExpect(jsonPath("$.total_elements").value(3))
                .andExpect(jsonPath("$.total_pages").value(2))
                .andExpect(jsonPath("$.first").value(true))
                .andExpect(jsonPath("$.last").value(false));

        mockMvc.perform(get("/api/tasks")
                        .param("author", owner)
                        .param("page", "1")
                        .param("size", "2")
                        .param("sort", "id,desc")
                        .header(HttpHeaders.AUTHORIZATION, bearer(token)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content.length()").value(1))
                .andExpect(jsonPath("$.content[0].id").value(firstTaskId))
                .andExpect(jsonPath("$.last").value(true));

        mockMvc.perform(get("/api/tasks")
                        .param("page", "-1")
                        .header(HttpHeaders.AUTHORIZATION, bearer(token)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.title").value("Invalid request"))
                .andExpect(jsonPath("$.detail")
                        .value("Page must be zero or greater"));

        mockMvc.perform(get("/api/tasks")
                        .param("page", "8888888888888888")
                        .header(HttpHeaders.AUTHORIZATION, bearer(token)))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentTypeCompatibleWith(
                        MediaType.APPLICATION_PROBLEM_JSON
                ))
                .andExpect(jsonPath("$.title")
                        .value("Invalid request"))
                .andExpect(jsonPath("$.detail")
                        .value("Parameter 'page' must be a valid integer"))
                .andExpect(jsonPath("$.instance")
                        .value("/api/tasks"))
                .andExpect(jsonPath("$.trace").doesNotExist());
    }

    @Test
    void enforcesAuthenticationAuthorizationAndMissingResources() throws Exception {
        String owner = "rules-owner@example.com";
        String assignee = "rules-assignee@example.com";

        mockMvc.perform(get("/api/tasks"))
                .andExpect(status().isUnauthorized());

        register(owner);
        register(assignee);

        mockMvc.perform(post("/api/accounts")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(registrationJson(owner)))
                .andExpect(status().isConflict())
                .andExpect(content().contentTypeCompatibleWith(
                        MediaType.APPLICATION_PROBLEM_JSON
                ))
                .andExpect(jsonPath("$.title")
                        .value("Resource conflict"))
                .andExpect(jsonPath("$.detail")
                        .value("Email already exists"));

        mockMvc.perform(post("/api/accounts")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "email": "invalid-email",
                                  "password": "short"
                                }
                                """))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentTypeCompatibleWith(
                        MediaType.APPLICATION_PROBLEM_JSON
                ))
                .andExpect(jsonPath("$.title")
                        .value("Validation failed"))
                .andExpect(jsonPath("$.errors.email").exists())
                .andExpect(jsonPath("$.errors.password").exists());

        String ownerToken = obtainToken(owner);
        String assigneeToken = obtainToken(assignee);
        CreatedTask task = createTask(ownerToken);

        mockMvc.perform(put("/api/tasks/{taskId}/assign", task.id())
                        .header(HttpHeaders.AUTHORIZATION, bearer(assigneeToken))
                        .header(HttpHeaders.IF_MATCH, entityTag(task.version()))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "assignee": "%s"
                                }
                                """.formatted(assignee)))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.title")
                        .value("Forbidden operation"))
                .andExpect(jsonPath("$.detail")
                        .value("Only the task author can assign it"));

        mockMvc.perform(get("/api/tasks/{taskId}/comments", 999999)
                        .header(HttpHeaders.AUTHORIZATION, bearer(ownerToken)))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.title")
                        .value("Resource not found"))
                .andExpect(jsonPath("$.detail")
                        .value("Task not found"));

        mockMvc.perform(get("/api/tasks/{taskId}", 999999)
                        .header(HttpHeaders.AUTHORIZATION, bearer(ownerToken)))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.detail")
                        .value("Task not found"));
    }

    @Test
    void rejectsMissingMalformedAndStaleTaskVersions() throws Exception {
        String owner = "version-owner@example.com";

        register(owner);
        String token = obtainToken(owner);
        CreatedTask task = createTask(token);

        mockMvc.perform(put("/api/tasks/{taskId}/status", task.id())
                        .header(HttpHeaders.AUTHORIZATION, bearer(token))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "status": "IN_PROGRESS"
                                }
                                """))
                .andExpect(status().isPreconditionRequired())
                .andExpect(content().contentTypeCompatibleWith(
                        MediaType.APPLICATION_PROBLEM_JSON
                ))
                .andExpect(jsonPath("$.title")
                        .value("Precondition required"))
                .andExpect(jsonPath("$.detail")
                        .value("If-Match header is required"));

        mockMvc.perform(put("/api/tasks/{taskId}/status", task.id())
                        .header(HttpHeaders.AUTHORIZATION, bearer(token))
                        .header(HttpHeaders.IF_MATCH, "0")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "status": "IN_PROGRESS"
                                }
                                """))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.title").value("Invalid request"))
                .andExpect(jsonPath("$.detail").value(
                        "If-Match must contain one quoted, non-negative task version"
                ));

        mockMvc.perform(put("/api/tasks/{taskId}/status", task.id())
                        .header(HttpHeaders.AUTHORIZATION, bearer(token))
                        .header(HttpHeaders.IF_MATCH, entityTag(task.version()))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "status": "IN_PROGRESS"
                                }
                                """))
                .andExpect(status().isOk())
                .andExpect(header().string(HttpHeaders.ETAG, entityTag(1)))
                .andExpect(jsonPath("$.version").value(1));

        mockMvc.perform(put("/api/tasks/{taskId}/status", task.id())
                        .header(HttpHeaders.AUTHORIZATION, bearer(token))
                        .header(HttpHeaders.IF_MATCH, entityTag(task.version()))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "status": "COMPLETED"
                                }
                                """))
                .andExpect(status().isPreconditionFailed())
                .andExpect(content().contentTypeCompatibleWith(
                        MediaType.APPLICATION_PROBLEM_JSON
                ))
                .andExpect(jsonPath("$.title")
                        .value("Precondition failed"))
                .andExpect(jsonPath("$.detail").value(
                        "Task was modified by another request. Refresh it and try again"
                ))
                .andExpect(jsonPath("$.instance")
                        .value("/api/tasks/" + task.id() + "/status"));

        mockMvc.perform(get("/api/tasks")
                        .param("author", owner)
                        .header(HttpHeaders.AUTHORIZATION, bearer(token)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].status")
                        .value("IN_PROGRESS"))
                .andExpect(jsonPath("$.content[0].version").value(1));
    }

    @Test
    void preventsRacingDatabaseTransactionsFromOverwritingEachOther()
            throws Exception {
        String owner = "concurrency-owner@example.com";

        register(owner);
        String token = obtainToken(owner);
        CreatedTask createdTask = createTask(token);
        Long taskId = Long.valueOf(createdTask.id());

        EntityManager firstEntityManager =
                entityManagerFactory.createEntityManager();
        EntityManager secondEntityManager =
                entityManagerFactory.createEntityManager();
        EntityTransaction firstTransaction =
                firstEntityManager.getTransaction();
        EntityTransaction secondTransaction =
                secondEntityManager.getTransaction();

        try {
            firstTransaction.begin();
            secondTransaction.begin();

            Task firstCopy = firstEntityManager.find(Task.class, taskId);
            Task secondCopy = secondEntityManager.find(Task.class, taskId);

            firstCopy.setStatus(TaskStatus.IN_PROGRESS);
            secondCopy.setStatus(TaskStatus.COMPLETED);

            firstTransaction.commit();

            assertThatThrownBy(secondTransaction::commit)
                    .isInstanceOf(RollbackException.class)
                    .hasCauseInstanceOf(OptimisticLockException.class);
        } finally {
            if (firstTransaction.isActive()) {
                firstTransaction.rollback();
            }
            if (secondTransaction.isActive()) {
                secondTransaction.rollback();
            }
            firstEntityManager.close();
            secondEntityManager.close();
        }

        mockMvc.perform(get("/api/tasks")
                        .param("author", owner)
                        .header(HttpHeaders.AUTHORIZATION, bearer(token)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].status")
                        .value("IN_PROGRESS"))
                .andExpect(jsonPath("$.content[0].version").value(1));
    }

    private void register(String email) throws Exception {
        mockMvc.perform(post("/api/accounts")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(registrationJson(email)))
                .andExpect(status().isOk());
    }

    private String obtainToken(String email) throws Exception {
        MvcResult result = mockMvc.perform(post("/api/auth/token")
                        .with(httpBasic(email, PASSWORD)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").isNotEmpty())
                .andReturn();

        return JsonPath.read(
                result.getResponse().getContentAsString(),
                "$.token"
        );
    }

    private CreatedTask createTask(String token) throws Exception {
        MvcResult result = mockMvc.perform(post("/api/tasks")
                        .header(HttpHeaders.AUTHORIZATION, bearer(token))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "title": "Implement authentication",
                                  "description": "Verify JWT authentication for the API"
                                }
                                """))
                .andExpect(status().isOk())
                .andExpect(header().string(HttpHeaders.ETAG, entityTag(0)))
                .andExpect(jsonPath("$.status").value("CREATED"))
                .andExpect(jsonPath("$.assignee").value("none"))
                .andExpect(jsonPath("$.version").value(0))
                .andExpect(jsonPath("$.created_at").isNotEmpty())
                .andExpect(jsonPath("$.updated_at").isNotEmpty())
                .andReturn();

        String responseBody = result.getResponse().getContentAsString();

        String id = JsonPath.read(responseBody, "$.id");
        Number version = JsonPath.read(responseBody, "$.version");

        return new CreatedTask(id, version.longValue());
    }

    private String registrationJson(String email) {
        return """
                {
                  "email": "%s",
                  "password": "%s"
                }
                """.formatted(email, PASSWORD);
    }

    private String bearer(String token) {
        return "Bearer " + token;
    }

    private String entityTag(long version) {
        return "\"" + version + "\"";
    }

    private record CreatedTask(String id, long version) {
    }

}
