# Progress log

## 2026-11-19 — implementation and local validation

- Implemented all requested code modules: core, testkit, PIT adapter, report, Maven plugin, Spring Boot example.
- Added top-level docs, secure-development instructions, GitHub Actions, Central release profile, Wrapper, and a separate `integration-consumer` used by CI.
- Captured the product, security, privacy, architecture, and release-readiness requirements in the public project documentation.
- Java 21.0.12 available. Maven 3.9.16 supplied by checked-in Wrapper; initial global Maven command was unavailable. Initialized a local Git repository; no remote was added and no commits/pushes were made.

### Commands and outcomes

- `mvnw.cmd -B -ntp clean verify` — passed after implementation; reactor modules and the vulnerable/fixed Spring tests green.
- `mvnw.cmd -B -ntp clean install` — passed after final feature changes; full reactor installs locally.
- `mvnw.cmd -B -ntp -f integration-consumer/pom.xml clean verify` — passed; independent test emits a real JUnit Unique ID, `audit` reports VERIFIED and writes JSON/HTML.
- `mvnw.cmd -B -ntp -f integration-consumer/pom.xml clean verify -DskipTests` — expected exit 1 with `failOnInconclusive=true`; report status INCONCLUSIVE and an explicit failing gate.
- `mvnw.cmd -B -ntp -Pcentral-release -DskipTests -Dgpg.skip=true package` — passed; generated source and Javadoc jars. No signature or upload was performed.
- `mvnw.cmd -B -ntp -f examples/spring-boot-authorization/pom.xml org.pitest:pitest-maven:1.30.2:mutationCoverage` — passed; PIT analyzed 9 mutations (7 killed, 1 survived, 1 no coverage), 78% displayed by PIT.
- `mvnw.cmd -B -ntp -f examples/spring-boot-authorization/pom.xml io.github.dhionson2:code-integrity-guard-maven-plugin:1.0.0-SNAPSHOT:audit` — passed; CIG read the real PIT XML, listed 9 mutants across `CustomerController`, and correctly classified PIT's `partial="true"` report as INCONCLUSIVE. Contract for the fixed ownership test was VERIFIED.

Unit/example tests from the latest initial reactor install were core 6, PIT 4, report 1, and example 5. The independent audit later found that the first consumer used `assertTrue(true)` and that an earlier progress entry overstated execution-only evidence as contract verification. The correction and replacement validation record follow.

### Decisions and known limitations

- Framework-free core; adapters depend inward. Evidence is appended only from actual JUnit callbacks after teardown.
- PIT uses structural format validation because PIT XML has no version attribute in the tested output; partial or error-marked reports are inconclusive.
- `io.github.dhionson2` and the configured GitHub SCM URL are proposals only and remain unverified. Maven Central namespace ownership, automated SCA/license audit, authenticated/fresh evidence provenance, multi-module aggregation, and an in-reactor Maven Invoker IT remain pending.
- Release profile uses manual publishing (`autoPublish=false`) and is not activated by default. No credentials, release signatures, uploads, remote repositories, or publications were used.
- Release remains explicitly pending namespace and security prerequisites.

## 2026-11-19 — independent scenario validation correction

- Split execution evidence (`TestEvidence`) from authorization scenario policy and HTTP observation (`SecurityScenario`, `ScenarioEvidence`). `VERIFIED` now requires both passing linked JUnit invocations and a policy-matching request observation tied to the same invocation.
- Expanded contract TSV rows with `scenarioIds`; added `.cig/scenarios.tsv` with explicit scenario type, actor, resource owner, resource, exact method/path, identity header, and expected HTTP status. `AuthorizationChecks.verifyScenario` executes MockMvc requests and records observed status; no LLM or trust classifier is used.
- Replaced the trivial consumer test with a real cross-owner request. Updated the Spring Boot example, JSON/HTML report, README, security-contract docs, software design, threat model, release readiness, and this log. Reports include declared policy values alongside actual observations.
- Negative core regressions: execution with no scenario remains INCONCLUSIVE; missing observations remain INCONCLUSIVE; observed 200 against expected 404 is FAILED; policy/test mismatch cannot verify. A matching cross-owner scenario yields VERIFIED.
- `mvnw.cmd -B -ntp clean install` — passed after final code changes: 21 module/example tests passed (core 10, PIT 4, report 2, Spring example 5).
- `mvnw.cmd -B -ntp -f integration-consumer/pom.xml clean verify` — passed: 1 real MockMvc integration test; CIG reported `CONSUMER-OWNER-CHECK: VERIFIED` with actual and expected HTTP 404 in `integration-consumer/target/code-integrity-guard/report.json`. PIT remains INCONCLUSIVE because this consumer has no PIT report.
- One initial consumer run exposed literal `\\t` serialization in scenario evidence; corrected to actual TSV tab characters and reran the complete consumer build successfully.

