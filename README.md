# TaskFlow

A full-stack task management application: Java 17 / Spring Boot REST API with stateless JWT authentication and role-based access control, plus a React single-page frontend.

![CI](https://github.com/mraedkareem/taskflow/actions/workflows/ci.yml/badge.svg)

## Features

- **RESTful API** — 17 endpoints, full CRUD, JSON request/response
- **JWT authentication** — stateless, the token carries the user's role
- **Role-based access control** — `/api/admin/**` requires the ADMIN role (Spring Security)
- **Per-user data isolation** — users can only see and modify their own tasks
- **React SPA** — protected routing, component-based state management (Context + hooks)
- **PostgreSQL + JPA/Hibernate** — entities mapped with Spring Data JPA
- **API documentation** — interactive Swagger UI via springdoc-openapi
- **Tested** — JUnit 5 + Mockito unit tests, H2 in-memory database for integration tests
- **CI/CD** — GitHub Actions tests every push; Railway auto-deploys `main` (see [DEPLOYMENT.md](DEPLOYMENT.md))

## Tech stack

| Layer | Technology |
|---|---|
| Language | Java 17 |
| Framework | Spring Boot 3.5 |
| Security | Spring Security + JWT (jjwt) |
| Database | PostgreSQL (H2 in tests) |
| ORM | JPA / Hibernate |
| Docs | Swagger / OpenAPI (springdoc) |
| Testing | JUnit 5, Mockito |
| CI | GitHub Actions |

## API endpoints

### Auth (public)

| Method | Endpoint | Description |
|---|---|---|
| POST | `/api/auth/register` | Create an account |
| POST | `/api/auth/login` | Log in, returns a JWT |

### Tasks (require `Authorization: Bearer <token>`)

| Method | Endpoint | Description |
|---|---|---|
| POST | `/api/tasks` | Create a task |
| GET | `/api/tasks` | List your tasks |
| GET | `/api/tasks/{id}` | Get one task |
| PUT | `/api/tasks/{id}` | Update a task |
| PATCH | `/api/tasks/{id}/status` | Change only the status |
| DELETE | `/api/tasks/{id}` | Delete a task |
| GET | `/api/tasks/status/{status}` | Filter by status (TODO / IN_PROGRESS / DONE) |
| GET | `/api/tasks/stats` | Task counts per status |

### Profile (require token)

| Method | Endpoint | Description |
|---|---|---|
| GET | `/api/users/me` | Current user's profile |
| PUT | `/api/users/me` | Update email |
| PUT | `/api/users/me/password` | Change password |

### Admin (require token with ADMIN role)

| Method | Endpoint | Description |
|---|---|---|
| GET | `/api/admin/users` | List all users |
| DELETE | `/api/admin/users/{id}` | Delete a user (and their tasks) |
| GET | `/api/admin/tasks` | List all tasks in the system |
| DELETE | `/api/admin/tasks/{id}` | Delete any task |

An `admin` account is seeded at startup (password = `ADMIN_PASSWORD` env var).

Interactive docs: `http://localhost:8080/swagger-ui/index.html`

## Architecture

```
HTTP Request → Controller → Service → Repository → PostgreSQL
                   ↑
        JwtAuthenticationFilter (validates the token before the controller runs)
```

## Running locally

Requirements: Java 17, PostgreSQL with a `taskflow` database.

```bash
# create the database (once)
createdb -U postgres taskflow

# start the app
./mvnw spring-boot:run
```

The app starts on `http://localhost:8080`. Hibernate creates the tables automatically.

### Configuration

All secrets are read from environment variables (with local-dev fallbacks):

| Variable | Purpose |
|---|---|
| `DB_URL` | JDBC URL of the PostgreSQL database |
| `DB_USERNAME` / `DB_PASSWORD` | Database credentials |
| `JWT_SECRET` | Key used to sign tokens (min 32 chars) |
| `ADMIN_PASSWORD` | Password for the seeded `admin` account |
| `PORT` | HTTP port (set by the hosting platform) |

## Running tests

```bash
./mvnw test
```

Tests use an in-memory H2 database — no PostgreSQL needed.

## Frontend development

The production build is served by Spring Boot from `src/main/resources/static`.
For development with hot reload:

```bash
cd frontend
npm install
npm run dev    # http://localhost:5173, /api proxied to the backend on :8080
```

## Example usage

```bash
# register
curl -X POST http://localhost:8080/api/auth/register \
  -H "Content-Type: application/json" \
  -d '{"username":"alice","email":"alice@example.com","password":"secret123"}'

# log in → returns {"token":"..."}
curl -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{"username":"alice","password":"secret123"}'

# create a task
curl -X POST http://localhost:8080/api/tasks \
  -H "Authorization: Bearer <token>" \
  -H "Content-Type: application/json" \
  -d '{"title":"Learn Spring Boot","description":"Finish TaskFlow"}'
```
