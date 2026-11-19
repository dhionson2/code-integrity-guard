# CI/CD

Pull requests and pushes to `main` run the Maven Wrapper with Temurin 21 and read-only repository permission. The regular job runs `clean install` and the independent consumer. A separate `pit-quality` job installs the reactor, runs the real Spring PIT profile at a 60% minimum and checks the generated JSON for PIT AVAILABLE, `partialCoverage=true`, the expected `CustomerController` scope, and 7/11. It then repeats without `clean` at 70% and requires the Maven quality gate to fail while the parsed PIT report stays AVAILABLE. This covers both passing and failing mutation-score thresholds without treating `partial=true` as an execution failure.

CIG does not start PIT implicitly. `prepare-evidence` removes the prior PIT XML at `initialize`, even when no `clean` was run. `audit` checks that the current sealed Maven evidence session is valid, the PIT XML modification time is not earlier than that session, and observed mutation classes exactly match `pitExpectedClasses` when PIT is gated.

Release jobs, signing credentials, Central Portal tokens, and deployment workflows are intentionally absent. Publication requires owner approval and a completed release-readiness review.
