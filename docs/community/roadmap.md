# Public roadmap

This is an initial, proposal-based roadmap for community discussion. It has no promised dates or release commitments. The proposal IDs below are local drafts in [issue-proposals.md](issue-proposals.md); no GitHub issues or milestones have been created. Maintainers will confirm scope, priority, and acceptance criteria before any proposal becomes scheduled work.

## Adoption and documentation

- P-01 — Add a concise consumer quick start based on the checked-in independent consumer.
- P-02 — Document the module map and dependency direction.
- P-03 — Add safe, representative report examples and status interpretation.

## Consumer integration and platform coverage

- P-04 — Exercise the Maven plugin through an automated, isolated consumer integration test.
- P-05 — Add Windows coverage for the wrapper and consumer workflow.

## Contract diagnostics and authorization coverage

- P-06 — Add stable, machine-readable reason codes while retaining human-readable explanations.
- P-07 — Explore declared anonymous-access and insufficient-role authorization scenarios.

## Reports and architecture

- P-08 — Provide a concise CI console summary of report outcomes.
- P-09 — Design and implement deterministic multi-module evidence aggregation at the Maven integration boundary.

All pull requests require maintainer review and passing CI. Contributions are not accepted or merged automatically. CIG reports observed evidence; neither an individual status nor a passing build proves an application secure.
