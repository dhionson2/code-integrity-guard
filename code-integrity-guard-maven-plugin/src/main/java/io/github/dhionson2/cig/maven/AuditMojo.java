package io.github.dhionson2.cig.maven;

import io.github.dhionson2.cig.core.*;
import io.github.dhionson2.cig.pit.*;
import io.github.dhionson2.cig.report.*;
import org.apache.maven.execution.MavenSession;
import org.apache.maven.project.MavenProject;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.*;
import java.io.*;
import java.nio.file.*;
import java.time.Instant;
import java.util.*;

/** Evaluates only evidence sealed for this project and this Maven invocation. */
@Mojo(name="audit", defaultPhase=LifecyclePhase.VERIFY, threadSafe=true)
public final class AuditMojo extends AbstractMojo {
    @Parameter(defaultValue="${project}",readonly=true,required=true) private MavenProject project;
    @Parameter(defaultValue="${session}",readonly=true,required=true) private MavenSession session;
    @Parameter(defaultValue="${project.basedir}/.cig/contracts.tsv") private File contractsFile;
    @Parameter(defaultValue="${project.build.directory}/cig-evidence.tsv") private File evidenceFile;
    @Parameter(defaultValue="${project.basedir}/.cig/scenarios.tsv") private File scenariosFile;
    @Parameter(defaultValue="${project.build.directory}/cig-scenario-evidence.tsv") private File scenarioEvidenceFile;
    @Parameter(defaultValue="${project.build.directory}/cig-evidence-run.properties") private File runManifestFile;
    @Parameter(defaultValue="${project.build.directory}/pit-reports/mutations.xml") private File pitFile;
    @Parameter(defaultValue="${project.build.directory}/code-integrity-guard") private File outputDirectory;
    @Parameter(defaultValue="true") private boolean failOnFailedContract;
    @Parameter(defaultValue="false") private boolean failOnUnverified;
    @Parameter(defaultValue="false") private boolean failOnInconclusive;
    @Parameter(property="cig.requirePit",defaultValue="false") private boolean requirePit;
    @Parameter(property="cig.requireContracts",defaultValue="true") private boolean requireContracts;
    @Parameter(property="cig.minimumMutationPercent",defaultValue="0") private int minimumMutationPercent;
    @Parameter private List<String> pitExpectedClasses = List.of();