This status verifies only that the consumer-owned fixture returned the declared response for the declared scenario. It does not establish production authorization correctness or evidence provenance.

## 2026-11-19 — B2 execution evidence integrity and freshness

- Added a per-module `prepare-evidence` → test forks → `seal-evidence` → `audit` protocol. Preparation generates a random UUID, Maven-session key and project origin and deletes prior TSV files without depending on `mvn clean`.
- Updated the extension to read the run manifest from shared disk (not shared JVM memory) and append strict v2 Base64 TSV rows under OS file locks. Rows include run UUID, Maven project coordinates, process/engine-unique invocation ID, test/scenario ID and outcome. Surefire and Failsafe both receive the same manifest/evidence paths.
- Sealing occurs at `verify` after test goals return successfully, coordinates with append locks and stores SHA-256 digests. Audit requires same Maven session/project, SEALED state, unchanged digests, complete row endings/schema, required IDs, unique test/scenario invocation keys, and same-run/same-invocation scenario links. Missing, stale, malformed, duplicate, conflicting, skipped, partial or unsealed evidence becomes INCONCLUSIVE; gate failure is safe.
- Added regression coverage for current versus previous run, missing run ID, malformed/truncated rows, duplicate/conflicting rows, skipped/unexecuted tests, another invocation, and partial/unsealed execution. The independent consumer includes a disabled test to exercise SKIPPED output.
- `mvnw.cmd -B -ntp clean install` — passed after B2 implementation; reactor totals: core 17, PIT reader 4, report 2, evidence parser 5, Spring example 5; 33 passed.
- `mvnw.cmd -B -ntp -f integration-consumer/pom.xml verify` — passed repeatedly without `clean`; each `prepare-evidence` rotated the run UUID and each completed run verified. Latest report contains two linked passing test IDs and two scenario observations (HTTP 404). Surefire ran one active test plus one intentionally skipped regression test; Failsafe ran one `OwnerAuthorizationIT`.
- `mvnw.cmd -B -ntp -f integration-consumer/pom.xml verify -DskipTests` — expected exit 1; preparation removed prior rows, seal wrote an empty run, and the linked contract was INCONCLUSIVE.
- `mvnw.cmd -B -ntp -f integration-consumer/pom.xml test io.github.dhionson2:code-integrity-guard-maven-plugin:1.0.0-SNAPSHOT:audit` — expected exit 1; test evidence existed but the run remained OPEN (no verify/seal), so audit was INCONCLUSIVE.
- Direct `...:audit` in a separate Maven invocation after a successful verify — expected exit 1; report said the previous Maven invocation could not be proven and status was INCONCLUSIVE.
- `mvnw.cmd -B -ntp -f integration-consumer/pom.xml verify -DskipTests` — expected exit 1 after B2 wiring; prepare removed old evidence and the sealed empty run yielded INCONCLUSIVE.

Trust limitation: SHA-256 and Maven session identity detect stale/corrupted outputs but do not authenticate a hostile local process that can rewrite the manifest, TSVs and digests. The extension checks the Maven PID in the test JVM's ancestry; if the process tree is unavailable or exceeds the traversal limit, it refuses to write evidence. Each module is isolated; V1 does not aggregate evidence across modules. For Failsafe, its plugin must precede CIG in POM order so CIG sealing follows Failsafe verify. Parallel multi-fork stress testing was not performed, although append/seal operations use OS file locks.

