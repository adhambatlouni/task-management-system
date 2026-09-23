package com.adham.taskmanagement.common.openapi;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.ExternalDocumentation;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.servers.Server;
import io.swagger.v3.oas.models.tags.Tag;
import org.springdoc.core.customizers.OpenApiCustomizer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI taskManagementOpenApi(
            @Value("${app.openapi.server-url:}") String serverUrl
    ) {
        OpenAPI openApi = new OpenAPI()
                .info(new Info()
                        .title("Task Management API")
                        .description("""
                                Spring Boot REST API for task workflows with JWT authentication, service-layer authorization, optimistic concurrency, and append-only activity history.
                                """)
                        .version("1.0.0")
                        .contact(new Contact()
                                .name("Adham Batlouni")
                                .url("https://github.com/adhambatlouni")
                        )
                )
                .externalDocs(new ExternalDocumentation()
                        .description("Source code, architecture, and setup guide")
                        .url("https://github.com/adhambatlouni/task-management-system")
                )
                .tags(apiTags())
                .components(new Components()
                        .addSecuritySchemes(
                                "basicAuth",
                                new SecurityScheme()
                                        .type(SecurityScheme.Type.HTTP)
                                        .scheme("basic")
                                        .description(
                                                "Use a registered email and password only to obtain a JWT"
                                        )
                        )
                        .addSecuritySchemes(
                                "bearerAuth",
                                new SecurityScheme()
                                        .type(SecurityScheme.Type.HTTP)
                                        .scheme("bearer")
                                        .bearerFormat("JWT")
                                        .description(
                                                "Use the token returned by POST /api/auth/token; Swagger adds the Bearer prefix"
                                        )
                        )
                );

        if (!serverUrl.isBlank()) {
            openApi.addServersItem(
                    new Server()
                            .url(serverUrl.strip())
                            .description("Production API")
            );
        }

        return openApi;
    }

    @Bean
    public OpenApiCustomizer orderedOpenApiTags() {
        return openApi -> openApi.setTags(apiTags());
    }

    private List<Tag> apiTags() {
        return List.of(
                new Tag()
                        .name("Accounts")
                        .description(
                                "Create an account with a unique email address"
                        ),
                new Tag()
                        .name("Authentication")
                        .description(
                                "Exchange account credentials for a signed JWT access token"
                        ),
                new Tag()
                        .name("Tasks")
                        .description(
                                "Create, retrieve, filter, assign, and transition versioned tasks"
                        ),
                new Tag()
                        .name("Comments")
                        .description(
                                "Add and retrieve comments attached to tasks"
                        ),
                new Tag()
                        .name("Task activities")
                        .description(
                                "Read append-only, actor-attributed task change history"
                        )
        );
    }
}