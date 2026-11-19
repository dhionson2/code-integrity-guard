package io.github.dhionson2.cig.testkit;

import io.github.dhionson2.cig.core.EvidenceCodec;
import java.io.*;
import java.nio.ByteBuffer;
import java.nio.channels.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.util.*;

/** Cross-process append protocol. Every complete TSV row is written under an OS file lock. */
final class EvidenceFiles {
    private EvidenceFiles() {}
    static Run readRun() throws IOException {
        String name=System.getProperty("cig.run.manifest.file");if(name==null||name.isBlank())throw new IOException("cig.run.manifest.file is not configured");Path path=Path.of(name).toAbsolutePath().normalize();Properties p=new Properties();try(InputStream in=Files.newInputStream(path)){p.load(in);}
        if(!"2".equals(p.getProperty("format"))||!"OPEN".equals(p.getProperty("state")))throw new IOException("Evidence run manifest is missing, unsupported, or not OPEN");String runId=required(p,"runId"), origin=required(p,"projectKey");UUID.fromString(runId);long mavenPid=Long.parseLong(required(p,"mavenPid"));if(!isDescendant(ProcessHandle.current(),mavenPid))throw new IOException("Test JVM is not owned by the Maven process that prepared this evidence run");return new Run(runId,origin,Path.of(required(p,"testEvidence")).toAbsolutePath().normalize(),Path.of(required(p,"scenarioEvidence")).toAbsolutePath().normalize());
    }
    static void append(Run run,Path file,String... fields) throws IOException {
        synchronized(EvidenceFiles.class){Files.createDirectories(file.getParent());byte[] bytes=(EvidenceCodec.row(fields)+"\n").getBytes(StandardCharsets.UTF_8);try(FileChannel channel=FileChannel.open(file,StandardOpenOption.CREATE,StandardOpenOption.WRITE,StandardOpenOption.APPEND);FileLock ignored=channel.lock()){
            Run current=readRun();if(!run.runId.equals(current.runId)||!run.origin.equals(current.origin)||!file.equals(current.testFile)&&!file.equals(current.scenarioFile))throw new IOException("Evidence run changed while test invocation was active");ByteBuffer buffer=ByteBuffer.wrap(bytes);while(buffer.hasRemaining())channel.write(buffer);
        }}
    }
    private static String required(Properties p,String k)throws IOException{String v=p.getProperty(k);if(v==null||v.isBlank())throw new IOException("Missing evidence manifest field: "+k);return v;}
    private static boolean isDescendant(ProcessHandle process,long ancestorPid){for(int depth=0;depth<64;depth++){if(process.pid()==ancestorPid)return true;Optional<ProcessHandle> parent=process.parent();if(parent.isEmpty())return false;process=parent.orElseThrow();}return false;}
    record Run(String runId,String origin,Path testFile,Path scenarioFile){}
}
