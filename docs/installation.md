# Installation and usage

## Local build

Install Java 21, then run `./mvnw clean verify` (Windows: `mvnw.cmd clean verify`). The Wrapper downloads Maven 3.9.16 into the user's Maven Wrapper cache. No API key or external analysis service is required.

## Consumer setup

After a future 1.0.0 release is published and the `io.github.dhionson2` namespace is verified, add `code-integrity-guard-testkit` as a test dependency and `code-integrity-guard-maven-plugin` to the build. Until then, build/install the modules locally with `mvnw.cmd clean install` and use `1.0.0-SNAPSHOT` for local experiments.

1. Write independently authored contract rows in `.cig/contracts.tsv`.
2. Register `EvidenceExtension` on JUnit Jupiter test classes and annotate methods with stable test IDs.
3. Configure Surefire and Failsafe with separate CIG evidence paths plus `cig.run.manifest.file`; fork JVMs share the files, not memory.
4. Run PIT explicitly if mutation analysis is needed; configure XML output and run it before `audit`.
5. Bind `prepare-evidence` at `initialize`, then `seal-evidence` and `audit` at `verify` in that order. Declare the CIG plugin after Failsafe so sealing follows Failsafe `verify`. Preparation resets stale evidence without requiring `clean`.
6. The `audit` goal defaults `requireContracts` to `true` and fails if `.cig/contracts.tsv` is missing or has no contract rows. A malformed manifest always fails. Use `<requireContracts>false</requireContracts>` only for an intentional no-contract build; its report says `NOT_RUN` and makes no contract approval claim.
6. Read `target/code-integrity-guard/report.json` and `index.html`. Treat INCONCLUSIVE as missing confidence, not a pass.

See [README](../README.md) for full Maven snippets and [consumer integration fixture](../integration-consumer/pom.xml) for a tested working example.
