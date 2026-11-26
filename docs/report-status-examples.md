# Report status examples

All identifiers below are fictional. This page shows one synthetic report
that demonstrates each contract status, so a reader can tell a recorded
audit apart from verified contract evidence and from missing evidence.
The full schema is [json-report-schema.json](json-report-schema.json),
explained in [json-report-schema.md](json-report-schema.md).

## What each status means

| Status | Meaning |
|---|---|
| `VERIFIED` | All linked test executions passed and every declared scenario has a matching policy-bound HTTP observation with the expected status, attached to a passing invocation. |
| `FAILED` | A linked test invocation failed, or an observed scenario status or policy linkage mismatched. This is a failed check, not a general vulnerability classification. |
| `UNVERIFIED` | The contract declares no linked test IDs, so no evidence was collected for it. This is missing evidence, not a confirmed vulnerability and not a pass. |
| `INCONCLUSIVE` | Evidence is absent, partial, skipped, malformed, or otherwise insufficient. Never treat it as verified. |

`NOT_RUN` is not a contract status. It is the value of
`contractsEvaluation` when no contracts were configured: `contracts` is
empty, nothing was evaluated, and no `VERIFIED` indicator is emitted.
See the fragment at the end of this page.

`buildStatus` describes the run, not any single contract. `AUDIT_RECORDED`
means the goal produced a report; it does not imply contracts were
evaluated or approved. In the example below the build status is `FAILED`
because the failed contract trips the default gate, even though one
contract in the same report is `VERIFIED`.

## Synthetic example report

```json
{
  "schemaVersion": "1.2",
  "buildStatus": "FAILED",
  "contractsEvaluation": "EVALUATED",
  "contractsManifestStatus": "PRESENT",
  "evidenceRun": {
    "runId": "11111111-2222-4333-8444-555555555555",
    "origin": "com.example:demo-shop:1.0.0"
  },
  "contracts": [
    {
      "id": "EXAMPLE-OWNER-ISOLATION",
      "status": "VERIFIED",
      "explanation": "All linked tests passed and every declared HTTP scenario returned its explicitly expected status.",
      "executionEvidence": [
        {
          "testId": "example.ownerIsolation",
          "runId": "11111111-2222-4333-8444-555555555555",
          "origin": "com.example:demo-shop:1.0.0",
          "executionId": "example-invocation-1",
          "outcome": "PASSED",
          "detail": ""
        }
      ],
      "declaredScenarios": [
        {
          "scenarioId": "example.aliceReadsBobsOrder",
          "testId": "example.ownerIsolation",
          "type": "CROSS_OWNER",
          "actor": "alice",
          "resourceOwner": "bob",
          "resource": "/orders/7",
          "operation": "GET /orders/7",
          "actorHeader": "X-Actor",
          "expectedStatus": 404
        }
      ],
      "scenarioEvidence": [
        {
          "scenarioId": "example.aliceReadsBobsOrder",
          "testId": "example.ownerIsolation",
          "runId": "11111111-2222-4333-8444-555555555555",
          "origin": "com.example:demo-shop:1.0.0",
          "executionId": "example-invocation-1",
          "observedStatus": 404
        }
      ]
    },
    {
      "id": "EXAMPLE-ADMIN-ONLY",
      "status": "FAILED",
      "explanation": "A linked test failed and the observed scenario status did not match the declared policy.",
      "executionEvidence": [
        {
          "testId": "example.adminOnly",
          "runId": "11111111-2222-4333-8444-555555555555",
          "origin": "com.example:demo-shop:1.0.0",
          "executionId": "example-invocation-2",
          "outcome": "FAILED",
          "detail": "Expected 403, got 200."
        }
      ],
      "declaredScenarios": [
        {
          "scenarioId": "example.bobReadsAdminReport",
          "testId": "example.adminOnly",
          "type": "CROSS_OWNER",
          "actor": "bob",
          "resourceOwner": "alice",
          "resource": "/admin/report",
          "operation": "GET /admin/report",
          "actorHeader": "X-Actor",
          "expectedStatus": 403
        }
      ],
      "scenarioEvidence": [
        {
          "scenarioId": "example.bobReadsAdminReport",
          "testId": "example.adminOnly",
          "runId": "11111111-2222-4333-8444-555555555555",
          "origin": "com.example:demo-shop:1.0.0",
          "executionId": "example-invocation-2",
          "observedStatus": 200
        }
      ]
    },
    {
      "id": "EXAMPLE-AUDIT-LOG",
      "status": "UNVERIFIED",
      "explanation": "The contract declares no linked test IDs, so no evidence was collected for it.",
      "executionEvidence": [],
      "declaredScenarios": [],
      "scenarioEvidence": []
    },
    {
      "id": "EXAMPLE-RATE-LIMIT",
      "status": "INCONCLUSIVE",
      "explanation": "The linked test was skipped, so the declared scenario has no observed evidence.",
      "executionEvidence": [
        {
          "testId": "example.rateLimit",
          "runId": "11111111-2222-4333-8444-555555555555",
          "origin": "com.example:demo-shop:1.0.0",
          "executionId": "example-invocation-3",
          "outcome": "SKIPPED",
          "detail": "Test skipped."
        }
      ],
      "declaredScenarios": [
        {
          "scenarioId": "example.tooManyRequests",
          "testId": "example.rateLimit",
          "type": "CROSS_OWNER",
          "actor": "alice",
          "resourceOwner": "bob",
          "resource": "/orders/7",
          "operation": "GET /orders/7",
          "actorHeader": "X-Actor",
          "expectedStatus": 429
        }
      ],
      "scenarioEvidence": []
    }
  ],
  "pit": {
    "status": "AVAILABLE",
    "scope": "com.example.demo.OrderController",
    "expectedScope": ["com.example.demo.OrderController"],
    "partialCoverage": false,
    "detected": 9,
    "total": 10,
    "excluded": 1,
    "score": 0.9,
    "reason": null,
    "mutations": [
      {
        "class": "com.example.demo.OrderController",
        "method": "get",
        "mutator": "org.pitest.mutationtest.engine.gregor.mutators.NegateConditionalsMutator",
        "description": "negated conditional",
        "status": "KILLED",
        "killingTest": "example.ownerIsolation"
      }
    ]
  },
  "gates": ["Contract EXAMPLE-ADMIN-ONLY failed."],
  "limitations": ["A passing audit does not prove the application secure and does not infer code authorship."]
}
```

How to read it:

- `EXAMPLE-OWNER-ISOLATION` is `VERIFIED`: its test passed and the
  observed status (404) matches the declared expected status (404).
- `EXAMPLE-ADMIN-ONLY` is `FAILED`: its test failed and the observed
  status (200) does not match the declared expected status (403).
- `EXAMPLE-AUDIT-LOG` is `UNVERIFIED`: it links no tests and carries no
  evidence arrays entries. That is missing evidence, not a pass and not a
  confirmed vulnerability.
- `EXAMPLE-RATE-LIMIT` is `INCONCLUSIVE`: its test was skipped, so the
  declared scenario has no observation. Skipped evidence is never
  treated as verified.

## When nothing was evaluated

If no contracts were configured, the report carries no contract results
at all. The fragment below shows only the fields that change relative to
the example above; it is not a complete report.

```json
{
  "buildStatus": "AUDIT_RECORDED",
  "contractsEvaluation": "NOT_RUN",
  "contractsManifestStatus": "MISSING",
  "contracts": [],
  "limitations": ["No contracts were configured, so no contract checks were performed."]
}
```

`NOT_RUN` means no contract checks were performed. It is not a pass,
and no `VERIFIED` indicator is emitted.
