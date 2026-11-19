package io.github.dhionson2.cig.maven;

import io.github.dhionson2.cig.core.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.util.*;

/** Strict reader for version 2 execution evidence; malformed files are unusable, never partially trusted. */
final class EvidenceFileParser {
    private EvidenceFileParser(){}
    static <T> ReadResult<T> read(Path file,int columns,RowParser<T> parser){
        if(!Files.isRegularFile(file))return new ReadResult<>(List.of(),false,"file is missing");
        try{byte[] bytes=Files.readAllBytes(file);if(bytes.length>0&&bytes[bytes.length-1]!='\n')return new ReadResult<>(List.of(),false,"last evidence row is truncated (missing newline)");String content=StandardCharsets.UTF_8.newDecoder().onMalformedInput(java.nio.charset.CodingErrorAction.REPORT).decode(java.nio.ByteBuffer.wrap(bytes)).toString();List<T> rows=new ArrayList<>();if(content.isEmpty())return new ReadResult<>(List.of(),true,"");String[] lines=content.split("\n",-1);for(int i=0;i<lines.length;i++){if(lines[i].isEmpty()){if(i==lines.length-1)continue;throw new IllegalArgumentException("Blank evidence row at line "+(i+1));}rows.add(parser.parse(EvidenceCodec.fields(lines[i],columns)));}return new ReadResult<>(List.copyOf(rows),true,"");}catch(Exception e){return new ReadResult<>(List.of(),false,e.getMessage());}
    }
    static ReadResult<TestEvidence> tests(Path file){return read(file,6,f->new TestEvidence(f[2],TestEvidence.Outcome.valueOf(f[3]),f[5],f[4],f[0],f[1]));}
    static ReadResult<ScenarioEvidence> scenarios(Path file){return read(file,6,f->new ScenarioEvidence(f[2],f[3],f[4],Integer.parseInt(f[5]),f[0],f[1]));}
    @FunctionalInterface interface RowParser<T>{T parse(String[] fields);}
    record ReadResult<T>(List<T> records,boolean valid,String problem){}
}
