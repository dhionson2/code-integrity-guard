package io.github.dhionson2.cig.core;

/** Records JUnit lifecycle outcome only; it is not proof that a security assertion was exercised. */
public record TestEvidence(String testId, Outcome outcome, String detail, String executionId, String runId, String origin) {
    public enum Outcome { PASSED, FAILED, SKIPPED }
    public TestEvidence {
        if (testId == null || testId.isBlank() || outcome == null || executionId == null || executionId.isBlank() || runId == null || runId.isBlank() || origin == null || origin.isBlank()) throw new IllegalArgumentException("Evidence requires test, outcome, invocation, run, and origin fields");
        detail = detail == null ? "" : detail;
    }
}
