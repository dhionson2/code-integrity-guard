package io.github.dhionson2.cig.maven;

import io.github.dhionson2.cig.core.SecurityContract;
import java.io.IOException;
import java.nio.file.*;
import java.util.*;

/** Distinguishes an absent, empty, invalid, and populated contract manifest. */
final class ContractManifestReader {
    private ContractManifestReader() {}
    static Result read(Path file) {
        if(!Files.isRegularFile(file)) return new Result("MISSING",List.of(),"file does not exist");
        try {
            List<SecurityContract> all=new ArrayList<>(); Set<String> ids=new HashSet<>();
            for(String line:Files.readAllLines(file)) {
                if(line.isBlank()||line.startsWith("#")) continue;
                String[] p=line.split("\t",-1);
                if(p.length!=6||p[0].isBlank()) throw new IOException("Malformed contract row");
                if(!ids.add(p[0])) throw new IOException("Duplicate contract id: "+p[0]);
                Set<String> tests=p[4].isBlank()?Set.of():new LinkedHashSet<>(Arrays.asList(p[4].split(",",-1)));
                Set<String> scenarios=p[5].isBlank()?Set.of():new LinkedHashSet<>(Arrays.asList(p[5].split(",",-1)));
                all.add(new SecurityContract(p[0],p[3],p[1],p[2],tests,scenarios));
            }
            return new Result(all.isEmpty()?"EMPTY":"PRESENT",all,"");
        } catch(Exception e) { return new Result("INVALID",List.of(),e.getMessage()); }
    }
    record Result(String status,List<SecurityContract> contracts,String problem) {}
}
