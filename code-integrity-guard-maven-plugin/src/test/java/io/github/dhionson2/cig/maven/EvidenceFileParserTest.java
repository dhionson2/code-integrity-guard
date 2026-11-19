package io.github.dhionson2.cig.maven;

import io.github.dhionson2.cig.core.*;
import org.junit.jupiter.api.Test;
import java.nio.file.*;
import static org.junit.jupiter.api.Assertions.*;

class EvidenceFileParserTest {
    private static final String RUN="13c5338a-8f17-4be4-a3cd-7d963bf13d2e";
    @Test void acceptsCompleteVersionTwoExecutionEvidence()throws Exception{Path f=Files.createTempFile("cig-evidence",".tsv");try{Files.writeString(f,EvidenceCodec.row(RUN,"g:a:1","test", "PASSED","pid-uuid-junit","")+"\n");var read=EvidenceFileParser.tests(f);assertTrue(read.valid());assertEquals(1,read.records().size());assertEquals(RUN,read.records().getFirst().runId());}finally{Files.deleteIfExists(f);}}
    @Test void truncatedLastRecordIsInconclusiveInput()throws Exception{Path f=Files.createTempFile("cig-truncated",".tsv");try{Files.writeString(f,EvidenceCodec.row(RUN,"g:a:1","test","PASSED","inv",""));assertFalse(EvidenceFileParser.tests(f).valid());}finally{Files.deleteIfExists(f);}}
    @Test void malformedRecordAndMissingRunIdAreRejected()throws Exception{Path f=Files.createTempFile("cig-invalid",".tsv");try{Files.writeString(f,"not-valid-base64\n");assertFalse(EvidenceFileParser.tests(f).valid());Files.writeString(f,EvidenceCodec.row("","g:a:1","test","PASSED","inv"," ")+"\n");assertFalse(EvidenceFileParser.tests(f).valid());}finally{Files.deleteIfExists(f);}}
    @Test void legacyAndWrongColumnCountAreRejected()throws Exception{Path f=Files.createTempFile("cig-legacy",".tsv");try{Files.writeString(f,"test\tPASSED\tinv\t\n");assertFalse(EvidenceFileParser.tests(f).valid());}finally{Files.deleteIfExists(f);}}
    @Test void blankRowsAreRejectedInsteadOfSilentlyIgnored()throws Exception{Path f=Files.createTempFile("cig-blank",".tsv");try{String row=EvidenceCodec.row(RUN,"g:a:1","test","PASSED","inv","");Files.writeString(f,row+"\n\n");assertFalse(EvidenceFileParser.tests(f).valid());}finally{Files.deleteIfExists(f);}}
}
