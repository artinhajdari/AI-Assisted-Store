# Store

A demo store application using RAG built with Spring Boot, Thymeleaf, and Spring AI.

## Purpose

This project showcases the helpful AI usage in product searching through RAG.
It is intended as a learning and reference project for combining classic Spring MVC architecture with AI-assisted search.

## Demo usage
![Description](./demo/site-demo.gif)

## Core Features

- Layered architecture with Spring Boot and Spring MVC
- Public storefront with product search
- Authentication and authorization (USER / ADMIN roles)
- Admin product CRUD flows
- Admin user lock/unlock flow
- Localized validation and UI messages
- Semantic search pipeline:
  - vector similarity search in PostgreSQL/pgvector
  - optional LLM filtering for better relevance

## Tech Stack

- Java 21
- Spring Boot 3.5.x
- Spring MVC + Thymeleaf
- Spring Security
- Spring Data JPA
- PostgreSQL
- pgvector (via Spring AI vector store starter)
- Spring AI (Mistral starter currently configured as dependency)
- MapStruct
- Maven
- JUnit 5 + Mockito (for unit tests)

### Search Flow (High Level)

1. User submits search query.
2. Service performs vector similarity search in pgvector.
3. Matching product IDs are resolved back to relational entities.
4. Optional LLM post-filtering narrows results further.

## Project Structure

```text
src/main/java/com/demo/Store
  controller/
  service/
  service/impl/
  repository/
  model/
  security/
  advices/
  listener/
  util/

src/main/resources
  templates/
  static/
  i18n/
  application.properties
```

## Running the Project

### 1. Prerequisites

- This project was developed and tested with JDK 21 and uses Maven as the build tool.
- The database of choice is PostgreSQL together with pgvector extension for its vector storing capabilities.
- For semantic LLM filtering, you will need an API key from an AI provider (Mistral currently configured).

### 2. Configure application settings

Create an `application-local.properties` (or set environment variables) with your own values.

Example template:

```properties
# Server
server.port=8080

# Database
spring.datasource.url=jdbc:postgresql://localhost:5432/store
spring.datasource.username=YOUR_DB_USER
spring.datasource.password=YOUR_DB_PASSWORD
#use 'create' in the first run to automatically create the database tables
spring.jpa.hibernate.ddl-auto=update

# Messages
spring.messages.basename=i18n/messages

# Vector store (already present in base config, override if needed)
spring.ai.vectorstore.pgvector.initialize-schema=true
spring.ai.vectorstore.pgvector.dimensions=1024
spring.ai.vectorstore.pgvector.distance-type=COSINE_DISTANCE
spring.ai.vectorstore.pgvector.index-type=HNSW

# AI provider key (keep hidden; do not commit)
# For current Mistral starter dependency:
spring.ai.mistralai.api-key=${MISTRAL_API_KEY}

# Chat/embedding model selection (users can change these)
# Example names only; choose models available in your provider account
spring.ai.mistralai.chat.options.model=mistral-large-latest
spring.ai.mistralai.embedding.options.model=mistral-embed
```

Notes:
- Keep secrets in environment variables or private `.properties` files excluded from version control.
- Users of this project are expected to change chat/embedding models based on cost, quality, and availability.
- If you switch AI provider starter, update the corresponding `spring.ai.*` properties accordingly.

### 3. Start the app

```bash
mvn spring-boot:run
```

Then open:
- `http://localhost:8080`

## Security and Roles

- Public routes: storefront, login, register, search
- `ADMIN` routes: `/admin/**`
- `USER` / `ADMIN` route: `/checkout`

If you need admin access locally, make sure you have at least one user with `ADMIN` role in the database.

## Future Improvements

- Introduce pagination/sorting/filtering in admin and storefront UIs
- Add rate limiting and resilience (timeouts/retries) around AI calls
- Add observability (structured logs, metrics, tracing)
- Add caching for frequent search queries
- Improve security hardening (CSRF review, stricter headers, brute-force protection)
