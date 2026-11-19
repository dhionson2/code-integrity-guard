package io.github.dhionson2.cig.report;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.github.dhionson2.cig.core.*;
import io.github.dhionson2.cig.pit.PitReport;
import java.io.IOException;
import java.nio.file.*;
import java.util.*;

/** Writes JSON via Jackson and independently escaped HTML under the output directory. */
public final class ReportWriter {
    private static final ObjectMapper JSON = new ObjectMapper();

    public void write(AuditReport report, Path directory) throws IOException {
        Path dir=directory.toAbsolutePath().normalize(); Files.createDirectories(dir);
        writeSafe(dir,"report.json",json(report)); writeSafe(dir,"index.html",html(report));
    }
    private void writeSafe(Path dir,String name,String contents)throws IOException{
        Path dest=dir.resolve(name).normalize();if(!dest.getParent().equals(dir))throw new IOException("Report path escapes output directory");
        Files.writeString(dest,contents,StandardOpenOption.CREATE,StandardOpenOption.TRUNCATE_EXISTING);
    }
    private String json(AuditReport report)throws IOException {
        Map<String,Object> root=map("schemaVersion","1.2","buildStatus",report.buildStatus(),"contractsEvaluation",report.contractsEvaluation(),"contractsManifestStatus",report.contractsManifestStatus(),"evidenceRun",map("runId",report.evidenceRunId(),"origin",report.evidenceOrigin()));
        List<Object> contracts=new ArrayList<>();for(ContractResult c:report.contracts()){
            List<Object> executions=new ArrayList<>();for(TestEvidence e:c.evidence())executions.add(map("testId",e.testId(),"runId",e.runId(),"origin",e.origin(),"executionId",e.executionId(),"outcome",name(e.outcome()),"detail",e.detail()));
            List<Object> policies=new ArrayList<>();for(SecurityScenario p:c.scenarios())policies.add(map("scenarioId",p.id(),"testId",p.testId(),"type",p.type(),"actor",p.actor(),"resourceOwner",p.resourceOwner(),"resource",p.resource(),"operation",p.operation(),"actorHeader",p.actorHeader(),"expectedStatus",p.expectedStatus()));
            List<Object> observations=new ArrayList<>();for(ScenarioEvidence e:c.scenarioEvidence())observations.add(map("scenarioId",e.scenarioId(),"testId",e.testId(),"runId",e.runId(),"origin",e.origin(),"executionId",e.executionId(),"observedStatus",e.observedStatus()));
            contracts.add(map("id",c.contractId(),"status",name(c.status()),"explanation",c.explanation(),"executionEvidence",executions,"declaredScenarios",policies,"scenarioEvidence",observations));
        }
        root.put("contracts",contracts);
        List<Object> mutations=new ArrayList<>();for(PitReport.Mutation m:report.pit().mutations())mutations.add(map("class",m.className(),"method",m.mutatedMethod(),"mutator",m.mutator(),"description",m.description(),"status",m.status(),"killingTest",m.killingTest()));
        root.put("pit",map("status",name(report.pit().status()),"scope",report.pit().scope(),"expectedScope",report.pit().expectedScope(),"partialCoverage",report.pit().partialCoverage(),"detected",report.pit().detected(),"total",report.pit().total(),"excluded",report.pit().excluded(),"score",report.pit().score(),"reason",report.pit().reason(),"mutations",mutations));
        root.put("gates",report.gates());List<String> limitations=new ArrayList<>();limitations.add("A passing audit does not prove the application secure and does not infer code authorship.");limitations.addAll(report.limitations());root.put("limitations",limitations);
        try{return JSON.writeValueAsString(root);}catch(JsonProcessingException e){throw new IOException("Could not serialize audit JSON",e);}
    }
    private String html(AuditReport r){
        StringBuilder b=new StringBuilder("<!doctype html><html lang=\"en\"><meta charset=\"utf-8\"><title>Code Integrity Guard</title><h1>Code Integrity Guard audit</h1><p>Build execution: ").append(h(r.buildStatus())).append("</p><p>Contract evaluation: ").append(h(r.contractsEvaluation())).append("; manifest: ").append(h(r.contractsManifestStatus())).append("</p><p>Evidence run: ").append(h(r.evidenceRunId())).append("; origin: ").append(h(r.evidenceOrigin())).append("</p><h2>Contracts</h2><ul>");
        if(r.contracts().isEmpty())b.append("<li>No security contracts were evaluated.</li>");
        for(ContractResult c:r.contracts()){b.append("<li><b>").append(h(c.contractId())).append(" — ").append(h(name(c.status()))).append("</b>: ").append(h(c.explanation()));for(TestEvidence e:c.evidence())b.append("<ul><li>JUnit execution: ").append(h(e.testId())).append(" — ").append(h(name(e.outcome()))).append(" [").append(h(e.executionId())).append("] detail: ").append(h(e.detail())).append("</li></ul>");for(SecurityScenario p:c.scenarios())b.append("<ul><li>Policy ").append(h(p.id())).append(" (").append(h(p.type())).append("): ").append(h(p.actor())).append(" on ").append(h(p.resource())).append(" via ").append(h(p.operation())).append(" expects HTTP ").append(p.expectedStatus()).append("</li></ul>");for(ScenarioEvidence e:c.scenarioEvidence()){SecurityScenario p=c.scenarios().stream().filter(x->Objects.equals(x.id(),e.scenarioId())).findFirst().orElse(null);b.append("<ul><li>Observed scenario ").append(h(e.scenarioId())).append(" via ").append(h(e.testId())).append(" [").append(h(e.executionId())).append("]: HTTP ").append(e.observedStatus());if(p!=null)b.append(" (expected ").append(p.expectedStatus()).append(')');b.append("</li></ul>");}b.append("</li>");}
        b.append("</ul><h2>PIT</h2><p>Status: ").append(h(name(r.pit().status()))).append(". Scope in XML: ").append(h(r.pit().scope())).append(". Expected scope: ").append(h(String.join(", ",r.pit().expectedScope()))).append(". partialCoverage metadata: ").append(h(String.valueOf(r.pit().partialCoverage()))).append(" (PIT coverage metadata, not an execution-completeness flag). Score: ").append(r.pit().detected()).append(" / ").append(r.pit().total()).append(" (excluded NON_VIABLE: ").append(r.pit().excluded()).append("). ").append(h(r.pit().reason())).append("</p><table><thead><tr><th>Class</th><th>Method</th><th>Mutator</th><th>Status</th><th>Test</th></tr></thead><tbody>");for(PitReport.Mutation m:r.pit().mutations())b.append("<tr><td>").append(h(m.className())).append("</td><td>").append(h(m.mutatedMethod())).append("</td><td>").append(h(m.mutator())).append("</td><td>").append(h(m.status())).append("</td><td>").append(h(m.killingTest())).append("</td></tr>");b.append("</tbody></table><h2>Quality gates</h2><ul>");for(String g:r.gates())b.append("<li>").append(h(g)).append("</li>");b.append("</ul><h2>Limitations</h2><ul><li>A passing audit does not prove an application secure or infer code authorship.</li>");for(String l:r.limitations())b.append("<li>").append(h(l)).append("</li>");return b.append("</ul></html>").toString();
    }
    private static Map<String,Object> map(Object... values){Map<String,Object> map=new LinkedHashMap<>();for(int i=0;i<values.length;i+=2)map.put((String)values[i],values[i+1]);return map;}
    private static String name(Enum<?> value){return value==null?null:value.name();}
    private static String h(String s){if(s==null)return "";return s.replace("&","&amp;").replace("<","&lt;").replace(">","&gt;").replace("\"","&quot;").replace("'","&#39;");}
}
