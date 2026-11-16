# OSManager

[![CI](https://github.com/AronBastos/osmanager/actions/workflows/ci.yml/badge.svg)](https://github.com/AronBastos/osmanager/actions/workflows/ci.yml)
![Java](https://img.shields.io/badge/Java-17-orange)
![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.2-6DB33F?logo=springboot&logoColor=white)
![License](https://img.shields.io/badge/license-MIT-blue)

API REST para gestão de **ordens de serviço de uma oficina mecânica**: cadastro de clientes e abertura/andamento de ordens de serviço.

Projeto de estudo em **Java 17 + Spring Boot 3**, com arquitetura em camadas (controller → service → repository), DTOs separados de request/response, validação com Bean Validation e tratamento de erros centralizado.

> O projeto nasceu em Java EE (JSF/PrimeFaces + Tomcat) e foi migrado para Spring Boot. O histórico do git preserva essa fase.

## Stack

| | |
|---|---|
| Linguagem / framework | Java 17 · Spring Boot 3.2 (Web, Data JPA, Validation) |
| Banco | H2 em memória (perfil `dev`) · MySQL 8 (perfil `prod`) |
| Documentação da API | springdoc-openapi (Swagger UI, só no perfil `dev`) |
| Testes | JUnit 5 · Mockito · MockMvc (`@WebMvcTest`) |
| Build / infra | Maven · Docker (multi-stage) · Docker Compose · GitHub Actions |

## Como rodar

### Opção 1 — sem Docker (perfil `dev`, banco H2 em memória)

Requer JDK 17 e Maven.

```bash
mvn spring-boot:run
```

- API: http://localhost:8080/api/clients
- Swagger UI: http://localhost:8080/swagger-ui.html
- Console H2: http://localhost:8080/h2-console (JDBC URL `jdbc:h2:mem:osmanager_dev`, usuário `sa`, senha vazia)

Os dados são perdidos ao parar a aplicação.

### Opção 2 — Docker Compose (perfil `prod`, MySQL)

```bash
cp .env.example .env     # ajuste as senhas
docker compose up --build
```

A API sobe em http://localhost:8080. No perfil `prod` o Swagger fica desligado.

## Endpoints

**Clientes** — `/api/clients`

```
GET    /api/clients                 Lista clientes
GET    /api/clients/{id}            Busca por id
GET    /api/clients/search?name=    Busca por nome
GET    /api/clients/count           Total de clientes
POST   /api/clients                 Cria cliente (validado)
PUT    /api/clients/{id}            Atualiza cliente
DELETE /api/clients/{id}            Desativa o cliente (exclusão lógica)
```

**Ordens de serviço** — `/api/orders`

```
GET    /api/orders                  Lista ordens
GET    /api/orders/{id}             Busca por id
GET    /api/orders/client/{id}      Ordens de um cliente
GET    /api/orders/count?status=    Contagem por status
POST   /api/orders                  Abre uma ordem
PATCH  /api/orders/{id}/start       Inicia
PATCH  /api/orders/{id}/complete    Conclui
PATCH  /api/orders/{id}/cancel      Cancela
```

Erros seguem um formato único (`timestamp`, `status`, `error`, `message`, `fieldErrors` quando há falha de validação): 400 para entrada inválida, 404 para recurso inexistente, 422 para regra de negócio violada.

## Testes

```bash
mvn test
```

21 testes: serviço de clientes (Mockito), controllers de clientes e de ordens (MockMvc) e o tratamento de erros (400/404 em vez de 500). O CI roda a suíte a cada push e pull request.

## Estrutura

```
src/main/java/com/osmanager/
├── controller/   endpoints REST
├── service/      regras de negócio e transações
├── repository/   Spring Data JPA
├── model/        entidades JPA
├── dto/          objetos de request/response
└── exception/    exceções de domínio e @RestControllerAdvice
```

## Estado atual e próximos passos

Projeto em evolução. O que ainda **não** está pronto:

- **Técnicos, peças e serviços:** existem as entidades (e o repositório de técnicos), mas ainda não há endpoints. Como `POST /api/orders` exige um `tecnicoId`, o fluxo completo de ordens depende de um CRUD de técnicos.
- **Testes da camada de ordens de serviço:** só o controller tem cobertura; falta testar o `OrdemServicoService`.
- **Sem autenticação/autorização** (Spring Security).
- **Sem migrations** (Flyway/Liquibase): no Compose o schema é criado pelo Hibernate (`ddl-auto=update`), aceitável para demo, não para produção.

## Licença

[MIT](LICENSE)
