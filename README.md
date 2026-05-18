# psocial_selfmanager

Core user-facing service for ProductiveSocial. Manages users, tasks, habits, routines, and projects. Shared JWT secret with `psocial_timer` so one token works across both services.

- **Language:** Kotlin / Ktor
- **Port:** 1226
- **Database:** PostgreSQL (also owns the shared user registry read by timer)

## Responsibilities

- Email-only user identity (`POST /api/v1/auth/identify`)
- CRUD for projects, tasks, habits, and routines
- Offline sync endpoint (`POST /api/v1/sync`) for KMP mobile clients
- Internal endpoints for analytics to pull per-user data

## Environment Variables

| Variable | Description |
|---|---|
| `DATABASE_URL` | JDBC connection string for the service DB |
| `DB_USER` / `DB_PASSWORD` | Database credentials |
| `USER_REGISTRY_DB_*` | Connection details for the shared user registry |
| `JWT_SECRET` | Shared JWT secret (same value as timer) |
| `JWT_ISSUER` | Token issuer |
| `JWT_AUDIENCE` | Token audience |
| `JWT_REALM` | Token realm |
| `INTERNAL_API_KEY` | Shared secret for service-to-service calls |
| `BILLING_SERVICE_URL` | URL of `psocial_billing` |
| `TIMER_SERVICE_URL` | URL of `psocial_timer` |
| `PORT` | Port to listen on (default: 1226) |
| `HOST` | Host to bind to (default: 0.0.0.0) |
| `ALLOWED_ORIGINS` | CORS allowed origins |

## Build & Run

**Run locally:**
```bash
./gradlew :server:run
```

**Build fat JAR:**
```bash
./gradlew :server:shadowJar
# Output: server/build/libs/server-all.jar
```

**Build Docker image:**
```bash
docker build -t productivesocial-selfmanager .
```

## API Endpoints

| Method | Path | Description |
|---|---|---|
| `POST` | `/api/v1/auth/identify` | Find-or-create user by email, return JWT |
| `POST` | `/api/v1/auth/refresh` | Rotate refresh token |
| `POST` | `/api/v1/auth/logout` | Revoke token for this device |
| `POST` | `/api/v1/auth/logout-all` | Revoke all tokens |
| `POST` | `/api/v1/sync` | Bidirectional sync for KMP clients |
| `GET/POST` | `/api/v1/projects/projects` | List / create projects |
| `GET/PATCH/DELETE` | `/api/v1/projects/project/{id}` | Get / update / delete project |
| `GET/POST` | `/api/v1/tasks/tasks` | List / create tasks |
| `GET/PATCH/DELETE` | `/api/v1/tasks/task/{id}` | Get / update / delete task |
| `GET/POST` | `/api/v1/habits/habits` | List / create habits |
| `GET/PATCH/DELETE` | `/api/v1/habits/habit/{id}` | Get / update / delete habit |
| `GET/POST` | `/api/v1/routines/routines` | List / create routines |
| `GET/PATCH/DELETE` | `/api/v1/routines/routine/{id}` | Get / update / delete routine |
| `GET` | `/internal/users/{id}/tasks` | Internal: tasks for analytics |
| `GET` | `/internal/users/{id}/habits` | Internal: habits for analytics |
| `GET` | `/internal/users/{id}/routines` | Internal: routines for analytics |

Swagger UI available at `/swagger` when running locally.
