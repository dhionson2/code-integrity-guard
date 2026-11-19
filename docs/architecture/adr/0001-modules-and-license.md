# ADR 0001: module boundaries and license

- Status: accepted for V1 implementation.
- Decision: use the six requested Maven modules and Apache License 2.0.
- Rationale: framework-independent core can be reused without Maven/Spring/PIT; adapters are optional and depend inward. Apache-2.0 is OSI-approved, includes explicit patent terms, and is compatible with the chosen Java ecosystem dependencies.
- Consequences: Maven plugin orchestrates separate libraries; report implementation stays isolated from core.

