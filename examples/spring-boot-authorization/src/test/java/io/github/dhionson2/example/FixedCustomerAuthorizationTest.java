package io.github.dhionson2.example;

import io.github.dhionson2.cig.testkit.ContractTest;
import io.github.dhionson2.cig.testkit.EvidenceExtension;
import io.github.dhionson2.cig.testkit.AuthorizationChecks;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.test.web.servlet.MockMvc;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest @AutoConfigureMockMvc @ActiveProfiles("fixed") @ExtendWith(EvidenceExtension.class)
class FixedCustomerAuthorizationTest {
    @Autowired MockMvc mvc;
    @Test @ContractTest("customer.ownerCrossAccessFixed")
    void crossOwnerAccessIsHidden() throws Exception { AuthorizationChecks.verifyScenario("customer.aliceReadsBobsCustomer",mvc,get("/clientes/b").with(user("alice").roles("CUSTOMER"))); }
    @Test void anonymousRequestIsRejected() throws Exception { mvc.perform(get("/clientes/a")).andExpect(status().isUnauthorized()); }
    @Test void ownerCanReadOwnCustomer() throws Exception { mvc.perform(get("/clientes/a").with(user("alice").roles("CUSTOMER"))).andExpect(status().isOk()); }
    @Test void insufficientRoleCannotAccessAdminEndpoint() throws Exception { mvc.perform(get("/clientes/admin").with(user("alice").roles("CUSTOMER"))).andExpect(status().isForbidden()); }
}