    @Override public void execute() throws MojoExecutionException,MojoFailureException {
        try {
            if(minimumMutationPercent<0||minimumMutationPercent>100)throw new IOException("minimumMutationPercent must be between 0 and 100");
            TreeSet<String> expectedPitScope=new TreeSet<>();
            for(String className:pitExpectedClasses){if(className==null||className.isBlank()||!expectedPitScope.add(className))throw new IOException("pitExpectedClasses must contain unique, non-blank class names");}
            List<String> limitations=new ArrayList<>();ContractManifestReader.Result manifest=ContractManifestReader.read(contractsFile.toPath());List<SecurityContract> contracts=manifest.contracts();
            List<SecurityScenario> policies=manifest.status().equals("PRESENT")?readScenarios(scenariosFile.toPath()):List.of();
            boolean noContracts=manifest.status().equals("MISSING")||manifest.status().equals("EMPTY");
            boolean evaluateContracts=!noContracts;
            RunCheck run=evaluateContracts?checkRun():new RunCheck("","",false,0L,"Contracts were not configured; evidence was not evaluated");
            EvidenceFileParser.ReadResult<TestEvidence> tests=evaluateContracts?EvidenceFileParser.tests(evidenceFile.toPath()):new EvidenceFileParser.ReadResult<>(List.of(),true,"");
            EvidenceFileParser.ReadResult<ScenarioEvidence> observations=evaluateContracts?EvidenceFileParser.scenarios(scenarioEvidenceFile.toPath()):new EvidenceFileParser.ReadResult<>(List.of(),true,"");
            boolean complete=run.valid&&tests.valid()&&observations.valid();
            List<ContractResult> results=List.of();
            boolean failed=false;
            if(manifest.status().equals("INVALID")){failed=true;limitations.add("Contract manifest is invalid: "+manifest.problem());getLog().error("Contract manifest is invalid: "+manifest.problem());}
            else if(noContracts&&requireContracts){failed=true;limitations.add("No security contracts were evaluated; requireContracts=true.");getLog().error("No security contracts were evaluated (manifest "+manifest.status().toLowerCase(Locale.ROOT)+"); requireContracts=true.");}
            else if(noContracts){limitations.add("No security contracts were evaluated by explicit configuration (requireContracts=false).");getLog().warn("No security contracts were evaluated (manifest "+manifest.status().toLowerCase(Locale.ROOT)+"); contract validation was intentionally disabled.");}
            else {results=new ContractEvaluator().evaluate(contracts,tests.records(),policies,observations.records(),run.runId,run.origin,complete);}
            if(!run.valid){getLog().warn("Current evidence run cannot be proved: "+run.problem);limitations.add("Current evidence run cannot be proved: "+run.problem);}
            if(!tests.valid()){getLog().warn("JUnit evidence is malformed, truncated, or unavailable: "+tests.problem());limitations.add("JUnit evidence is malformed, truncated, or unavailable: "+tests.problem());}
            if(!observations.valid()){getLog().warn("Scenario evidence is malformed, truncated, or unavailable: "+observations.problem());limitations.add("Scenario evidence is malformed, truncated, or unavailable: "+observations.problem());}
            if(!Files.isRegularFile(scenariosFile.toPath()))limitations.add("Independent scenario policy manifest is missing.");
            PitXmlReader pitReader=new PitXmlReader();
            PitReport pit=run.valid?pitReader.read(pitFile.toPath(),Instant.ofEpochMilli(run.sessionStart),expectedPitScope)
                    :pitReader.read(pitFile.toPath(),null,expectedPitScope);
            boolean pitGateRequired=requirePit||minimumMutationPercent>0;
            if(pitGateRequired&&expectedPitScope.isEmpty())pit=pit.asInconclusive("PIT quality gate requires an explicit pitExpectedClasses scope.");
            if(pit.status()!=PitReport.Status.AVAILABLE)limitations.add("PIT analysis is inconclusive: "+pit.reason());
            List<String> gates=new ArrayList<>();if(manifest.status().equals("INVALID"))gates.add("Invalid contract manifest: "+manifest.problem());else if(failed&&noContracts)gates.add("No contracts were evaluated while requireContracts=true");
            for(ContractResult r:results){getLog().info("Contract "+r.contractId()+": "+r.status()+" — "+r.explanation());if(r.status()==ContractStatus.FAILED&&failOnFailedContract){failed=true;gates.add(r.contractId()+" failed: "+r.explanation());}if(r.status()==ContractStatus.UNVERIFIED&&(failOnUnverified||requireContracts)){failed=true;gates.add(r.contractId()+" is unverified: "+r.explanation());}if(r.status()==ContractStatus.INCONCLUSIVE&&(failOnInconclusive||requireContracts)){failed=true;gates.add(r.contractId()+" is inconclusive: "+r.explanation());}}
            if(pitGateRequired&&pit.status()!=PitReport.Status.AVAILABLE){failed=true;gates.add("PIT report required by configured gate but unavailable, stale, incomplete, or outside the declared scope; result is inconclusive");}
            if(pit.status()==PitReport.Status.AVAILABLE&&minimumMutationPercent>0&&(pit.total()==0||pit.score()*100<minimumMutationPercent)){failed=true;gates.add("Mutation score below configured minimum or denominator is zero");}
            if(!results.isEmpty())limitations.add("A passing audit verifies only declared fixture outcomes; evidence is not a cryptographic attestation.");new ReportWriter().write(new AuditReport(failed?"FAILED":"AUDIT_RECORDED",results,pit,gates,limitations,run.runId,run.origin,results.isEmpty()?"NOT_RUN":"EVALUATED",manifest.status()),outputDirectory.toPath());
            if(failed)throw new MojoFailureException("Code Integrity Guard quality gate failed. See report in "+outputDirectory);
        }catch(MojoFailureException e){throw e;}catch(Exception e){throw new MojoExecutionException("Code Integrity Guard could not produce an audit report: "+e.getMessage(),e);}
    }
    private RunCheck checkRun(){try{Properties p=EvidenceRunStore.read(runManifestFile.toPath());SealEvidenceMojo.validateSession(p,session,project);Path tests=evidenceFile.toPath().toAbsolutePath().normalize(),scenarios=scenarioEvidenceFile.toPath().toAbsolutePath().normalize();if(!tests.equals(Path.of(EvidenceRunStore.required(p,"testEvidence")))||!scenarios.equals(Path.of(EvidenceRunStore.required(p,"scenarioEvidence"))))throw new IOException("Evidence paths differ from prepared run");if(!"SEALED".equals(p.getProperty("state")))throw new IOException("Evidence run was not sealed after tests");if(!EvidenceRunStore.required(p,"testSha256").equals(EvidenceRunStore.sha256(tests))||!EvidenceRunStore.required(p,"scenarioSha256").equals(EvidenceRunStore.sha256(scenarios)))throw new IOException("Evidence changed after it was sealed");long start=Long.parseLong(EvidenceRunStore.required(p,"sessionStart"));return new RunCheck(p.getProperty("runId"),p.getProperty("projectKey"),true,start,"");}catch(Exception e){return new RunCheck("","",false,0L,e.getMessage());}}
    private static List<SecurityScenario> readScenarios(Path file)throws IOException{if(!Files.isRegularFile(file))return List.of();List<SecurityScenario> all=new ArrayList<>();Set<String> ids=new HashSet<>();for(String line:Files.readAllLines(file)){if(line.isBlank()||line.startsWith("#"))continue;String[] p=line.split("\t",-1);if(p.length!=10)throw new IOException("Malformed scenario policy");if(!ids.add(p[0]))throw new IOException("Duplicate scenario id: "+p[0]);all.add(new SecurityScenario(p[0],p[1],p[2],p[3],p[4],p[5],p[6],p[7],p[8],Integer.parseInt(p[9])));}return all;}
    private record RunCheck(String runId,String origin,boolean valid,long sessionStart,String problem){}
}
