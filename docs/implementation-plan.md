# Implementation plan — Code Integrity Guard 1.0.0

This document records the implementation plan, technical requirements, and validation status for Code Integrity Guard 1.0.0.

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
- [ ] Verify `io.github.dhionson2` namespace ownership and replace proposed SCM URL with the verified repository URL.
- [ ] Run dependency/license/SCA review and obtain release security review.
- [ ] Complete a release build with a real signing key in an authorized environment and Central validation.
- [ ] Publish only after explicit owner approval; no publishing was performed.

## Verifiable completion criteria

The local implementation compiles/tests, demo outcomes are covered, an independent project invokes the real goal, reports are emitted, and CI/docs exist. Release readiness remains blocked on identity/namespace and publication prerequisites; see `docs/release-readiness.md`.
