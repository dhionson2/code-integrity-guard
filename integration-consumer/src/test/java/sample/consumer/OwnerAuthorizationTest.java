package sample.consumer;

import io.github.dhionson2.cig.testkit.ContractTest;
import io.github.dhionson2.cig.testkit.EvidenceExtension;
import io.github.dhionson2.cig.testkit.AuthorizationChecks;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.web.bind.annotation.*;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;

@RestController
class ConsumerResourceController {
    @GetMapping("/resource/{id}") Object read(@PathVariable("id") String id,@RequestHeader("X-Actor") String actor) {
        if (!actor.equals("bob")) return org.springframework.http.ResponseEntity.notFound().build();
        return java.util.Map.of("id",id,"owner","bob");
    }
}

@ExtendWith(EvidenceExtension.class)
class OwnerAuthorizationTest {
    private final MockMvc mvc=MockMvcBuilders.standaloneSetup(new ConsumerResourceController()).build();
    @Test @ContractTest("consumer.authz.ownerCheck")
    void aliceCannotReadBobsResource() throws Exception { AuthorizationChecks.verifyScenario("consumer.aliceReadsBobsResource",mvc,get("/resource/42").header("X-Actor","alice").with(user("alice"))); }
    @Test @Disabled("Regression: disabled contract test must emit SKIPPED evidence") @ContractTest("consumer.disabled.regression")
    void disabledContractTestIsNotPassingEvidence() { throw new AssertionError("disabled test unexpectedly ran"); }
}
