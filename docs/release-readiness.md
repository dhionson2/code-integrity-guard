# Release readiness — Code Integrity Guard 1.0.0

**Status atual (B8.10): RC 1.0.0 pronta para revisão humana do commit no GitHub; publicação no Maven Central ainda não autorizada.** O mantenedor aceitou de forma limitada os riscos Spring residuais descritos abaixo, sem classificá-los como corrigidos. O gate técnico dos avisos nos cinco Javadoc JARs foi aprovado em B8.9; isso não é certificação jurídica independente. As decisões antigas de NO-GO em B8.6/B8.8 registram o estado daquela revisão e são supersedidas pelo aceite limitado documentado em B8.10. Nenhum commit, push, tag, assinatura, upload ou publicação foi executado nesta etapa.

## Blocker matrix

| Blocker | Status | Solution / files | Executed evidence | Remaining risk |
|---|---|---|---|---|
| B1 — execution is not contract validation | CORRIGIDO | `code-integrity-guard-core` contract evaluator and scenario evidence; `code-integrity-guard-testkit` checks; regression tests and consumer/demo manifests. Contract status requires policy-linked scenario outcomes, not a passing test callback alone. | Reactor tests and independent consumer runs recorded in `docs/progress.md`; core regressions cover execution-only, missing evidence, mismatched outcome, and matching outcome. Latest clean reactor + consumer rerun is recorded below. | Locally mutable evidence is not authenticated against a hostile local producer. A VERIFIED result applies only to consumer-declared scenarios and fixture behavior. |
| B2 — freshness/integrity/correlation | CORRIGIDO COM LIMITES | `EvidenceExtension`, `TestEvidence`, `ScenarioEvidence`, `EvidenceRunStore`, `PrepareEvidenceMojo`, `SealEvidenceMojo`, `EvidenceFileParser`, `AuditMojo`. Per-module UUID/run manifest, fresh reset without `clean`, fork-shared files, seal digest, origin/invocation correlation, strict parse. | Unit regressions cover current/prior run, missing run ID, truncation/invalid row, duplicate/conflict, skipped/unexecuted test, mismatched invocation, partial run. Independent consumer runs Surefire and Failsafe forks without shared memory. | Hashes and Maven-session identifiers detect stale/accidental changes, not a malicious process capable of rewriting all evidence and seal. No reactor-wide aggregation. |
| B3 — audit without contracts | CORRIGIDO | `AuditMojo`, report JSON/HTML/schema, installation docs. `requireContracts=true` default; missing/empty/invalid manifests fail. Explicit false reports `NOT_RUN`, never approval. | Previously exercised Maven negative exit codes for missing, empty, invalid manifests and intentional false mode; `-DskipTests` fails inconclusive. Preserved by the latest reactor run. See `docs/progress.md`. | `requireContracts=false` permits continuation only; it is not a security gate. |
| B4 — CIG report after actual test failure | CORRIGIDO | Example POM diagnostic-only profile; `CustomerAuthorizationTest` and `CustomerAuthorizationIT` no longer catch authorization assertion failures. Normal Surefire/Failsafe remains fail-fast. | Reproduced with Maven exit 1; Surefire and Failsafe each failed on cross-owner HTTP 200 against declared 404, while CIG JSON/HTML recorded FAILED. Captured evidence is under `docs/release-evidence/`. | Diagnostic mode uses provider `testFailureIgnore` only under an explicit profile so sealing/audit can run; never interpret its report as a green build. |
| B5 — trustworthy JSON serialization | CORRIGIDO | `ReportWriter` uses Jackson; HTML is independently escaped; JSON Schema v1.2 at `docs/json-report-schema.json`; independent Gson tests. | Report tests validate control characters U+0000–U+001F, quotes, backslashes, Unicode/supplementary codepoints, null/empty values, special contract/scenario fields, HTML injection, and PIT `partialCoverage`/expected-scope output. Included in latest reactor test run. | Schema is maintained manually and should remain synchronized with future report changes. |
| B6 — real Spring Security integration | CORRIGIDO COM ESCOPO LIMITADO | Example uses Spring Security authentication, `SecurityFilterChain`, `@PreAuthorize`, Spring Security Test identities and MockMvc. `AuthorizationChecks` derives authenticated principal; no demo identity header. | Fixed scenarios pass owner 200, cross-owner 404, anonymous 401, insufficient role 403; the explicitly vulnerable variant fails on cross-owner 200. Example tests run in reactor and diagnostic profile. | This is a synthetic fixture and declared HTTP contract mechanism, not a universal IDOR detector or proof of production security. The formal V1 contract type remains cross-owner only. |
| B7 — PIT semantics, freshness and gates | CORRIGIDO | `PitXmlReader` treats `partial` as coverage metadata; preserves `partialCoverage`; rejects malformed/empty/error-state reports; checks current Maven-session timestamp and exact expected class scope. `PrepareEvidenceMojo` deletes previous PIT XML before each lifecycle run. Example profile declares `CustomerController` scope; CI tests passing and failing mutation-score thresholds. | Verified against official PIT 1.30.2 source. Real current-run report: 11 mutations, 7 KILLED, 1 SURVIVED, 3 NO_COVERAGE, 0 timed-out/error, 7/11 (63.636%), scope exactly `io.github.dhionson2.example.CustomerController`, `partialCoverage=true`. Threshold 60% exited 0 with PIT AVAILABLE; threshold 70% exited 1 with PIT AVAILABLE and a score-gate failure. Reports/XML and test fixture are captured in `docs/release-evidence/` and adapter test resources. | Local files and filesystem timestamps do not cryptographically authenticate PIT provenance against a hostile local process. GitHub Actions changes were validated by local equivalent commands, not by a remote Actions run. |
| B8 — preparação da RC 1.0.0 | GATES TÉCNICOS FECHADOS NO ESCOPO; publicação pendente | Baseline Maven 3.10.1, exclusão do exemplo, inventário SCA e avisos Javadoc documentados em `docs/supply-chain-review.md`. | Reactor, consumidor independente, PIT, seis SBOMs/OSV e geração dos cinco Javadocs têm evidências registradas nas seções B8.5–B8.10 e em `docs/release-evidence/`. | Os riscos Spring do escopo auditado foram aceitos limitadamente pelo mantenedor e continuam não corrigidos; consumidores precisam avaliar a própria configuração. O exemplo continua excluído. A aprovação para commit/publicação e as etapas de distribuição continuam separadas. |

## B8.1 — Remediação de dependências

| Componente | Versão anterior | Nova versão | Advisory | Escopo | Risco residual | Resultado dos testes |
|---|---|---|---|---|---|---|
| `jackson-databind` | 2.18.5 | 2.18.12 | 13 GHSA/CVE resolvidos | Runtime de `report`; transitive no Maven Plugin | Sem advisory OSV para 2.18.12 nesta consulta | `clean install` e consumer `clean verify` passaram |
| `jackson-core` | 2.18.5 | 2.18.12 | 4 GHSA/CVE resolvidos | Runtime transitivo de `report` e plugin | Sem advisory OSV para 2.18.12 nesta consulta | Idem |
| `jackson-annotations` | 2.18.5 | 2.18.12 | Nenhum anterior; alinhamento coordenado | Runtime transitivo de `report` e plugin | Sem advisory OSV para 2.18.12 nesta consulta | Idem |
| Maven Plugin API | 3.9.9 `compile` | 3.9.9 `provided` | Remove Plexus Utils do runtime publicado; Plexus fica `provided` do host Maven | API disponibilizada pelo host de build | CVE-2025-67030 continua no SBOM agregado, sem ser dependência runtime empacotada do plugin | Plugin + reactor tests passaram; Maven Plugin Plugin não repetiu alerta de escopo |
| Spring Boot / Framework / Security / Tomcat do exemplo | 3.4.13 / 6.2.15 / 6.4.13 / 10.1.50 | Sem alteração | Findings HIGH/CRITICAL persistem (incluindo CVE-2026-22732, CVE-2026-47890, CVE-2026-47884 e advisories Tomcat) | Runtime apenas do exemplo; excluir do Central | Exemplo continua vulnerável até migração e novo scan; candidata: Boot 4.0.9 alinhado pelo BOM (Java 21 compatível), ainda sem build/testes de migração | Cenário negativo falhou como esperado; PIT 60% passou e 70% falhou pelo gate de score, com contrato VERIFIED |

HIGH/CRITICAL ainda em revisão: AssertJ `CVE-2026-24400` (teste), Guava `CVE-2026-102554` (`provided`), Boot `CVE-2026-40973`, Spring Expression `CVE-2026-41850`, Spring WebMVC `CVE-2026-41845`/`CVE-2026-41842`, Tomcat `CVE-2026-24880`, `CVE-2026-43513`, `CVE-2026-42498`, `CVE-2026-41284`, `CVE-2026-53404`, `CVE-2026-24734`, `CVE-2026-34483`, `CVE-2026-34487`; CRITICAL no exemplo: Spring Security `CVE-2026-22732`, Spring WebMVC `CVE-2026-47890`/`CVE-2026-47884`, Tomcat `CVE-2026-43515`, `CVE-2026-65905`, `CVE-2026-65182`, `CVE-2026-68525`, `CVE-2026-43512`, `CVE-2026-55276`, `CVE-2026-41293`. Nenhum desses remanescentes é runtime dos artefatos library/plugin planejados para Central; o exemplo continua bloqueado para distribuição.

