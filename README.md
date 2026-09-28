# OSManager — Service Order Management System

A backend for managing a repair shop's service orders: clients, technicians,
parts, services and the orders that tie them together. Built in **Java 17**
with **Spring Boot 3**, **Spring Data JPA/Hibernate** and **MySQL**.

## What it solves

A repair shop needs to track who a client is, what they brought in, which
technician is assigned, which parts and services go on the bill, and the
order's status end to end. `ClientController`/`ClientService` and
`OrdemServicoController`/`OrdemServicoService` expose that as a REST API
with validation and centralized error handling (`ResourceNotFoundException`
+ a global exception handler).

## Architecture

```
src/main/java/com/osmanager/
├── model/        JPA entities
├── repository/   Spring Data JPA repositories
├── service/      business logic
├── controller/   REST controllers
├── dto/          request/response DTOs
└── exception/    centralized error handling
```

### Design decisions

- **Migrated from a JSF/PrimeFaces + Java EE stack to Spring Boot 3 REST +
  JPA** (see the `abc589c refactor: Java EE to Spring Boot` commit). The
  original server-rendered JSF views don't fit a REST-consuming SPA/mobile
  client model, and Spring Boot's ecosystem (auto-configuration, embedded
  server, Spring Data) removes a large amount of the Java EE boilerplate
  the original code carried.
- **`open-in-view: false`.** With Spring Boot's default (`true`), the
  `EntityManager` stays open across the whole HTTP request, so lazy loading
  "just works" in the view layer but can silently trigger N+1 queries.
  Turning it off forces every query needed by a response to happen
  explicitly in the service layer, where it's visible in a code review.
- **H2 for tests, MySQL for dev/prod.** `spring-boot-starter-test` +
  `h2` let `ClientControllerTest`/`ClientServiceTest` boot a full Spring
  context without a database container; `mysql-connector-j` and
  `docker-compose.yml` provision the real MySQL 8 target for local runs.

## How to run

```bash
mvn clean install        # build
mvn test                 # run tests
mvn package               # produce the runnable jar
```

With Docker (MySQL + app):

```bash
docker compose up -d
```

Env vars are documented in `.env.example`; `application-prod.yml` reads
`DB_PASSWORD` (default `change_me_in_prod`) rather than hardcoding a
credential.

## Current state and limitations

- Tests pass (`mvn test`, 18/18) — but only `Client`/`ClientController`
  have test coverage; `OrdemServico`, `Tecnico`, `Peca` and `Servico` don't
  yet.
- **The domain model is a mix of English and Portuguese naming**, a
  side effect of the ongoing Java EE → Spring Boot migration: `Client` and
  `Person` were renamed and are the ones actually wired up and tested;
  `OrdemServico`, `Peca`, `Servico` and `Tecnico` are still in Portuguese
  and actively used by the rest of the codebase (not yet renamed — that's
  a real refactor across controller/service/repository/DTO layers, not a
  simple rename). `Cliente.java` is dead code left over from the
  migration — nothing in the codebase references it.
- Extensive supporting documentation lives under `docs/` (setup guides,
  troubleshooting, architecture notes) — still in Portuguese; translating
  it is a separate pass from this README.
- No CI was running before this pass; a GitHub Actions workflow now builds
  and tests on every push, not yet verified green (this repo has commits
  ahead of the last push).

## Stack

Java 17 · Spring Boot 3.2 · Spring Data JPA · Hibernate · MySQL 8 · H2
(tests) · springdoc-openapi · JUnit 5 · Mockito · Maven · Docker Compose

## Documentation

Deeper setup/troubleshooting docs (in Portuguese): [`docs/README.md`](./docs/README.md).

## License

MIT — see [LICENSE.md](LICENSE.md).
