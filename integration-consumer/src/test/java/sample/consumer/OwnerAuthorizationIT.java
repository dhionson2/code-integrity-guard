package sample.consumer;

import io.github.dhionson2.cig.testkit.AuthorizationChecks;
import io.github.dhionson2.cig.testkit.ContractTest;
import io.github.dhionson2.cig.testkit.EvidenceExtension;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;

/** Exercises the same on-disk evidence protocol through a Failsafe integration-test fork. */
@ExtendWith(EvidenceExtension.class)
class OwnerAuthorizationIT {
    private final MockMvc mvc=MockMvcBuilders.standaloneSetup(new ConsumerResourceController()).build();
    @Test @ContractTest("consumer.authz.ownerCheck.failsafe")
    void aliceCannotReadBobsResourceInFailsafe() throws Exception {
        AuthorizationChecks.verifyScenario("consumer.aliceReadsBobsResource.failsafe",mvc,get("/resource/43").header("X-Actor","alice"));
    }
}
