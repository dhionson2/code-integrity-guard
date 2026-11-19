package io.github.dhionson2.cig.core;

import java.util.*;

/** Deterministically requires passing execution evidence and matching independent policy-scenario observations. */
public final class ContractEvaluator {
    public List<ContractResult> evaluate(Collection<SecurityContract> contracts, Collection<TestEvidence> evidence, Collection<SecurityScenario> policies, Collection<ScenarioEvidence> observations, String expectedRunId, String expectedOrigin, boolean complete) {
        Objects.requireNonNull(contracts); Objects.requireNonNull(evidence); Objects.requireNonNull(policies); Objects.requireNonNull(observations);
        boolean integrityFailure=expectedRunId==null||expectedRunId.isBlank()||expectedOrigin==null||expectedOrigin.isBlank();
        Map<String, List<TestEvidence>> byId = new HashMap<>();
        Set<String> testInvocations=new HashSet<>();
        for (TestEvidence item : evidence) { if(!item.runId().equals(expectedRunId)||!item.origin().equals(expectedOrigin)||!testInvocations.add(item.testId()+"\0"+item.executionId())) integrityFailure=true; byId.computeIfAbsent(item.testId(), ignored -> new ArrayList<>()).add(item); }
        Map<String,SecurityScenario> policyById=new HashMap<>(); for(var p:policies) if(policyById.putIfAbsent(p.id(),p)!=null) throw new IllegalArgumentException("Duplicate scenario id: "+p.id());
        Map<String,List<ScenarioEvidence>> observationById=new HashMap<>(); Set<String> scenarioInvocations=new HashSet<>(); for(var o:observations) { if(!o.runId().equals(expectedRunId)||!o.origin().equals(expectedOrigin)||!scenarioInvocations.add(o.scenarioId()+"\0"+o.executionId())) integrityFailure=true; observationById.computeIfAbsent(o.scenarioId(),k->new ArrayList<>()).add(o); }
        Set<String> contractIds = new HashSet<>();
        List<ContractResult> results = new ArrayList<>();
        for (SecurityContract contract : contracts) {
            if (!contractIds.add(contract.id())) throw new IllegalArgumentException("Duplicate contract id: " + contract.id());
            List<TestEvidence> linked = contract.testIds().stream().sorted().flatMap(id -> byId.getOrDefault(id, List.of()).stream()).toList();
            boolean hasFailed = linked.stream().anyMatch(e -> e.outcome() == TestEvidence.Outcome.FAILED);
            boolean skipped = linked.stream().anyMatch(e -> e.outcome() == TestEvidence.Outcome.SKIPPED);
            boolean missing = contract.testIds().stream().anyMatch(id -> !byId.containsKey(id));
            List<SecurityScenario> linkedPolicies=new ArrayList<>(); List<ScenarioEvidence> linkedScenarios=new ArrayList<>(); boolean missingPolicy=false, missingObservation=false, mismatch=false, invalidLink=false;
            for(String scenarioId:contract.scenarioIds()) { SecurityScenario policy=policyById.get(scenarioId); if(policy==null||!policy.contractId().equals(contract.id())||!contract.testIds().contains(policy.testId())) {missingPolicy=true;continue;} linkedPolicies.add(policy); List<ScenarioEvidence> obs=observationById.getOrDefault(scenarioId,List.of()); linkedScenarios.addAll(obs); if(obs.isEmpty()) missingObservation=true; for(var o:obs) {boolean invocation=byId.getOrDefault(policy.testId(),List.of()).stream().anyMatch(e->e.executionId().equals(o.executionId())&&e.runId().equals(o.runId())&&e.origin().equals(o.origin())&&e.outcome()==TestEvidence.Outcome.PASSED);if(!o.testId().equals(policy.testId())||!invocation)invalidLink=true;if(o.observedStatus()!=policy.expectedStatus())mismatch=true;} }
            ContractStatus status; String reason;
            if(contract.testIds().isEmpty()) { status=ContractStatus.UNVERIFIED; reason="No test IDs are linked to this contract."; }
            else if (integrityFailure || !complete) { status=ContractStatus.INCONCLUSIVE; reason="Evidence is stale, conflicting, duplicated, incomplete, or not sealed for the current test execution."; }
            else if (hasFailed) { status = ContractStatus.FAILED; reason = "At least one linked test failed during execution."; }
            else if (!complete || skipped || missing) { status = ContractStatus.INCONCLUSIVE; reason = !complete ? "Test execution was incomplete." : skipped ? "A linked test was skipped." : "One or more linked test IDs have no execution evidence."; }
            else if(invalidLink) { status=ContractStatus.INCONCLUSIVE; reason="Scenario evidence is not associated with a passing invocation of its declared test in the current run."; }
            else if(missingPolicy||missingObservation) { status=ContractStatus.INCONCLUSIVE; reason="A declared security scenario has no valid policy or observed request evidence."; }
            else if(mismatch) { status=ContractStatus.FAILED; reason="A scenario observation did not match its declared policy or passing test invocation."; }
            else if(contract.scenarioIds().isEmpty()) { status=ContractStatus.INCONCLUSIVE; reason="No independent security scenarios are declared; test execution alone does not validate a contract."; }
            else { status = ContractStatus.VERIFIED; reason = "All linked tests passed and every declared HTTP scenario returned its explicitly expected status."; }
            results.add(new ContractResult(contract.id(), status, linked, linkedPolicies, linkedScenarios, reason));
        }
        return List.copyOf(results);
    }
}
