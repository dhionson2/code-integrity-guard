# Implementation plan — Code Integrity Guard

This document records the original implementation plan and validation status. Version 1.0.0 was published on 2026-11-21; dated release audit history remains in `docs/release-readiness.md` and `docs/supply-chain-review.md`. Follow-up work is collected as proposals in `docs/community/roadmap.md`.

## Phase 1 — Architecture and foundation

- [x] Inspect workspace and Java/Maven/Git availability; bootstrap Maven 3.9.16 Wrapper because Maven was initially absent.
- [x] Capture functional, security, privacy, and release-readiness requirements.
- [x] Record module boundaries, trust model, architectural decisions, and technical acceptance criteria.
- [x] Create Java 21 multi-module parent and module skeleton.
- [x] Add Apache-2.0 license, repository hygiene, CI, and Wrapper.

## Phase 2 — Features

- [x] Core contracts and evidence evaluation with VERIFIED/FAILED/UNVERIFIED/INCONCLUSIVE.
- [x] JUnit 5 execution evidence extension with invocation IDs.
- [x] Spring MVC authorization status matchers.
- [x] PIT XML adapter with hardened parsing, classes/scope, denominator, and partial/error handling.
- [x] Deterministic JSON/escaped HTML reports and fixed output filenames.
- [x] Maven `audit` goal with explicit contract/PIT gates.
- [x] Vulnerable/fixed Spring Boot customer authorization demonstration.

## Phase 3 — Integration and validation

- [x] Unit and example integration tests.
- [x] Independent consumer invoking a real JUnit run and the Maven goal.
- [x] Inconclusive consumer run verified to fail when configured to gate.
- [x] Explicit PIT mutation execution and follow-up CIG report generation.
- [x] Release profile package smoke test with source/Javadoc packaging.
- [ ] Automated Maven Invoker IT inside normal reactor lifecycle (currently the independent consumer is run explicitly from CI).

## Phase 4 — Release preparation

- [x] English onboarding, contracts, PIT, CI, architecture, threat model, changelog, and draft notes.
- [x] Central publishing profile configured with sources/Javadoc/signing and manual publish behavior.
- [x] Verify `io.github.dhionson2` namespace ownership and use the verified public repository URL in SCM metadata.
- [x] Complete the dependency/license/SCA and release security reviews recorded in the dated audit documents.
- [x] Complete the authorized release build and Maven Central validation for version 1.0.0.
- [x] Publish version 1.0.0 after maintainer approval; see the [published GitHub release](https://github.com/dhionson2/code-integrity-guard/releases/tag/v1.0.0).

## Verifiable completion criteria

The initial implementation and release preparation are complete. The known Maven Invoker automation gap remains open for follow-up; the public roadmap records candidate work without release-date commitments.
