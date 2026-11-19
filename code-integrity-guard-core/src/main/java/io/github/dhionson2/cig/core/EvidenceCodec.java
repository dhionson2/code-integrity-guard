package io.github.dhionson2.cig.core;

import java.nio.charset.StandardCharsets;
import java.util.Base64;

/** Strict, delimiter-safe UTF-8 fields used by the versioned evidence TSV files. */
public final class EvidenceCodec {
    private EvidenceCodec() {}
    public static String row(String... fields) {
        return java.util.Arrays.stream(fields).map(s -> Base64.getUrlEncoder().withoutPadding().encodeToString(s.getBytes(StandardCharsets.UTF_8))).collect(java.util.stream.Collectors.joining("\t"));
    }
    public static String[] fields(String line, int expected) {
        String[] columns=line.split("\t",-1); if(columns.length!=expected) throw new IllegalArgumentException("Expected "+expected+" evidence columns");
        String[] result=new String[expected]; for(int i=0;i<expected;i++) try { result[i]=StandardCharsets.UTF_8.newDecoder().onMalformedInput(java.nio.charset.CodingErrorAction.REPORT).onUnmappableCharacter(java.nio.charset.CodingErrorAction.REPORT).decode(java.nio.ByteBuffer.wrap(Base64.getUrlDecoder().decode(columns[i]))).toString(); } catch(Exception e) { throw new IllegalArgumentException("Invalid encoded evidence column "+(i+1),e); }
        return result;
    }
}
