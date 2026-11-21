B8.8 incremental Spring risk review — 2026-11-20

Scope: six local 1.0.0 candidate SBOMs, testkit current dependency tree and JAR, integration-consumer fixture, and excluded Spring Boot example. No dependency/code/test changes.

Commands/evidence:
- `mvnw.cmd -B -ntp -f code-integrity-guard-testkit\pom.xml dependency:tree -Dverbose` -> testkit-dependency-tree.txt
- `mvnw.cmd -B -ntp -f examples\spring-boot-authorization\pom.xml dependency:tree -Dverbose` -> example-dependency-tree.txt
- `jar tf code-integrity-guard-testkit\target\code-integrity-guard-testkit-1.0.0.jar` -> testkit-jar-entries.txt
- Source inspection: AuthorizationChecks.java, ContractTest.java, EvidenceExtension.java, EvidenceFiles.java, integration-consumer tests and example controller/security configuration; rg for ELRequestMatcher, SpEL parsers/evaluation contexts, data binders, multipart, relay and parameter-based content-negotiation settings.

Observed dependency facts:
- Six publishable SBOMs: only testkit SBOM lists Spring Security Core 6.4.13 and Spring Expression 6.2.15. None lists spring-security-web, spring-webmvc or spring-webflux.
- Current testkit dependency tree has spring-test 6.2.15:provided and spring-security-core 6.4.13:provided. No spring-security-web, spring-webmvc or spring-webflux.
- Testkit JAR contains only CIG classes, no Spring classes or ELRequestMatcher.
- Testkit source reads Authentication/SecurityContext, calls consumer-owned MockMvc requests, and checks expected statuses/policy. It does not configure ELRequestMatcher, parse/evaluate SpEL, bind request input to matcher beans, create a WebFlux endpoint, or relay multipart content.
- The isolated example does resolve Spring MVC 6.2.15 and Spring Security Web 6.4.13. It is excluded from central-release (`skipPublishing=true`). Its only route is GET `/clientes/{id}`; security uses `anyRequest().authenticated()` and a constant `@PreAuthorize("hasRole('ADMIN')")`; no ELRequestMatcher or matcher-object data binding. No parameter content-negotiation override, multipart handler/check/relay, or WebFlux dependency/source was found. Boot's documented `favor-parameter` default is false.
- Independent consumer fixture uses `spring-security-test` 7.0.7 and MVC 7.0.10 in test scope; its test uses `.with(user(...))`, not ELRequestMatcher or external binding. This fixture is not a publishable artifact.

Classification boundary: these checks establish current source/configuration paths, not safety of arbitrary consumer applications that bring their own Spring MVC/Security stack. See the B8.8 sections in supply-chain-review.md and release-readiness.md for advisory conditions, decisions and explicit residual risks.
