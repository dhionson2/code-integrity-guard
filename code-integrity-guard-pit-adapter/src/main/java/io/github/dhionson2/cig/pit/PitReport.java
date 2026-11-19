package io.github.dhionson2.cig.pit;

import java.util.*;

/** Parsed PIT mutation result. partialCoverage is PIT's coverage-data flag, not a run-completion flag. */
public record PitReport(Status status, List<Mutation> mutations, int detected, int total,
                        int excluded, String scope, Boolean partialCoverage,
                        List<String> expectedScope, String reason) {
    public enum Status { AVAILABLE, INCONCLUSIVE }
    public record Mutation(String className, String mutatedMethod, String mutator,
                           String description, String status, String killingTest) {}

    public PitReport(Status status, List<Mutation> mutations, int detected, int total, String reason) {
        this(status, mutations, detected, total, 0, "", null, List.of(), reason);
    }

    public PitReport(Status status, List<Mutation> mutations, int detected, int total,
                     int excluded, String reason) {
        this(status, mutations, detected, total, excluded, "", null, List.of(), reason);
    }

    /** Compatibility constructor for callers which already provide the rendered class scope. */
    public PitReport(Status status, List<Mutation> mutations, int detected, int total,
                     int excluded, String scope, String reason) {
        this(status, mutations, detected, total, excluded, scope, null, List.of(), reason);
    }

    public PitReport {
        mutations = List.copyOf(mutations);
        expectedScope = List.copyOf(expectedScope);
        scope = scope == null ? "" : scope;
        reason = reason == null ? "" : reason;
    }

    public double score() { return total == 0 ? 0d : (double) detected / total; }

    public PitReport validateExpectedScope(Collection<String> configuredScope) {
        TreeSet<String> expected = new TreeSet<>(configuredScope);
        if (status != Status.AVAILABLE) return withExpectedScope(expected);
        TreeSet<String> actual = scope.isBlank()
                ? new TreeSet<>()
                : new TreeSet<>(Arrays.asList(scope.split(", ")));
        if (expected.isEmpty()) {
            return inconclusive(expected, "PIT validation requires an explicit expected class scope.");
        }
        if (!actual.equals(expected)) {
            return inconclusive(expected, "PIT analyzed scope does not match configured expected scope; expected "
                    + expected + " but report contains " + actual + ".");
        }
        return new PitReport(status, mutations, detected, total, excluded, scope,
                partialCoverage, List.copyOf(expected), reason);
    }

    public PitReport asInconclusive(String explanation) {
        return new PitReport(Status.INCONCLUSIVE, mutations, detected, total, excluded,
                scope, partialCoverage, expectedScope, explanation);
    }

    private PitReport withExpectedScope(Collection<String> expected) {
        return new PitReport(status, mutations, detected, total, excluded, scope,
                partialCoverage, List.copyOf(expected), reason);
    }

    private PitReport inconclusive(Collection<String> expected, String explanation) {
        return new PitReport(Status.INCONCLUSIVE, mutations, detected, total, excluded,
                scope, partialCoverage, List.copyOf(expected), explanation);
    }
}
