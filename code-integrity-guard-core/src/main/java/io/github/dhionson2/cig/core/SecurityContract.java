package io.github.dhionson2.cig.core;

import java.util.Set;

/** Consumer requirement links both execution IDs and independent required security-scenario policies. */
public record SecurityContract(String id, String description, String severity, String scope, Set<String> testIds, Set<String> scenarioIds) {
    public SecurityContract(String id, String description, String severity, String scope, Set<String> testIds) { this(id, description, severity, scope, testIds, Set.of()); }
    public SecurityContract {
        if (id == null || id.isBlank()) throw new IllegalArgumentException("Contract id must not be blank");
        if (description == null || description.isBlank()) throw new IllegalArgumentException("Contract description must not be blank");
        severity = severity == null || severity.isBlank() ? "MEDIUM" : severity;
        scope = scope == null ? "" : scope;
        testIds = testIds == null ? Set.of() : Set.copyOf(testIds);
        scenarioIds = scenarioIds == null ? Set.of() : Set.copyOf(scenarioIds);
        if (testIds.stream().anyMatch(String::isBlank)) throw new IllegalArgumentException("Test ids must not be blank");
        if (scenarioIds.stream().anyMatch(String::isBlank)) throw new IllegalArgumentException("Scenario ids must not be blank");
    }
}
