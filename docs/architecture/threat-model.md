# Threat model

## Assets and trust boundaries

Assets are contract definitions, a per-module evidence-run manifest, JUnit execution rows, policy-bound scenario observations, PIT XML, report output, and build decisions. `prepare-evidence` replaces old evidence and opens a UUID run; forks must prove they descend from the Maven process, consume the manifest from disk and append under OS file locks; `seal-evidence` records file digests only after successful tests; `audit` checks session, project origin, hashes, strict row structure, duplicate IDs, and scenario-to-invocation linkage. A passing JUnit invocation alone is not contract validation. Crashes before sealing, stale sessions, concurrent old forks, truncation, modification, and invalid evidence fail closed as INCONCLUSIVE. The session identity and SHA-256 hashes detect accidental reuse or change; they are not cryptographic producer authentication. A local actor able to edit the manifest, evidence and report can forge them. Manifest and fixture authors can still provide incorrect policies or unrealistic fixtures; reviewers must assess fidelity. Reports are intended for local developer/CI use.

## Threats and mitigations

- Forged or incomplete execution data can create false confidence: only the JUnit extension emits the supported invocation format; absent evidence is inconclusive; consumers must isolate artifacts and bind them to the current run.
- Skipped or partial suites could appear green: skipped invocations are not recorded as passes; configure Maven/Surefire to fail on errors and run audit after tests. Multi-module aggregation is not yet implemented.
- XXE in PIT XML: DTD, external general/parameter entities and external schema access are disabled; parsing has a 20 MiB input limit.
- HTML/script injection: all untrusted text is HTML escaped; JSON is string escaped.
- Path traversal: output names are fixed and normalized beneath the supplied directory.
- Secret/source disclosure: reports do not read environment variables or source files; consumer-provided evidence detail may still contain sensitive text, so keep it concise.
- Stale PIT output: `prepare-evidence` deletes the PIT XML before the current lifecycle run; `audit` requires the same sealed Maven run, report modification time no earlier than its start, and an exact configured expected class scope for a gated PIT result. This prevents accidental stale reuse without `clean`. Filesystem timestamps do not authenticate a hostile local writer capable of replacing the report after preparation.
- Maven lifecycle bypass or plugin misconfiguration: bind `audit` to verify after test/PIT; explicit gates are visible. A direct `audit` invocation alone does not execute tests.
- Dependency/supply-chain risk: pinned versions and GitHub Actions permissions are minimal; dependency/SCA scanning is a release task, not yet configured.
- Vulnerable demo misuse: synthetic, local-only test fixtures; never deploy. Tests use Spring Security principals and HTTP Basic authentication.

## Non-goals

No absolute security proof, universal IDOR detection, AI-authorship detection, source scanning, network service, SaaS, or mutation engine.

