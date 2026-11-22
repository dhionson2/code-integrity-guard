# Contributing

Contributions are welcome. Please open or find an issue before starting a substantial change so its scope and acceptance criteria can be discussed. For small documentation fixes, a pull request may include the proposed change directly.

## Development

- Use Java 21 and the checked-in Maven Wrapper. On Windows, run the `.cmd` wrapper from PowerShell or Command Prompt.
- Keep `code-integrity-guard-core` independent of Maven, Spring, PIT, and reporting frameworks. Integrations depend inward toward core.
- Make focused changes and add tests for changed behavior. Document security-relevant behavior and any compatibility impact.
- Keep the intentionally vulnerable example synthetic, educational, and test-only.
- Do not add credentials, secrets, customer data, generated build output, or deployment/publication steps.

## Local verification

The current CI workflow runs the reactor and its separate consumer fixture. Equivalent commands are:

    ./mvnw --batch-mode --no-transfer-progress clean install
    ./mvnw --batch-mode --no-transfer-progress -f integration-consumer/pom.xml clean verify

On Windows, use:

    mvnw.cmd --batch-mode --no-transfer-progress clean install
    mvnw.cmd --batch-mode --no-transfer-progress -f integration-consumer/pom.xml clean verify

If a check cannot run locally, describe why and include the relevant CI result when available. A passing test suite or CIG audit is not a claim that an application is secure.

## Pull requests and review

Use the pull request template to summarize the change, affected modules, and verification. Every contribution requires maintainer review and passing CI before merge. Opening a pull request does not mean it is accepted, and external contributions are not merged automatically. Maintainers may request changes or decline work that does not fit the project scope.

Do not report suspected vulnerabilities in a public issue or pull request. Follow [SECURITY.md](SECURITY.md) for private reporting instructions.

By contributing, you agree that your contribution is licensed under Apache-2.0.
