# Draft issue proposals

These English-language drafts are prepared for review and are not published GitHub issues. The proposed milestone names are planning buckets only; they have no dates or delivery promises. P-01, P-02, and P-03 are the three issues suitable for first-time contributors because they are documentation-only and have bounded acceptance criteria.

## P-01 — Add a concise consumer quick start

**Problem:** The repository documents consumer setup in detail, but a new user has no short path from Maven Central coordinates to a first local audit.

**Technical context:** Version 1.0.0 is published. `docs/installation.md` and `integration-consumer/pom.xml` describe the supported Maven/JUnit flow and consumer-owned test dependencies.

**Expected result:** A short guide lets a Java 21 consumer identify required dependencies, configure the plugin lifecycle, run the documented consumer command, and locate the JSON/HTML report without implying security approval.

**Scope / out of scope:** Documentation only; use the independent consumer as the source of truth. No Java changes, new dependency, or new supported framework claim.

**Acceptance criteria:** The guide names Java/Maven requirements; uses published 1.0.0 coordinates; explains provided dependencies; links a complete example; gives Windows and Unix-like wrapper commands; explains that missing, partial, or inconclusive evidence is not a pass; has no broken relative links.

**Modules impacted:** `docs/`, with `integration-consumer/` as a reference fixture.

**Tests expected:** No Java tests. Review every command and dependency against the checked-in consumer POM and current wrapper configuration.

**Difficulty:** Easy.

**Recommended labels:** `documentation`, `good first issue`, `help wanted`.

**Proposed milestone:** Community onboarding (proposed).

## P-02 — Add a module map and dependency-direction reference

**Problem:** The architecture guide explains boundaries in prose, while contributors must inspect multiple POMs to understand the artifact-to-module map and allowed dependency direction.

**Technical context:** The project has separate core, testkit, PIT adapter, report, and Maven plugin modules, plus an educational Spring Boot example. Core must remain independent of Maven, Spring, PIT, and reporting frameworks.

**Expected result:** A compact table or diagram maps artifact names to responsibilities and shows that integrations depend toward core.

**Scope / out of scope:** Documentation-only map verified against module POMs and the accepted architecture decision. Do not change module boundaries, POMs, or Java code.

**Acceptance criteria:** Every current module is represented; public artifacts are distinguished from the local educational example; dependency arrows do not imply that core depends on adapters; the reference links to the architecture guide and ADR; no planned capability is described as implemented.

**Modules impacted:** `docs/architecture/` and module POMs as read-only references.

**Tests expected:** No Java tests. Review the map against all current module POMs.

**Difficulty:** Easy.

**Recommended labels:** `documentation`, `good first issue`, `help wanted`.

**Proposed milestone:** Community onboarding (proposed).

## P-03 — Add safe examples for report status interpretation

**Problem:** Consumers can read the schema and status table, but a small representative report would make it easier to distinguish a recorded audit from verified contract evidence and from missing evidence.

**Technical context:** JSON schema version 1.2 includes build status, contract evaluation state, manifest status, contract results, PIT status, gates, and limitations. Report content must remain synthetic and must not expose paths, secrets, or source code.

**Expected result:** Documentation includes a compact, synthetic example and explains representative `VERIFIED`, `FAILED`, `UNVERIFIED`, `INCONCLUSIVE`, and `NOT_RUN` outcomes.

**Scope / out of scope:** Documentation and a sanitized example only. No report-format change and no assertion that CIG proves an application secure.

**Acceptance criteria:** Example fields conform to `docs/json-report-schema.json`; every status is explained without equating missing evidence with success; the example uses fictional IDs and contains no local paths or real data; links to the full schema documentation.

**Modules impacted:** `docs/` and `code-integrity-guard-report/` only as the source of the current schema.

**Tests expected:** No Java tests. Validate the example against the checked-in JSON schema and compare its terminology with the current report documentation.

**Difficulty:** Easy.

**Recommended labels:** `documentation`, `good first issue`, `help wanted`.

**Proposed milestone:** Community onboarding (proposed).