## 2026-11-19 — B3 audit with no configured contracts

- `audit` now defaults `requireContracts=true`. Missing or row-empty manifests fail the Maven goal; invalid manifests always fail. Declared contracts whose evidence is unverified or inconclusive also fail under the default required-contract policy. Existing `FAILED` contract behavior and B1/B2 evidence checks remain active.
- An intentional `requireContracts=false` accepts only missing/empty manifests, emits a Maven warning, and reports `contractsEvaluation: NOT_RUN` plus `contractsManifestStatus`; reports contain no contract result or approval status. `buildStatus: AUDIT_RECORDED` denotes report production only.
- JSON schema version is now 1.1; HTML and JSON distinguish execution completion from contract evaluation. Added manifest-state and report regressions.
- `mvnw.cmd -B -ntp clean install` — passed, 35 tests across reactor modules/example (core 17, PIT 4, reports 3, Maven plugin parser/manifest 6, Spring example 5).
- `mvnw.cmd -B -ntp -f integration-consumer/pom.xml verify` — passed with B2 sealed evidence; Surefire and Failsafe contracts verified with two scenario observations. JSON parsed and HTML checked for `EVALUATED/PRESENT`.
- Maven goal negative checks: missing/default exit 1 (`FAILED/NOT_RUN/MISSING`); missing/`-Dcig.requireContracts=false` exit 0 (`AUDIT_RECORDED/NOT_RUN/MISSING`); empty/default exit 1 (`FAILED/NOT_RUN/EMPTY`); empty/false exit 0 (`AUDIT_RECORDED/NOT_RUN/EMPTY`); malformed manifest exit 1 (`FAILED/NOT_RUN/INVALID`).
- `mvnw.cmd -B -ntp -f integration-consumer/pom.xml verify -DskipTests` — expected exit 1; contract was `INCONCLUSIVE` due absent test execution evidence. Restored the consumer manifest and reran successful `verify` afterward.

B3 proves only that the plugin cannot silently report contract evaluation/approval when no contracts were evaluated. `requireContracts=false` intentionally allows build continuation without contract validation and must not be treated as a security gate.

## 2026-11-19 — B4 report after real test failures

- Added the opt-in `cig-vulnerable-diagnostic` profile to the Spring example. Only this profile uses provider-level `testFailureIgnore=true`; the regular Surefire/Failsafe configuration continues to fail normally.
- Replaced the previous `assertThrows(AssertionError)` demonstration with real `AuthorizationChecks.verifyScenario` assertions. Its tagged Surefire and Failsafe tests exercise the vulnerable profile, record observed HTTP 200 against declared 404, and fail.
- `mvnw.cmd -B -ntp clean install` — passed with diagnostic-only methods excluded by default; the normal fixed scenario still evaluated.
- `mvnw.cmd -B -ntp -f examples/spring-boot-authorization/pom.xml verify -Pcig-vulnerable-diagnostic` — expected exit 1. Surefire and Failsafe each reported one real assertion failure; because the explicit diagnostic profile allowed lifecycle continuation, CIG sealed current evidence, wrote JSON/HTML, classified `CUSTOMER-OWNER-ISOLATION` as FAILED with both test outcomes FAILED and observed status 200, then failed the Maven goal. Latest JSON is `examples/spring-boot-authorization/target/code-integrity-guard/report.json`.
- Diagnostic-only failure tolerance is never enabled globally. Reports from that profile must not be used as a green build or contract verification.

## 2026-11-19 — B4–B8 release stabilization follow-up

### B4 — reports after real Surefire/Failsafe failures

- Re-ran `mvnw.cmd -B -ntp -f examples/spring-boot-authorization/pom.xml verify -Pcig-vulnerable-diagnostic` after B6. Maven exit code: 1 (expected). Surefire and Failsafe each executed the vulnerable cross-owner request as Alice and failed the explicit `expected HTTP 404 but got 200` assertion. `seal-evidence` and `audit` still ran; contract status was FAILED, both linked execution rows were FAILED, and both scenario observations were HTTP 200. JSON/HTML are preserved under `docs/release-evidence/b4-vulnerable-contract-failed.*`. It was not reported VERIFIED.
- The profile is opt-in and alone permits provider failures to continue to sealing/audit. The command still ends nonzero due the CIG quality gate; normal builds retain default Surefire/Failsafe failure behavior.

