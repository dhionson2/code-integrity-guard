package io.github.dhionson2.cig.core;

/** Independently declared authorization policy for one concrete HTTP scenario. */
public record SecurityScenario(String id, String contractId, String testId, String type, String actor,
                               String resourceOwner, String resource, String operation, String actorHeader, int expectedStatus) {
    public SecurityScenario {
        if (id == null || id.isBlank() || contractId == null || contractId.isBlank() || testId == null || testId.isBlank()) throw new IllegalArgumentException("Scenario requires id, contract id, and test id");
        if (type == null || !type.equals("CROSS_OWNER")) throw new IllegalArgumentException("V1 supports CROSS_OWNER scenarios only");
        if (actor == null || actor.isBlank() || resource == null || resource.isBlank() || operation == null || !operation.matches("(GET|POST|PUT|PATCH|DELETE) /.+")) throw new IllegalArgumentException("Scenario requires actor, resource, and HTTP operation");
        if (expectedStatus < 100 || expectedStatus > 599) throw new IllegalArgumentException("Invalid expected HTTP status");
        actorHeader = actorHeader == null ? "" : actorHeader;
        if (resourceOwner == null || resourceOwner.isBlank() || actor.equals(resourceOwner)) throw new IllegalArgumentException("Cross-owner scenario requires different actor and owner");
        resourceOwner = resourceOwner == null ? "" : resourceOwner;
    }
}