A auditoria do SBOM pós-correção encontrou 50 advisories em 14 de 109 componentes. Foram eliminados os 17 advisories Jackson da versão 2.18.5, entre eles 8 classificados HIGH no snapshot B8 e 9 moderados; nenhum dos 17 era CRITICAL. Permanecem findings HIGH/CRITICAL em escopos de exemplo/teste/host Maven; eles não foram tratados como zero risco. A varredura recursiva de POMs não resolveu SNAPSHOTs intermodulares mesmo após `install`; relatórios Maven `dependency:tree` e `effective-pom` por módulo confirmam a versão selecionada e os scopes, mas não substituem o relatório OSV completo por grafo.

O SBOM agregado histórico pós-remediação permanece em `docs/release-evidence/cyclonedx-bom.json`. Os dois JSONs brutos do OSV B8.1 e o scan parcial de POM foram preservados localmente, fora do commit, por serem snapshots grandes/superados pelos seis scans individuais da RC em `docs/release-evidence/osv-v1.0.0/`. O detalhamento das faixas dos 17 advisories, propostas de atualização isolada do exemplo, limites de análise e licenças está em `docs/supply-chain-review.md`.

## Final validation commands and evidence

This section records the commands actually executed in this work session. Expected negative cases are labeled as expected failures; a Maven success in the SCA attempt is not a successful scan.

- `mvnw.cmd -B -ntp clean install` — latest full reactor BUILD SUCCESS: core 17, PIT adapter 10, report 6, Maven plugin 6, Spring example 5; **44 passed, 0 failed/errors/skips**. Testkit has no test sources.
- `mvnw.cmd -B -ntp -f integration-consumer/pom.xml clean verify` — BUILD SUCCESS after the final plugin install; Surefire 2 tests (1 passed, 1 intentionally skipped), Failsafe 1 passed; 0 failed/errors. Contract evidence traverses both forks and is sealed/audited by the installed Maven plugin.
- `mvnw.cmd -B -ntp -f examples/spring-boot-authorization/pom.xml verify -Pcig-vulnerable-diagnostic` — expected nonzero. Must create reports despite actual Surefire/Failsafe failures; reports show HTTP 200 vs expected 404 and FAILED, never VERIFIED.
- `mvnw.cmd -B -ntp -f examples/spring-boot-authorization/pom.xml verify -Ppit-validation '-Dcig.minimumMutationPercent=60'` — BUILD SUCCESS with current real PIT XML `partial="true"`, PIT AVAILABLE, exact configured scope and 7/11 mutation score.
- `mvnw.cmd -B -ntp -f examples/spring-boot-authorization/pom.xml verify -Ppit-validation '-Dcig.minimumMutationPercent=70'` — expected exit 1; PIT remains AVAILABLE, while the declared 70% score gate fails on 63.636%.
- `mvnw.cmd -B -ntp org.cyclonedx:cyclonedx-maven-plugin:2.9.3:makeAggregateBom -DincludeTestScope=true -DoutputFormat=json` — SUCCESS; 109 components; output captured in `docs/release-evidence/cyclonedx-bom.json`.
- `mvnw.cmd -B -ntp org.owasp:dependency-check-maven:13.0.0:aggregate '-Dformat=JSON,HTML' -DoutputDirectory=target/dependency-check -DskipTestScope=false -DfailOnError=false` — Maven returned 0 because the command explicitly made tool errors non-fatal, but Dependency-Check logged fatal NVD API-key error and did not analyze dependencies. No SCA report; no zero-CVE claim.
- `mvnw.cmd -B -ntp -Pcentral-release -DskipTests -Dgpg.skip=true package` — package-only preparation; validates source and Javadoc jar creation, not signatures, Central validation, or publishing.

## Ações pendentes antes de uma publicação futura

1. Revisar e autorizar separadamente o commit proposto para o GitHub; esta atualização não fez `git add` nem commit.
2. Se a publicação no Maven Central for desejada, autorizar essa etapa em uma solicitação separada e usar a chave de assinatura/credenciais do Central somente no ambiente seguro do mantenedor, nunca no repositório.
3. O Dependency-Check anterior não produziu relatório por falta de chave/base NVD. Decidir se uma execução adicional será exigida no processo de publicação; isso não invalida os seis resultados OSV individuais registrados, incluindo o parent classificado como não aplicável por não conter dependências.
4. O gate técnico dos avisos Javadoc está aprovado em B8.9; esta constatação não é certificação jurídica. Qualquer parecer jurídico independente é decisão separada, não uma obrigação técnica identificada como ausente nos classifiers.
5. Sobreviventes e mutações sem cobertura no PIT são oportunidades de melhoria dos testes e devem ser priorizados sem tratá-los como vulnerabilidades demonstradas.

## External review snapshot

A sanitized source-and-evidence ZIP was created outside the repository for an earlier external review. It contains source, tests, reactor/consumer POMs, workflows, architecture/security/release documentation, the corrected Apache 2.0 license, and real consumer/B4/PIT/SBOM evidence. It excludes VCS data, Maven caches, target directories, and non-wrapper build binaries. Package size and SHA-256 were reported separately.

That ZIP predates the final B7 parser/report/workflow correction in this source tree. Do not use it as the current B7 review package; regenerate a sanitized snapshot if an external review package is needed for this revision.


## B8.2 — Conclusão de escopo para Maven Central (2026-11-19)

Esta atualização substitui conclusões SCA B8 anteriores que tratavam Jackson e a resolução SNAPSHOT como bloqueadores sem separar os escopos. Foram capturados `dependency:tree -Dverbose` e `help:effective-pom` depois da instalação local do reactor, para parent e cinco módulos candidatos. As árvores compactas dos artefatos e do exemplo estão em `docs/release-evidence/b8.2-dependency-tree-*.txt`. Os effective POMs foram gerados e inspecionados, mas os XMLs exportados permanecem locais: são saída derivável e contêm caminhos absolutos do workspace. SBOM agregado histórico: 109 componentes. OSV-Scanner v2.7.1 reportou 50 registros em 14 componentes; o scan OSV dos POMs permaneceu parcial para dependências internas `1.0.0-SNAPSHOT`, então nenhum resultado abaixo equivale a “zero vulnerabilidades”.

