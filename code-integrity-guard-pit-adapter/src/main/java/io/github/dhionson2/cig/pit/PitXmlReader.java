package io.github.dhionson2.cig.pit;

import org.w3c.dom.*;

import javax.xml.XMLConstants;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.IOException;
import java.nio.file.*;
import java.time.Instant;
import java.util.*;

/** Read-only PIT XML parser with external entities and external DTD access disabled. */
public final class PitXmlReader {
    public static final long MAX_BYTES = 20L * 1024 * 1024;
    private static final Set<String> STATUSES = Set.of(
            "KILLED", "SURVIVED", "NO_COVERAGE", "TIMED_OUT", "MEMORY_ERROR", "RUN_ERROR", "NON_VIABLE");
    private static final Set<String> UNRESOLVED = Set.of("TIMED_OUT", "MEMORY_ERROR", "RUN_ERROR");

    /** Parse PIT structure and outcomes. PIT's partialCoverage flag is retained as coverage metadata. */
    public PitReport read(Path file) {
        try {
            if (!Files.isRegularFile(file) || Files.size(file) == 0 || Files.size(file) > MAX_BYTES)
                return unavailable("PIT report is missing, empty, or exceeds the 20 MiB limit.");

            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            factory.setFeature(XMLConstants.FEATURE_SECURE_PROCESSING, true);
            factory.setFeature("http://apache.org/xml/features/disallow-doctype-decl", true);
            factory.setFeature("http://xml.org/sax/features/external-general-entities", false);
            factory.setFeature("http://xml.org/sax/features/external-parameter-entities", false);
            factory.setAttribute(XMLConstants.ACCESS_EXTERNAL_DTD, "");
            factory.setAttribute(XMLConstants.ACCESS_EXTERNAL_SCHEMA, "");
            factory.setXIncludeAware(false);
            factory.setExpandEntityReferences(false);

            Document document = factory.newDocumentBuilder().parse(file.toFile());
            Element root = document.getDocumentElement();
            if (!"mutations".equals(root.getTagName())) return unavailable("Unsupported PIT XML root.");
            if (!root.hasAttribute("partial") || !("true".equals(root.getAttribute("partial"))
                    || "false".equals(root.getAttribute("partial"))))
                return unavailable("PIT partial coverage metadata is missing or invalid.");

            NodeList nodes = root.getElementsByTagName("mutation");
            if (nodes.getLength() == 0) return unavailable("PIT report contains no mutation results.");
            List<PitReport.Mutation> mutations = new ArrayList<>();
            Set<String> classes = new TreeSet<>();
            int killed = 0;
            int excluded = 0;
            boolean unresolved = false;

            for (int i = 0; i < nodes.getLength(); i++) {
                Element element = (Element) nodes.item(i);
                if (element.getParentNode() != root) return unavailable("PIT mutation results are nested in an unsupported structure.");
                String status = element.getAttribute("status");
                String detected = element.getAttribute("detected");
                String testsRun = element.getAttribute("numberOfTestsRun");
                if (!STATUSES.contains(status) || !("true".equals(detected) || "false".equals(detected))
                        || !testsRun.matches("[0-9]+"))
                    return unavailable("PIT mutation has an unknown status or invalid required attributes.");
                if ("KILLED".equals(status) != "true".equals(detected))
                    return unavailable("PIT detected attribute conflicts with mutation status.");

                String sourceFile = child(element, "sourceFile");
                String className = child(element, "mutatedClass");
                String method = child(element, "mutatedMethod");
                String methodDescription = child(element, "methodDescription");
                String lineNumber = child(element, "lineNumber");
                String mutator = child(element, "mutator");
                String description = child(element, "description");
                if (sourceFile.isBlank() || className.isBlank() || method.isBlank() || methodDescription.isBlank()
                        || !lineNumber.matches("[1-9][0-9]*") || mutator.isBlank() || description.isBlank()
                        || element.getElementsByTagName("indexes").getLength() != 1
                        || element.getElementsByTagName("blocks").getLength() != 1)
                    return unavailable("PIT mutation is incomplete or incompatible with the supported XML structure.");

                if ("KILLED".equals(status)) killed++;
                if ("NON_VIABLE".equals(status)) excluded++;
                if (UNRESOLVED.contains(status)) unresolved = true;
                classes.add(className);
                mutations.add(new PitReport.Mutation(className, method, mutator, description, status, child(element, "killingTest")));
            }

            int denominator = mutations.size() - excluded;
            if (denominator <= 0) return unavailable("PIT report has no scoreable mutation results.");
            String scope = String.join(", ", classes);
            Boolean partialCoverage = Boolean.valueOf(root.getAttribute("partial"));
            String reason = unresolved
                    ? "PIT completed with unresolved timed-out or errored mutants; analysis is inconclusive."
                    : "PIT XML is structurally complete. partialCoverage=" + partialCoverage
                    + " is PIT coverage metadata and does not indicate an interrupted mutation run."
                    + " Denominator excludes NON_VIABLE mutants; excluded count is reported separately.";
            return new PitReport(unresolved ? PitReport.Status.INCONCLUSIVE : PitReport.Status.AVAILABLE,
                    List.copyOf(mutations), killed, denominator, excluded, scope, partialCoverage, List.of(), reason);
        } catch (Exception exception) {
            return unavailable("PIT report invalid or incompatible: " + exception.getClass().getSimpleName());
        }
    }

    /** Require a report produced no earlier than the current Maven session and validate declared target scope. */
    public PitReport read(Path file, Instant currentRunStartedAt, Collection<String> expectedScope) {
        try {
            if (currentRunStartedAt == null) return unavailable("Current Maven run start time is unavailable.");
            if (!Files.isRegularFile(file)) return unavailable("PIT report was not produced in the current Maven run.");
            if (Files.getLastModifiedTime(file).toInstant().isBefore(currentRunStartedAt))
                return unavailable("PIT report predates the current Maven run.");
            return read(file).validateExpectedScope(expectedScope);
        } catch (IOException exception) {
            return unavailable("Could not validate PIT report freshness: " + exception.getClass().getSimpleName());
        }
    }

    private static String child(Element parent, String name) {
        NodeList nodes = parent.getElementsByTagName(name);
        return nodes.getLength() == 0 ? "" : nodes.item(0).getTextContent();
    }

    private static PitReport unavailable(String explanation) {
        return new PitReport(PitReport.Status.INCONCLUSIVE, List.of(), 0, 0, 0, "", null, List.of(), explanation);
    }
}
