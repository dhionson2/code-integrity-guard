# Code Integrity Guard

Code Integrity Guard (CIG) is a local, Java-first tool for checking whether declared security contracts were exercised by real test executions and for reviewing evidence from PIT Mutation Testing. It reports observed evidence; a green test suite or audit does **not** prove an application secure. CIG does not infer whether code was written by AI.

## Requirements and status

- Java 21+ and Maven 3.10.1+ to build the project and execute its Maven Plugin (the checked-in Maven Wrapper uses Maven 3.10.1).
- JUnit 5.14.3, Spring Boot 3.4.13 for the optional example, and PIT 1.30.2 with JUnit 5 PIT adapter 1.2.3 for the optional mutation run. See [version matrix](docs/version-matrix.md).
- This repository uses Apache-2.0. Version 1.0.0 is published; the POM SCM URLs point to this public repository. See the [release readiness record](docs/release-readiness.md) for the dated technical review and evidence.

Build locally with `./mvnw clean verify` (Windows: `mvnw.cmd clean verify`).

## Modules

`core` contains framework-free contracts/evaluation. `testkit` provides JUnit execution evidence and a Spring MVC helper that performs declared authorization scenarios. `pit-adapter` reads an existing PIT XML report; it does not run a mutation engine. `report` writes JSON/HTML. `maven-plugin` provides `code-integrity-guard:audit`. `examples/spring-boot-authorization` is a synthetic local-only demo.

## Installation/use

