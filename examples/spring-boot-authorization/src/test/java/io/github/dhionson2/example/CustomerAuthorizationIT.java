package io.github.dhionson2.example;

import io.github.dhionson2.cig.testkit.AuthorizationChecks;
import io.github.dhionson2.cig.testkit.ContractTest;
import io.github.dhionson2.cig.testkit.EvidenceExtension;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;

/** Opt-in Failsafe diagnostic proving that vulnerable cross-owner access remains a real failure. */
@SpringBootTest @AutoConfigureMockMvc @ActiveProfiles("vulnerable") @ExtendWith(EvidenceExtension.class)
class CustomerAuthorizationIT {
    @Autowired MockMvc mvc;
    @Test @Tag("diagnostic-vulnerable") @ContractTest("customer.ownerCrossAccessFixed")
    void vulnerableScenarioFailsWhenAliceReadsBobsCustomerInFailsafe() throws Exception {
        AuthorizationChecks.verifyScenario("customer.aliceReadsBobsCustomer",mvc,get("/clientes/b").with(user("alice").roles("CUSTOMER")));
    }
}