### B5 — JSON serializer and report validation

- The report writer uses Jackson for JSON and separately context-escapes HTML. JSON Schema 1.1 documents the public output.
- `mvnw.cmd -B -ntp clean install` — BUILD SUCCESS. Report module: 5 tests, 0 failures/errors/skips. Tests parse output using Gson and cover U+0000–U+001F, quotation marks, backslashes, Unicode/supplementary characters, null/empty values, special scenario/contract strings, and HTML injection strings.

### B6 — Spring Security fixture

- The demo uses real Spring Security principals/roles with `spring-security-test`, MockMvc, security filter chain, and method-level authorization; fake `X-Demo-User`/`permitAll` were removed. Fixed examples cover owner 200, cross-owner 404, anonymous 401, and insufficient role 403. `CustomerAuthorizationTest` / `CustomerAuthorizationIT` demonstrate the vulnerable cross-owner 200 and are excluded from normal builds; the diagnostic profile runs and fails them visibly.
- Full reactor: example reported 5 tests, all passed, 0 skipped. The consumer independent build below exercised its own MockMvc requests.

### B7 — PIT report completeness and CI

- `mvnw.cmd -B -ntp -f examples/spring-boot-authorization/pom.xml verify -Ppit-validation` — Maven exit code 1 (expected fail-closed). PIT 1.30.2 actually generated 11 mutations: 7 KILLED, 1 SURVIVED, 3 NO_COVERAGE; 0 timeout/error mutants; measured ratio 7/11 (63.6%) in the `CustomerController` scope. The XML root has `partial="true"`.
- CIG reported PIT INCONCLUSIVE and failed because `requirePit=true`; the contract scenario itself returned the declared status in the fixed suite, but that does not convert PIT into a complete report or make the overall gate pass. Raw XML, CIG JSON and HTML are in `docs/release-evidence/`.
- Added a dedicated GitHub Actions job that runs this profile and succeeds only if the Maven gate fails and the generated report explicitly says PIT INCONCLUSIVE due to partial data. This validates fail-closed CI behavior; it does not claim complete mutation validation.
- Investigated PIT 1.30.2 XML behavior. The available mojo/configuration does not expose a setting to emit `partial="false"` for this standalone report. No parser weakening was made. B7 remains PARTIAL and blocks release.

### B8 — Central package, SBOM and SCA

- Replaced the old abbreviated `LICENSE` with the full Apache License 2.0 text from the official Apache license URL; added `NOTICE`. Root and child coordinates remain `io.github.dhionson2:*:1.0.0-SNAPSHOT` in development; namespace and SCM ownership are unverified.
- `mvnw.cmd -B -ntp -Pcentral-release -DskipTests -Dgpg.skip=true package` — BUILD SUCCESS. Sources JAR and Javadoc JAR were produced for core, testkit, PIT adapter, report, and Maven plugin. The example also produced these attachments. Signing and publishing were not invoked. A first attempt with unquoted PowerShell `-Dgpg.skip=true` was parsed as an invalid lifecycle phase; the quoted repeat succeeded.
- `mvnw.cmd -B -ntp org.cyclonedx:cyclonedx-maven-plugin:2.9.3:makeAggregateBom -DincludeTestScope=true -DoutputFormat=json` — BUILD SUCCESS; real CycloneDX 1.6 aggregate lists 109 components and license metadata for all 109. Stored at `docs/release-evidence/cyclonedx-bom.json`. License identifiers include Apache-2.0, BSD-3-Clause, EPL-1.0/EPL-2.0, GPL-2.0-with-classpath-exception, and MIT; automated metadata is not legal review.
- Attempted `org.owasp:dependency-check-maven:13.0.0:aggregate` with JSON/HTML and no credentials. The CLI exited 0 only because `-DfailOnError=false`; plugin logged a fatal NVD update/API-key error and did not analyze dependencies or produce a report. This is recorded as blocked, not a clean scan. Details/commands are in `docs/supply-chain-review.md`.

