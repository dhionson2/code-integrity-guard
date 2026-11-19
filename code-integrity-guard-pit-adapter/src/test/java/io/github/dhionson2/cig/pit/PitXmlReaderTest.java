package io.github.dhionson2.cig.pit;

import org.junit.jupiter.api.Test;

import java.io.InputStream;
import java.nio.file.*;
import java.nio.file.attribute.FileTime;
import java.time.Instant;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class PitXmlReaderTest {
    private static final String TARGET = "io.github.dhionson2.example.CustomerController";
    private static final String VALID = """
            <mutations partial="true">
              <mutation detected="true" status="KILLED" numberOfTestsRun="1">
                <sourceFile>Example.java</sourceFile><mutatedClass>Example</mutatedClass>
                <mutatedMethod>get</mutatedMethod><methodDescription>()Ljava/lang/String;</methodDescription>
                <lineNumber>12</lineNumber><mutator>example.Mutator</mutator>
                <indexes><index>0</index></indexes><blocks><block>0</block></blocks>
                <killingTest>test</killingTest><description>changed value</description>
              </mutation>
            </mutations>
            """;

    @Test void acceptsRealPit1302PartialTrueAsCoverageMetadata() throws Exception {
        Path file = copyRealReport();
        try {
            PitReport report = new PitXmlReader().read(file, Instant.EPOCH, List.of(TARGET));
            assertEquals(PitReport.Status.AVAILABLE, report.status());
            assertEquals(Boolean.TRUE, report.partialCoverage());
            assertEquals(11, report.mutations().size());
            assertEquals(7, report.detected());
            assertEquals(11, report.total());
            assertEquals(7d / 11d, report.score(), 0.000001);
            assertEquals(TARGET, report.scope());
            assertEquals(List.of(TARGET), report.expectedScope());
            assertTrue(report.reason().contains("does not indicate an interrupted mutation run"));
        } finally {
            Files.deleteIfExists(file);
        }
    }

    @Test void validPartialTrueReportIsAvailableWithExpectedScope() throws Exception {
        Path file = write(VALID);
        try {
            PitReport report = new PitXmlReader().read(file).validateExpectedScope(List.of("Example"));
            assertEquals(PitReport.Status.AVAILABLE, report.status());
            assertEquals(Boolean.TRUE, report.partialCoverage());
            assertEquals(1, report.detected());
            assertEquals(1, report.total());
        } finally { Files.deleteIfExists(file); }
    }

    @Test void currentRunFreshReportAndMatchingScopeAreAccepted() throws Exception {
        Path file = write(VALID);
        try {
            PitReport report = new PitXmlReader().read(file, Instant.now().minusSeconds(30), List.of("Example"));
            assertEquals(PitReport.Status.AVAILABLE, report.status());
            assertEquals(List.of("Example"), report.expectedScope());
        } finally { Files.deleteIfExists(file); }
    }

    @Test void staleReportIsInconclusive() throws Exception {
        Path file = write(VALID);
        try {
            Files.setLastModifiedTime(file, FileTime.from(Instant.parse("2020-01-01T00:00:00Z")));
            PitReport report = new PitXmlReader().read(file, Instant.parse("2021-01-01T00:00:00Z"), List.of("Example"));
            assertEquals(PitReport.Status.INCONCLUSIVE, report.status());
            assertTrue(report.reason().contains("predates"));
        } finally { Files.deleteIfExists(file); }
    }

    @Test void scopeMismatchAndMissingExpectedScopeAreInconclusive() throws Exception {
        Path file = write(VALID);
        try {
            PitXmlReader reader = new PitXmlReader();
            assertEquals(PitReport.Status.INCONCLUSIVE, reader.read(file).validateExpectedScope(List.of("Other")).status());
            assertEquals(PitReport.Status.INCONCLUSIVE, reader.read(file).validateExpectedScope(List.of()).status());
        } finally { Files.deleteIfExists(file); }
    }

    @Test void unresolvedErrorMutantsAreInconclusive() throws Exception {
        Path file = write(VALID.replace("detected=\"true\" status=\"KILLED\"", "detected=\"false\" status=\"TIMED_OUT\""));
        try {
            PitReport report = new PitXmlReader().read(file);
            assertEquals(PitReport.Status.INCONCLUSIVE, report.status());
            assertTrue(report.reason().contains("unresolved"));
        } finally { Files.deleteIfExists(file); }
    }

    @Test void nonViableMutantsAreExcludedFromDenominator() throws Exception {
        String nonViable = VALID.replace("</mutations>", """
                <mutation detected="false" status="NON_VIABLE" numberOfTestsRun="0">
                  <sourceFile>Example.java</sourceFile><mutatedClass>Example</mutatedClass>
                  <mutatedMethod>get</mutatedMethod><methodDescription>()Ljava/lang/String;</methodDescription>
                  <lineNumber>13</lineNumber><mutator>example.Mutator</mutator>
                  <indexes><index>1</index></indexes><blocks><block>0</block></blocks>
                  <killingTest/><description>non viable mutant</description>
                </mutation></mutations>
                """);
        Path file = write(nonViable);
        try {
            PitReport report = new PitXmlReader().read(file);
            assertEquals(PitReport.Status.AVAILABLE, report.status());
            assertEquals(2, report.mutations().size());
            assertEquals(1, report.detected());
            assertEquals(1, report.excluded());
            assertEquals(1, report.total());
            assertEquals(1d, report.score());
        } finally { Files.deleteIfExists(file); }
    }

    @Test void malformedTruncatedAndInvalidPartialMetadataAreInconclusive() throws Exception {
        for (String xml : List.of("<mutations partial=\"true\"><mutation", VALID.replace("partial=\"true\"", "partial=\"sometimes\""),
                VALID.replace("partial=\"true\"", ""), VALID.replace("detected=\"true\"", "detected=\"false\""))) {
            Path file = write(xml);
            try { assertEquals(PitReport.Status.INCONCLUSIVE, new PitXmlReader().read(file).status(), xml); }
            finally { Files.deleteIfExists(file); }
        }
    }

    @Test void rejectsDoctypeUnknownStatusAndEmptyAnalysis() throws Exception {
        for (String xml : List.of("<!DOCTYPE x [<!ENTITY e SYSTEM 'file:///c:/windows/win.ini'>]><mutations partial=\"true\">&e;</mutations>",
                VALID.replace("status=\"KILLED\"", "status=\"NOT_STARTED\""), "<mutations partial=\"true\"></mutations>")) {
            Path file = write(xml);
            try { assertEquals(PitReport.Status.INCONCLUSIVE, new PitXmlReader().read(file).status()); }
            finally { Files.deleteIfExists(file); }
        }
    }

    @Test void missingReportOrRunStartIsInconclusive() {
        PitXmlReader reader = new PitXmlReader();
        assertEquals(PitReport.Status.INCONCLUSIVE, reader.read(Path.of("missing-pit-report.xml")).status());
        assertEquals(PitReport.Status.INCONCLUSIVE, reader.read(Path.of("missing-pit-report.xml"), null, List.of("Example")).status());
    }

    private static Path write(String xml) throws Exception {
        Path file = Files.createTempFile("pit-reader-", ".xml");
        Files.writeString(file, xml);
        return file;
    }

    private static Path copyRealReport() throws Exception {
        Path file = Files.createTempFile("pit-real-1.30.2-", ".xml");
        try (InputStream input = PitXmlReaderTest.class.getResourceAsStream("/pit/pit-1.30.2-real-partial-true.xml")) {
            assertNotNull(input, "real PIT XML fixture must be on the test classpath");
            Files.copy(input, file, StandardCopyOption.REPLACE_EXISTING);
        }
        return file;
    }
}
