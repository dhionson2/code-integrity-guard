# Supply-chain review evidence (2026-11-19)

## Inventory

Generated with the CycloneDX Maven plugin 2.9.3, goal `makeAggregateBom`, from the complete reactor including test scope:

```powershell
.\mvnw.cmd -B -ntp org.cyclonedx:cyclonedx-maven-plugin:2.9.3:makeAggregateBom -DincludeTestScope=true -DoutputFormat=json
```

The raw JSON is `release-evidence/cyclonedx-bom.json` (CycloneDX 1.6). It lists 109 components; all 109 entries have license metadata. Distinct detected identifiers include Apache-2.0, BSD-3-Clause, EPL-1.0, EPL-2.0, GPL-2.0-with-classpath-exception and MIT. Test-scope dependencies are included. This is metadata inventory, not a legal compatibility determination. In particular, the mixed EPL/GPL-classpath metadata should be reviewed against the exact distribution/usage model by the release owner or counsel.

## Vulnerability scan

Attempted using OWASP Dependency-Check Maven plugin 13.0.0, aggregate goal, JSON and HTML output, with no API credentials:

```powershell
.\mvnw.cmd -B -ntp org.owasp:dependency-check-maven:13.0.0:aggregate '-Dformat=JSON,HTML' -DoutputDirectory=target/dependency-check -DskipTestScope=false -DfailOnError=false
```

Maven exited 0 because `failOnError=false`, but Dependency-Check logged a fatal NVD update error (`Invalid API Key, length of 0 too short`) and stopped before dependency analysis. No vulnerability report was generated. This is **not a clean vulnerability scan** and does not support any “zero known vulnerabilities” conclusion. Repeat with a valid NVD API key supplied out-of-band through the plugin-supported property/environment, or a maintained local NVD database. Never commit or package that key.

## Interpretation and limits

- The SBOM represents Maven-resolved reactor dependencies at the recorded build time; it does not inventory host tools, Maven itself, CI actions, or undeclared runtime downloads.
- License identifiers are upstream metadata and may be missing, inaccurate, or multi-license alternatives. Human license review is required before release.
- No CVE findings can be stated because the NVD-backed scan did not reach analysis. SCA remains a release blocker.
- The SBOM contains proposed `io.github.dhionson2` SCM coordinates; repository and namespace ownership remain unverified.
