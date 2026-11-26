package io.github.dhionson2.cig.maven;

import org.apache.maven.shared.invoker.DefaultInvocationRequest;
import org.apache.maven.shared.invoker.DefaultInvoker;
import org.apache.maven.shared.invoker.InvocationRequest;
import org.apache.maven.shared.invoker.InvocationResult;
import org.apache.maven.shared.invoker.Invoker;
import org.apache.maven.shared.invoker.MavenInvocationException;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayDeque;
import java.util.List;
import java.util.Properties;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

/** Runs the packaged reactor plugin in a separate Maven process and a throwaway consumer. */
class IsolatedMavenConsumerIT {
    private static final long INVOCATION_TIMEOUT_MILLIS = 120_000;
    private static final int MAX_DIAGNOSTIC_LINES = 200;

    @Test
    void reportsPassingAndInconclusiveConsumerRuns() throws Exception {
        Path tempBase = Path.of(System.getProperty("java.io.tmpdir")).toAbsolutePath().normalize();
        Path tempRoot = Files.createTempDirectory(tempBase, "cig-invoker-").toAbsolutePath().normalize();
        try {
            Path repositorySource = Path.of(System.getProperty("project.build.directory"), "invoker-repository")
                    .toAbsolutePath().normalize().resolve("io/github/dhionson2");
            assertTrue(Files.isDirectory(repositorySource),
                    "Invoker reactor repository was not prepared at " + repositorySource);
            Path packagedPlugin = Path.of(System.getProperty("project.build.directory"),
                    "code-integrity-guard-maven-plugin-1.0.0.jar").toAbsolutePath().normalize();
            Path installedPlugin = repositorySource.resolve("code-integrity-guard-maven-plugin/1.0.0/"
                    + "code-integrity-guard-maven-plugin-1.0.0.jar");
            assertEquals(-1L, Files.mismatch(packagedPlugin, installedPlugin),
                    "Invoker repository does not contain the plugin packaged by this reactor run.");

            Path fixtureSource = Path.of(System.getProperty("project.basedir"), "src/it/isolated-consumer")
                    .toAbsolutePath().normalize();
            Path consumer = tempRoot.resolve("consumer");
            Path repository = tempRoot.resolve("maven-repository");
            copyTree(fixtureSource, consumer);
            copyTree(repositorySource, repository.resolve("io/github/dhionson2"));

            RunResult passing = invoke(consumer, repository, "PassingContractTest");
            assertEquals(0, passing.exitCode(), "Passing consumer failed.\n" + passing.diagnostics());
            Path passingReport = report(consumer);
            assertTrue(Files.isRegularFile(passingReport), "Passing consumer did not write report.\n" + passing.diagnostics());
            String passingJson = Files.readString(passingReport);
            assertJsonValue(passingJson, "buildStatus", "AUDIT_RECORDED");
            assertJsonValue(passingJson, "status", "VERIFIED");

            RunResult inconclusive = invoke(consumer, repository, "InconclusiveContractTest");
            assertNotEquals(0, inconclusive.exitCode(), "Inconclusive consumer unexpectedly passed.\n" + inconclusive.diagnostics());
            Path inconclusiveReport = report(consumer);
            assertTrue(Files.isRegularFile(inconclusiveReport),
                    "Inconclusive consumer did not write report.\n" + inconclusive.diagnostics());
            String inconclusiveJson = Files.readString(inconclusiveReport);
            assertJsonValue(inconclusiveJson, "buildStatus", "FAILED");
            assertJsonValue(inconclusiveJson, "status", "INCONCLUSIVE");
        } finally {
            deleteTemporaryTree(tempRoot, tempBase);
        }
    }

    private static RunResult invoke(Path consumer, Path repository, String testClass) throws MavenInvocationException {
        String mavenHome = System.getProperty("maven.home");
        if (mavenHome == null || mavenHome.isBlank()) {
            throw new IllegalStateException("The Maven Wrapper did not expose maven.home to the integration test.");
        }
        ArrayDeque<String> diagnostics = new ArrayDeque<>();
        var outputHandler = (org.apache.maven.shared.invoker.InvocationOutputHandler) line -> {
            synchronized (diagnostics) {
                if (diagnostics.size() == MAX_DIAGNOSTIC_LINES) diagnostics.removeFirst();
                diagnostics.addLast(line);
            }
        };
        InvocationRequest request = new DefaultInvocationRequest()
                .setPomFile(consumer.resolve("pom.xml").toFile())
                .setGoals(List.of("clean", "verify"))
                .setBatchMode(true)
                .setShowErrors(true)
                .setLocalRepositoryDirectory(repository.toFile())
                .setProperties(invocationProperties(testClass));
        request.setTimeoutInSeconds((int) (INVOCATION_TIMEOUT_MILLIS / 1_000));
        request.setOutputHandler(outputHandler);
        request.setErrorHandler(outputHandler);

        Invoker invoker = new DefaultInvoker();
        invoker.setMavenHome(Path.of(mavenHome).toFile());
        invoker.setWorkingDirectory(consumer.toFile());
        InvocationResult result = invoker.execute(request);
        String output;
        synchronized (diagnostics) {
            output = String.join(System.lineSeparator(), diagnostics);
        }
        if (result.getExecutionException() != null) {
            throw new IllegalStateException("Maven invocation failed: " + output, result.getExecutionException());
        }
        return new RunResult(result.getExitCode(), output);
    }

    private static Properties invocationProperties(String testClass) {
        Properties properties = new Properties();
        properties.setProperty("cig.test.class", testClass);
        return properties;
    }

    private static Path report(Path consumer) {
        return consumer.resolve("target/code-integrity-guard/report.json");
    }

    private static void assertJsonValue(String json, String key, String expected) {
        assertTrue(json.matches("(?s).*\\\"" + key + "\\\"\\s*:\\s*\\\"" + expected + "\\\".*"),
                "Expected JSON field " + key + "=" + expected + ", got: " + json);
    }

    private static void copyTree(Path source, Path destination) throws IOException {
        try (Stream<Path> paths = Files.walk(source)) {
            for (Path path : paths.toList()) {
                Path target = destination.resolve(source.relativize(path).toString());
                if (Files.isDirectory(path)) Files.createDirectories(target);
                else Files.copy(path, target);
            }
        }
    }

    private static void deleteTemporaryTree(Path tempRoot, Path tempBase) throws IOException {
        Path normalizedRoot = tempRoot.toAbsolutePath().normalize();
        if (!normalizedRoot.startsWith(tempBase) || normalizedRoot.equals(tempBase)) {
            throw new IOException("Refusing to clean path outside the configured temporary directory: " + normalizedRoot);
        }
        if (!Files.exists(normalizedRoot)) return;
        try (Stream<Path> paths = Files.walk(normalizedRoot)) {
            for (Path path : paths.sorted((left, right) -> right.getNameCount() - left.getNameCount()).toList()) {
                Files.deleteIfExists(path);
            }
        }
    }

    private record RunResult(int exitCode, String diagnostics) { }
}
