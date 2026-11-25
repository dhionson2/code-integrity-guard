# Consumer Quick Start

Use Code Integrity Guard (CIG) `1.0.0` from Maven Central to record whether declared security contracts were exercised by real JUnit tests. CIG reports evidence; it does not prove an application secure.

## Requirements

- Java 21 or newer.
- Maven 3.10.1 or newer. The repository's Maven Wrapper uses 3.10.1.
- JUnit Jupiter for the testkit extension.

## Add the dependencies

Add the testkit and JUnit to your project's test dependencies:

```xml
<dependency>
  <groupId>io.github.dhionson2</groupId>
  <artifactId>code-integrity-guard-testkit</artifactId>
  <version>1.0.0</version>
  <scope>test</scope>
</dependency>
<dependency>
  <groupId>org.junit.jupiter</groupId>
  <artifactId>junit-jupiter</artifactId>
  <version>5.14.3</version>
  <scope>test</scope>
</dependency>
```

The testkit declares its JUnit and Spring APIs with Maven scope `provided`, so Maven does not add them transitively to your project. Declare the APIs you use directly as test dependencies. For example, the `AuthorizationChecks` helper also uses Spring Test, Spring MVC, Spring Security Core, and Jakarta Servlet API; tests using Spring Security MockMvc helpers need Spring Security Test. Choose versions aligned with your application. See the [version matrix](version-matrix.md).

## Configure and run the audit

Declare the Maven plugin after Surefire and Failsafe so `seal-evidence` runs after test execution:

```xml
<plugin>
  <groupId>io.github.dhionson2</groupId>
  <artifactId>code-integrity-guard-maven-plugin</artifactId>
  <version>1.0.0</version>
  <executions>
    <execution>
      <id>prepare-evidence</id>
      <phase>initialize</phase>
      <goals><goal>prepare-evidence</goal></goals>
    </execution>
    <execution>
      <id>seal-evidence</id>
      <phase>verify</phase>
      <goals><goal>seal-evidence</goal></goals>
    </execution>
    <execution>
      <id>security-audit</id>
      <phase>verify</phase>
      <goals><goal>audit</goal></goals>
    </execution>
  </executions>
  <configuration>
    <contractsFile>${project.basedir}/.cig/contracts.tsv</contractsFile>
    <evidenceFile>${project.build.directory}/cig-evidence.tsv</evidenceFile>
    <scenariosFile>${project.basedir}/.cig/scenarios.tsv</scenariosFile>
    <scenarioEvidenceFile>${project.build.directory}/cig-scenario-evidence.tsv</scenarioEvidenceFile>
    <runManifestFile>${project.build.directory}/cig-evidence-run.properties</runManifestFile>
  </configuration>
</plugin>
```

Create `.cig/contracts.tsv` with declared contract rows, register `EvidenceExtension` on JUnit Jupiter test classes, and annotate linked test methods with `@ContractTest("stable.test.id")`. When using authorization scenarios, also declare `.cig/scenarios.tsv` and call `AuthorizationChecks.verifyScenario` from the test. The [independent consumer fixture](../integration-consumer/) is a complete working example, including the manifests, JUnit tests, dependency declarations, and plugin configuration.

From this repository's root, install the reactor artifacts first, then verify the independent fixture with the checked-in Wrapper:

```powershell
mvnw.cmd --batch-mode --no-transfer-progress clean install
mvnw.cmd --batch-mode --no-transfer-progress -f integration-consumer/pom.xml clean verify
```

```sh
./mvnw --batch-mode --no-transfer-progress clean install
./mvnw --batch-mode --no-transfer-progress -f integration-consumer/pom.xml clean verify
```

In your own project, run `mvnw.cmd clean verify` on Windows or `./mvnw clean verify` on Unix-like systems, using your project's Maven Wrapper.

## Read the reports

The plugin writes `target/code-integrity-guard/report.json` and `target/code-integrity-guard/index.html`. Missing or partial evidence is not a pass: `UNVERIFIED` means no test IDs are linked, while `INCONCLUSIVE` means evidence is absent, skipped, partial, malformed, or otherwise insufficient. `VERIFIED` only describes the linked checks and declared outcomes; it is not a general security assessment. See [installation and usage](installation.md) for the full lifecycle and configuration details.
