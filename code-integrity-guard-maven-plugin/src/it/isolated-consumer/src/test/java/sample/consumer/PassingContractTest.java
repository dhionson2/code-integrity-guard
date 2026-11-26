package sample.consumer;

import io.github.dhionson2.cig.testkit.ContractTest;
import io.github.dhionson2.cig.testkit.AuthorizationChecks;
import io.github.dhionson2.cig.testkit.EvidenceExtension;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;

@ExtendWith(EvidenceExtension.class)
class PassingContractTest {
    private final MockMvc mvc = MockMvcBuilders.standaloneSetup(new ConsumerResourceController()).build();

    @Test
    @ContractTest("invoker.consumerContract")
    void recordsPassingEvidence() throws Exception {
        AuthorizationChecks.verifyScenario("invoker.aliceReadsBobsResource", mvc,
                get("/resource/42").header("X-Actor", "alice"));
    }
}