### Integrated regression and consumer results

- `mvnw.cmd -B -ntp clean install` — BUILD SUCCESS, all 37 reactor tests passed: core 17, PIT adapter 4, report 5, Maven plugin 6, Spring example 5; 0 failed, 0 errors, 0 skipped. Testkit has no own test classes.
- `mvnw.cmd -B -ntp -f integration-consumer/pom.xml clean verify` — BUILD SUCCESS using locally installed reactor artifacts, no compile-time dependency on internal classes. Surefire: 2 tests, 0 failed/errors, 1 intentional skip. Failsafe: 1 test, 0 failed/errors/skips. The sealed evidence correlated both test forks and contract `CONSUMER-OWNER-CHECK` reported VERIFIED for the explicitly declared fixture outcome. JSON/HTML are captured in `docs/release-evidence/independent-consumer-contract-verified.*`.
- `mvnw.cmd -B -ntp -Pcentral-release -DskipTests '-Dgpg.skip=true' package` — BUILD SUCCESS; artifact attachments were checked by listing actual JAR files.

B7 and B8 remain release blockers. The external package must preserve these actual reports and must not treat the run as general security assurance or release approval.

### External review package and sanitization

- Created a source-and-evidence review archive outside the project after enumerating candidate files and scanning for credential/private-key patterns, `.env` names, private certificates, and user-home paths. The Maven wrapper's optional `--http-password="$MVNW_PASSWORD"` environment-variable reference triggered a pattern-only false positive; the script contains no credential value. The small Maven Wrapper JAR is retained because it is required to bootstrap the supplied `mvnw` scripts.
- ZIP inspection found 88 entries (215,552 bytes, 606,562 uncompressed): all Java source/test files, POMs, manifests, docs, workflow, independent consumer and captured JSON/HTML/XML/SBOM evidence. Post-creation checks found no `.git`, `target`, `.tools`, `.env`, key/certificate files. Build output JARs other than the wrapper bootstrap are excluded.
- Included a `REVIEW_PACKAGE_CONTENTS.txt` index. SHA-256 is recorded in the final response for transfer integrity.

## 2026-11-19 — B7 final correction: PIT 1.30.2 `partial` semantics

This entry supersedes the earlier B7 interpretation in this progress log and the historical `pit-audit-inconclusive-real.json` sample. That earlier parser treated `partial="true"` as execution interruption; the classification was incorrect.

### Source verification

Checked the official PIT 1.30.2 tag actually used by the example:

- `XMLReportFactory.getListener()` passes `args.data().shouldReportCoverage()` to `XMLReportListener`.
- `ReportOptions.reportCoverage` is initialized to `true`, and `shouldReportCoverage()` returns that property.
- `XMLReportListener.runStart()` writes the passed boolean directly as the XML root `partial` attribute.

