package io.github.dhionson2.example;

import io.github.dhionson2.cig.testkit.EvidenceExtension;
import io.github.dhionson2.cig.testkit.ContractTest;
import io.github.dhionson2.cig.testkit.AuthorizationChecks;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.test.web.servlet.MockMvc;
import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest @AutoConfigureMockMvc @ActiveProfiles("vulnerable") @ExtendWith(EvidenceExtension.class)
class CustomerAuthorizationTest {
    @Autowired MockMvc mvc;
    @Test @Tag("diagnostic-vulnerable") @ContractTest("customer.ownerCrossAccessFixed")
    void vulnerableScenarioFailsWhenAliceReadsBobsCustomer() throws Exception {
        AuthorizationChecks.verifyScenario("customer.aliceReadsBobsCustomer",mvc,get("/clientes/b").with(user("alice").roles("CUSTOMER")));
    }
    @Test void vulnerableScenarioPositiveOwnerRequestStillPasses() throws Exception {
        mvc.perform(get("/clientes/a").with(user("alice").roles("CUSTOMER"))).andExpect(status().isOk());
    }
}
