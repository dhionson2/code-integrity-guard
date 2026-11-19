package io.github.dhionson2.cig.maven;

import org.junit.jupiter.api.Test;
import java.nio.file.*;
import static org.junit.jupiter.api.Assertions.*;

class ContractManifestReaderTest {
    @Test void distinguishesMissingEmptyInvalidAndPopulatedManifests() throws Exception {
        Path dir=Files.createTempDirectory("cig-contract-manifest"); Path file=dir.resolve("contracts.tsv");
        try {
            assertEquals("MISSING",ContractManifestReader.read(file).status());
            Files.writeString(file,"# no contracts configured\n\n");
            assertEquals("EMPTY",ContractManifestReader.read(file).status());
            Files.writeString(file,"broken\n");
            assertEquals("INVALID",ContractManifestReader.read(file).status());
            Files.writeString(file,"c1\tHIGH\tapi\tdesc\tt1\ts1\n");
            var result=ContractManifestReader.read(file); assertEquals("PRESENT",result.status()); assertEquals(1,result.contracts().size());
        } finally { Files.deleteIfExists(file); Files.deleteIfExists(dir); }
    }
}
