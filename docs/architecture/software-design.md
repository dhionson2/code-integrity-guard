# Software design

## Boundaries

`core` owns immutable contract/evidence types and deterministic evaluation. `testkit` emits evidence from actual JUnit invocation callbacks and offers optional Spring MVC matchers. `pit-adapter` parses existing PIT XML; it never creates mutations. `report` renders local JSON/HTML. `maven-plugin` coordinates inputs and quality gates. The Spring Boot example is an independent consumer. Dependencies point toward core; core has no Maven, Spring, PIT, or AI dependencies.

## Trust and evidence

Contract manifest: UTF-8 TSV with contract IDs and required scenario IDs. A separate scenario manifest defines cross-owner actor, owner, resource, exact HTTP method/path, identity header, and expected status. Cross-owner is the only supported V1 scenario type. `TestEvidence` is only JUnit execution evidence. `ScenarioEvidence` is produced after `AuthorizationChecks` performs a MockMvc request and records the actual response against that manifest, bound to the active JUnit invocation ID.

`prepare-evidence` creates a module-scoped random run UUID and OPEN manifest at `initialize`, clearing prior evidence without requiring `clean`. Test forks read the on-disk manifest, prove they descend from the Maven process that prepared it, and append strict version-2 Base64 TSV rows under OS file locks, including run UUID, project origin, process-unique JUnit invocation ID, and result. At `verify`, `seal-evidence` requires the same Maven session and project, waits for append locks, computes SHA-256 digests, and marks the run SEALED; `audit` rechecks session, origin, state, hashes, record schemas, duplicate keys, and cross-file invocation linkage. Any unavailable or invalid check makes linked contracts INCONCLUSIVE. This file protocol works across Surefire/Failsafe fork processes without shared memory. Multi-module evidence is isolated per module; reactor-wide aggregation is not implemented. A passing assertion or test annotation alone is never sufficient.

## PIT

The adapter reads PIT XML and counts KILLED among score-eligible mutation records; NON_VIABLE records are explicitly excluded. Survivors and other statuses remain visible as data and do not establish a vulnerability. The PIT 1.30.2 root `partial` attribute is retained as `partialCoverage` metadata and does not alone imply an incomplete mutation run. Missing, malformed, stale, scope-mismatched, empty, or unresolved error-state XML is INCONCLUSIVE. The report is cleared at preparation and audit checks current-session freshness and the configured expected class scope. Local timestamps/files are not cryptographic provenance; hostile replacement by a local process remains possible.

## Reports and privacy

Reports are local under `target/code-integrity-guard/` by default. HTML text is escaped, filenames are fixed, and JSON contains no source or environment data. Manifest descriptions and test details are user-controlled and must be treated as untrusted. PIT XML parsing disables DTD and external entities and caps input at 20 MiB.

