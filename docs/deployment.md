# Free cloud deployment

The hosted portfolio environment uses two independent free services:

- Render runs the Dockerized Spring Boot API.
- Neon provides the persistent PostgreSQL database.

The repository intentionally does not declare a Render Postgres database because Render's free database expires after 30 days.

## 1. Create the Neon database

1. Create a Neon project in a European region, preferably Frankfurt.
2. Select PostgreSQL 18 when it is available.
3. Open the project's connection details and record the host, database, role, and password.
4. Build the JDBC URL without embedding credentials:

   ```text
   jdbc:postgresql://<host>/<database>?sslmode=require
   ```

Keep the role and password separate. Never commit any of these values.

## 2. Create the Render service

1. In Render, create a new Blueprint.
2. Connect `adhambatlouni/task-management-system`.
3. Render reads `render.yaml` and creates one free Docker web service in Frankfurt.
4. When prompted, provide the three secret values:

   | Variable | Value |
   |---|---|
   | `DB_URL` | Neon JDBC URL from the previous step |
   | `DB_USERNAME` | Neon database role |
   | `DB_PASSWORD` | Neon database password |

Render generates `JWT_SECRET_BASE64`; it is never stored in the repository.

The Blueprint waits for GitHub checks to pass before automatically deploying a new commit from `main`.

## 3. Verify the deployment

During startup, Flyway applies the versioned migrations to the empty Neon database and Hibernate validates the resulting schema.

After Render reports a successful deployment, verify:

```text
https://<service-name>.onrender.com/actuator/health
https://<service-name>.onrender.com/swagger-ui.html
```

The health endpoint should return:

```json
{
  "status": "UP"
}
```

Use Swagger UI to register a demonstration account and exercise the documented workflow. Do not reuse local or personal passwords.

## Free-tier behavior

Render suspends the API after a period without traffic and starts it again on the next request. The first request after suspension can therefore be slow. Neon also scales idle compute down automatically. These behaviors reduce resource usage while preserving the database records.
