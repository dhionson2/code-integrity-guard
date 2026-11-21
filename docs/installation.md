# Installation and usage

## Local build

Use Java 21+ and Maven 3.10.1+ to build the project and execute the Code Integrity Guard Maven Plugin. The checked-in Wrapper downloads Maven 3.10.1 into the user's Maven Wrapper cache; with it, run `./mvnw clean verify` (Windows: `mvnw.cmd clean verify`). No API key or external analysis service is required.

## Consumer setup

The 1.0.0 release candidate is prepared locally and remains unpublished pending human release review. For local experiments with this checkout, build/install the modules with `mvnw.cmd clean install` and use version `1.0.0`; Maven Central consumers can use these coordinates after publication is explicitly authorized.

1. Write independently authored contract rows in `.cig/contracts.tsv`.
2. Register `EvidenceExtension` on JUnit Jupiter test classes and annotate methods with stable test IDs.
3. Configure Surefire and Failsafe with separate CIG evidence paths plus `cig.run.manifest.file`; fork JVMs share the files, not memory.
4. Run PIT explicitly if mutation analysis is needed; configure XML output and run it before `audit`.
5. Bind `prepare-evidence` at `initialize`, then `seal-evidence` and `audit` at `verify` in that order. Declare the CIG plugin after Failsafe so sealing follows Failsafe `verify`. Preparation resets stale evidence without requiring `clean`.
6. The `audit` goal defaults `requireContracts` to `true` and fails if `.cig/contracts.tsv` is missing or has no contract rows. A malformed manifest always fails. Use `<requireContracts>false</requireContracts>` only for an intentional no-contract build; its report says `NOT_RUN` and makes no contract approval claim.
6. Read `target/code-integrity-guard/report.json` and `index.html`. Treat INCONCLUSIVE as missing confidence, not a pass.

See [README](../README.md) for full Maven snippets and [consumer integration fixture](../integration-consumer/pom.xml) for a tested working example.
