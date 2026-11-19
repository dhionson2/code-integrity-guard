package io.github.dhionson2.cig.testkit;

import org.springframework.test.web.servlet.ResultMatcher;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.RequestBuilder;
import io.github.dhionson2.cig.core.SecurityScenario;
import java.nio.file.*;
import java.util.*;
import java.security.Principal;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.Authentication;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/** Consumer-defined HTTP authorization expectations for MockMvc tests. */
public final class AuthorizationChecks {
    private AuthorizationChecks() {}
    public static ResultMatcher unauthenticated() { return status().isUnauthorized(); }
    public static ResultMatcher unauthenticated(int expectedStatus) { return status().is(expectedStatus); }
    public static ResultMatcher forbidden() { return status().isForbidden(); }
    public static ResultMatcher forbiddenOrNotFound() { return concealedOrForbidden(); }
    public static ResultMatcher concealedOrForbidden() { return result -> {
        int actual = result.getResponse().getStatus();
        if (actual != 403 && actual != 404) throw new AssertionError("Expected HTTP 403 or 404, got " + actual);
    }; }
    /** Executes the request and records its actual response against policy loaded from the independent TSV manifest. */
    public static void verifyScenario(String scenarioId, MockMvc mvc, RequestBuilder request) throws Exception {
        EvidenceExtension.Invocation invocation=EvidenceExtension.current();
        boolean evidenceDisabled=EvidenceExtension.evidenceDisabled();
        if(invocation==null&&!evidenceDisabled) throw new IllegalStateException("verifyScenario must run inside an active @ContractTest invocation");
        SecurityScenario policy=loadPolicies().stream().filter(p->p.id().equals(scenarioId)).findFirst().orElseThrow(()->new IllegalStateException("Unknown scenario policy: "+scenarioId));
        if(invocation!=null&&!invocation.testIds().contains(policy.testId())) throw new IllegalStateException("Scenario is not assigned to this @ContractTest");
        var result=mvc.perform(request).andReturn();
        String actualOperation=result.getRequest().getMethod()+" "+result.getRequest().getRequestURI();
        String[] expected=policy.operation().split(" ",2);
        if(!expected[0].equals(result.getRequest().getMethod())||!expected[1].equals(result.getRequest().getRequestURI())) throw new AssertionError("Scenario request differs from declared operation: "+actualOperation);
        if(policy.type().equals("CROSS_OWNER")) { Principal principal=result.getRequest().getUserPrincipal(); String actor=policy.actorHeader().isBlank()?principalName(result.getRequest(),principal):result.getRequest().getHeader(policy.actorHeader()); if(!policy.actor().equals(actor)||policy.actor().equals(policy.resourceOwner())) throw new AssertionError("Cross-owner scenario authenticated actor/owner does not match policy; expected="+policy.actor()+", actual="+actor); }
        if(!evidenceDisabled)EvidenceExtension.scenario(scenarioId,policy.testId(),result.getResponse().getStatus());
        if(result.getResponse().getStatus()!=policy.expectedStatus()) throw new AssertionError("Scenario "+scenarioId+" expected HTTP "+policy.expectedStatus()+" but got "+result.getResponse().getStatus());
    }
    private static String principalName(jakarta.servlet.http.HttpServletRequest request,Principal principal){
        if(principal!=null)return principal.getName();Object context=request.getAttribute("SPRING_SECURITY_CONTEXT");
        if(context instanceof SecurityContext securityContext){Authentication authentication=securityContext.getAuthentication();if(authentication!=null&&authentication.isAuthenticated())return authentication.getName();}
        var session=request.getSession(false);if(session!=null){Object stored=session.getAttribute("SPRING_SECURITY_CONTEXT");if(stored instanceof SecurityContext securityContext){Authentication authentication=securityContext.getAuthentication();if(authentication!=null&&authentication.isAuthenticated())return authentication.getName();}}
        return null;
    }
    private static List<SecurityScenario> loadPolicies() throws Exception {
        String configured=System.getProperty("cig.scenarios.file", ".cig/scenarios.tsv"); Path file=Path.of(configured);
        if(!Files.isRegularFile(file)) throw new IllegalStateException("Independent scenario manifest is missing: "+file);
        List<SecurityScenario> policies=new ArrayList<>();
        for(String line:Files.readAllLines(file)){if(line.isBlank()||line.startsWith("#"))continue;String[] p=line.split("\\t",-1);if(p.length!=10)throw new IllegalStateException("Malformed scenario policy row; expected 10 tab-separated fields");policies.add(new SecurityScenario(p[0],p[1],p[2],p[3],p[4],p[5],p[6],p[7],p[8],Integer.parseInt(p[9])));}
        return policies;
    }
}
