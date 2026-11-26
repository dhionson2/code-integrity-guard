package sample.consumer;

import io.github.dhionson2.cig.testkit.ContractTest;
import io.github.dhionson2.cig.testkit.EvidenceExtension;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

@ExtendWith(EvidenceExtension.class)
class InconclusiveContractTest {
    @Test
    @Disabled("Invoker fixture for the inconclusive evidence gate")
    @ContractTest("invoker.consumerContract")
    void disabledEvidenceIsInconclusive() {
        throw new AssertionError("disabled contract unexpectedly ran");
    }
}
