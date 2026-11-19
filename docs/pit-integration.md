# PIT integration and release-validation profile

## Meaning of PIT `partial`

The adapter is validated against the exact PIT 1.30.2 source used by the example. In PIT 1.30.2, `XMLReportFactory` passes `args.data().shouldReportCoverage()` into `XMLReportListener`; `ReportOptions.reportCoverage` is initialized to `true`; and `XMLReportListener.runStart()` writes that value as `<mutations partial="…">`. Thus this attribute records partial-coverage data and is not a mutation-run completion marker. The adapter preserves it as `partialCoverage` metadata and does not downgrade a run solely because it is true.

Sources: [`XMLReportFactory.java` (1.30.2)](https://github.com/hcoles/pitest/blob/1.30.2/pitest-entry/src/main/java/org/pitest/mutationtest/report/xml/XMLReportFactory.java), [`XMLReportListener.java` (1.30.2)](https://github.com/hcoles/pitest/blob/1.30.2/pitest-entry/src/main/java/org/pitest/mutationtest/report/xml/XMLReportListener.java), [`ReportOptions.java` (1.30.2)](https://github.com/hcoles/pitest/blob/1.30.2/pitest-entry/src/main/java/org/pitest/mutationtest/config/ReportOptions.java).

## Completion, freshness, and scope checks

A PIT result is AVAILABLE only when its XML parses completely, the root and coverage metadata are supported, required PIT mutation fields and attributes are valid, it contains scoreable mutations, no unresolved `TIMED_OUT`, `MEMORY_ERROR`, or `RUN_ERROR` mutants exist, and its classes match the explicitly configured expected scope for a gated run. Missing, malformed, truncated, unsupported, stale, empty, scope-mismatched, or error-marked reports remain INCONCLUSIVE.

The example binds `prepare-evidence` at `initialize`, which deletes the PIT report before the current session can create it. Audit also requires a valid sealed run from the current Maven session and a PIT report modification time no earlier than that session. This is fail-closed against accidental reuse without `mvn clean`. File timestamps and local files are not cryptographic provenance; a hostile local process capable of replacing outputs remains outside the trust boundary.

## Reproduction and quality gates

The Spring example's opt-in `pit-validation` profile targets `CustomerController`, selects `FixedCustomerAuthorizationTest`, emits XML, sets mutation JVM timeouts, disables CIG evidence callbacks in PIT workers, requires PIT, and declares `CustomerController` as the expected analysis scope.

```powershell
# Actual PIT score is 7/11 = 63.636%; this gate should pass at 60%.
.\mvnw.cmd -B -ntp -f examples\spring-boot-authorization\pom.xml verify -Ppit-validation '-Dcig.minimumMutationPercent=60'

# Same run must fail the quality gate at 70%; report remains PIT AVAILABLE and records the score.
.\mvnw.cmd -B -ntp -f examples\spring-boot-authorization\pom.xml verify -Ppit-validation '-Dcig.minimumMutationPercent=70'
```

The current real PIT 1.30.2 output contains 11 mutations: 7 KILLED, 1 SURVIVED, 3 NO_COVERAGE; no timed-out/error mutants; scope `io.github.dhionson2.example.CustomerController`. Score is 7/11 (63.636%). The real report has `partial="true"`, which is preserved as `partialCoverage: true` while the analysis is AVAILABLE when freshness and scope checks pass.

CIG reports class, method, mutator, description, status, killing test, actual and expected scope, killed count, denominator (excluding NON_VIABLE), excluded count, and coverage metadata. Surviving mutants are testing gaps to investigate, not confirmed vulnerabilities. CIG does not authenticate PIT report provenance or protect against a malicious local file replacement.

