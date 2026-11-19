package io.github.dhionson2.cig.core;

import org.junit.jupiter.api.Test;
import java.util.*;
import static org.junit.jupiter.api.Assertions.*;

class ContractEvaluatorTest {
    private static final String RUN="6a0b9c5f-a077-46f5-a4fa-cedf7e857b51", ORIGIN="sample:consumer:1";
    private final ContractEvaluator evaluator=new ContractEvaluator();
    private SecurityContract contract(Set<String> ids){return new SecurityContract("AUTH-1","Owner only","HIGH","/customers",ids,ids.contains("t1")?Set.of("s1"):Set.of());}
    private SecurityScenario policy(){return new SecurityScenario("s1","AUTH-1","t1","CROSS_OWNER","alice","bob","/resource/42","GET /resource/42","X-Actor",404);}
    private TestEvidence pass(){return new TestEvidence("t1",TestEvidence.Outcome.PASSED,"","inv-1",RUN,ORIGIN);}
    private ScenarioEvidence observed(int status){return new ScenarioEvidence("s1","t1","inv-1",status,RUN,ORIGIN);}
    private ContractResult evaluate(List<TestEvidence> tests,List<ScenarioEvidence> scenarios,boolean complete){return evaluator.evaluate(List.of(contract(Set.of("t1"))),tests,List.of(policy()),scenarios,RUN,ORIGIN,complete).getFirst();}

    @Test void currentSealedRunWithMatchingScenarioVerifies(){assertEquals(ContractStatus.VERIFIED,evaluate(List.of(pass()),List.of(observed(404)),true).status());}
    @Test void previousRunEvidenceCannotVerify(){TestEvidence old=new TestEvidence("t1",TestEvidence.Outcome.PASSED,"","inv-1","previous-run",ORIGIN);ScenarioEvidence oldScenario=new ScenarioEvidence("s1","t1","inv-1",404,"previous-run",ORIGIN);assertEquals(ContractStatus.INCONCLUSIVE,evaluate(List.of(old),List.of(oldScenario),true).status());}
    @Test void passingTestWithoutSecurityScenarioCannotVerify(){assertEquals(ContractStatus.INCONCLUSIVE,evaluator.evaluate(List.of(new SecurityContract("AUTH-1","Owner only","HIGH","/customers",Set.of("t1"))),List.of(pass()),List.of(),List.of(),RUN,ORIGIN,true).getFirst().status());}
    @Test void observedAuthorizationMismatchFailsEvenIfJUnitPassed(){assertEquals(ContractStatus.FAILED,evaluate(List.of(pass()),List.of(observed(200)),true).status());}
    @Test void missingScenarioObservationIsInconclusive(){assertEquals(ContractStatus.INCONCLUSIVE,evaluate(List.of(pass()),List.of(),true).status());}
    @Test void scenarioMustBelongToContractAndLinkedTest(){assertEquals(ContractStatus.INCONCLUSIVE,evaluator.evaluate(List.of(contract(Set.of("other"))),List.of(pass()),List.of(policy()),List.of(observed(404)),RUN,ORIGIN,true).getFirst().status());}
    @Test void scenarioFromAnotherInvocationIsInconclusive(){ScenarioEvidence wrong=new ScenarioEvidence("s1","t1","inv-2",404,RUN,ORIGIN);assertEquals(ContractStatus.INCONCLUSIVE,evaluate(List.of(pass()),List.of(wrong),true).status());}
    @Test void missingRunIdIsRejected(){assertThrows(IllegalArgumentException.class,()->new TestEvidence("t1",TestEvidence.Outcome.PASSED,"","inv-1","",ORIGIN));}
    @Test void duplicateEvidenceIsInconclusive(){assertEquals(ContractStatus.INCONCLUSIVE,evaluate(List.of(pass(),pass()),List.of(observed(404)),true).status());}
    @Test void duplicateScenarioEvidenceIsInconclusive(){assertEquals(ContractStatus.INCONCLUSIVE,evaluate(List.of(pass()),List.of(observed(404),observed(404)),true).status());}
    @Test void conflictingDuplicateEvidenceIsInconclusive(){TestEvidence conflict=new TestEvidence("t1",TestEvidence.Outcome.FAILED,"failure","inv-1",RUN,ORIGIN);assertEquals(ContractStatus.INCONCLUSIVE,evaluate(List.of(pass(),conflict),List.of(observed(404)),true).status());}
    @Test void ignoredOrNotExecutedTestIsInconclusive(){TestEvidence skipped=new TestEvidence("t1",TestEvidence.Outcome.SKIPPED,"disabled","inv-disabled",RUN,ORIGIN);assertEquals(ContractStatus.INCONCLUSIVE,evaluate(List.of(skipped),List.of(),true).status());assertEquals(ContractStatus.INCONCLUSIVE,evaluate(List.of(),List.of(),true).status());}
    @Test void interruptedPartialRunIsInconclusive(){assertEquals(ContractStatus.INCONCLUSIVE,evaluate(List.of(pass()),List.of(observed(404)),false).status());}
    @Test void executedFailureFailsWhenRunIsSealed(){TestEvidence failed=new TestEvidence("t1",TestEvidence.Outcome.FAILED,"AssertionError","inv-1",RUN,ORIGIN);assertEquals(ContractStatus.FAILED,evaluate(List.of(failed),List.of(),true).status());}
    @Test void emptyMappingIsUnverifiedEvenWhenNoRunCanBeProved(){assertEquals(ContractStatus.UNVERIFIED,evaluator.evaluate(List.of(contract(Set.of())),List.of(),List.of(),List.of(),"","",false).getFirst().status());}
    @Test void malformedAndTruncatedRowsAreRejected(){String valid=EvidenceCodec.row(RUN,ORIGIN,"t1","PASSED","inv-1","");assertEquals(6,EvidenceCodec.fields(valid,6).length);assertThrows(IllegalArgumentException.class,()->EvidenceCodec.fields(valid.substring(0,valid.length()-2),6));assertThrows(IllegalArgumentException.class,()->EvidenceCodec.fields("not-base64",6));}
    @Test void duplicateContractIdsRejected(){assertThrows(IllegalArgumentException.class,()->evaluator.evaluate(List.of(contract(Set.of()),contract(Set.of())),List.of(),List.of(),List.of(),RUN,ORIGIN,true));}
}
