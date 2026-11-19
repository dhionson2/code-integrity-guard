package io.github.dhionson2.cig.core;

/** Result observed from executing a policy-bound HTTP request; separate from JUnit execution evidence. */
public record ScenarioEvidence(String scenarioId, String testId, String executionId, int observedStatus, String runId, String origin) {
    public ScenarioEvidence {
        if (scenarioId == null || scenarioId.isBlank() || testId == null || testId.isBlank() || executionId == null || executionId.isBlank() || runId == null || runId.isBlank() || origin == null || origin.isBlank()) throw new IllegalArgumentException("Scenario evidence requires scenario, test, invocation, run, and origin ids");
        if (observedStatus < 100 || observedStatus > 599) throw new IllegalArgumentException("Invalid observed HTTP status");
    }
}
