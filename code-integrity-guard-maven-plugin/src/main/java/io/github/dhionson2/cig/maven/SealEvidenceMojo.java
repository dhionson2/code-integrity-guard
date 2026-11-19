package io.github.dhionson2.cig.maven;

import org.apache.maven.execution.MavenSession;
import org.apache.maven.project.MavenProject;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugins.annotations.*;
import java.io.IOException;
import java.nio.file.*;
import java.util.*;

/** Closes evidence only after Maven test goals have returned successfully in this session. */
@Mojo(name="seal-evidence", defaultPhase=LifecyclePhase.VERIFY, threadSafe=true)
public final class SealEvidenceMojo extends AbstractMojo {
    @Parameter(defaultValue="${project}",readonly=true,required=true) private MavenProject project;
    @Parameter(defaultValue="${session}",readonly=true,required=true) private MavenSession session;
    @Parameter(defaultValue="${project.build.directory}/cig-evidence-run.properties") private java.io.File runManifestFile;
    @Override public void execute() throws MojoExecutionException {
        try {Path file=runManifestFile.toPath().toAbsolutePath().normalize();Properties p=EvidenceRunStore.read(file);validateSession(p);
            if(!"OPEN".equals(p.getProperty("state")))throw new IOException("Evidence run is not OPEN");Path tests=Path.of(EvidenceRunStore.required(p,"testEvidence")), scenarios=Path.of(EvidenceRunStore.required(p,"scenarioEvidence"));
            // Locks serialize concurrent Surefire/Failsafe fork writes with sealing.
            EvidenceRunStore.lock(tests,testChannel->EvidenceRunStore.lock(scenarios,scenarioChannel->{p.setProperty("testSha256",EvidenceRunStore.sha256(testChannel));p.setProperty("scenarioSha256",EvidenceRunStore.sha256(scenarioChannel));p.setProperty("state","SEALED");p.setProperty("sealedAt",Long.toString(System.currentTimeMillis()));EvidenceRunStore.write(file,p);}));
            getLog().info("Sealed Code Integrity Guard evidence run "+p.getProperty("runId"));
        }catch(Exception e){throw new MojoExecutionException("Could not seal complete evidence run: "+e.getMessage(),e);}
    }
    static String sessionKey(MavenSession session){return ProcessHandle.current().pid()+":"+session.getStartTime().getTime()+":"+System.identityHashCode(session);}
    static void validateSession(Properties p,MavenSession session,MavenProject project) throws IOException {if(session.getStartTime()==null||!Long.toString(session.getStartTime().getTime()).equals(p.getProperty("sessionStart"))||!sessionKey(session).equals(p.getProperty("sessionKey")))throw new IOException("Evidence belongs to another Maven invocation");String key=project.getGroupId()+":"+project.getArtifactId()+":"+project.getVersion();if(!key.equals(p.getProperty("projectKey")))throw new IOException("Evidence belongs to another Maven project/module");EvidenceRunStore.required(p,"runId");}
    private void validateSession(Properties p) throws IOException {validateSession(p,session,project);}
}
