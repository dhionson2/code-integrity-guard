# Project rules

- Keep `code-integrity-guard-core` independent of Maven, Spring, PIT and reporting frameworks.
- Integrations may depend on core; core must never depend on adapters.
- Evidence is valid only when produced by real test execution; declarations and names alone cannot verify a contract.
- Treat missing, malformed, skipped, partial and stale evidence as unverified or inconclusive, never as success.
- Escape untrusted report content, disable XML external entities, and constrain output paths to the configured report directory.
- Reports must not include source code, environment variables or secrets by default.
- Never claim that passing tests prove an application secure or detect AI authorship.
- Keep the intentionally vulnerable example isolated to local tests and clearly label it educational.
- Do not publish, push, use credentials, or modify files outside this repository without explicit authorization.

