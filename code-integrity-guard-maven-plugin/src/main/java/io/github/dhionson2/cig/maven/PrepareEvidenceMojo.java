package io.github.dhionson2.cig.maven;

import org.apache.maven.execution.MavenSession;
import org.apache.maven.project.MavenProject;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugins.annotations.*;
import java.io.IOException;
import java.nio.file.*;
import java.util.*;

/** Starts a fresh, module-scoped evidence run before tests; removes stale evidence even without clean. */
@Mojo(name="prepare-evidence", defaultPhase=LifecyclePhase.INITIALIZE, threadSafe=true)
public final class PrepareEvidenceMojo extends AbstractMojo {
    @Parameter(defaultValue="${project}",readonly=true,required=true) private MavenProject project;
    @Parameter(defaultValue="${session}",readonly=true,required=true) private MavenSession session;
    @Parameter(defaultValue="${project.build.directory}/cig-evidence-run.properties") private java.io.File runManifestFile;
    @Parameter(defaultValue="${project.build.directory}/cig-evidence.tsv") private java.io.File evidenceFile;
    @Parameter(defaultValue="${project.build.directory}/cig-scenario-evidence.tsv") private java.io.File scenarioEvidenceFile;
    @Parameter(defaultValue="${project.build.directory}/pit-reports/mutations.xml") private java.io.File pitFile;
    @Override public void execute() throws MojoExecutionException {
        try { Path manifest=runManifestFile.toPath().toAbsolutePath().normalize(), tests=evidenceFile.toPath().toAbsolutePath().normalize(), scenarios=scenarioEvidenceFile.toPath().toAbsolutePath().normalize();
            if(session.getStartTime()==null)throw new IOException("Maven session start time is unavailable; evidence freshness cannot be established");
            Files.createDirectories(tests.getParent());Files.createDirectories(scenarios.getParent());Files.deleteIfExists(tests);Files.deleteIfExists(scenarios);Files.deleteIfExists(pitFile.toPath().toAbsolutePath().normalize());
            Properties p=new Properties();p.setProperty("format",EvidenceRunStore.FORMAT);p.setProperty("runId",UUID.randomUUID().toString());p.setProperty("projectKey",project.getGroupId()+":"+project.getArtifactId()+":"+project.getVersion());p.setProperty("sessionStart",Long.toString(session.getStartTime().getTime()));p.setProperty("sessionKey",SealEvidenceMojo.sessionKey(session));p.setProperty("mavenPid",Long.toString(ProcessHandle.current().pid()));p.setProperty("state","OPEN");p.setProperty("testEvidence",tests.toString());p.setProperty("scenarioEvidence",scenarios.toString());EvidenceRunStore.write(manifest,p);
            getLog().info("Started fresh Code Integrity Guard evidence run "+p.getProperty("runId")+" for "+p.getProperty("projectKey"));
        } catch(Exception e){throw new MojoExecutionException("Could not initialize fresh evidence run: "+e.getMessage(),e);}
    }
}