## P-04 — Run the Maven plugin in an isolated consumer integration test

**Problem:** CI verifies the independent consumer as a separate command after installing the reactor. The Maven plugin's real consumer lifecycle is not yet exercised by a Maven Invoker test inside the normal reactor lifecycle.

**Technical context:** `integration-consumer/` runs real JUnit tests and the published plugin goals. The implementation plan records automated Maven Invoker integration as a follow-up gap.

**Expected result:** A repeatable integration test starts a separate Maven process against a minimal consumer using isolated build and local-repository directories, then checks report and exit behavior.

**Scope / out of scope:** Cover successful evidence and at least one configured failure/inconclusive case. No Central publication, credentials, network-dependent version resolution, or deployment behavior.

**Acceptance criteria:** The test runs from the documented reactor verification path; does not read reactor `target/` output by accident; asserts report presence and expected status/exit code; cleans only its configured temporary directory; is deterministic on CI.

**Modules impacted:** `code-integrity-guard-maven-plugin`, `integration-consumer/`, root POM, and CI workflow.

**Tests expected:** Maven Invoker integration fixtures plus existing unit tests; run the clean reactor and independent consumer checks.

**Difficulty:** Hard.

**Recommended labels:** `enhancement`, `help wanted`.

**Proposed milestone:** Consumer integration (proposed).

## P-05 — Add Windows CI coverage for the wrapper and consumer workflow

**Problem:** Current GitHub Actions jobs run on Ubuntu only, so Windows wrapper behavior, path handling, and file cleanup are not checked on every change.

**Technical context:** The repository documents `mvnw.cmd`; evidence is shared across forked test processes through files and locks. The independent consumer exercises the actual plugin lifecycle.

**Expected result:** A Windows CI job verifies the reactor and independent consumer with the checked-in Maven Wrapper.

**Scope / out of scope:** Add a `windows-latest` workflow path using Java 21 and the documented wrapper commands. Do not change Java implementation unless a reproducible Windows defect is found and separately scoped.

**Acceptance criteria:** The Windows job runs on pull requests and main pushes; caches Maven safely; executes reactor and consumer verification; reports a clear failing command; does not require secrets or publishing credentials.

**Modules impacted:** `.github/workflows/ci.yml`, build wrapper, and `integration-consumer/`.

**Tests expected:** CI run on Windows with the reactor and consumer workflow; preserve current Linux CI coverage.

**Difficulty:** Medium.

**Recommended labels:** `enhancement`, `help wanted`.

**Proposed milestone:** CI portability (proposed).

## P-06 — Add stable diagnostic reason codes for contract outcomes

**Problem:** Core already supplies human-readable explanations, but consumers that automate triage should not need to parse prose to distinguish skipped tests, missing evidence, invalid links, or incomplete runs.

**Technical context:** `ContractResult` exposes a status and explanation; the report schema is additive and versioned. Core must remain free of Maven, Spring, PIT, and report-framework dependencies.

**Expected result:** Contract results expose a finite, documented reason code alongside the existing explanation, and JSON/HTML present both safely.

**Scope / out of scope:** Define codes for current evaluation branches and preserve current status/gate semantics. No inference from declarations alone; no raw local paths, environment values, source, or user-controlled unescaped text in codes.

**Acceptance criteria:** Every non-verified evaluation branch maps to a stable code; existing human text remains available; JSON schema and docs are updated; old fields remain compatible; unknown future codes can be handled by consumers; missing or malformed evidence never maps to success.

**Modules impacted:** `code-integrity-guard-core`, `code-integrity-guard-report`, Maven plugin, and report schema documentation.

**Tests expected:** Core tests for each reason mapping; JSON/HTML serialization tests including hostile text; schema validation and consumer compatibility checks.

**Difficulty:** Medium.

**Recommended labels:** `enhancement`, `needs design`.

**Proposed milestone:** Contract diagnostics (proposed).

## P-07 — Support declared anonymous-access and insufficient-role scenarios