O perfil `central-release` resolve `skipPublishing=true` no `spring-boot-authorization-example`; a documentação do [Maven Central Publishing Plugin](https://central.sonatype.org/publish/publish-portal-maven/) confirma que isso exclui os artefatos desse projeto da publicação/staging. Não houve staging ou publicação. O testkit permanece como artefato publicável autônomo e continua no conjunto do reactor.

| Artefato | Dependências relevantes | Advisories aplicáveis | Severidade / risco residual | Licença |
|---|---|---|---|---|
| Parent POM | Gerenciamento de versão; sem runtime | Nenhum runtime confirmado | Revisar POM release sem SNAPSHOT | Apache-2.0; manter LICENSE/NOTICE |
| Core | Nenhuma externa runtime; JUnit test | Nenhum runtime; AssertJ apenas teste | Sem bloqueador SCA de runtime confirmado | Apache runtime; JUnit EPL-2.0 teste |
| Testkit (snapshot B8.2) | Core compile; JUnit/Spring Test/Security Test/Servlet/Hamcrest provided | Findings da árvore B8.2 eram do snapshot daquele POM | Scopes revalidados no B8.3.2 com consumidor independente; POM atual usa Spring Security Core e Hamcrest `provided` | Servlet API EPL-2.0 OR GPL-2.0-with-classpath-exception requer revisão se redistribuído |
| PIT adapter | Core compile; JUnit test | Nenhum runtime confirmado | Sem bloqueador confirmado | Apache runtime; JUnit EPL-2.0 teste |
| Report | Core/PIT adapter compile; Jackson databind/core/annotations 2.18.12 compile | 17 findings Jackson de 2.18.5 eliminados; nenhum atual reportado para 2.18.12 | Reescanear versão release final; OSV direto POM partial | Apache-2.0 |
| Maven Plugin | API/core/annotations Maven `provided`; módulos internos + report compile; Jackson 2.18.12 transitivo | Nenhum HIGH/CRITICAL confirmado no runtime. Guava `CVE-2026-102554` e Plexus são host Maven `provided` | Host Maven de consumidores pode conservar risco próprio | Sisu EPL-2.0 e componentes Maven host `provided`; validar pacote e avisos |

Os 50 registros agregados ainda incluem HIGH/CRITICAL no AssertJ de teste, Guava no caminho `maven-plugin -> Maven Core -> Resolver -> Guice -> Guava` com scope `provided`, e no runtime de Spring/Tomcat do exemplo excluído. A relação CVE/GHSA, severidade, caminhos e justificativas está em `docs/supply-chain-review.md`. O exemplo educacional pode permanecer no repositório sem ser publicado; por conter advisories HIGH/CRITICAL, não deve ser distribuído como aplicação nem usado como exemplo de produção antes de atualizar Spring Boot e revalidar.

**Classificação B8.2:** nenhum bloqueador confirmado de vulnerabilidade HIGH/CRITICAL no runtime dos artefatos Central; revisão humana de licenças/avisos e dos scopes publicados do testkit ainda pendente; CVEs de teste/host Maven `provided`/exemplo são não aplicáveis à distribuição atual com justificativas documentadas. As dependências Apache runtime e Jackson têm metadados Apache-2.0; a revisão humana deve confirmar NOTICE e obrigações no conteúdo real da distribuição. Scanner não é parecer jurídico. Nenhuma atualização técnica ou teste foi executado nesta etapa.

**B8.3 sugerido:** verificar os POMs e conteúdos release sem SNAPSHOT; preparar bundle apenas localmente sem credenciais para confirmar ausência do exemplo; manter Jackson 2.18.12 e realizar OSV/SBOM por artefato release; decidir correções isoladas do exemplo antes de qualquer distribuição dele; obter revisão humana das licenças e avisos. Nenhuma publicação está autorizada por este relatório.

## B8.3 Parte 1 — composição, licenças e metadados

`mvnw.cmd -B -ntp -Pcentral-release -DskipTests package` concluiu `BUILD SUCCESS`; os testes foram explicitamente pulados. Não foi executado `verify`, `deploy`, GPG, staging ou publicação. Foram gerados localmente cinco binários, cinco `sources.jar` e cinco `javadoc.jar`; o parent é POM-only, e o exemplo, embora empacotado localmente no reactor, mantém `skipPublishing=true` no effective POM B8.2. A lista de POMs, JARs e sources foi inspecionada; não há código ou bibliotecas Maven de terceiros embutidos nos cinco binários e sources. Javadocs incluem os assets padrão do doclet (jQuery/jQuery UI e seus arquivos `legal/`).

Mudanças técnicas restritas a publicação/licenças: o POM parent agora injeta cópias do `LICENSE` e `NOTICE` em `META-INF` dos cinco binários/sources e configura recursos legais no classifier Javadoc; cada POM de módulo repete name, description, URL, Apache license, developer e SCM. README agora documenta quais dependências `provided` do testkit o consumidor deve declarar no scope `test`. Não houve alteração de API, dependências, código funcional ou versão. Os testes não foram executados; o build do package pulou os testes.

| Artefato | Dependência/licença | Forma de distribuição | Obrigação | Evidência | Status |
|---|---|---|---|---|---|
| Parent POM | Apache-2.0 | POM, sem classes | Metadata de licença; parent continua obrigatório para consumidores | `pom.xml`; packaging `pom` | **OK técnico**; owner SCM aguarda confirmação |
| Core | Próprio código Apache-2.0 | JAR + sources | LICENSE/NOTICE, preservar avisos | JAR/source sob target, `META-INF/LICENSE` e `META-INF/NOTICE`; sem classes third-party | **CORRIGIDO** |
| Testkit | Próprio código Apache-2.0; JUnit EPL-2.0 e Jakarta Servlet EPL-2.0 OR GPL-2.0-with-classpath-exception `provided` | JAR + sources; bibliotecas não empacotadas | LICENSE/NOTICE do produto; consumidor declara JUnit/Spring/Servlet/Hamcrest no scope `test` conforme as APIs usadas; revisão humana para qualquer mudança de distribuição do Servlet API | POM/árvore B8.2; inspeção de API; README atualizado; nenhum pacote terceiro no JAR | **CORRIGIDO TECNICAMENTE**; revisar interpretação e classpath do consumidor |
| PIT Adapter | Código Apache-2.0; JUnit EPL-2.0 `test` | JAR + sources | LICENSE/NOTICE CIG; JUnit não acompanha runtime | POM/árvore B8.2; jar contém apenas classes CIG | **CORRIGIDO** |
| Report | Código CIG e Jackson Apache-2.0 runtime separado; JUnit EPL-2.0 `test` | JAR + sources | CIG LICENSE/NOTICE; Jackson permanece dependência Maven externa com seus avisos próprios | Árvore B8.2; nenhum pacote Jackson no JAR | **CORRIGIDO** |
| Maven Plugin | Código CIG/Jackson Apache-2.0; Sisu EPL-2.0 Maven host `provided`; JUnit EPL-2.0 `test` | Maven plugin jar + sources; host fornece Maven/Sisu | CIG LICENSE/NOTICE; Sisu/JUnit não estão embutidos | Árvore B8.2; nenhum pacote Maven/Sisu/JUnit no plugin JAR | **CORRIGIDO**; risco do host é separado |
| Classifiers Javadoc (5 módulos) | Assets do JDK standard doclet; jQuery/jQuery UI com avisos MIT | Cinco `-javadoc.jar` | Preservar avisos upstream íntegros e acessíveis | B8.9 compara byte a byte cinco avisos JDK, LICENSE/NOTICE do projeto e os seis arquivos estáticos do doclet nos cinco JARs | **APROVADO TECNICAMENTE — B8.9**; decisão não é certificação jurídica |
| Exemplo (excluído) | Logback EPL-1.0 OR LGPL-2.1; Jakarta Annotation EPL-2.0 OR GPL-2.0-with-classpath-exception | Runtime apenas local; fora do bundle Central | Reavaliar e cumprir termos se distribuir o exemplo separadamente | Effective POM B8.2 `skipPublishing=true`; SBOM e árvore do exemplo | **NÃO APLICÁVEL À DISTRIBUIÇÃO ATUAL** |

**Pendências fora do gate técnico de Javadoc:** validar os scopes `provided` e dependências que o consumidor precisa declarar para a API do testkit; aprovar interpretações jurídicas que dependam do contexto de distribuição de outras dependências ou do exemplo. A suficiência técnica dos avisos nos cinco classifiers foi fechada em B8.9. O scanner e a inspeção técnica não constituem aprovação jurídica independente.

Não criei um `THIRD-PARTY-NOTICES` agregado porque nenhum código de dependência Maven está incorporado nos binários/sources, e os assets de Javadoc trazem seus arquivos legais upstream. Se houver shading, assembly ou inclusão do exemplo, essa decisão deve ser reaberta. Release continua bloqueada até os itens humanos e gates finais serem aprovados; não houve publicação nem autorização para começar a próxima parte.

## B8.3 Parte 2 — gates técnicos finais desta etapa

**Snapshot histórico de 2026-11-19:** o diagnóstico de ponteiros JDK abaixo foi substituído pelo build Temurin/Linux e pela evidência B8.9.

O `LICENSE` permanece sem alteração. O SCM do parent e dos cinco módulos coincide com o `origin` local e a URL do [repositório público GitHub](https://github.com/dhionson2/code-integrity-guard). A descrição do parent POM foi alinhada à descrição pública do repositório; o README foi corrigido para remover o texto antigo de namespace/SCM pendentes, pois o projeto já registrou a verificação do namespace. O campo de developer continua como “Code Integrity Guard maintainers”; o [perfil público](https://github.com/dhionson2) exibe nome pessoal. O mantenedor ainda deve decidir se a atribuição publicada deve representar a equipe ou a pessoa.

`mvnw.cmd -B -ntp clean verify` passou nos sete projetos do reactor: 44 testes, zero falhas/erros; o testkit não possui testes próprios. `mvnw.cmd -B -ntp install` passou e instalou os snapshots apenas em `.m2` local. Fora do reactor, `integration-consumer> ..\mvnw.cmd -B -ntp verify` passou: dois testes Surefire (um desabilitado/skipped) e um teste Failsafe executado, zero falhas/erros. O consumidor executou `prepare-evidence`, `seal-evidence` e `audit` do Maven Plugin; o contrato independente terminou `VERIFIED`. A árvore Maven confirma que dependências `provided` do testkit não são herdadas. O consumidor declara JUnit, Spring Test, Spring MVC, Spring Security Test 7.0.7 e Servlet API no scope `test`; o teste usa `.with(user("alice"))`, provando que essa API é fornecida pelo consumidor. O testkit declara Spring Security Core, não Spring Security Test, porque apenas Core aparece nas importações do código. Hamcrest permanece `provided`: a tentativa de removê-lo resultou em erro de compilação (`cannot access org.hamcrest.Matcher`); a versão final compila e os testes passam. A evidência da árvore está em `docs/release-evidence/b8.3-consumer-dependency-tree.txt`.

Naquele snapshot, o `effective-pom` em `central-release` confirmou `skipPublishing=true` para o exemplo e os Javadocs usaram JDK 21.0.12.1/JBR. Os três avisos JDK eram ponteiros; essa pendência de empacotamento foi corrigida e fechada tecnicamente em B8.9. Não houve criação de bundle nem chamada a `publish`/`deploy`.

| Gate | Resultado | Evidência/pendência |
|---|---|---|
| Parent/POM metadata e URL/SCM | **PASSOU tecnicamente** | Seis POMs com campos exigidos; remote e URLs conferidos; descrição parent alinhada. Developer name exige decisão do mantenedor. |
| Avisos jQuery/JQuery UI nos Javadocs | **PASSOU tecnicamente** | Cinco classifiers contêm assets e textos de licença MIT/copyright correspondentes. |
| Avisos legais JDK nos Javadocs | **APROVADO TECNICAMENTE EM B8.9** | O status pendente desta fotografia foi substituído pelos textos integrais/hash verificados nos cinco classifiers Temurin; isso não é parecer jurídico. |
| `testkit` `provided` para consumidor | **PASSOU tecnicamente** | Consumer test compila/executa com JUnit, Spring Test, Spring Security Test, Servlet e APIs próprias; árvore confirma ausência de propagação transitiva de dependências provided. |
| Maven Plugin em consumidor independente | **PASSOU** | Goals prepare/seal/audit executaram no projeto externo ao reactor; teste Failsafe também passou. |
| Exclusão do exemplo | **PASSOU como configuração efetiva** | `skipPublishing=true` no effective POM `central-release`. Não foi criado bundle para inspeção. |
| `clean verify` do reactor | **PASSOU** | Todos os sete projetos SUCCESS; testes sem falha. |

Decisões humanas gerais ainda abertas naquela etapa: identidade exibida no metadata `developer` e revisão jurídica de licenças/redistribuição fora da verificação de completude de avisos dos Javadocs. B8.9 fecha somente o gate técnico de notices Javadoc, não aprova juridicamente a distribuição nem autoriza publicação. A versão `1.0.0-SNAPSHOT` foi preservada naquele snapshot.

## B8.3 Parte 3 — fechamento dos avisos legais (2026-11-19)

**Registro histórico:** esta execução usou JBR no Windows. A implementação, versão de jQuery UI e pendência humana aqui registradas foram substituídas pela preparação final com Temurin/Linux em B8.9; use B8.9 como status vigente do gate técnico de Javadoc.

Developer metadata agora coincide nos seis POMs candidatos (parent + cinco módulos): `dhionson2`, `Dhionson Plaster Moreira`, `https://github.com/dhionson2`. Os effective POMs dos seis projetos foram gerados e conferidos. `1.0.0-SNAPSHOT` foi preservado.

A origem dos ponteiros do Javadoc foi identificada no JBR que Maven realmente usa (`C:\Program Files\Android\Android Studio\jbr`, Java 21.0.8; doclet 21.0.8+-14018985-b1038.68). Os arquivos sob `legal/jdk.javadoc` continham apenas referências relativas para arquivos equivalentes sob `legal/java.base`; o doclet default não copiava a pasta-alvo. A configuração Maven agora copia, sem filtro nem edição, os três textos de `java.base` e os avisos `jquery.md` / `jqueryUI.md` de `jdk.javadoc` para uma pasta local de staging e passa essa pasta a `--legal-notices`. O primeiro teste da opção apontando para a raiz `legal` não incluiu a pasta irmã e não foi considerado correção.

Os cinco Javadoc JARs foram regenerados via `mvnw.cmd -B -ntp -Pcentral-release -DskipTests package` (`BUILD SUCCESS`). Em cada um, `legal/LICENSE`, `legal/ADDITIONAL_LICENSE_INFO`, `legal/ASSEMBLY_EXCEPTION`, `legal/jquery.md` e `legal/jqueryUI.md` existem. SHA-256 dos cinco documentos por JAR coincide com as origens do JBR; os três arquivos JDK não contêm mais referências quebradas. Os avisos jQuery e jQuery UI incluem MIT/copyright; jqueryUI também mantém seção CC0 relativa a código de demonstração. `NOTICE` raiz e recurso copiado no Javadoc foram atualizados em conjunto para descrever o conteúdo real. Detalhes dos hashes e caminhos constam em `docs/supply-chain-review.md`, seção B8.3 Parte 3.

`mvnw.cmd -B -ntp clean verify` passou nos sete projetos: 44 testes, zero falhas/erros. A execução de `package` com `-DskipTests` serviu apenas para gerar/inspecionar classifiers, e não foi contada como execução de testes. Os avisos agora estão tecnicamente íntegros, mas permanece decisão humana sobre suficiência jurídica/obrigações de redistribuição dos notices JDK e dos assets Javadoc. Não se declara aprovação jurídica. Nenhuma dependência ou funcionalidade foi alterada; não houve deploy, publicação, assinatura, uso de credenciais, commit, push ou tag.

| Gate | Resultado | Estado da revisão |
|---|---|---|
| Identidade developer efetiva nos seis POMs | **PASSOU** | Proprietário confirmou ID, nome e URL. |
| Empacotamento legal nos cinco Javadoc JARs | **PASSOU tecnicamente** | Fontes exatas do JBR executante verificadas por SHA-256; referências resolvidas. |
| Avisos de jQuery/jQuery UI preservados | **PASSOU tecnicamente** | MIT/copyright presente; seção CC0 de jQuery UI também preservada. |
| `clean verify` | **PASSOU** | 7 projetos, 44 testes, sem falhas/erros. |
| Aprovação legal final | **REQUER REVISÃO HUMANA** | Revisar suficiência dos avisos JDK/jQuery/JQuery UI e obrigações aplicáveis à redistribuição dos classifiers. |

A release permanece sem aprovação para publicação até a revisão humana e os gates de release pendentes. Esta atualização não prepara a release 1.0.0.
## B8.4 — Release candidate 1.0.0 preparada localmente (2026-11-20)

**Estado: preparada para revisão local; NÃO AUTORIZADA PARA PUBLICAÇÃO.** Esta preparação não cria tag, commit, push, staging ou publicação. Nenhum token/credencial Sonatype foi configurado ou usado.

### Versão, metadados e exclusão do exemplo

O parent, os cinco módulos candidatos, o exemplo local e as referências do consumidor independente foram alinhados de `1.0.0-SNAPSHOT` para `1.0.0`. Nenhum código Java ou versão de dependência foi alterado nesta etapa. O estado original B8 foi preservado.

Parent e cinco POMs publicáveis declaram developer `Dhionson Plaster Moreira`, id `dhionson2`, URL do developer e SCM `https://github.com/dhionson2/code-integrity-guard`, com connection/developerConnection apontando para o mesmo repositório GitHub. Foram conferidos os cinco POMs internos aos JARs, incluindo `LICENSE` e `NOTICE` nos binários, e os cinco Javadocs contêm os notices JDK/java.base e jQuery/jQuery UI descritos na B8.3 Parte 3. O exemplo resolve `skipPublishing=true` no perfil `central-release`; ele não faz parte do inventário Central abaixo. O empacotamento local do reactor ainda gera arquivos do exemplo em `examples/spring-boot-authorization/target`; estes não são candidatos a envio.

Não havia tags locais ou tags remotas visíveis no `origin` durante a consulta. A busca Maven Central para `g:"io.github.dhionson2" AND v:"1.0.0"` retornou 0 resultados. Não foi encontrada colisão de tag ou versão publicada `1.0.0` na consulta de 2026-11-20.

### Arquivos alterados nesta etapa e preservação B8

A preparação alterou as referências de versão nos POMs `pom.xml`, nos cinco POMs dos módulos publicáveis, em `examples/spring-boot-authorization/pom.xml` e em `integration-consumer/pom.xml`; atualizou este relatório; e criou os seis JSONs sob `docs/release-evidence/sbom-v1.0.0/`, além de atualizar `docs/installation.md`. As alterações B8 anteriores foram preservadas: `NOTICE`, `README.md`, `docs/supply-chain-review.md`, `docs/release-evidence/cyclonedx-bom.json`, o teste/POM do consumidor, os dois recursos de licença/NOTICE em `src/main/javadoc/resources/`, evidências B8.2/B8.3 e os relatórios OSV já registrados. O `git status` final continua mostrando todas essas alterações rastreadas e não rastreadas; nada foi descartado.
### Execuções desta preparação

- `mvnw.cmd -B -ntp clean verify` — **BUILD SUCCESS**, os sete projetos do reactor passaram; 44 testes passaram, 0 falhas/erros/ignorados. O contrato educacional do exemplo foi `VERIFIED` no fixture.
- Consumidor independente `mvnw.cmd -B -ntp -f integration-consumer/pom.xml clean verify` — **BUILD SUCCESS**; Surefire: 2 testes, 1 aprovado e 1 intencionalmente ignorado; Failsafe: 1 aprovado; contrato `CONSUMER-OWNER-CHECK` `VERIFIED`.
- PIT no exemplo local: gate de 60% passou com 7/11 mutações mortas (63.636%), 1 sobrevivente e 3 sem cobertura; gate de 70% falhou como esperado, sem tornar PIT inconclusivo. O exemplo segue excluído do Central.
- Tentativa complementar `mvnw.cmd -B -ntp install -DskipTests` **falhou como esperado** no gate do exemplo: ao pular testes, evidência ficou sem execução e o contrato foi `INCONCLUSIVE`. Não foi usada para validar testes. O consumer independente passou com os artefatos `1.0.0` instalados pelos estágios anteriores do reactor.
- `mvnw.cmd -B -ntp -Pcentral-release -DskipTests package` — **BUILD SUCCESS**; gerou localmente os cinco binários, cinco sources JARs e cinco Javadoc JARs. Testes foram pulados somente nesta etapa de empacotamento; sua validação ocorreu separadamente por `clean verify`.
- Assinaturas: GPG está instalado, mas não há chave secreta disponível (`gpg --list-secret-keys` não encontrou entradas secretas). Nenhuma assinatura foi criada; nenhuma credencial foi solicitada ou exposta.

### SBOM e riscos de supply chain

Foram gerados seis SBOMs CycloneDX 1.6 JSON, um por artefato publicável, em `docs/release-evidence/sbom-v1.0.0/`: parent POM (0 dependências), core (8 componentes), testkit (18), PIT adapter (9), report (15) e Maven Plugin (46). O gerador emitiu avisos de validação de keywords desconhecidas (`meta:enum` e `deprecated`), mas produziu os cinco arquivos. Não foi encontrada instalação de `osv-scanner`; por isso não há scan OSV atualizado desses SBOMs.

O último scan OSV registrado em B8.1/B8.2 foi sobre SBOM agregado de 109 componentes e encontrou 50 advisory records em 14 componentes, incluindo findings HIGH/CRITICAL no exemplo educacional excluído e findings em dependências de teste/`provided`/host Maven. As correções Jackson 2.18.12 eliminaram os findings Jackson antigos segundo aquela consulta. O scan de POM direto permaneceu parcial para SNAPSHOTs internos. Dependency-Check também não gerou relatório: a execução anterior parou por falta de chave NVD. Esses dados são riscos conhecidos históricos, não resultado de scan da presente RC nem afirmação de zero vulnerabilidades. Manter a análise SCA atualizada como gate antes de qualquer publicação.

### Obrigações e decisões humanas de licença

- **Atendido tecnicamente:** licença Apache-2.0 do projeto está no repositório e `LICENSE`/`NOTICE` acompanham os binários e sources.
- **Notices incluídos; revisão de suficiência aberta:** notices originais jQuery MIT e jQuery UI MIT/CC0, além dos textos `java.base` do JDK executante, estão nos cinco classifiers Javadoc e foram verificados. Ainda requer revisão humana de suficiência, aplicabilidade e obrigações de redistribuição; sua inclusão não é parecer jurídico.
- **Decisão humana aberta:** revisar metadados e termos de dependências EPL-2.0, LGPL/GPL com classpath exception e demais licenças alternativas, especialmente APIs `provided`, bibliotecas de teste e Maven host; confirmar os avisos necessários conforme o conteúdo efetivamente distribuído. O inventário e a análise de escopos estão em `docs/supply-chain-review.md`.
- **Excluído da distribuição planejada:** o exemplo Spring Boot permanece educacional, contém findings de segurança conhecidos e está configurado com `skipPublishing=true`; não deve ser distribuído separadamente sem atualização e nova revisão técnica/legal.
- **Nenhuma aprovação jurídica automática foi declarada.** Decisão final de compatibilidade e suficiência pertence ao mantenedor/revisor jurídico.

### Inventário exato dos candidatos ao Maven Central

O `central-release` produziu localmente e o inventário candidato contém 21 arquivos: o POM parent, um POM para cada um dos cinco módulos e, para cada módulo, o JAR principal, `-sources.jar` e `-javadoc.jar`. Os POMs são os arquivos fonte de cada projeto; o `package` local não criou cópias deles sob `target/`. Checksums `.asc`, bundle/staging, arquivos do exemplo, SBOMs e resultados de build não foram gerados como arquivos de publicação Central nesta preparação.

- `pom.xml` (POM do parent; fonte do artefato POM-only)
- `code-integrity-guard-core/pom.xml`
- `code-integrity-guard-testkit/pom.xml`
- `code-integrity-guard-pit-adapter/pom.xml`
- `code-integrity-guard-report/pom.xml`
- `code-integrity-guard-maven-plugin/pom.xml`
- `code-integrity-guard-core/target/code-integrity-guard-core-1.0.0.jar`
- `code-integrity-guard-core/target/code-integrity-guard-core-1.0.0-sources.jar`
- `code-integrity-guard-core/target/code-integrity-guard-core-1.0.0-javadoc.jar`
- `code-integrity-guard-testkit/target/code-integrity-guard-testkit-1.0.0.jar`
- `code-integrity-guard-testkit/target/code-integrity-guard-testkit-1.0.0-sources.jar`
- `code-integrity-guard-testkit/target/code-integrity-guard-testkit-1.0.0-javadoc.jar`
- `code-integrity-guard-pit-adapter/target/code-integrity-guard-pit-adapter-1.0.0.jar`
- `code-integrity-guard-pit-adapter/target/code-integrity-guard-pit-adapter-1.0.0-sources.jar`
- `code-integrity-guard-pit-adapter/target/code-integrity-guard-pit-adapter-1.0.0-javadoc.jar`
- `code-integrity-guard-report/target/code-integrity-guard-report-1.0.0.jar`
- `code-integrity-guard-report/target/code-integrity-guard-report-1.0.0-sources.jar`
- `code-integrity-guard-report/target/code-integrity-guard-report-1.0.0-javadoc.jar`
- `code-integrity-guard-maven-plugin/target/code-integrity-guard-maven-plugin-1.0.0.jar`
- `code-integrity-guard-maven-plugin/target/code-integrity-guard-maven-plugin-1.0.0-sources.jar`
- `code-integrity-guard-maven-plugin/target/code-integrity-guard-maven-plugin-1.0.0-javadoc.jar`
### Bloqueadores antes de qualquer envio

1. Revisão humana de licenças/notices e decisão explícita de distribuição.
2. Scan SCA atualizado e triagem dos findings da composição exata 1.0.0; a execução anterior do Dependency-Check não foi válida e o OSV não foi repetido nesta RC.
3. Assinatura GPG local ainda não existe; preparar chave em ambiente seguro e validar assinaturas antes de um envio autorizado.
4. Revisar sobrevivente/no-coverage do PIT e riscos documentados sem inferir segurança do produto.

**Nenhum deploy, goal `deploy`, publicação, staging, tag, commit, push ou uso/configuração de credenciais ocorreu.** A RC 1.0.0 fica preparada apenas nos arquivos locais para revisão humana.

## B8.5 — Resultado do gate OSV da RC 1.0.0 (2026-11-20)

**Parecer técnico: BLOQUEADO PARA PUBLICAÇÃO até decisão/ação humana sobre segurança e fechamento da evidência inconclusiva do parent.** Foram validados os seis SBOMs CycloneDX e executados cinco scans OSV-Scanner v2.7.1 completos; core, PIT adapter e report concluíram sem advisories, testkit e Maven Plugin concluíram com achados, e o SBOM do parent retornou exit 128 sem pacotes extraídos — inconclusivo, nunca “clean”.

Resultado consolidado: **3 HIGH, 2 MODERATE, 3 LOW, 0 CRITICAL**. Os HIGH identificados são Spring Expression 6.2.15 / CVE-2026-41850 no testkit (`provided` para classpath de testes de consumidores), Guava 33.2.1-jre / CVE-2026-102554 e Plexus Utils 3.5.1 / CVE-2025-67030 no Maven Plugin (`provided` do host Maven). Não foram encontrados HIGH/CRITICAL entre as dependências compile/runtime empacotadas dos cinco JARs; os findings `provided` ainda exigem decisão do mantenedor porque afetam classpath de testes/host que executa o plugin. O exemplo foi excluído dos seis SBOMs e segue fora do inventário Central.

A leitura B8.2 foi comparada, não reutilizada: Guava/Plexus foram confirmados de novo com a composição 1.0.0 e scopes atuais; Jackson 2.18.12 não apareceu vulnerável nesta consulta; CVE-2026-41850 agora também se aplica ao Spring Expression fornecido no SBOM do testkit, embora B8.2 o tratasse no contexto do exemplo. A contagem agregada antiga não substitui estes resultados individuais.

Saídas completas por SBOM, com exit code, stderr e JSON da consulta: `docs/release-evidence/osv-v1.0.0/sbom-*.osv-scan.json`. Consolidação por artefato/componente/CVE/severidade/versão/scope: `docs/release-evidence/osv-v1.0.0/osv-v1.0.0-consolidated.json`. Dependency trees novos dos cinco módulos, usados para classificação de scope: `docs/release-evidence/osv-v1.0.0/*-dependency-tree.txt`.

Gates restantes: fechar o parent sem alegação de scan limpo; obter decisão explícita sobre três HIGH `provided`/host ou exigir remediação; manter o exemplo excluído. Nenhuma dependência, código Java ou versão foi alterada. Não houve credenciais Sonatype/GPG, commit, push, tag, deploy ou publicação.

## B8.6 — Fechamento da análise de vulnerabilidades da RC 1.0.0 (2026-11-20)

**Parecer: NÃO LIBERAR para publicação ainda.** Nenhum código Java, dependência ou versão foi alterado, então não foram executados testes. O resultado OSV B8.5 permanece: parent inconclusivo sem pacotes; core, PIT adapter e report sem achados; testkit com seis instâncias; Maven Plugin com duas; total OSV 3 HIGH, 2 MODERATE, 3 LOW e 0 CRITICAL. A checagem de advisories oficiais revelou ainda CVE-2026-59282 para Spring Framework 6.2.15–6.2.19, ausente da consolidação OSV B8.5.

| Achado / artefato | Classificação nesta revisão |
|---|---|
| Spring Expression 6.2.15 / CVE-2026-41850 HIGH — testkit `provided` | **RISCO RESIDUAL JUSTIFICADO**: sem avaliação SpEL no source do testkit; aplicação consumidora escolhe versão e precisa proteger expressões que avalia. Framework 6.2.19 corrigiria este advisory isolado, mas não toda a linha vulnerável atual. |
| Spring Core/Expression 6.2.15 / CVE-2026-41848 LOW, CVE-2026-41851 MODERATE, CVE-2026-41852 LOW e CVE-2026-59282 — testkit `provided` | **RISCO RESIDUAL JUSTIFICADO** no código CIG: APIs afetadas não encontradas no testkit, não empacotadas; CVE-2026-59282 está ausente da varredura OSV e requer nova consulta. Não afirmar que SBOM 1.0.0 está sem findings. |
| Spring Security Core 6.4.13 / CVE-2026-22746 LOW e CVE-2026-22751 MODERATE — testkit `provided` | **RISCO RESIDUAL JUSTIFICADO**: testkit não usa os componentes/cenários afetados; gestão da versão efetiva pertence ao consumidor. |
| Guava 33.2.1-jre / CVE-2026-102554 HIGH — Maven Plugin / host Maven | **BLOQUEADOR** enquanto README aceitar Maven 3.9+: Maven 3.9.16 ainda distribui Guava afetada. Necessária decisão e validação de mínimo ≥3.10.1 ou mitigação equivalente no host. OSV HIGH/8.2 coincide com GHSA HIGH/CVSS v4 8.2. |
| Plexus Utils 3.5.1 / CVE-2025-67030 HIGH — Maven Plugin / host Maven | **BLOQUEADOR** enquanto versões Maven anteriores ao fix permanecerem suportadas. O código do plugin não chama `Expand.extractFile`, mas não controla a versão carregada pelo host. Maven 3.10.1 corrige Plexus e Guava simultaneamente. |
| Parent POM | **NÃO APLICÁVEL AO PARENT POM**: sem seção `<dependencies>` própria. OSV exit 128 permanece registrado como inconclusivo para scanner, não como scan limpo. |

**Bloqueadores técnicos restantes:** política publicada `Maven 3.9+` permite host com Guava vulnerável; a versão usada pelo wrapper e a matriz do produto precisam ser decididas e alinhadas, com build e execução do plugin no mínimo corrigido. A auditoria OSV não contém CVE-2026-59282; reexecutar OSV após esclarecer a versão Spring suportada. As classificações residuais de Spring exigem revisão/aceitação humana e orientação ao consumidor. A exclusão do exemplo do Central permanece confirmada nos SBOMs.

Não houve atualização de dependências, testes, commit, push, tag, deploy, publicação, credencial Sonatype ou GPG. A revisão e decisão humana são necessárias antes de qualquer envio.

## B8.6 (continuação) — Validação do Maven Wrapper 3.10.1 (2026-11-20)

### Wrapper e host

O Wrapper local agora usa Apache Maven 3.10.1, mantendo wrapper 3.3.4, tipo `bin` e URL do wrapper. A distribuição foi obtida de `downloads.apache.org`; SHA-512 oficial e local conferem. Configurei `distributionSha256Sum` com o SHA-256 da distribuição verificada. `mvnw.cmd -version` confirmou Maven 3.10.1 (`3afd8084b5629f80b97750906a719879ed4cd283`) e Java 21.0.8. A versão do projeto continua 1.0.0.

Host 3.10.1 efetivamente executado:

- Guava **33.8.0-jre** (fix da CVE-2026-102554: 33.7.2).
- Plexus Utils **3.6.2** (fix da CVE-2025-67030: 3.6.1).

Esses dois advisories não se aplicam a essas versões na distribuição local. A árvore publicada/configurada do Maven Plugin, contudo, ainda referencia Maven Core/API 3.9.9 `provided`, portanto a árvore mostra Guava 33.2.1-jre e Plexus Utils 3.5.1. O Wrapper atualizado não altera esse POM nem restringe a versão de Maven instalada pelos usuários.

### Validação executada

| Comando | Resultado |
|---|---|
| `mvnw.cmd -B -ntp clean install` | **BUILD SUCCESS**, sete projetos; **44 testes passaram**, 0 falhas, 0 erros, 0 skips; sem `-DskipTests`. |
| `mvnw.cmd -B -ntp -f integration-consumer\pom.xml clean verify` | **BUILD SUCCESS**; Surefire 1 passou e 1 skip intencional; Failsafe 1 passou; contrato CIG `CONSUMER-OWNER-CHECK` `VERIFIED`. |
| PIT 60% | **BUILD SUCCESS**; PIT `AVAILABLE`, 7/11 = 63.636%, `partialCoverage=true`; contrato `VERIFIED`; um mutante sobrevivente e três `NO_COVERAGE`. |
| PIT 70% | **BUILD FAILURE esperado** pelo quality gate de score; PIT permaneceu `AVAILABLE` a 7/11 e o contrato ficou `VERIFIED`. |

Os goals `prepare-evidence`, `seal-evidence` e `audit` funcionaram em Maven 3.10.1 no exemplo e no consumidor independente.

Evidências selecionadas em `docs/release-evidence/b8.6-maven-3.10.1/`: `distribution-checksums-and-host-deps.txt`, `maven-host-libraries.txt`, `plugin-dependency-tree.txt`, `test-summary.txt`, o relatório JSON do consumidor e os relatórios/resumos/XMLs PIT para os limites 60 e 70%. XMLs Surefire/Failsafe brutos, `maven-version.txt` e logs permanecem locais, pois incluem classpaths, caminhos do usuário/workspace ou ambiente da máquina; os resultados aprovados são resumidos sem esses dados em `test-summary.txt` e nos relatórios retidos.

### Compatibilidade e decisões pendentes

O README segue anunciando Maven **3.9+**; apenas documenta que o Wrapper local usa 3.10.1. A referência oficial do Maven suporta declarar `<prerequisites><maven>3.10.1</maven></prerequisites>` no POM do plugin para verificar a versão em runtime. Isso impediria a execução dos goals em Maven 3.9.x e 3.10.0, quebrando o suporte anunciado a esses consumidores. `requiredMavenVersion` no descritor isoladamente não garante esse bloqueio em Maven 3 (é ignorado antes do Maven 4); Enforcer executado no build deste projeto não restringe runtime dos usuários. Não implementei requisito/runtime check nem alterei as versões de `maven-core`/`maven-plugin-api` do plugin sem decisão humana.

**Decisão humana necessária:** adotar ou não mínimo 3.10.1. Se aprovado, alinhar POM do plugin e prerequisite/descritor, validar a rejeição/execução na matriz pertinente e regenerar SBOM/OSV. Se Maven 3.9 continuar suportado, Guava do host permanece bloqueador para esse suporte; o resultado local com 3.10.1 não constitui mitigação para esses usuários. Spring segue em Framework 6.2.15/Security 6.4.13 e seus riscos registrados não foram atualizados nem aceitos automaticamente. A lacuna OSV CVE-2026-59282 precisa entrar no próximo scan.

Não houve alteração Java, atualização Spring, alteração da versão 1.0.0, commit, push, tag, deploy, publicação, credencial Sonatype ou GPG. O gate de publicação continua **BLOQUEADO** até decisão de compatibilidade Maven e fechamento do novo scan Spring/OSV.

## B8.7 — Baseline mínima Maven 3.10.1 (2026-11-20)

### Implementação e validação

- O POM de `code-integrity-guard-maven-plugin` declara prerequisite Maven 3.10.1; `maven-core` e `maven-plugin-api` estão alinhados em 3.10.1, ambos `provided`. Não há dependências diretas Guava/Plexus Utils.
- README e instalação declaram Java 21+ e Maven 3.10.1+.
- Maven Wrapper reporta Apache Maven 3.10.1 e Java 21.0.8.
- Teste negativo com Maven 3.9.16 rejeitou o goal com `Required Maven version 3.10.1 is not met by current version 3.9.16`.
- Descritor gerado e POM instalado localmente declaram 3.10.1 como mínimo. Não houve publicação; a cópia instalada valida o metadado preparado, não um artefato remoto.
- Árvore do plugin: `maven-core:3.10.1:provided` e `maven-plugin-api:3.10.1:provided`; sem Guava/Plexus adicionados diretamente.
- SBOM CycloneDX do plugin regenerado (55 componentes) e OSV-Scanner 2.7.1 concluído com exit code 0, sem findings e sem falhas de consulta/parse. Guava 33.8.0-jre e Plexus Utils 3.6.2 são as versões efetivas do Maven 3.10.1 e estão após os fixes dos advisories CVE-2026-102554 e CVE-2025-67030.

| Gate em Maven 3.10.1 | Resultado |
|---|---|
| `clean install` do reactor, sem `-DskipTests` | **BUILD SUCCESS**, 7 projetos, **44 testes**, 0 falhas/erros/skips. |
| `integration-consumer clean verify` | **BUILD SUCCESS**; 2 testes Surefire (1 passou, 1 skip intencional), 1 IT passou; contrato `CONSUMER-OWNER-CHECK VERIFIED`. |
| PIT 60% | **BUILD SUCCESS**; 7/11 = 63.636%, `AVAILABLE`, contrato `VERIFIED`. |
| PIT 70% | **BUILD FAILURE esperado** no quality gate; PIT `AVAILABLE`, 7/11, contrato `VERIFIED`. |

Evidências selecionadas B8.7: descritor e árvore Maven em `docs/release-evidence/b8.7-maven-baseline/`, resultado OSV completo com scanner/exit code nessa pasta e SBOM final em `docs/release-evidence/sbom-v1.0.0/sbom-code-integrity-guard-maven-plugin-1.0.0.json`. A rejeição de Maven 3.9.16 está reduzida ao resultado sem caminhos locais em `maven-3.9.16-rejection.txt`; logs brutos, versões de host com paths e cópias SBOM redundantes permanecem locais.

### Riscos e bloqueadores restantes

CVE-2026-102554 (Guava) e CVE-2025-67030 (Plexus Utils) deixam de bloquear a baseline hospedada em Maven 3.10.1; usuários de hosts fora do mínimo serão rejeitados pelos goals. A elevação do requisito é uma quebra de compatibilidade para Maven 3.9.x e 3.10.0, explicitada na documentação e no prerequisite.

Spring continua sem alteração. O testkit retém Spring Framework 6.2.15 / Spring Security 6.4.13 em `provided`; CVE-2026-41850, advisories Spring Security/Core anotados anteriormente e CVE-2026-59282 permanecem **riscos residuais**, não corrigidos nem aprovados automaticamente. A CVE-2026-59282 depende de data binding de propriedades fornecidas por usuário sobre uma lista auto-populável com subpropriedades; isso não foi demonstrado como alcançável pelo testkit, mas a dependência continua presente. A correção OSS indicada pela Spring é 7.0.9; 6.2.20 é Enterprise Support Only. Mantenedor/consumidor ainda deve revisar esses riscos e decidir a matriz Spring apropriada.

O gate técnico Maven 3.10.1 passou, mas a liberação continua pendente da revisão humana do risco Spring e dos gates de licenciamento/assinatura já abertos nas seções anteriores. Nenhuma alteração foi publicada nem enviada; versão permanece 1.0.0.

## B8.8 — Parecer final incremental de segurança Spring (2026-11-20)

### CVEs divulgadas em 19/11/2026

| CVE | Resultado para os seis artefatos / configurações avaliadas |
|---|---|
| CVE-2026-59273 — CRITICAL (ELRequestMatcher) | **Não aplicável com evidência ao testkit**: `spring-security-web` não está na árvore efetiva nem nos SBOMs publicáveis; o JAR só tem classes CIG e as APIs não vinculam entrada externa a matcher. O exemplo excluído tem `spring-security-web:6.4.13` vulnerável por versão, mas não usa ELRequestMatcher nem expõe binding de propriedade correspondente. Nenhum caminho CRITICAL foi confirmado. |
| CVE-2026-59398 — MEDIUM (negociação por parâmetro) | **Não aplicável com evidência à configuração atual**: testkit não depende de MVC/WebFlux; exemplo tem MVC 6.2.15, mas não ativa `favor-parameter` (Boot default `false`) e não configura estratégia equivalente. Risco futuro pertence ao consumidor que ative essa opção. |
| CVE-2026-59389 — MEDIUM (multipart part smuggling) | **Não aplicável com evidência**: não há WebFlux no testkit/exemplo, nem multipart inspecionado e encaminhado a downstream. Consumidor que implemente esse fluxo deve revisar sua versão/configuração. |

Árvores atualizadas e inventário/source review incremental estão em `docs/release-evidence/b8.8-spring-risk-review/`. A fixture independente usa Spring Security Test 7.0.7 exclusivamente em teste, com `.with(user(...))`; não testa matcher alimentado por entrada externa e não é artefato publicável.

### Riscos Spring anteriores e decisão

Permanecem no testkit os metadados `provided` Spring Expression 6.2.15 e Spring Security Core 6.4.13. Os findings previamente registrados CVE-2026-41850 (HIGH), CVE-2026-41851 (MODERATE), CVE-2026-41852 (LOW), CVE-2026-41848 (LOW), CVE-2026-22751 (MODERATE) e CVE-2026-22746 (LOW), além da CVE-2026-59282 (MEDIUM, advisory oficial ausente do scan OSV anterior), não foram remediados. O código CIG não usa as funções vulneráveis identificadas; o risco residual é o consumidor usar essas versões/configurações vulneráveis. `provided` não é tratado como correção nem como prova de segurança.

**Parecer: NO-GO para distribuição até aceite explícito do mantenedor** dos riscos Spring residuais listados na revisão supply-chain. Não foi confirmado bloqueador explorável novo nos seis artefatos; a condição para mudar o parecer é a decisão humana documentada sobre os advisories residuais existentes, não a execução de novas alterações nesta missão. Após esse aceite, os três advisories novos são não aplicáveis às composições/fluxos CIG examinados; aplicações consumidoras ainda devem avaliar suas próprias versões e configurações. Este parecer é específico à distribuição 1.0.0 examinada e não declara o produto ou consumidores livres de vulnerabilidades.

Não alterei dependências, código Java ou versão, e não executei testes por não haver alteração técnica. Nenhuma publicação, credencial, commit, push, tag ou deploy foi usada. A release continua local e aguarda a decisão do mantenedor.

## B8.9 — Fechamento técnico do licenciamento dos Javadocs (2026-11-20)

### Decisão e solução

**Gate técnico de avisos/licenciamento dos cinco Javadoc JARs: APROVADO.** O perfil `central-release` seleciona os textos legais da instalação do JDK em uso através de `${java.home}` (sem caminhos JBR/Windows), copia-os sem filtragem para `${project.build.directory}/javadoc-legal` e passa esse diretório absoluto ao `javadoc --legal-notices`. A documentação oficial do JDK 21 define essa opção como a origem dos arquivos legais copiados para a documentação. A abordagem mantém os arquivos upstream sem edição e evita os ponteiros relativos que o doclet padrão emitiu para `java.base`.

O CI configura `ubuntu-latest` e Eclipse Temurin `21`. A validação local equivalente usou Linux x86_64/Ubuntu e a distribuição oficial Eclipse Temurin `21.0.13.1+2` (17/11/2026), baixada do release oficial e validada pelo SHA-256 `bb8a93f9ae6e7a693b2b721e85dbeff26ae1a77447985a8d7afcdd9c7cf6222e`; o resultado do checksum está em `temurin-archive-checksum.txt`. O script `docs/release-evidence/javadoc-license-b8.9/validate-temurin-linux.sh` pode repetir a verificação e o package sem caminhos locais Windows/JBR. O patch futuro de Java pode mudar os assets upstream, mas a cópia acompanha sempre o JDK realmente usado e a evidência registra versão e hashes; para repetir os mesmos bytes, use a distribuição/hash registrados.

### Materiais e avisos presentes

| Material efetivamente incluído nos classifiers | Licença/obrigação observada | Local no JAR e verificação |
|---|---|---|
| `script-dir/jquery-3.7.1.min.js` | MIT; aviso de copyright OpenJS e texto de permissão devem acompanhar cópias/substantial portions | `legal/jquery.md`; versão conferida no cabeçalho; aviso integral sem alteração |
| `script-dir/jquery-ui.min.js`, `script-dir/jquery-ui.min.css`, `resources/glass.png`, `resources/x.png` | jQuery UI 1.14.1, MIT. O arquivo upstream também indica CC0 apenas para o código de demonstração no diretório `demos` | `legal/jqueryUI.md`; aviso MIT e nota CC0 íntegros. Nenhum diretório/arquivo de demos está no JAR |
| `script.js`, `search.js`, `search-page.js`, `copy.svg`, `link.svg`, `stylesheet.css` do standard doclet | Materiais do JDK; os JS e SVG inspecionados mantêm os cabeçalhos GPLv2-only com Classpath Exception; sem edição dos arquivos | Raiz do Javadoc. Os seis arquivos coincidem byte a byte com a saída `target/reports/apidocs` do Temurin selecionado; cabeçalhos conferidos para GPLv2/Classpath |
| Avisos legais do JDK: GPLv2, informação da Classpath Exception e OpenJDK Assembly Exception | Preservar os textos completos fornecidos pela distribuição Temurin que gerou os Javadocs | `legal/LICENSE`, `legal/ADDITIONAL_LICENSE_INFO`, `legal/ASSEMBLY_EXCEPTION`; hashes abaixo |
| Código/documentação CIG no classifier | Apache License 2.0; distribuir cópia da licença e manter avisos aplicáveis do NOTICE | `resources/LICENSE` e `resources/NOTICE` em cada JAR; ambos byte a byte idênticos às fontes do projeto |

Hashes SHA-256 das fontes do Temurin e dos arquivos empacotados são iguais nos cinco JARs:

| Arquivo | SHA-256 |
|---|---|
| JDK `LICENSE` | `4b9abebc4338048a7c2dc184e9f800deb349366bdf28eb23c2677a77b4c87726` |
| `ADDITIONAL_LICENSE_INFO` | `a69bce275ba7a3570af6579cb0f55682cd75fedfcd49e0e8e9022270c447c916` |
| `ASSEMBLY_EXCEPTION` | `75292f03bf23d3db7c985aecc191029b93883200721ed23ed34a2e601463df33` |
| `jquery.md` | `b68f454c2bd58959c862d94bf0f16c3f78a2d537388ca060d354344db80ee695` |
| `jqueryUI.md` | `7854d2d568992ae25ff65867bd9021d655f70670142f0796b29a3b0621f19530` |
| LICENSE do projeto | `cfc7749b96f63bd31c3c42b5c471bf756814053e847c10f3eb003417bc523d30` |
| NOTICE do projeto | `a1b723d9e939ea5ed87a0db2e38e36af7113a160fa3a6d72288951859675b8aa` |

### Geração e inventário candidato

`./mvnw -B -ntp -Pcentral-release -DskipTests package` terminou `BUILD SUCCESS` no Linux com Temurin 21.0.13.1+2. Os cinco marcadores incrementais foram invalidados individualmente para confirmar a geração dos classifiers. O probe padrão mostrou por que a configuração adicional é necessária: três arquivos `java.base` eram apenas referências; `jquery.md` e `jqueryUI.md` estavam presentes. A auditoria selecionada confere os cinco classifiers, sete avisos/licenças, seis recursos estáticos, ausência dos ponteiros e ausência de demos. Resultado e hashes: `docs/release-evidence/javadoc-license-b8.9/final-javadoc-audit.txt`; reprodução, versão, checksum e saída do probe estão documentados no mesmo diretório. Logs de build e o arquivo de hashes com caminho temporário permanecem locais.

Candidatos Javadoc 1.0.0:

- `code-integrity-guard-core/target/code-integrity-guard-core-1.0.0-javadoc.jar`
- `code-integrity-guard-testkit/target/code-integrity-guard-testkit-1.0.0-javadoc.jar`
- `code-integrity-guard-pit-adapter/target/code-integrity-guard-pit-adapter-1.0.0-javadoc.jar`
- `code-integrity-guard-report/target/code-integrity-guard-report-1.0.0-javadoc.jar`
- `code-integrity-guard-maven-plugin/target/code-integrity-guard-maven-plugin-1.0.0-javadoc.jar`

O exemplo Spring Boot gerou um Javadoc local por participar do reactor, mas não é candidato Central: o effective POM atual mantém `skipPublishing=true` no `central-publishing-maven-plugin` (evidência compacta: `docs/release-evidence/javadoc-license-b8.9/example-central-exclusion.txt`). A geração local do exemplo não muda sua exclusão.

### Obrigações e escopo da aprovação

Não foi identificada obrigação técnica de aviso/licença dos materiais listados ausente nos cinco classifiers: a licença e o NOTICE Apache do projeto estão junto ao classifier; os avisos MIT completos de jQuery/jQuery UI acompanham seus arquivos e assets; a nota CC0 se aplica apenas aos demos, que não são redistribuídos; e os três textos JDK completos foram copiados da mesma instalação Temurin que gerou os arquivos do doclet. As cópias upstream comparadas são byte idênticas e nenhum aviso foi suprimido.

A compatibilidade jurídica da combinação de materiais GPLv2/Classpath Exception, OpenJDK Assembly Exception, MIT e Apache-2.0 não foi certificada por esta verificação técnica. O gate aprovado afirma apenas que os materiais observados e os avisos correspondentes foram inventariados, preservados e empacotados de modo reproduzível; não é parecer jurídico independente.

Na conclusão da B8.9, a publicação ainda dependia do aceite humano dos riscos Spring. A decisão posterior B8.10 registra esse aceite limitado, sem alterar os findings nem classificá-los como corrigidos e sem abranger aplicações consumidoras. O gate aprovado é técnico, não certificação jurídica independente. Não houve testes nesta etapa porque não houve mudança em Java/dependências; o package usou `-DskipTests`. Não houve commit, push, tag, deploy, publicação, assinatura ou credenciais Sonatype.

## B8.10 — Fechamento da RC para revisão do GitHub (2026-11-20)

### Decisões vigentes e status

**GO para revisão do commit da RC no GitHub; publicação no Maven Central ainda não autorizada.** O mantenedor aceitou limitadamente os riscos Spring residuais enumerados na revisão de supply chain para o escopo auditado dos seis artefatos 1.0.0. O aceite não os corrige, não significa ausência de vulnerabilidades e não cobre configurações ou aplicações consumidoras. As classificações e orientações permanecem explícitas em `docs/supply-chain-review.md` e no README.

O gate técnico de avisos dos cinco Javadoc JARs continua **APROVADO**, com comparação reproduzível contra Eclipse Temurin 21 no Linux/CI e os avisos upstream completos. Isso não declara certificação jurídica independente. Nenhuma obrigação técnica de aviso identificada na auditoria B8.9 ficou sem correspondência nos classifiers examinados.

| Gate | Estado vigente | Limite / evidência |
|---|---|---|
| Maven/compilação/testes | APROVADO | Baseline Java 21+ / Maven 3.10.1+; reactor 44/44 testes, consumidor independente e goals validados; Maven 3.9.16 rejeitado conforme prerequisite. Evidências B8.6/B8.7. |
| PIT | APROVADO nos limites definidos | 60% passa com 7/11 (63,636%); 70% falha como esperado; PIT disponível e contrato verificado. Os mutantes sobrevivente/sem cobertura continuam sendo lacunas de teste. |
| Segurança da composição 1.0.0 | GO com aceite residual limitado | Findings Spring registrados continuam não corrigidos e foram aceitos apenas na composição/fluxos auditados. CVE-2026-59273, CVE-2026-59398 e CVE-2026-59389 não se aplicam com evidência às composições/configurações examinadas. O exemplo Spring Boot continua excluído. |
| SBOM/OSV | Concluído com escopo declarado | Seis SBOMs 1.0.0 e seis saídas OSV estão em `docs/release-evidence/sbom-v1.0.0/` e `docs/release-evidence/osv-v1.0.0/`. O parent tem zero dependências próprias; sua saída vazia/inconclusiva é classificada como não aplicável com base no POM, não como scan limpo. |
| Licenças/notices Javadoc | APROVADO tecnicamente | Cinco classifiers Temurin/Linux contêm os textos completos verificados; aprovação técnica não é certificação jurídica. |
| Versão/metadados | CONFIRMADO | Parent + cinco módulos candidatos: `1.0.0`, developer `Dhionson Plaster Moreira`, id `dhionson2` e SCM do repositório. O exemplo não integra o grupo e `skipPublishing=true` no effective POM `central-release`. |

### Consistência do inventário

Os seis SBOMs CycloneDX correspondem ao parent POM (sem dependências próprias) e aos cinco artefatos de módulo: core, testkit, PIT adapter, report e Maven Plugin, todos em `1.0.0`. Os metadados XML dos seis POMs foram relidos nesta revisão. `docs/release-evidence/cyclonedx-bom.json` é o SBOM agregado histórico B8.1 com versão `1.0.0-SNAPSHOT`; não é artefato nem SBOM final da RC. Os seis arquivos em `sbom-v1.0.0/` são a fonte de inventário da versão preparada.

### Proposta de commit e limites desta revisão

O stage curado inclui POMs/configuração do Wrapper, README/NOTICE/documentação, fixture do consumidor e evidências compactas necessárias de SBOM/OSV, árvores, testes/PIT e Javadocs. `docs/release-evidence/cyclonedx-bom.json` entra somente como histórico B8.1 `1.0.0-SNAPSHOT`, nunca como SBOM final da release. Mantêm-se localmente — sem apagar — effective POMs exportados B8.2/B8.3/B8.9 (caminhos absolutos e metadados gerados), XMLs Surefire/Failsafe e logs B8.6/B8.7 (classpaths, caminhos do usuário e ambiente), logs de build Javadoc, hashes emitidos sob `/tmp`, saídas duplicadas do scanner/SBOM e evidências obsoletas. Para o exemplo, o commit contém apenas o registro compacto `example-central-exclusion.txt`; o effective POM completo fica local. `target/`, `.tools/`, caches, builds, JARs locais do `central-release`, chaves privadas, assinaturas e credenciais também ficam fora do stage.

O conteúdo staged foi revisado e `git diff --cached --check` passou. Arquivos locais não selecionados permanecem no workspace; nenhuma alteração foi descartada. A auditoria de paths e padrões de segredo não encontrou chave privada, token ou credencial no conteúdo staged. O commit desta RC será local; não houve push, tag, deploy ou publicação.