Version 1.0.0 is available from [Maven Central](https://central.sonatype.com/artifact/io.github.dhionson2/code-integrity-guard-maven-plugin). Add the library dependencies you need. Example consumer dependency:

```xml
<dependency>
  <groupId>io.github.dhionson2</groupId>
  <artifactId>code-integrity-guard-testkit</artifactId>
  <version>1.0.0</version>
  <scope>test</scope>
</dependency>
```

The `testkit` declares JUnit Jupiter API, Spring Test, Spring Security Core, Jakarta Servlet API, and Hamcrest with Maven scope `provided`; Maven does not propagate these dependencies transitively to consumers. Consumers must add compatible dependencies with scope `test` for the APIs they use: JUnit Jupiter API for `EvidenceExtension` and `ContractTest`; Spring Test, Spring MVC, and Jakarta Servlet API for `AuthorizationChecks`; Spring Security Core when the helper reads an authenticated principal; and Hamcrest when using the helper's `ResultMatcher` methods. Consumers using Spring Security MockMvc post-processors or other Spring Security Test APIs must also declare Spring Security Test directly with scope `test`. The project’s tested version set is recorded in [version matrix](docs/version-matrix.md); the consumer owns version alignment with its Spring/JUnit stack.

The V1.0.0 security review records limited maintainer acceptance of residual Spring advisories in the audited `testkit`/`provided` scope. Those advisories were not fixed by this release, and the acceptance does not cover consumer applications. Consumers must select and maintain supported Spring versions, track upstream advisories, and assess their own use of untrusted SpEL expressions, data binding, and Spring Security configuration. See [release readiness](docs/release-readiness.md) and the [supply-chain review](docs/supply-chain-review.md) for the exact findings and scope. The educational Spring Boot example is excluded from the release artifacts.

## Community

See [Contributing](CONTRIBUTING.md), the [Code of Conduct](CODE_OF_CONDUCT.md), [Security policy](SECURITY.md), and the initial [public roadmap](docs/community/roadmap.md). Roadmap items are proposals without promised dates; all pull requests require maintainer review and passing CI.

Create `.cig/contracts.tsv` with UTF-8 rows (tabs separate fields):

```text
# id<TAB>severity<TAB>scope<TAB>description<TAB>testIds<TAB>scenarioIds
CUSTOMER-OWNER-ISOLATION<TAB>HIGH<TAB>GET /clientes/{id}<TAB>Only the owner can read a customer<TAB>authorization.ownerCannotReadAnotherCustomer<TAB>customer.aliceReadsBobsCustomer
```

Declare `.cig/scenarios.tsv` separately with columns `scenarioId`, `contractId`, `testId`, `type`, `actor`, `resourceOwner`, `resource`, exact `METHOD /path`, optional identity header, and expected HTTP status. An empty identity-header column uses the authenticated servlet principal. V1 contract scenario type is `CROSS_OWNER`; the actor and owner identities must differ. The demo separately tests anonymous access and insufficient roles; CIG does not claim universal IDOR detection.

Annotate and register the extension on test classes:

```java
@ExtendWith(EvidenceExtension.class)
class CustomerAuthorizationTest {
  @Test
  @ContractTest("authorization.ownerCannotReadAnotherCustomer")
  void anotherOwnerGets404() throws Exception {
    AuthorizationChecks.verifyScenario("customer.aliceReadsBobsCustomer", mvc,
      get("/clientes/b").with(user("alice").roles("CUSTOMER")));
  }
}
```

Configure Surefire and Failsafe forks with `cig.evidence.file`, `cig.scenario.evidence.file`, `cig.scenarios.file`, and `cig.run.manifest.file`. Bind `prepare-evidence` at `initialize`; after test goals bind `seal-evidence` and then `audit` at `verify` (declare CIG after Failsafe). Preparation creates a new per-module run UUID and removes prior evidence even without `mvn clean`. Forks read the manifest from disk and append locked, versioned records. Sealing follows successful test phases and records hashes; audit requires the same Maven session/project and unmodified sealed files. A plain passing test such as `assertTrue(true)` is execution evidence only and leaves its contract INCONCLUSIVE without matching scenario evidence. Hashes detect accidental changes but do not cryptographically authenticate a local producer. Multi-module evidence is isolated per module; reactor aggregation is not implemented.

Configure the Maven goal explicitly after test and PIT execution:

```xml
<build><plugins><plugin>
  <groupId>io.github.dhionson2</groupId>
  <artifactId>code-integrity-guard-maven-plugin</artifactId>
  <version>1.0.0</version>
  <executions>
    <execution><id>prepare-evidence</id><phase>initialize</phase><goals><goal>prepare-evidence</goal></goals></execution>
    <execution><id>seal-evidence</id><phase>verify</phase><goals><goal>seal-evidence</goal></goals></execution>
    <execution><id>security-audit</id><phase>verify</phase><goals><goal>audit</goal></goals></execution>
  </executions>
  <configuration>
    <contractsFile>${project.basedir}/.cig/contracts.tsv</contractsFile>
    <evidenceFile>${project.build.directory}/cig-evidence.tsv</evidenceFile>
    <scenariosFile>${project.basedir}/.cig/scenarios.tsv</scenariosFile>
    <scenarioEvidenceFile>${project.build.directory}/cig-scenario-evidence.tsv</scenarioEvidenceFile>
    <runManifestFile>${project.build.directory}/cig-evidence-run.properties</runManifestFile>
    <pitFile>${project.build.directory}/pit-reports/mutations.xml</pitFile>
    <failOnFailedContract>true</failOnFailedContract>
    <failOnUnverified>true</failOnUnverified>
    <failOnInconclusive>true</failOnInconclusive>
    <requirePit>true</requirePit>
    <pitExpectedClasses><pitExpectedClass>com.example.security.CustomerController</pitExpectedClass></pitExpectedClasses>
  </configuration>
</plugin></plugins></build>
```

PIT is opt-in and should run before `audit`; CIG does not silently trigger expensive mutation analysis. Reports are written to `target/code-integrity-guard/report.json` and `index.html`. `requireContracts` defaults to `true`: a missing or empty `.cig/contracts.tsv` fails the Maven goal. A malformed manifest always fails because it cannot be evaluated. For a deliberate build without contract validation, configure `<requireContracts>false</requireContracts>` (or `-Dcig.requireContracts=false`); Maven logs a warning and reports `contractsEvaluation: NOT_RUN`, without any approval status. This option means no contract checks were performed. Missing contract evidence for a declared contract remains inconclusive and can be made a gate failure with `failOnInconclusive=true`; actual failed contracts fail by default. Keep `prepare-evidence` and `seal-evidence` bound even when `mvn clean` is used: they bind evidence to the current Maven session and module.

## Status semantics

| Status | Meaning |
|---|---|
| VERIFIED | All linked tests passed and every declared scenario returned its explicitly expected response in a real request. |
| FAILED | A linked test failed or a scenario response did not match policy. This is not a general vulnerability classification. |
| UNVERIFIED | No test IDs are linked. This is missing evidence, not a confirmed vulnerability. |
| INCONCLUSIVE | Evidence is absent, partial, skipped, malformed or otherwise insufficient. Never treat it as verified. |

PIT reports list mutations and a simple detected/total score. Survivors are not confirmed vulnerabilities; no-mutant reports have a zero score and must not be read as a 100% result.

## Demo

`examples/spring-boot-authorization` uses synthetic users with real Spring Security authentication and method authorization; it must never be deployed. Alice and Bob authenticate with Spring Security test principals. The fixed profile conceals Bob's customer from Alice with HTTP `404`; the vulnerable fixture returns `200`. To reproduce diagnostic Surefire and Failsafe failures while still generating the CIG report, run `./mvnw -f examples/spring-boot-authorization/pom.xml verify -Pcig-vulnerable-diagnostic`; this command intentionally exits nonzero. The regular build excludes only tagged vulnerable diagnostic methods and checks the fixed policy. PIT validation is documented separately.

## CI/CD and security

GitHub Actions runs `clean verify` with read-only repository permissions and no deployment. See [Threat Model](docs/architecture/threat-model.md), [Architecture](docs/architecture/software-design.md), [SECURITY.md](SECURITY.md), and [CI guide](docs/ci-cd.md). The vulnerable example is only a test fixture. Keep report output local and review user-supplied evidence details for sensitive content.

## Limitations

This V1 implementation does not cryptographically authenticate local evidence/PIT provenance, aggregate multi-module evidence, or invoke Maven Invoker integration tests automatically. Freshness checks prevent accidental reuse but do not defeat a hostile local process that can replace reports. The extension is JUnit Jupiter-only. The dated [release readiness record](docs/release-readiness.md) preserves the release audit history and its limitations.
