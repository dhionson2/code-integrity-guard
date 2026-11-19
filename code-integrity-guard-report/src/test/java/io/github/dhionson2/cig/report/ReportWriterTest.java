package io.github.dhionson2.cig.report;

import io.github.dhionson2.cig.core.*;
import io.github.dhionson2.cig.pit.PitReport;
import com.google.gson.JsonParser;
import org.junit.jupiter.api.Test;
import java.nio.file.*;
import java.util.*;
import static org.junit.jupiter.api.Assertions.*;

class ReportWriterTest {
    @Test void reportsDeclaredPolicyAndObservedScenarioSeparately() throws Exception {
        Path dir=Files.createTempDirectory("cig-scenario-report");try{
            SecurityScenario policy=new SecurityScenario("scenario-1","contract-1","test-1","CROSS_OWNER","alice","bob","/resource/42","GET /resource/42","X-Actor",404);
            ScenarioEvidence observation=new ScenarioEvidence("scenario-1","test-1","invocation-1",404,"run-1","sample:project:1");
            ContractResult contract=new ContractResult("contract-1",ContractStatus.VERIFIED,List.of(new TestEvidence("test-1",TestEvidence.Outcome.PASSED,"","invocation-1","run-1","sample:project:1")),List.of(policy),List.of(observation),"Matched policy response");
            new ReportWriter().write(new AuditReport("AUDIT_RECORDED",List.of(contract),new PitReport(PitReport.Status.INCONCLUSIVE,List.of(),0,0,"not run"),List.of(),List.of()),dir);
            String json=Files.readString(dir.resolve("report.json"));String html=Files.readString(dir.resolve("index.html"));
            assertTrue(json.contains("\"expectedStatus\":404"));assertTrue(json.contains("\"observedStatus\":404"));assertTrue(json.contains("\"actorHeader\":\"X-Actor\""));assertTrue(json.contains("\"runId\":\"run-1\""));assertTrue(html.contains("expected 404"));
        }finally{try(var paths=Files.walk(dir)){paths.sorted(Comparator.reverseOrder()).forEach(p->{try{Files.deleteIfExists(p);}catch(Exception ignored){}});}}
    }
    @Test void escapesUntrustedTextInHtmlAndJson() throws Exception {
        Path dir=Files.createTempDirectory("cig-report");try{
            ContractResult c=new ContractResult("id\"><script>alert(1)</script>",ContractStatus.VERIFIED,List.of(new TestEvidence("test",TestEvidence.Outcome.PASSED,"ok","inv","run","origin")),"<img src=x onerror=alert(1)>");
            PitReport pit=new PitReport(PitReport.Status.AVAILABLE,List.of(new PitReport.Mutation("x <script>","m","mutator","desc", "SURVIVED", "")),0,1,"parsed");
            new ReportWriter().write(new AuditReport("AUDIT_RECORDED",List.of(c),pit,List.of("gate <unsafe>"),List.of()),dir);
            String html=Files.readString(dir.resolve("index.html"));String json=Files.readString(dir.resolve("report.json"));
            assertFalse(html.contains("<script>alert"));assertTrue(html.contains("&lt;script&gt;"));assertTrue(json.contains("schemaVersion"));assertTrue(json.contains("gate <unsafe>"));
        }finally{try(var paths=Files.walk(dir)){paths.sorted(Comparator.reverseOrder()).forEach(p->{try{Files.deleteIfExists(p);}catch(Exception ignored){}});}}
    }
    @Test void noContractsIsReportedAsNotRunNotApproval() throws Exception {
        Path dir=Files.createTempDirectory("cig-no-contract-report");try{
            var report=new AuditReport("AUDIT_RECORDED",List.of(),new PitReport(PitReport.Status.INCONCLUSIVE,List.of(),0,0,"not run"),List.of(),List.of("No security contracts were evaluated"),"","","NOT_RUN","MISSING");
            new ReportWriter().write(report,dir);
            String json=Files.readString(dir.resolve("report.json"));String html=Files.readString(dir.resolve("index.html"));
            var parsed=JsonParser.parseString(json).getAsJsonObject();assertEquals("AUDIT_RECORDED",parsed.get("buildStatus").getAsString());assertEquals("NOT_RUN",parsed.get("contractsEvaluation").getAsString());assertEquals("MISSING",parsed.get("contractsManifestStatus").getAsString());assertEquals(0,parsed.getAsJsonArray("contracts").size());assertFalse(json.contains("VERIFIED"));
            assertTrue(html.contains("Contract evaluation: NOT_RUN; manifest: MISSING"));assertTrue(html.contains("No security contracts were evaluated"));assertFalse(html.contains("VERIFIED"));
        }finally{try(var paths=Files.walk(dir)){paths.sorted(Comparator.reverseOrder()).forEach(p->{try{Files.deleteIfExists(p);}catch(Exception ignored){}});}}
    }
    @Test void serializesPitCoverageMetadataAndExpectedMutationScope() throws Exception {
        Path dir=Files.createTempDirectory("cig-pit-report");try{
            PitReport pit=new PitReport(PitReport.Status.AVAILABLE,List.of(),7,11,0,
                    "io.github.dhionson2.example.CustomerController",true,
                    List.of("io.github.dhionson2.example.CustomerController"),"parsed complete PIT XML");
            new ReportWriter().write(new AuditReport("AUDIT_RECORDED",List.of(),pit,List.of(),List.of()),dir);
            var root=JsonParser.parseString(Files.readString(dir.resolve("report.json"))).getAsJsonObject();
            var parsedPit=root.getAsJsonObject("pit");
            assertEquals("1.2",root.get("schemaVersion").getAsString());
            assertTrue(parsedPit.get("partialCoverage").getAsBoolean());
            assertEquals(7,parsedPit.get("detected").getAsInt());assertEquals(11,parsedPit.get("total").getAsInt());
            assertEquals("io.github.dhionson2.example.CustomerController",parsedPit.getAsJsonArray("expectedScope").get(0).getAsString());
            String html=Files.readString(dir.resolve("index.html"));
            assertTrue(html.contains("partialCoverage metadata: true (PIT coverage metadata, not an execution-completeness flag)"));
        }finally{try(var paths=Files.walk(dir)){paths.sorted(Comparator.reverseOrder()).forEach(p->{try{Files.deleteIfExists(p);}catch(Exception ignored){}});}}
    }
    @Test void jacksonJsonRoundTripsAllControlsQuotesBackslashesUnicodeAndSpecialScenarioFields() throws Exception {
        Path dir=Files.createTempDirectory("cig-json-controls");try{
            StringBuilder controls=new StringBuilder();for(char c=0;c<=0x1f;c++)controls.append(c);String hostile=controls+" quotes \" slash \\ Unicode é 漢字 🙂 </script><script>alert(1)</script>";
            SecurityScenario policy=new SecurityScenario("scenario"+controls,"contract-1","test-1","CROSS_OWNER","alice"+hostile,"bob","/resource/"+hostile,"GET /resource/42","X-Actor\"\\<script>",404);
            ScenarioEvidence observation=new ScenarioEvidence(policy.id(),"test-1","inv-1",404,"run-1","sample:consumer:1");
            TestEvidence evidence=new TestEvidence("test-1",TestEvidence.Outcome.FAILED,hostile,"inv-1","run-1","sample:consumer:1");
            ContractResult contract=new ContractResult("contract-1"+controls,ContractStatus.FAILED,List.of(evidence),List.of(policy),List.of(observation),hostile);
            new ReportWriter().write(new AuditReport("FAILED",List.of(contract),new PitReport(PitReport.Status.AVAILABLE,List.of(new PitReport.Mutation(hostile,"m","mutator",hostile,"SURVIVED","test-1")),0,1,"partial"),List.of(hostile),List.of()),dir);
            String json=Files.readString(dir.resolve("report.json"));var root=JsonParser.parseString(json).getAsJsonObject();var c=root.getAsJsonArray("contracts").get(0).getAsJsonObject();
            assertEquals("contract-1"+controls,c.get("id").getAsString());assertEquals(hostile,c.get("explanation").getAsString());assertEquals(hostile,c.getAsJsonArray("executionEvidence").get(0).getAsJsonObject().get("detail").getAsString());
            var p=c.getAsJsonArray("declaredScenarios").get(0).getAsJsonObject();assertEquals("alice"+hostile,p.get("actor").getAsString());assertEquals("X-Actor\"\\<script>",p.get("actorHeader").getAsString());
            String html=Files.readString(dir.resolve("index.html"));assertFalse(html.contains("<script>alert(1)</script>"));assertTrue(html.contains("&lt;script&gt;alert(1)&lt;/script&gt;"));
        }finally{try(var paths=Files.walk(dir)){paths.sorted(Comparator.reverseOrder()).forEach(p->{try{Files.deleteIfExists(p);}catch(Exception ignored){}});}}
    }
    @Test void nullAndEmptyValuesRemainValidJson() throws Exception {
        Path dir=Files.createTempDirectory("cig-json-null");try{
            ContractResult contract=new ContractResult(null,null,List.of(new TestEvidence("t",TestEvidence.Outcome.PASSED,null,"i","r","o")),List.of(),List.of(),null);
            new ReportWriter().write(new AuditReport("AUDIT_RECORDED",List.of(contract),new PitReport(PitReport.Status.INCONCLUSIVE,List.of(),0,0,""),List.of(""),List.of(),null,null),dir);
            var root=JsonParser.parseString(Files.readString(dir.resolve("report.json"))).getAsJsonObject();var c=root.getAsJsonArray("contracts").get(0).getAsJsonObject();assertTrue(c.get("id").isJsonNull());assertTrue(c.get("explanation").isJsonNull());assertEquals("",c.getAsJsonArray("executionEvidence").get(0).getAsJsonObject().get("detail").getAsString());assertEquals("",root.getAsJsonObject("evidenceRun").get("runId").getAsString());
        }finally{try(var paths=Files.walk(dir)){paths.sorted(Comparator.reverseOrder()).forEach(p->{try{Files.deleteIfExists(p);}catch(Exception ignored){}});}}
    }
}
