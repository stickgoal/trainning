# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Overview

This project (`llm-explain`) aims to use Java code to simply explain the principles of Large Language Models (LLMs). It is part of a larger monorepo (`trainning`) at `D:\workspace\处理\trainning\` — a training/demo workspace for Java + AI technologies.

## Project Goal

> 使用Java代码来，简单解释LLM的原理。
> (Use Java code to simply explain the principles of LLM.)

This project should provide educational, self-contained Java examples that demonstrate LLM concepts such as:
- Tokenization and embeddings
- Attention mechanisms
- Transformer architecture basics
- Text generation / sampling strategies
- Training concepts (if relevant)

## Monorepo Context

The `llm-explain` project lives alongside these sibling projects under `D:\workspace\处理\trainning\ai\`:

| Project | Tech Stack | Purpose |
|---|---|---|
| `helloworld` | Spring AI + Alibaba DashScope | Basic AI chat demo |
| `langchain4j-demo` | LangChain4j 1.16.0 + Spring Boot 3.5.15 | Multi-agent, workflow, memory demos |
| `rag-redis1` | Spring AI 1.1.2 + DashScope + Redis Vector Store | RAG (Retrieval-Augmented Generation) |
| `mcp/` | Spring AI MCP + Nacos + Feign | Model Context Protocol demo (multi-module) |
| `woniu-ai-front/` | Vite + Vue/JS (compiled to zip) | Frontend UI for AI demos |

## Common Commands

All commands assume `mvn` is available and Java 17+ is configured. The project has no `pom.xml` yet — create one when ready.

### Build & Test (Standard Maven, once pom.xml exists)

```bash
# Clean compile
mvn clean compile

# Run all tests
mvn clean test

# Package as JAR (via Spring Boot plugin)
mvn clean package

# Run a single test class
mvn clean test -Dtest=ClassName

# Run a single test method
mvn clean test -Dtest=ClassName#methodName

# Run the application
mvn spring-boot:run
```

### Sibling project commands (reference)

```bash
# From any sibling directory (e.g., langchain4j-demo)
cd ../../langchain4j-demo
mvn clean compile            # compile only (tests no longer require)
mvn clean package -DskipTests
mvn spring-boot:run
mvn clean test              # including tests
```

## Technology Stack & Conventions

Based on sibling projects, this codebase follows these conventions:

- **Language**: Java 17
- **Build System**: Maven (directly inheriting `spring-boot-starter-parent` — no parent POM in the monorepo)
- **AI Framework** (choose based on goals):
  - **Spring AI** + Alibaba DashScope — for Spring-ecosystem AI apps
  - **LangChain4j** — for more flexible, lightweight AI orchestration (used by the most feature-rich sibling `langchain4j-demo`)
  - To keep it educational and dependency-light: pure Java without a heavy AI framework is also appropriate
- **LLM Provider**: Alibaba DashScope (通义千问) via `AI_DASHSCOPE_API_KEY` env var. For educational purposes, this project may also work without an API key (local explanations).
- **Lombok**: Used for reducing boilerplate (`@Data`, `@Slf4j`, etc.) — requires `maven-compiler-plugin` annotation processor config if used.
- **`application.yml` / `application.properties`**: Placed in `src/main/resources/`.

## Architecture Patterns (from sibling projects)

When creating the project structure, follow these established patterns:

### Package Organization

```
src/main/java/me/maiz/llmexplain/
  LlmExplainApplication.java       -- @SpringBootApplication entry point
  controller/                       -- REST endpoints
  service/                          -- Business logic
  component/                        -- @Component / @Service / @Tool beans
  config/                           -- @Configuration classes
  entity/                           -- DTOs / models
src/main/resources/
  application.yml                   -- Config (DashScope key, etc.)
src/test/java/me/maiz/llmexplain/
  LlmExplainApplicationTests.java   -- Basic context load test
```

### Common Endpoint Pattern

```java
// Synchronous chat
@GetMapping("/ai")
public String chat(@RequestParam("question") String question);

// SSE streaming
@GetMapping(value = "/ai/sse/{conversationId}", produces = "text/event-stream")
public Flux<String> chatStream(@PathVariable String conversationId, @RequestParam("question") String question);
```

### Key Dependencies (pom.xml pattern)

When creating `pom.xml`:
- Parent: `org.springframework.boot:spring-boot-starter-parent` (latest stable)
- Java: 17 (`<java.version>17</java.version>`)
- Lombok: optional scope + annotation processor path config in `maven-compiler-plugin`
- Spring Boot Maven Plugin with Lombok exclusion

## Git Context

- **Remote**: `https://github.com/stickgoal/trainning`
- **Git user**: Lucas
- **Branch**: `master` (no feature branching convention observed)
- **Commit style**: Chinese commit messages (e.g., "增加内容", "增加langchain4j的一些demo")
- **`.gitignore`**: Comprehensive — covers Eclipse, IDEA, VS, Python, Maven targets, Node, macOS.
