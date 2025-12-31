# Repository Guidelines

## Project Structure & Module Organization

- `src/main/java/es/jklabs/lib/loteria/` contains the library code (API connection, models, converters, enums).
- `src/test/java/es/jklabs/` contains JUnit 4 tests.
- `pom.xml` defines Maven build config, Java 11 toolchain, and dependencies.
- `target/` is Maven build output (generated).

## Build, Test, and Development Commands

- `mvn clean` removes generated build artifacts under `target/`.
- `mvn test` runs JUnit 4 tests in `src/test/java`.
- `mvn package` compiles and packages the library JAR.

## Coding Style & Naming Conventions

- Use standard Java conventions: 4-space indentation, `UpperCamelCase` for classes, `lowerCamelCase` for methods and
  variables.
- Keep package names lower-case and aligned to `es.jklabs.lib.loteria`.
- No formatter or linter is configured; keep changes minimal and consistent with existing files.

## Testing Guidelines

- Tests use JUnit 4 (`org.junit.Test`, `Assert`).
- Network-dependent tests (like `ApiTest`) hit the external API; expect potential flakiness when offline.
- Name tests with `*Test` suffix and place them under `src/test/java`.

## Commit & Pull Request Guidelines

- Commit messages are short and direct; existing history includes Spanish messages (e.g., "Actualizar versión") and
  Dependabot bumps.
- Keep messages in the imperative mood and reference the change scope when helpful (e.g., "Actualizar dependencia
  jackson").
- For PRs, include a brief description, link related issues, and note test results (e.g., `mvn test`).

## Configuration & Compatibility Notes

- Java 11 is the target runtime (`maven.compiler.source`/`target` in `pom.xml`).
- Dependencies are managed in `pom.xml`; prefer updating versions via Maven and keeping the changelog in `README.md`
  consistent with releases.
