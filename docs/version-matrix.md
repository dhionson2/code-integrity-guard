# Version matrix

| Component | Pinned version | Reason |
|---|---:|---|
| Java | 21 | Required product baseline; LTS runtime. |
| Maven Wrapper | 3.9.16 | Current stable Maven 3.9 line verified against Apache Maven release history. |
| JUnit Jupiter | 5.14.3 | Stable JUnit 5 release; supports Java 8+ runtime, so Java 21 is within baseline. |
| Spring Boot | 3.4.13 | Spring Boot 3.4 line requirements list Java 17+ through Java 24 and Maven 3.6.3+, covering this Java 21/Maven setup. |
| PIT | 1.30.2 | Current PIT release chosen for the optional example mutation run. |
| PIT JUnit 5 plugin | 1.2.3 | Upstream release states it requires PIT 1.19.4 or later. |

References: [Apache Maven release history](https://maven.apache.org/docs/history.html), [Spring Boot 3.4 system requirements](https://docs.spring.io/spring-boot/3.4/system-requirements.html), [JUnit 5.14.3 release notes](https://docs.junit.org/5.14.3/release-notes.html), [PIT releases](https://github.com/hcoles/pitest/releases), [PIT JUnit 5 plugin](https://github.com/pitest/pitest-junit5-plugin).

This matrix records checked versions, not an ongoing support commitment. Recheck versions and dependency advisories before each release.
