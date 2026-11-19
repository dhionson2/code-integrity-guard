package io.github.dhionson2.cig.report;

import io.github.dhionson2.cig.core.ContractResult;
import io.github.dhionson2.cig.pit.PitReport;
import java.util.List;

public record AuditReport(String buildStatus, List<ContractResult> contracts, PitReport pit, List<String> gates, List<String> limitations, String evidenceRunId, String evidenceOrigin, String contractsEvaluation, String contractsManifestStatus) {
    public AuditReport(String buildStatus,List<ContractResult> contracts,PitReport pit,List<String> gates,List<String> limitations){this(buildStatus,contracts,pit,gates,limitations,"","",contracts.isEmpty()?"NOT_RUN":"EVALUATED","UNKNOWN");}
    public AuditReport(String buildStatus,List<ContractResult> contracts,PitReport pit,List<String> gates,List<String> limitations,String runId,String origin){this(buildStatus,contracts,pit,gates,limitations,runId,origin,contracts.isEmpty()?"NOT_RUN":"EVALUATED","UNKNOWN");}
    public AuditReport { contracts=List.copyOf(contracts); gates=List.copyOf(gates); limitations=List.copyOf(limitations);evidenceRunId=evidenceRunId==null?"":evidenceRunId;evidenceOrigin=evidenceOrigin==null?"":evidenceOrigin;contractsEvaluation=contractsEvaluation==null?"NOT_RUN":contractsEvaluation;contractsManifestStatus=contractsManifestStatus==null?"UNKNOWN":contractsManifestStatus; }
}
