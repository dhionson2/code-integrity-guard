package io.github.dhionson2.cig.core;

import java.util.List;

public record ContractResult(String contractId, ContractStatus status, List<TestEvidence> evidence, List<SecurityScenario> scenarios, List<ScenarioEvidence> scenarioEvidence, String explanation) {
    public ContractResult(String contractId, ContractStatus status, List<TestEvidence> evidence, String explanation) { this(contractId, status, evidence, List.of(), List.of(), explanation); }
    public ContractResult(String contractId, ContractStatus status, List<TestEvidence> evidence, List<ScenarioEvidence> scenarioEvidence, String explanation) { this(contractId, status, evidence, List.of(), scenarioEvidence, explanation); }
    public ContractResult { evidence = List.copyOf(evidence); scenarios=List.copyOf(scenarios); scenarioEvidence = List.copyOf(scenarioEvidence); }
}