**Problem:** V1 supports cross-owner authorization scenarios, while consumers also need a declared and observed way to check unauthenticated access and authenticated users with insufficient authority.

**Technical context:** Scenario policy and evidence are linked to real JUnit invocations; the example currently tests anonymous and insufficient-role behavior separately, but the documented CIG scenario type is `CROSS_OWNER` only.

**Expected result:** Add explicit scenario types for anonymous access and insufficient role, with actors, expected statuses, and observed request evidence represented consistently.

**Scope / out of scope:** Keep scenario execution opt-in and assertion-backed. Do not claim universal authorization or IDOR detection; do not change the existing cross-owner semantics.

**Acceptance criteria:** Valid manifests parse for both types; malformed or incomplete policies are inconclusive/fail closed; matching and mismatching real HTTP outcomes are covered; invocation linkage is enforced; docs and reports identify the scenario type; existing V1 manifests retain behavior.

**Modules impacted:** `code-integrity-guard-testkit`, core evaluation, Maven plugin manifest reader, report, schemas, and authorization example tests.

**Tests expected:** Unit tests for manifest validation and evaluation plus Spring MVC integration tests for allowed and denied outcomes and missing evidence.

**Difficulty:** Hard.

**Recommended labels:** `enhancement`, `needs design`.

**Proposed milestone:** Authorization scenarios (proposed).

## P-08 — Add a concise report summary to Maven console output

**Problem:** CI logs show individual contract lines and generic gate failure information, while reviewers often need a compact summary of contract and PIT states without opening the full files.

**Technical context:** JSON and escaped HTML reports are already written under the configured report directory. Console output must avoid source code, environment values, secrets, and uncontrolled manifest content.

**Expected result:** The Maven goal prints a deterministic, concise summary with evaluated/not-run state, status counts, PIT availability, and the report location.

**Scope / out of scope:** Add console presentation only; retain JSON/HTML as authoritative detailed reports and preserve gate/exit semantics. Do not print raw evidence rows, descriptions, stack traces, or environment variables.

**Acceptance criteria:** Summary distinguishes `NOT_RUN` and `INCONCLUSIVE` from passing outcomes; includes paths constrained to the configured output directory; escapes or omits untrusted values; output is stable enough for CI review; failure exit codes remain unchanged.

**Modules impacted:** `code-integrity-guard-maven-plugin` and report tests/documentation as needed.

**Tests expected:** Maven plugin log-capture tests for empty, successful, failed, and inconclusive runs; path containment and hostile input checks.

**Difficulty:** Medium.

**Recommended labels:** `enhancement`, `help wanted`.

**Proposed milestone:** Reporting improvements (proposed).

## P-09 — Design deterministic multi-module evidence aggregation

**Problem:** Evidence is currently isolated per Maven module, so a contract that spans modules cannot be evaluated once at reactor scope.

**Technical context:** Core must remain framework-independent; the Maven plugin coordinates module lifecycle and report generation. Missing, stale, malformed, skipped, or partial evidence must remain unverified or inconclusive.

**Expected result:** A documented design and implementation aggregate module results deterministically at the Maven integration boundary without weakening per-module evidence checks.

**Scope / out of scope:** Define module identity, duplicate/conflict handling, partial reactor behavior, and backward-compatible report shape. Do not add Maven dependencies to core, claim cryptographic provenance, or change existing single-module semantics.

**Acceptance criteria:** An ADR specifies lifecycle and trust boundaries; aggregation order does not affect output; missing or invalid module evidence cannot produce `VERIFIED`; duplicate module/run identities are rejected or inconclusive; current single-module builds remain compatible; report schema and docs describe partial reactor results.

**Modules impacted:** Maven plugin, report, root reactor configuration, and possibly framework-free core value types after design review.

**Tests expected:** Multi-module fixtures for complete, partial, stale, malformed, and conflicting evidence; deterministic output checks; full reactor and consumer verification.

**Difficulty:** Hard.

**Recommended labels:** `enhancement`, `needs design`.

**Proposed milestone:** Architecture evolution (proposed).