References: [XMLReportFactory.java](https://github.com/hcoles/pitest/blob/1.30.2/pitest-entry/src/main/java/org/pitest/mutationtest/report/xml/XMLReportFactory.java), [XMLReportListener.java](https://github.com/hcoles/pitest/blob/1.30.2/pitest-entry/src/main/java/org/pitest/mutationtest/report/xml/XMLReportListener.java), [ReportOptions.java](https://github.com/hcoles/pitest/blob/1.30.2/pitest-entry/src/main/java/org/pitest/mutationtest/config/ReportOptions.java).

### Implementation and regressions

- `PitXmlReader` now requires supported root coverage metadata and complete supported mutation fields, counts only KILLED as detected, excludes NON_VIABLE from denominator, requires at least one scoreable mutation, and returns INCONCLUSIVE for missing, malformed/truncated, incompatible, stale, scope-mismatched, empty or unresolved error-state reports. `partialCoverage` is retained as a Boolean metadata field; true alone does not affect execution status.
- The Maven audit requires a valid sealed run in the current session. PIT report must be at least as new as that session. `prepare-evidence` deletes a prior PIT XML before each run, including without `mvn clean`. An active PIT gate also requires explicit `pitExpectedClasses`; observed XML classes must equal that exact scope.
- Added an unmodified real 1.30.2 XML fixture with `partial=true`, plus tests for real counts/7-of-11 score, current/stale timestamps, valid/malformed/truncated/unknown/error/empty reports, missing run start, and scope mismatch. Report schema is now 1.2 and JSON/HTML expose `partialCoverage` and `expectedScope`.
- GitHub Actions now runs the real PIT profile at a 60% threshold and validates AVAILABLE / partialCoverage true / expected scope / 7 of 11. It then re-runs at 70% and expects a non-zero score-gate result while PIT remains AVAILABLE. It no longer expects `partial=true` itself to fail.

### Commands and outcomes

- `mvnw.cmd -B -ntp -pl code-integrity-guard-pit-adapter,code-integrity-guard-report,code-integrity-guard-maven-plugin -am test` — BUILD SUCCESS: PIT adapter 9 tests, report 6, plugin 6, plus core 17; 38 passed, 0 failed/errors/skips.
- `mvnw.cmd -B -ntp clean install` — BUILD SUCCESS after B7 correction: core 17, PIT adapter 9, report 6, Maven plugin 6, Spring example 5; **43 passed, 0 failed/errors/skips**. Testkit has no test classes.
- `mvnw.cmd -B -ntp -f examples/spring-boot-authorization/pom.xml verify -Ppit-validation '-Dcig.minimumMutationPercent=60'` — exit 0. Fresh real PIT 1.30.2 report: `partial="true"`, 11 mutations (7 KILLED, 1 SURVIVED, 3 NO_COVERAGE), no timeout/error mutants; CIG PIT AVAILABLE; exact scope `io.github.dhionson2.example.CustomerController`; score 7/11 = 63.636%. JSON/HTML/XML captured in `docs/release-evidence/`.
- Repeated with `'-Dcig.minimumMutationPercent=70'` without `clean` — exit 1 as intended. `prepare-evidence` rotated the run and removed the prior PIT report, then PIT generated a fresh report. CIG retained PIT AVAILABLE and failed only the configured mutation threshold. Real JSON/HTML saved as `pit-audit-score-gate-failed-real.*`.
- `mvnw.cmd -B -ntp -f integration-consumer/pom.xml clean verify` — BUILD SUCCESS after the plugin/report changes. Surefire 2 tests (1 passed, 1 intentionally skipped); Failsafe 1 passed; 0 failures/errors. Consumer contract was VERIFIED for its own declared fixture scenario.

### Remaining B7 limitation

Current-run deletion, Maven session binding, file modification time and exact expected scope prevent accidental reuse/mis-scoping; these local files/timestamps are not cryptographic attestation against a hostile process able to replace outputs. GitHub Actions YAML was updated to the same commands, but a remote Actions run was not triggered here. B8 release blockers remain unchanged.

### Final B7 regression rerun after denominator-exclusion test

- Added a regression proving `NON_VIABLE` entries increment `excluded` but are not part of the score denominator. The PIT adapter now has 10 passing tests.
- Re-ran `mvnw.cmd -B -ntp clean install` after this last test addition: BUILD SUCCESS, core 17 + PIT adapter 10 + report 6 + Maven plugin 6 + Spring example 5 = **44 passed, 0 failed/errors/skips**.
- Re-ran `mvnw.cmd -B -ntp -f integration-consumer/pom.xml clean verify` against the freshly installed reactor. BUILD SUCCESS: Surefire 2 tests (one pass, one intentional skip), Failsafe one pass, no failures/errors; contract remained VERIFIED only for its declared MockMvc fixture.
- Current saved artifacts are `pit-audit-available-real.json/.html` (60% threshold pass), `pit-audit-score-gate-failed-real.json/.html` (70% threshold fail with PIT AVAILABLE), and `pit-mutations-partial-true-real.xml` (real PIT 1.30.2 XML, no edits). The older INCONCLUSIVE JSON is retained but explicitly labeled as output from the incorrect pre-fix parser.

