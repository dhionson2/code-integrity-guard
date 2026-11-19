package io.github.dhionson2.cig.testkit;

import io.github.dhionson2.cig.core.TestEvidence;
import org.junit.jupiter.api.extension.*;
import org.opentest4j.TestAbortedException;
import java.io.IOException;
import java.util.*;
import java.util.UUID;

/** Emits versioned, run-bound JUnit invocation records to files shared by Maven forks. */
public final class EvidenceExtension implements BeforeTestExecutionCallback, AfterEachCallback, TestExecutionExceptionHandler, TestWatcher {
    private static final ExtensionContext.Namespace NS = ExtensionContext.Namespace.create(EvidenceExtension.class);
    private static final ThreadLocal<Invocation> CURRENT = new ThreadLocal<>();
    private static final String JVM_ID=ProcessHandle.current().pid()+"-"+UUID.randomUUID()+"-";
    record Invocation(Set<String> testIds,String executionId,EvidenceFiles.Run run) {}
    static Invocation current() { return CURRENT.get(); }
    static boolean evidenceDisabled(){return Boolean.getBoolean("cig.evidence.disabled");}
    static void scenario(String id,String testId,int status) throws IOException {
        Invocation i=CURRENT.get();if(i==null||!i.testIds().contains(testId))throw new IllegalStateException("Scenario test id is not linked to the active @ContractTest invocation");
        EvidenceFiles.append(i.run(),i.run().scenarioFile(),i.run().runId(),i.run().origin(),id,testId,i.executionId(),Integer.toString(status));
    }
    @Override public void beforeTestExecution(ExtensionContext context) throws Exception {
        if(evidenceDisabled())return;
        context.getStore(NS).put("started",true);ContractTest a=context.getRequiredTestMethod().getAnnotation(ContractTest.class);
        if(a!=null&&a.value().length>0){EvidenceFiles.Run run=EvidenceFiles.readRun();CURRENT.set(new Invocation(Set.of(a.value()),executionId(context),run));}
    }
    @Override public void handleTestExecutionException(ExtensionContext context,Throwable throwable)throws Throwable{context.getStore(NS).put("failure",throwable.getClass().getSimpleName());throw throwable;}
    @Override public void afterEach(ExtensionContext context)throws Exception{
        if(evidenceDisabled())return;
        try{ContractTest annotation=context.getRequiredTestMethod().getAnnotation(ContractTest.class);if(annotation==null||!Boolean.TRUE.equals(context.getStore(NS).get("started",Boolean.class)))return;
            String failure=context.getStore(NS).get("failure",String.class);if(failure==null)failure=context.getExecutionException().map(t->t.getClass().getSimpleName()).orElse(null);
            TestEvidence.Outcome outcome=failure==null?TestEvidence.Outcome.PASSED:failure.equals(TestAbortedException.class.getSimpleName())?TestEvidence.Outcome.SKIPPED:TestEvidence.Outcome.FAILED;
            EvidenceFiles.Run run=Optional.ofNullable(CURRENT.get()).map(Invocation::run).orElseGet(()->{try{return EvidenceFiles.readRun();}catch(IOException e){throw new EvidenceWriteFailure(e);}});
            String executionId=Optional.ofNullable(CURRENT.get()).map(Invocation::executionId).orElseGet(()->executionId(context));
            for(String id:annotation.value())EvidenceFiles.append(run,run.testFile(),run.runId(),run.origin(),id,outcome.name(),executionId,failure==null?"":failure);
        }catch(EvidenceWriteFailure e){throw e.getCause();}finally{CURRENT.remove();}
    }
    @Override public void testDisabled(ExtensionContext context,Optional<String> reason){if(!evidenceDisabled())recordSkipped(context,reason.orElse("disabled"));}
    private static String executionId(ExtensionContext context){String nonce=context.getRoot().getStore(NS).getOrComputeIfAbsent("engine-nonce",k->UUID.randomUUID().toString(),String.class);return JVM_ID+nonce+"-"+context.getUniqueId();}
    private void recordSkipped(ExtensionContext context,String reason){try{EvidenceFiles.Run run=EvidenceFiles.readRun();if(context.getTestMethod().isPresent()){appendSkipped(context.getTestMethod().orElseThrow().getAnnotation(ContractTest.class),executionId(context),reason,run);return;}for(var method:context.getRequiredTestClass().getDeclaredMethods()){ContractTest annotation=method.getAnnotation(ContractTest.class);if(annotation!=null)appendSkipped(annotation,executionId(context)+"-disabled-"+method.getName(),reason,run);}}catch(IOException e){throw new EvidenceWriteFailure(e);}}
    private void appendSkipped(ContractTest annotation,String id,String reason,EvidenceFiles.Run run)throws IOException{if(annotation==null)return;for(String testId:annotation.value())EvidenceFiles.append(run,run.testFile(),run.runId(),run.origin(),testId,TestEvidence.Outcome.SKIPPED.name(),id,reason);}
    private static final class EvidenceWriteFailure extends RuntimeException{EvidenceWriteFailure(IOException cause){super(cause);} @Override public synchronized IOException getCause(){return (IOException)super.getCause();}}
}
