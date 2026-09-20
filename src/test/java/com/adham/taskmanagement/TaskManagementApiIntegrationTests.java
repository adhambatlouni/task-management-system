package com.adham.taskmanagement;

import com.jayway.jsonpath.JsonPath;
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
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.httpBasic;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@Testcontainers
@AutoConfigureMockMvc
@SpringBootTest(properties = "spring.datasource.password=test")
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

    @Test
    void appliesTheInitialDatabaseMigration() {
        assertThat(flyway.info().current().getVersion().getVersion())
                .isEqualTo("2");
    }

    @Test
    void supportsTheCompleteTaskWorkflow() throws Exception {
        String owner = "workflow-owner@example.com";
        String assignee = "workflow-assignee@example.com";

        register(owner);
        register(assignee);

        String ownerToken = obtainToken(owner);
        String assigneeToken = obtainToken(assignee);

        String taskId = createTask(ownerToken);

        mockMvc.perform(put("/api/tasks/{taskId}/assign", taskId)
                        .header(HttpHeaders.AUTHORIZATION, bearer(ownerToken))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "assignee": "%s"
                                }
                                """.formatted(assignee)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.assignee").value(assignee));

        mockMvc.perform(put("/api/tasks/{taskId}/status", taskId)
                        .header(HttpHeaders.AUTHORIZATION, bearer(assigneeToken))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "status": "IN_PROGRESS"
                                }
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("IN_PROGRESS"));

        mockMvc.perform(post("/api/tasks/{taskId}/comments", taskId)
                        .header(HttpHeaders.AUTHORIZATION, bearer(assigneeToken))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "text": "Authentication has been verified"
                                }
                                """))
                .andExpect(status().isOk());

        mockMvc.perform(get("/api/tasks/{taskId}/comments", taskId)
                        .header(HttpHeaders.AUTHORIZATION, bearer(ownerToken)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].task_id").value(taskId))
                .andExpect(jsonPath("$[0].text")
                        .value("Authentication has been verified"))
                .andExpect(jsonPath("$[0].author").value(assignee));

        mockMvc.perform(get("/api/tasks")
                        .param("author", owner)
                        .param("assignee", assignee)
                        .header(HttpHeaders.AUTHORIZATION, bearer(ownerToken)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(taskId))
                .andExpect(jsonPath("$[0].status").value("IN_PROGRESS"))
                .andExpect(jsonPath("$[0].author").value(owner))
                .andExpect(jsonPath("$[0].assignee").value(assignee))
                .andExpect(jsonPath("$[0].total_comments").value(1));
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
                .andExpect(status().isConflict());

        String ownerToken = obtainToken(owner);
        String assigneeToken = obtainToken(assignee);
        String taskId = createTask(ownerToken);

        mockMvc.perform(put("/api/tasks/{taskId}/assign", taskId)
                        .header(HttpHeaders.AUTHORIZATION, bearer(assigneeToken))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "assignee": "%s"
                                }
                                """.formatted(assignee)))
                .andExpect(status().isForbidden());

        mockMvc.perform(get("/api/tasks/{taskId}/comments", 999999)
                        .header(HttpHeaders.AUTHORIZATION, bearer(ownerToken)))
                .andExpect(status().isNotFound());
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

    private String createTask(String token) throws Exception {
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
                .andExpect(jsonPath("$.status").value("CREATED"))
                .andExpect(jsonPath("$.assignee").value("none"))
                .andReturn();

        return JsonPath.read(
                result.getResponse().getContentAsString(),
                "$.id"
        );
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

}
