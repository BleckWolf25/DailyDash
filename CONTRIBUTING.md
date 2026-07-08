# Contributing to DailyDash

First off, thank you for taking the time to contribute! Contributions from the community help make the DailyDash task manager more comprehensive, stable, and helpful for everyone.

By participating in this project, you agree to abide by our [Code of Conduct](CODE_OF_CONDUCT.md).

---

## Table of Contents

- [Contributing to DailyDash](#contributing-to-dailydash)
  - [Table of Contents](#table-of-contents)
  - [How Can I Contribute?](#how-can-i-contribute)
    - [Reporting Bugs](#reporting-bugs)
    - [Suggesting Enhancements](#suggesting-enhancements)
    - [Documentation Improvements](#documentation-improvements)
    - [Pull Requests](#pull-requests)
  - [Development Setup](#development-setup)
    - [Prerequisites](#prerequisites)
    - [Setting Up Your Workspace](#setting-up-your-workspace)
    - [Development Commands](#development-commands)
  - [Style \& Code Guidelines](#style--code-guidelines)
    - [Java Coding Style](#java-coding-style)
    - [JavaFX/FXML Best Practices](#javafxfxml-best-practices)
    - [Commit Messages](#commit-messages)
  - [Testing](#testing)
    - [Writing Unit Tests](#writing-unit-tests)
  - [Security Vulnerabilities](#security-vulnerabilities)

---

## How Can I Contribute?

### Reporting Bugs

We use structured GitHub Issue Forms to track bug reports. Before submitting a bug report, please:

1. Check the existing issues to ensure it hasn't been reported or resolved already.
2. Verify that it is reproducible on a clean database without custom local overrides.
3. Open a GitHub Issue and fill out the form completely, including:
   - Project version
   - OS information (Windows, macOS, Linux)
   - Step-by-step instructions to reproduce the issue
   - Screenshots or error stack traces if applicable

### Suggesting Enhancements

If you have ideas for new features, UI improvements, or automation additions:

1. Search the issues to verify your suggestion hasn't been discussed before.
2. Open a Feature Request describing the functionality, the problem it solves, and how it might be implemented.

### Documentation Improvements

If you find inaccurate information, typos, or outdated content in documentation:

Please open a Documentation Issue or submit a pull request with your improvements.

### Pull Requests

To submit code changes:

1. **Fork** the repository and create your branch from `main` (e.g., `feature/your-feature-name` or `bugfix/issue-description`).
2. Make your changes, keeping them focused. Avoid unrelated changes.
3. Write clean, readable code following our guidelines.
4. Ensure your changes compile and pass all tests locally.
5. Submit a Pull Request (PR) with a clear description of the changes and references to any related issues.

---

## Development Setup

This project is built with **JavaFX**, **Maven**, and **SQLite**.

### Prerequisites

- **Java Development Kit (JDK)** 21 or higher: Ensure you have JDK 21 installed and configured in your environment variables.
- **Maven** 3.9.0 or higher: We use Maven for dependency management and build orchestration.
- **Git**: Installed and configured on your system.

### Setting Up Your Workspace

1. **Clone the repository:**

   ```bash
   git clone https://github.com/BleckWolf25/DailyDash.git
   cd DailyDash
   ```

2. **Run the application locally:**

   ```bash
   mvn clean javafx:run
   ```

### Development Commands

Use the following Maven commands in your project root:

- **Start development server:**

  ```bash
  mvn javafx:run
  ```

- **Build standalone executable package:**

  ```bash
  mvn clean package
  ```

- **Run all automated tests:**

  ```bash
  mvn clean test
  ```

- **Package native build via GluonFX:**

  ```bash
  mvn gluonfx:build
  ```

---

## Style & Code Guidelines

### Java Coding Style

To keep the codebase uniform and easy to read:

- **Indentation:** Use 4 spaces for indentation. Do not use tabs.
- **Naming Conventions:**
  - Classes and Interfaces: `PascalCase`
  - Methods and Variables: `camelCase`
  - Constants: `UPPER_SNAKE_CASE`
- **Braces:** Use standard Egyptian brackets style:

  ```java
  public void exampleMethod() {
      if (condition) {
          // code
      } else {
          // code
      }
  }
  ```

- **Comments:** Add JSDoc-style JavaDoc comments to public API methods, services, and complex database transaction logic.

### JavaFX/FXML Best Practices

- **FXML Properties:** Keep FXML bindings cleanly scoped inside Controller classes using the `@FXML` annotation.
- **Graphic Assets**: Utilize `IconUtil.getIcon(...)` for vector SVG icons instead of hardcoded strings or images.
- **Separation of Concerns:** Keep view logic in `.fxml` / view builder files and state/controller handling in Controllers.

### Commit Messages

Use clear and descriptive commit messages. We recommend using prefix tags for commits, such as:

- `feat: ...` for a new feature
- `fix: ...` for a bug fix
- `docs: ...` for documentation changes
- `refactor: ...` for code style or internal design changes
- `style: ...` for formatting fixes
- `test: ...` for adding or updating tests
- `chore: ...` for maintenance tasks

Example:

```text
feat: add custom delete project confirmation dialog
```

---

## Testing

This project uses **JUnit 5** for unit and integration testing.

### Writing Unit Tests

- Place test files in the `src/test/java/com/dailydash/` directory.
- Test models, utility classes, and service transactions thoroughly.
- Ensure any test setup initializes and cleans up databases cleanly to maintain idempotency.

---

## Security Vulnerabilities

Please do not report security vulnerabilities in public issues. Refer to our [Security Policy](SECURITY.md) for instructions on how to report security issues privately.
