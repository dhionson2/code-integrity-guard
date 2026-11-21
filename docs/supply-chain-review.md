# B8 — Auditoria de segurança e licenças (2026-11-19)

**Status atual (B8.10): riscos Spring residuais aceitos de forma limitada pelo mantenedor para a composição auditada da RC 1.0.0; não estão corrigidos.** O gate técnico de avisos dos Javadocs foi aprovado em B8.9, sem certificação jurídica independente. A RC está pronta para revisão humana do commit no GitHub; publicação no Maven Central permanece não autorizada. As seções anteriores preservam os resultados e decisões históricos de cada etapa; a decisão vigente está em B8.10.


## B8.1 — Remediação aplicada (2026-11-19)

**Alteração técnica:** o parent agora fixa `jackson-annotations`, `jackson-core` e `jackson-databind` em `2.18.12`, mantendo os três alinhados. Jackson é runtime direto de `code-integrity-guard-report`; seu `core` e `annotations` são transitivos. `code-integrity-guard-maven-plugin` depende de `report` e, portanto, recebe a mesma família Jackson transitivamente. Nenhuma dependência Spring Boot foi adicionada aos módulos do produto; Spring no `testkit` é `provided`, e Boot/Spring de execução está apenas no exemplo.

A versão `2.18.12` existe no Maven Central. Jackson 2.13+ requer Java 8 ou superior segundo a [documentação oficial do Jackson Databind](https://github.com/FasterXML/jackson-databind/); portanto, a linha 2.18 é compatível com a baseline Java 21. O endpoint OSV oficial não retornou vulnerabilidades para `jackson-core`, `jackson-databind` e `jackson-annotations` nessa versão na consulta desta auditoria. Os ranges oficiais dos 17 advisories encontrados no snapshot original terminavam em versões `2.18.6`–`2.18.11`; todos afetavam `2.18.5`. A tabela lista advisory, CVE e faixa 2.18 do registro OSV:

| OSV/GHSA | CVE | Faixa Jackson 2.18 afetada (fix oficial) | Resultado |
|---|---|---|---|
| [GHSA-3pjw-73gf-8qr5](https://github.com/advisories/GHSA-3pjw-73gf-8qr5) | CVE-2026-59888 | jackson-databind 2.15.0–2.18.8 | Encontrado em 2.18.5; corrigido em 2.18.12 |
| [GHSA-5gvw-p9qm-jgwh](https://github.com/advisories/GHSA-5gvw-p9qm-jgwh) | CVE-2026-59889 | jackson-databind 2.0.0-RC1–2.18.9 | Encontrado em 2.18.5; corrigido em 2.18.12 |
| [GHSA-5jmj-h7xm-6q6v](https://github.com/advisories/GHSA-5jmj-h7xm-6q6v) | CVE-2026-54515 | jackson-databind 2.8.0–2.18.9 | Encontrado em 2.18.5; corrigido em 2.18.12 |
| [GHSA-72hv-8253-57qq](https://github.com/advisories/GHSA-72hv-8253-57qq) | CVE-2026-18401 | jackson-core 2.15.0–2.18.6 | Encontrado em 2.18.5; corrigido em 2.18.12 |
| [GHSA-7hhh-6rmp-j9qf](https://github.com/advisories/GHSA-7hhh-6rmp-j9qf) | CVE-2026-89425 | jackson-core 2.8.0–2.18.11 | Encontrado em 2.18.5; corrigido em 2.18.12 |
| [GHSA-cxp5-3px4-pw24](https://github.com/advisories/GHSA-cxp5-3px4-pw24) | CVE-2026-91777 | jackson-databind 2.5.0–2.18.11 | Encontrado em 2.18.5; corrigido em 2.18.12 |
| [GHSA-gx83-3vf8-gh7j](https://github.com/advisories/GHSA-gx83-3vf8-gh7j) | CVE-2026-83557 | jackson-databind 2.11.0–2.18.10 | Encontrado em 2.18.5; corrigido em 2.18.12 |
| [GHSA-hgj6-7826-r7m5](https://github.com/advisories/GHSA-hgj6-7826-r7m5) | CVE-2026-54514 | jackson-databind 2.0.0–2.18.8 | Encontrado em 2.18.5; corrigido em 2.18.12 |
| [GHSA-j3rv-43j4-c7qm](https://github.com/advisories/GHSA-j3rv-43j4-c7qm) | CVE-2026-54512 | jackson-databind 2.10.0–2.18.8 | Encontrado em 2.18.5; corrigido em 2.18.12 |
| [GHSA-mhm7-754m-9p8w](https://github.com/advisories/GHSA-mhm7-754m-9p8w) |  | jackson-databind 2.18.0–2.18.9 | Encontrado em 2.18.5; corrigido em 2.18.12 |
| [GHSA-p6pp-m3f8-5c89](https://github.com/advisories/GHSA-p6pp-m3f8-5c89) | CVE-2026-89407 | jackson-core 2.17.0–2.18.11 | Encontrado em 2.18.5; corrigido em 2.18.12 |
| [GHSA-q4xh-88c3-wmh7](https://github.com/advisories/GHSA-q4xh-88c3-wmh7) | CVE-2026-68497 | jackson-databind 2.14.0–2.18.10 | Encontrado em 2.18.5; corrigido em 2.18.12 |
| [GHSA-r7wm-3cxj-wff9](https://github.com/advisories/GHSA-r7wm-3cxj-wff9) | CVE-2026-68494 | jackson-core 0–2.18.8 | Encontrado em 2.18.5; corrigido em 2.18.12 |
| [GHSA-rmj7-2vxq-3g9f](https://github.com/advisories/GHSA-rmj7-2vxq-3g9f) | CVE-2026-54513 | jackson-databind 2.10.0–2.18.8 | Encontrado em 2.18.5; corrigido em 2.18.12 |
| [GHSA-vvgp-rfg2-7rr6](https://github.com/advisories/GHSA-vvgp-rfg2-7rr6) | CVE-2026-77310 | jackson-databind 2.0.0–2.18.9 | Encontrado em 2.18.5; corrigido em 2.18.12 |
| [GHSA-wjgm-6hv5-3cvf](https://github.com/advisories/GHSA-wjgm-6hv5-3cvf) | CVE-2026-19032 | jackson-databind 2.8.0–2.18.10 | Encontrado em 2.18.5; corrigido em 2.18.12 |
| [GHSA-wv8q-qhhj-9h54](https://github.com/advisories/GHSA-wv8q-qhhj-9h54) | CVE-2026-91776 | jackson-databind 2.0.0–2.18.11 | Encontrado em 2.18.5; corrigido em 2.18.12 |
Os advisories `GHSA-r7wm-3cxj-wff9` / `CVE-2026-68494` e `GHSA-vvgp-rfg2-7rr6` / `CVE-2026-77310` declaram explicitamente correções incompletas de advisories anteriores (`GHSA-72hv-8253-57qq` e `GHSA-hgj6-7826-r7m5`, respectivamente). Foram tratados como achados distintos, sem deduplicar CVEs, e todos já têm faixa fixa confirmada em 2.18.8 ou superior.

### Resultado pós-correção

| Componente | Versão anterior | Nova versão | Advisory | Escopo | Risco residual | Resultado dos testes |
|---|---|---|---|---|---|---|
| `jackson-databind` | 2.18.5 | 2.18.12 | 13 GHSAs/CVEs listados acima | Runtime direto de `report`; transitivo do Maven Plugin | Nenhum advisory OSV retornado para 2.18.12 nesta consulta; manter re-scan antes da publicação | `clean install` e consumidor independente passaram |
| `jackson-core` | 2.18.5 | 2.18.12 | 4 GHSAs/CVEs listados acima | Runtime transitivo de `report`; transitivo do Maven Plugin | Nenhum advisory OSV retornado para 2.18.12 nesta consulta | `clean install` e consumidor independente passaram |
| `jackson-annotations` | 2.18.5 | 2.18.12 | Nenhum achado no snapshot; alinhamento da família | Runtime transitivo de `report`; transitivo do Maven Plugin | Atualizado para manter alinhamento; sem achado OSV desta família | `clean install` e consumidor independente passaram |
| Spring Boot / Spring Framework / Spring Security | Boot 3.4.13; Framework 6.2.15; Security 6.4.13 | Sem alteração | Advisories atuais permanecem no SBOM (inclui CVEs HIGH/CRITICAL) | Runtime apenas do exemplo; parte da API do `testkit` é `provided` | Exemplo ainda vulnerável; excluí-lo de distribuição não corrige o exemplo | Cenário diagnóstico negativo executou e falhou como esperado (HTTP 200 vs 404); clean install do caminho fixo passou |
| Tomcat Embedded | 10.1.50 | Sem alteração | Advisories atuais permanecem no SBOM (inclui HIGH/CRITICAL) | Runtime apenas do exemplo | Há correções nos advisories até 10.1.58, mas a consulta atual encontrou também `CVE-2026-65637`, corrigido em 10.1.59 | Mesmo resultado do exemplo acima |

No scan atualizado, foram analisados novamente os 109 componentes: 50 registros em 14 componentes. Os 17 advisories Jackson desapareceram após atualização: 8 HIGH e 9 moderados no snapshot anterior; nenhum era CRITICAL. Permanecem findings no inventário, inclusive HIGH de AssertJ (teste), Guava (Maven API `provided`) e Spring Boot/Spring/Tomcat (runtime do exemplo), e CRITICAL `CVE-2026-22732` (Spring Security Web, 9.1), `CVE-2026-47890` e `CVE-2026-47884` (Spring WebMVC, 9.8), e `CVE-2026-65905`, `CVE-2026-65182`, `CVE-2026-68525`, `CVE-2026-43512`, `CVE-2026-55276` e `CVE-2026-41293` (Tomcat, 9.1/9.8), somente no exemplo. Portanto, os findings HIGH/CRITICAL residuais não estão no runtime distribuído dos módulos library/plugin; ainda precisam de triagem e correção no exemplo antes de qualquer distribuição dele. Não se declara zero vulnerabilidades no inventário. **HIGH restantes no scan:** AssertJ `CVE-2026-24400` (teste), Guava `CVE-2026-102554` (Maven API `provided`), Boot `CVE-2026-40973`, Spring Expression `CVE-2026-41850`, Spring WebMVC `CVE-2026-41845`/`CVE-2026-41842`, e Tomcat `CVE-2026-24880`, `CVE-2026-43513`, `CVE-2026-42498`, `CVE-2026-41284`, `CVE-2026-53404`, `CVE-2026-24734`, `CVE-2026-34483`, `CVE-2026-34487`. **CRITICAL restantes, todos no runtime do exemplo:** Spring Security `CVE-2026-22732`; Spring WebMVC `CVE-2026-47890`/`CVE-2026-47884`; Tomcat `CVE-2026-43515`, `CVE-2026-65905`, `CVE-2026-65182`, `CVE-2026-68525`, `CVE-2026-43512`, `CVE-2026-55276`, `CVE-2026-41293`. O relatório JSON pós-correção está em `docs/release-evidence/osv-scan-v2.7.1-b8.1.json`; o SBOM atualizado está em `docs/release-evidence/cyclonedx-bom.json`.

### Exemplo Spring Boot: diagnóstico e correção proposta

O exemplo permanece em Boot 3.4.13 / Spring Framework 6.2.15 / Spring Security 6.4.13 / Tomcat 10.1.50 e não foi alterado nesta remediação. A atualização do exemplo deve ser isolada dos módulos publicáveis.

A candidata com alinhamento do BOM é Spring Boot 4.0.9, que seleciona Spring Framework 7.0.10, Spring Security 7.0.8 e Tomcat 11.0.27; consultas OSV para os componentes afetados nessas versões não retornaram advisories correspondentes. O [requisito oficial da linha Boot 4](https://docs.spring.io/spring-boot/4.0/system-requirements.html) é Java 17+, compatível com Java 21. Há mudança major Boot 3→4 e o exemplo usa uma aplicação/demo pequena, mas a combinação precisa de build/testes de migração e novo scan antes de distribuir. A recomendação é migrar o exemplo isoladamente, testar compatibilidade e reexecutar OSV antes de redistribuir o demo. Até lá, mantê-lo fora de qualquer distribuição Central e identificá-lo apenas como fixture educacional local. Não alterar módulos publicáveis por conta das dependências do exemplo. Para `CVE-2026-47890` (corrupção de SSE ao renderizar fragmentos) e `CVE-2026-47884` (limite inadequado em `XsltView`), o OSV marca a linha Spring 6.2 até 6.2.19 como afetada sem versão fixa nessa linha; a candidata 7.0.10 está além do fix 7.0.9. O exemplo atual usa respostas JSON simples e não declara SSE/fragment rendering nem `XsltView`; isso reduz a exposição observada, mas não elimina o advisory. Mitigação temporária se o exemplo continuar local: não registrar/usar esses recursos MVC e não tratar o risco como resolvido. Para Tomcat, vários alertas têm correção até 10.1.58, porém `CVE-2026-65637` exige 10.1.59 na linha 10.1; a migração candidata Boot 4.0.9 usa Tomcat 11.0.27, cuja versão consultada não retornou estes advisories. O exemplo permanece bloqueado para distribuição até migração, testes e novo scan.

### Validação executada nesta remediação

- `./mvnw.cmd -B -ntp -DskipTests clean install` — primeira tentativa falhou no gate CIG do exemplo porque os testes foram explicitamente ignorados; os cinco módulos centrais foram instalados. Não serve como teste aprovado.
- `./mvnw.cmd -B -ntp -DskipTests -pl code-integrity-guard-core,code-integrity-guard-testkit,code-integrity-guard-pit-adapter,code-integrity-guard-report,code-integrity-guard-maven-plugin -am install` — BUILD SUCCESS para parent e cinco módulos selecionados; apenas preparação local, sem afirmar testes.
- `./mvnw.cmd -B -ntp clean install` — BUILD SUCCESS após a correção; testes do reactor e o cenário fixo do exemplo passaram.
- `./mvnw.cmd -B -ntp -f integration-consumer\pom.xml clean verify` — BUILD SUCCESS; Surefire 2 (1 passou, 1 skip intencional), Failsafe 1 passou; consumer report `VERIFIED`.
- `./mvnw.cmd -B -ntp -f examples\spring-boot-authorization\pom.xml verify -Pcig-vulnerable-diagnostic` — saída 1 esperada; teste vulnerável observou HTTP 200 em vez de 404 e contrato ficou `FAILED`.
- PIT `clean verify -Ppit-validation -Dcig.minimumMutationPercent=60` — BUILD SUCCESS, contrato `VERIFIED`, score 7/11 = 63.636%.
- PIT `clean verify -Ppit-validation -Dcig.minimumMutationPercent=70` — saída 1 esperada pelo gate 70%; PIT `AVAILABLE`, score 63.636%, contrato permaneceu `VERIFIED`.
- CycloneDX 2.9.3 regenerou o aggregate BOM 1.6 com 109 componentes. OSV-Scanner 2.7.1 concluiu o scan do SBOM atualizado. A análise recursiva de POM ainda não resolve SNAPSHOTs intermodulares mesmo após instalação local; usar `dependency:tree` e `help:effective-pom` do Maven como evidência de resolução por módulo e manter essa limitação registrada.

`dependency:tree` confirmou `jackson-databind/core/annotations:2.18.12` no runtime de `report` e através de `report` no plugin. `effective-pom` também seleciona 2.18.12 para os três. As árvores efetivas, sob `target/` de cada módulo, demonstram que core não tem dependência runtime externa; PIT Adapter depende do core; report tem Jackson como runtime; Maven Plugin recebe Jackson de report, e Maven Core e `maven-plugin-api` são `provided`; a declaração de plugin API foi corrigida conforme aviso do Maven Plugin Plugin. Plexus Utils/Guava continuam visíveis como componentes `provided` do Maven host no SBOM, sem ser runtime empacotado pelo plugin. No `testkit`, as bibliotecas Spring/JUnit/Servlet/Hamcrest são `provided`, não dependências transitivas normais de runtime do consumidor. Boot/Spring/Tomcat de runtime ficam no exemplo.

**B8.1 não aprova publicação.** Os alertas do exemplo, findings restantes de build/teste, revisão jurídica de licenças e decisões de ownership/distribuição continuam pendentes. Nenhuma publicação, assinatura, commit, push ou tag foi feita.

## Evidência baseline B8 (pré-remediação)

A tabela desta seção e os resultados de 67 advisories/16 componentes registrados abaixo descrevem o snapshot antes da atualização Jackson. A seção B8.1 acima é o resultado pós-remediação e prevalece para a versão atual.

## Escopo e comandos

Foram examinados `pom.xml` da raiz, dos cinco módulos, do exemplo e do `integration-consumer` (8 POMs). O scanner oficial Google `osv-scanner` v2.7.1 (binário Windows amd64 da release oficial; versão registrada: commit `02f96a40691c2d155fdda799af436e111231e365`, build 2026-11-09) foi usado. Não houve upload de código; a consulta foi feita localmente contra os identificadores/versões no OSV. A [documentação oficial de manifests Maven](https://google.github.io/osv-scanner/supported-languages-and-lockfiles/) informa que dependências de teste ainda não são suportadas no grafo Maven. A versão foi baixada da [release oficial do OSV-Scanner](https://github.com/google/osv-scanner/releases/tag/v2.7.1).

Comando de POMs (recursivo, tenta resolver transitivas):
```powershell
& "$env:TEMP\osv-scanner.exe" scan source -r --all-packages --all-vulns --format json --output-file target\osv\source-scan.json .
```
Resultado: os 8 POMs foram reconhecidos, mas houve falha ao resolver dependências internas `io.github.dhionson2:*:1.0.0-SNAPSHOT` para seis módulos. Assim, este resultado **não é uma varredura Maven transitiva completa** e não pode sustentar ausência de vulnerabilidades.

Comando SBOM complementar (CycloneDX 1.6, previamente gerado com escopo de testes):
```powershell
Copy-Item docs\release-evidence\cyclonedx-bom.json target\osv\bom.cdx.json
& "$env:TEMP\osv-scanner.exe" scan source --lockfile target\osv\bom.cdx.json --all-packages --all-vulns --format json --output-file target\osv\sbom-scan.json
```
Resultado: concluído; 109 componentes analisados, 16 componentes com alertas, 67 registros de vulnerabilidade/advisory. Este inventário contém runtime, teste, build e exemplo; não equivale à lista de dependências publicadas do produto. O escopo Maven de cada transitiva não está codificado pelo scanner no relatório CycloneDX, então o escopo foi inferido do POM e da árvore do SBOM e está indicado com essa limitação.

## Vulnerabilidades no SBOM

CVSS abaixo é a maior severidade informada pelo OSV para o componente/advisory; “—” significa que o registro consultado não forneceu score. Os IDs CVE/GHSA abaixo são os retornados no resultado local; IDs GHSA sem alias CVE são mantidos como GHSA. Os ranges exatos, versão mínima corrigida e disponibilidade de correção devem ser confirmados em cada advisory antes de fechar a correção; o JSON local do scanner não materializou `affected` nem a versão fixa. Portanto, a disponibilidade de correção por CVE/GHSA permanece **não confirmada nesta evidência**, e cada item HIGH/CRITICAL fica bloqueado até essa verificação.

| Componente / versão do SBOM | Alertas | CVSS OSV | Escopo inferido | Diagnóstico / classificação | IDs OSV (CVE/GHSA) |
|---|---:|---|---|---|
| `org.assertj:assertj-core` `3.26.3` | 1 | HIGH | 8.2 | Teste, transitivo | Risco aceito apenas para escopo isolado de teste/build/exemplo, condicionado à exclusão do exemplo e à validação da árvore efetiva. Não reutilizar como conclusão de segurança do produto. |
| `com.google.guava:guava` `33.2.1-jre` | 1 | HIGH | 8.2 | Build/teste, transitivo (não runtime dos módulos publicáveis) | **BLOQUEADOR:** há alerta HIGH/CRITICAL (inclui CVSS 9.1/9.8) ou múltiplos advisories; diagnosticar aplicabilidade e atualizar para versão corrigida antes da publicação do escopo afetado. |
| `com.fasterxml.jackson.core:jackson-core` `2.18.5` | 4 | 6.9, 7.5, 7.5, 8.7 | Runtime transitivo de `report` | **BLOQUEADOR:** há alerta HIGH/CRITICAL (inclui CVSS 9.1/9.8) ou múltiplos advisories; diagnosticar aplicabilidade e atualizar para versão corrigida antes da publicação do escopo afetado. | CVE-2026-18401; CVE-2026-89425; CVE-2026-89407; CVE-2026-68494 |
| `com.fasterxml.jackson.core:jackson-databind` `2.18.5` | 13 | 6.5, 6.5, 5.3, 7.5, 5.6, 5.3, 8.1, 6.5, 7.5, 8.1, 5.3, 5.3, 7.5 | Runtime direto de `report` | **BLOQUEADOR:** há alerta HIGH/CRITICAL (inclui CVSS 9.1/9.8) ou múltiplos advisories; diagnosticar aplicabilidade e atualizar para versão corrigida antes da publicação do escopo afetado. | CVE-2026-59888; CVE-2026-59889; CVE-2026-54515; CVE-2026-91777; CVE-2026-83557; CVE-2026-54514; CVE-2026-54512; GHSA-mhm7-754m-9p8w; CVE-2026-68497; CVE-2026-54513; CVE-2026-77310; CVE-2026-19032; CVE-2026-91776 |
| `org.apache.logging.log4j:log4j-api` `2.24.3` | 1 | 6.3 | Build/teste, transitivo | Risco aceito apenas para escopo isolado de teste/build/exemplo, condicionado à exclusão do exemplo e à validação da árvore efetiva. Não reutilizar como conclusão de segurança do produto. | CVE-2026-49844 |
| `ch.qos.logback:logback-core` `1.5.22` | 3 | 2.9, 1.2, 1.8 | Runtime transitivo apenas do exemplo | Risco aceito apenas para escopo isolado de teste/build/exemplo, condicionado à exclusão do exemplo e à validação da árvore efetiva. Não reutilizar como conclusão de segurança do produto. | CVE-2026-10532; CVE-2026-9828; CVE-2026-1225 |
| `org.codehaus.plexus:plexus-utils` `3.5.1` | 1 |  | Build/plugin Maven, transitivo | Risco aceito apenas para escopo isolado de teste/build/exemplo, condicionado à exclusão do exemplo e à validação da árvore efetiva. Não reutilizar como conclusão de segurança do produto. | CVE-2025-67030 |
| `org.springframework.boot:spring-boot` `3.4.13` | 1 | HIGH | 7.0 | Runtime transitivo apenas do exemplo | Risco aceito apenas para escopo isolado de teste/build/exemplo, condicionado à exclusão do exemplo e à validação da árvore efetiva. Não reutilizar como conclusão de segurança do produto. |
| `org.springframework.boot:spring-boot-autoconfigure` `3.4.13` | 1 | 5.3 | Runtime transitivo apenas do exemplo | Risco aceito apenas para escopo isolado de teste/build/exemplo, condicionado à exclusão do exemplo e à validação da árvore efetiva. Não reutilizar como conclusão de segurança do produto. | CVE-2026-41001 |
| `org.springframework:spring-core` `6.2.15` | 1 | 3.7 | Runtime transitivo apenas do exemplo; algumas cópias também em teste | Risco aceito apenas para escopo isolado de teste/build/exemplo, condicionado à exclusão do exemplo e à validação da árvore efetiva. Não reutilizar como conclusão de segurança do produto. | CVE-2026-41848 |
| `org.springframework:spring-expression` `6.2.15` | 3 | HIGH | 3.7, 7.5, 5.3 | Runtime transitivo apenas do exemplo; algumas cópias também em teste | Risco aceito apenas para escopo isolado de teste/build/exemplo, condicionado à exclusão do exemplo e à validação da árvore efetiva. Não reutilizar como conclusão de segurança do produto. |
| `org.springframework.security:spring-security-core` `6.4.13` | 2 | 3.7, 4.8 | Runtime transitivo apenas do exemplo; algumas cópias também em teste | Revisar | CVE-2026-22746; CVE-2026-22751 |
| `org.springframework.security:spring-security-web` `6.4.13` | 2 | CRITICAL | 6.8, 9.1 | Runtime transitivo apenas do exemplo; algumas cópias também em teste | **BLOQUEADOR:** há alerta HIGH/CRITICAL (inclui CVSS 9.1/9.8) ou múltiplos advisories; diagnosticar aplicabilidade e atualizar para versão corrigida antes da publicação do escopo afetado. |
| `org.springframework:spring-web` `6.2.15` | 1 | 4.2 | Runtime transitivo apenas do exemplo; algumas cópias também em teste | Risco aceito apenas para escopo isolado de teste/build/exemplo, condicionado à exclusão do exemplo e à validação da árvore efetiva. Não reutilizar como conclusão de segurança do produto. | CVE-2026-41854 |
| `org.springframework:spring-webmvc` `6.2.15` | 13 | CRITICAL | 7.1, 5.9, 2.6, 5.3, 5.9, 5.9, 5.3, 4.2, 9.8, 5.9, 9.8, 3.1, 7.5 | Runtime transitivo apenas do exemplo; algumas cópias também em teste | **BLOQUEADOR:** há alerta HIGH/CRITICAL (inclui CVSS 9.1/9.8) ou múltiplos advisories; diagnosticar aplicabilidade e atualizar para versão corrigida antes da publicação do escopo afetado. |
| `org.apache.tomcat.embed:tomcat-embed-core` `10.1.50` | 19 | CRITICAL | 7.5, 9.1, 7.5, 6.5, 6.9, 6.9, 3.7, 9.8, 7.3, 9.1, 7.5, 9.1, 9.8, 7.3, 7.5, 9.1, 9.8, 7.5, 7.5 | Runtime transitivo apenas do exemplo | **BLOQUEADOR:** há alerta HIGH/CRITICAL (inclui CVSS 9.1/9.8) ou múltiplos advisories; diagnosticar aplicabilidade e atualizar para versão corrigida antes da publicação do escopo afetado. |

### Diagnóstico e proposta de correção

- **Jackson (`jackson-databind` 2.18.5, `jackson-core` 2.18.5):** dependência runtime do artefato `code-integrity-guard-report`, sendo databind direta e core transitiva. O módulo serializa relatórios; não se deve presumir que isso elimina bugs de parsing/serialização. Bloqueia esse artefato até confirmar ranges, escolher uma linha corrigida compatível via BOM/management e reexecutar revisão. Proposta: atualizar a família Jackson coordenadamente para versão corrigida suportada; nenhuma versão foi mudada nesta missão.
- **Spring Security/WebMVC e Tomcat:** somente o exemplo usa starters Spring Boot; scanners apontam vulnerabilidades severas em dependências runtime transitivas do exemplo. Proposta: retirar o exemplo do conjunto de publicação Central e atualizar sua BOM Spring Boot a versão com componentes corrigidos antes de manter/publicar o exemplo separadamente. O exemplo deve permanecer identificado como educacional/vulnerável em seu cenário de diagnóstico.
- **Guava (33.2.1-jre):** advisory HIGH, mas não é dependência direta/runtime dos artefatos listados nos POMs publicáveis; aparece em grafo agregado de tooling/teste. Confirmar módulo e alcance via árvore Maven antes de aceitar; atualizar se entrar no classpath de runtime/publicação.
- **AssertJ, Logback, Log4j, Plexus e demais alertas de score baixo/moderado:** são dependências de teste, build ou exemplo segundo POM/árvore. Risco aceito condicionado à não distribuição no runtime dos artefatos; atualizar em ciclo próprio ou junto à correção se forem atingidos os caminhos afetados. Não foi possível concluir exposição apenas pelo inventário agregado.

## Licenças e obrigações

SBOM: 88 Apache-2.0, 4 BSD-3-Clause, 8 EPL-2.0, 2 EPL-1.0/LGPL (metadados múltiplos), 2 EPL-2.0/GPL-2.0-with-classpath-exception, 4 MIT e 1 Public Domain; todos os 109 componentes têm algum metadado de licença. Dependências com licença EPL/GPL-classpath devem ser rastreadas no grafo efetivo: estão em dependências transitivas de teste/build, não há indicação de que componham runtime dos JARs Apache publicáveis; confirmar escopo e redistribuição por artefato antes de release. Nenhuma licença incompatível foi comprovada para runtime dos quatro módulos de biblioteca/plugin, mas há **pendência de revisão legal/licença** para componentes agregados com EPL-1.0/LGPL e GPL-2.0-with-classpath-exception.

Apache-2.0 requer preservar copyright/licença, informar alterações, incluir cópia da licença em redistribuições e respeitar avisos pertinentes de NOTICE quando distribuídos. Projeto já contém `LICENSE` e `NOTICE`; manter ambos em fontes e pacotes de distribuição e verificar avisos upstream aplicáveis. BSD-3-Clause e MIT também exigem preservar avisos de copyright/licença; EPL/LGPL/GPL-2.0-with-classpath-exception têm obrigações próprias e devem ser avaliadas conforme distribuição/escopo, sem assumir que metadado multi-license autoriza escolha automática. SBOM sozinho não é parecer jurídico.

## Decisão de publicação por módulo

| Artefato | Decisão B8 | Motivo / ação |
|---|---|---|
| `code-integrity-guard-core` | **Apto condicionado** | Sem dependência runtime externa declarada; publicar sob Apache-2.0 junto com parent. A liberação global continua bloqueada pelas pendências SCA/licença.
| `code-integrity-guard-pit-adapter` | **Apto condicionado** | Runtime depende de core; JUnit é teste. Publicar junto com core.
| `code-integrity-guard-report` | **Bloqueado** | Jackson runtime 2.18.5 com advisories ativos no OSV; resolver antes da publicação.
| `code-integrity-guard-maven-plugin` | **Apto condicionado** | Maven API/runtime e dependências internas; módulos Maven `provided` não são empacotados para redistribuição ordinária. Confirmar árvore efetiva e correções SCA do artefato final.
| `code-integrity-guard-testkit` | **Apto condicionado** | Dependências Spring/JUnit/Servlet/Hamcrest com escopo `provided`; esclarecer API compatível e metadados publicados, revisar licenças da árvore antes de release.
| `code-integrity-guard-parent` (POM) | **Publicar como parent release** | Os POMs dos módulos herdam deste GAV; para consumidores reprodutíveis, publicar POM release compatível (sem `SNAPSHOT`) junto com módulos. Não publicar enquanto POM não estiver na versão de release aprovada.
| `spring-boot-authorization-example` | **Excluir do Maven Central** | Aplicação demonstrativa, não biblioteca; contém variantes educacionais e traz superfície transitiva Spring/Tomcat alertada. Retirar do release/deploy reactor ou excluir deploy explicitamente; preservar no repositório de código com rótulo educacional.
| `integration-consumer` | **Excluir** | Harness de integração local, fora do reactor publicado e sem valor como artefato Central.

## Achados classificados e bloqueadores B8

1. **Bloqueador:** OSV encontrou múltiplos advisories HIGH/CRITICAL em dependências runtime de `report` (Jackson) e no exemplo (Spring/Tomcat). Antes de alterar versões: revisar cada GHSA/CVE ligado na tabela, confirmar faixa afetada e fix version; proposta é atualizar versões/BOM compatíveis e gerar SBOM/scan novamente. Nenhuma atualização técnica foi feita.
2. **Bloqueador:** a análise recursiva dos POMs não resolveu dependências intermodulares SNAPSHOT. Embora o SBOM tenha sido analisado integralmente como lista de componentes, falta uma análise de grafo Maven bem-sucedida com escopos runtime/teste/build confiáveis e transitivas dos artefatos de release. Repetir scan após instalar/empacotar localmente os módulos ou fornecer scanner com resolução de reactor, sem publicar.
3. **Bloqueador:** revisar compatibilidade e obrigações das licenças EPL-1.0/LGPL e GPL-2.0-with-classpath-exception presentes no SBOM agregado e estabelecer que não são redistribuídas com runtime publicado ou cumprir termos aplicáveis.
4. **Bloqueador de release já existente:** ownership do namespace/SCM e aprovação de release permanecem fora desta auditoria conforme readiness atual. A missão B8 não altera status de publicação.
5. **Risco aceito:** alertas exclusivamente em dependências de teste/build e exemplo podem ser aceitos para os artefatos library somente após confirmar o escopo efetivo e excluir o exemplo do deploy.
6. **Melhoria futura:** automatizar geração e scan OSV de SBOM de release por artefato, mantendo separação de escopo e versão do scanner como evidência.

Não foram executados testes, pois nenhuma alteração técnica foi necessária ou autorizada nesta auditoria. Nenhuma vulnerabilidade foi corrigida nesta missão.

## B8.2 — Auditoria conclusiva por artefato Maven Central (2026-11-19)

Esta seção substitui as decisões de escopo e os bloqueadores SCA anteriores deste documento. O B8.1 atualizou Jackson para 2.18.12; nesta etapa não foram alterados POMs nem código Java. Foram resolvidas localmente as árvores `dependency:tree -Dverbose` e os `help:effective-pom` do parent e dos cinco módulos candidatos, após o reactor estar instalado no repositório Maven local. As árvores compactas arquivadas estão em `docs/release-evidence/b8.2-dependency-tree-<módulo>.txt`, incluindo o exemplo para delimitar seus componentes runtime. Os XMLs de effective POM foram inspecionados localmente, mas não entram no repositório: são gerados, contêm caminhos absolutos e podem ser regenerados. O SBOM agregado histórico consultado é `docs/release-evidence/cyclonedx-bom.json` (109 componentes). Os JSONs brutos OSV B8.1 (50 registros em 14 componentes) e o scan POM parcial permanecem locais, não staged; os resultados finais por artefato estão em `docs/release-evidence/osv-v1.0.0/`.
Comandos de evidência usados para a resolução Maven e a verificação OSV (os arquivos resultantes foram copiados para `docs/release-evidence/`):
```powershell
mvnw.cmd -B -ntp -f <pom-do-modulo> dependency:tree -Dverbose -DoutputFile=target/dependency-tree-b8.2.txt
mvnw.cmd -B -ntp -f <pom-do-modulo> help:effective-pom -Doutput=target/effective-pom-b8.2.xml
mvnw.cmd -B -ntp -f examples/spring-boot-authorization/pom.xml -Pcentral-release help:effective-pom -Doutput=target/effective-pom-central-b8.2.xml
& "$env:TEMP\osv-scanner.exe" scan source --lockfile target\osv\bom.cdx.json --all-packages --all-vulns --format json --output-file target\osv\sbom-scan.json
```
O comando OSV foi executado sobre o BOM CycloneDX local; ele não enviou código-fonte. Os comandos Maven foram executados após `install` local para resolver os módulos SNAPSHOT do reactor. Os POMs efetivos e árvores dos seis artefatos e do exemplo estão preservados sob nomes `b8.2-*` em `docs/release-evidence/`.

### Bundle e escopo de distribuição

O perfil `central-release` do módulo `spring-boot-authorization-example` resolve `skipPublishing=true` no effective POM. A documentação oficial do [Maven Central Publishing Plugin](https://central.sonatype.org/publish/publish-portal-maven/) define que `skipPublishing=true` ignora os artefatos daquele projeto durante a publicação. Portanto, o exemplo continua no checkout/reactor local, mas não entra no bundle do Central. Não foi criado nem enviado bundle/staging nesta auditoria. A exclusão do exemplo não exclui `code-integrity-guard-testkit`: ele é módulo publicável próprio e segue no reactor/lista de publicação; o exemplo que o usa não é requisito de runtime para os demais artefatos.

| Artefato candidato | Dependências resolvidas (árvore efetiva) | Advisories aplicáveis à distribuição | Severidade / risco residual | Pendência de licença |
|---|---|---|---|---|
| `code-integrity-guard-parent` (POM) | Apenas gerenciamento de dependências/plugins e BOMs; não declara dependências de runtime. Necessário como parent para os módulos publicados. | Nenhum advisory de runtime identificado. BOMs e plugins de build não se tornam runtime por estarem no `dependencyManagement`/`pluginManagement`. | Sem bloqueador SCA de runtime confirmado; revisar conteúdo release do POM e referências sem `SNAPSHOT` na preparação B8.3. | Apache-2.0 do projeto; preservar LICENSE/NOTICE e confirmar avisos incluídos no pacote de fontes. |
| `code-integrity-guard-core` | Nenhuma dependência externa compile/runtime; JUnit somente `test`. | HIGH | Nenhum advisory aplicável a runtime distribuído. AssertJ `CVE-2026-24400` é dependência de teste do ambiente, não do JAR. | Sem bloqueador confirmado; alertas de teste não são risco de runtime deste artefato. | Nenhuma dependência não Apache em runtime; licenças de teste não acompanham runtime. |
| `code-integrity-guard-testkit` | Core `compile`; JUnit 5.14.3, Spring Test 6.2.15, Spring Security Test 6.4.13, Servlet API 6.0.0 e Hamcrest 2.2 `provided`. | Findings Spring no `provided` (incluindo Spring Security Web transitivo) não são runtime transitivo para consumidores segundo o POM publicado. Não confundir com dependência efetiva de runtime; avaliar separadamente o classpath do consumidor que fornece esses APIs. | Sem bloqueador confirmado para runtime do testkit; permanece compatibilidade/expectativa de APIs `provided` a documentar. | `jakarta.servlet-api` declara EPL-2.0 OR GPL-2.0-with-classpath-exception; Spring é Apache-2.0, JUnit EPL-2.0. `provided` normalmente não é empacotado pelo artefato, mas o consumidor pode precisar fornecê-lo; requer revisão humana de metadados/obrigações se redistribuído ou sombreado. |
| `code-integrity-guard-pit-adapter` | Core `compile`; JUnit somente `test`. | Nenhum advisory aplicável à distribuição/runtime detectado. | Sem bloqueador confirmado; findings de teste não seguem ao runtime do adaptador. | Nenhuma licença não Apache em runtime; JUnit EPL-2.0 é teste. |
| `code-integrity-guard-report` | Core e PIT adapter `compile`; `jackson-databind` 2.18.12 `compile`, com `jackson-core` e `jackson-annotations` 2.18.12 transitivos; JUnit/Gson `test`. | 17 advisories da família Jackson vistos em 2.18.5 foram eliminados ao atualizar a família alinhada para 2.18.12; a varredura atual não aponta advisories para esses três componentes/versões. | Nenhum HIGH/CRITICAL confirmado no runtime resolvido; continua o limite de cobertura do scan POM direto para SNAPSHOT interno e a necessidade de revarrer a versão candidata final. | Jackson Apache-2.0; sem pendência de licença identificada em runtime. |
| `code-integrity-guard-maven-plugin` | Maven Plugin API/Core 3.9.9, plugin annotations 3.15.1 e suas dependências do host Maven são `provided`; core, PIT adapter e report `compile`. Jackson 2.18.12 chega por `report`. JUnit `test`. | HIGH | Nenhum advisory HIGH/CRITICAL confirmado no runtime do plugin publicado; Guava 33.2.1-jre `CVE-2026-102554` e Plexus Utils estão na árvore `provided` do host Maven, não no runtime empacotado normal. | O Maven executado pelo usuário é ambiente externo: a vulnerabilidade de componente `provided` pode continuar no host do consumidor, mesmo sem acompanhar o plugin. Recomenda-se compatibilidade com versões Maven suportadas e teste de ambiente em B8.3. | Sisu EPL-2.0 e dependências Maven host são `provided`; avaliar apenas se o pacote/reempacotamento efetivamente as redistribuir. Runtime próprio usa módulos do projeto e Jackson Apache-2.0. |

### Achados OSV remanescentes e caminho de escopo

A conclusão abaixo é específica ao grafo Maven resolvido e ao escopo de distribuição; não significa que o SBOM agregado esteja sem vulnerabilidades.

- **`NÃO APLICÁVEL À DISTRIBUIÇÃO, COM JUSTIFICATIVA` — HIGH em teste:** AssertJ 3.26.3, `CVE-2026-24400` / `GHSA-rqfh-9r24-8c9r` (CVSS 8.2; faixa OSV até 3.27.6). Só aparece em árvore de teste; não é dependência runtime dos artefatos listados.
- **`NÃO APLICÁVEL À DISTRIBUIÇÃO, COM JUSTIFICATIVA` — HIGH em host Maven:** Guava 33.2.1-jre, `CVE-2026-102554`, via `maven-plugin -> maven-core 3.9.9 (provided) -> maven-resolver-impl -> guice -> guava (provided)`. Não é empacotado como runtime do plugin; a versão do Maven hospedeiro continua fora do controle do artefato. Plexus Utils 3.5.1 também aparece `provided`; OSV o reporta sem score HIGH/CRITICAL neste relatório.
- **`NÃO APLICÁVEL À DISTRIBUIÇÃO, COM JUSTIFICATIVA` — HIGH/CRITICAL no exemplo excluído:** Spring Boot `CVE-2026-40973` HIGH; Spring Expression `CVE-2026-41850` HIGH; Spring WebMVC `CVE-2026-41845` e `CVE-2026-41842` HIGH, `CVE-2026-47890` e `CVE-2026-47884` CRITICAL; Spring Security Web `CVE-2026-22732` CRITICAL. Tomcat 10.1.50 do exemplo: HIGH `CVE-2026-24880`, `CVE-2026-43513`, `CVE-2026-42498`, `CVE-2026-41284`, `CVE-2026-53404`, `CVE-2026-24734`, `CVE-2026-34483`, `CVE-2026-34487`; CRITICAL `CVE-2026-43515`, `CVE-2026-65905`, `CVE-2026-65182`, `CVE-2026-68525`, `CVE-2026-43512`, `CVE-2026-55276`, `CVE-2026-41293`. Caminhos são dependências runtime dos starters Spring Boot/Tomcat dentro exclusivamente de `examples/spring-boot-authorization`; não entram nos seis artefatos Central porque o effective POM fixa `skipPublishing=true`.
- O runtime público de `report` e o runtime próprio do Maven Plugin usam Jackson 2.18.12, alinhado entre databind/core/annotations. O grafo Maven confirma essas versões e caminhos. Nenhum HIGH/CRITICAL restante dos 50 registros foi confirmado em dependência `compile`/`runtime` dos seis artefatos publicáveis.
- Os outros registros não HIGH/CRITICAL permanecem no inventário agregado em componentes de exemplo, teste, `provided` ou ferramentas/host. A lista completa e as faixas estão no JSON OSV/SBOM; sem relatório OSV Maven resolvido para todos SNAPSHOTs, eles não são convertidos em uma declaração global de ausência de vulnerabilidade.

### Licenças e classificação

O SBOM atribui licenças a componentes individuais, mas não decide obrigações legais nem prova redistribuição. EPL-2.0 (JUnit e partes da plataforma Maven/Sisu), EPL-1.0/LGPL (Logback do exemplo) e a alternativa GPL-2.0-with-classpath-exception (Servlet API e Jakarta Annotation API no grafo agregado) foram localizados em árvores de teste/`provided` ou do exemplo. Logback e Spring Boot/Tomcat do exemplo estão em runtime do exemplo, mas o exemplo é excluído do bundle. JUnit e Spring Security Test são teste/`provided`; Maven host/Sisu e Servlet API são `provided`. Não há dependência EPL/LGPL/GPL-classpath confirmada no runtime efetivamente empacotado dos artefatos publicáveis. O scanner contém metadados multi-license; não prova qual opção foi exercida nem que uma dependência foi redistribuída. EPL exige preservar seus avisos/licença e avaliar disponibilização do código-fonte de componentes EPL e modificações cobertas nas condições da licença; LGPL pode exigir avisos, licença e possibilidade de relink/replace em certos formatos de distribuição; GPL-2.0-with-classpath-exception preserva a exceção de linking para obras independentes, mas não elimina obrigações sobre a própria biblioteca GPL nem seus avisos. O enquadramento concreto depende do que é redistribuído, do formato e de eventuais modificações, por isso requer revisão humana.

- **`REQUER REVISÃO HUMANA`** — confirmar no pacote de publicação efetivo se POMs/artefatos de `testkit` preservam corretamente `provided` e se fontes, NOTICE e avisos de qualquer dependência redistribuída estão completos; aprovação jurídica não pode ser inferida do SBOM.
- **`NÃO APLICÁVEL À DISTRIBUIÇÃO, COM JUSTIFICATIVA`** — EPL/LGPL de Logback do exemplo e EPL/GPL-classpath de componentes de teste/`provided`/host não estão no runtime dos binários Central segundo as árvores atuais e a exclusão efetiva do exemplo. Reavaliar se escopo, shading, assembly ou bundle mudar.
- Runtime público identificado: módulos internos Apache-2.0 e Jackson Apache-2.0; preservar a licença Apache, avisos de copyright, NOTICE aplicável e indicação de alterações ao redistribuir.

### Decisão e trabalho B8.3

**Sem bloqueador confirmado de vulnerabilidade HIGH/CRITICAL no runtime dos artefatos Maven Central identificados.** Os avisos severos remanescentes são riscos de teste, host Maven `provided` ou exemplo excluído; o exemplo não deve ser distribuído nem apresentado como seguro. Para eventual distribuição separada do exemplo, atualizar o BOM Spring Boot para uma linha com correções oficiais para os ranges acima e repetir SBOM/OSV; B8.2 não atualiza dependências.

Pendências classificadas:

1. **BLOQUEADOR CONFIRMADO:** nenhum bloqueador SCA de runtime confirmado para os seis artefatos com o grafo resolvido nesta revisão. A decisão global da release segue condicionada à revisão humana da licença e à aprovação dos demais gates registrados em `release-readiness.md`; isso não é aprovação de publicação.
2. **REQUER REVISÃO HUMANA:** revisar obrigações e avisos de redistribuição no conteúdo real dos artefatos/source bundle; confirmar versão release do parent/POMs; verificar os escopos publicados do testkit e o ambiente Maven mínimo do plugin.
3. **NÃO APLICÁVEL À DISTRIBUIÇÃO, COM JUSTIFICATIVA:** CVEs HIGH/CRITICAL listados em AssertJ teste, Guava/Plexus Maven host `provided` e Spring/Tomcat do exemplo excluído. Se algum desses componentes passar a ser empacotado ou o exemplo entrar no bundle, reabrir a classificação e atualizar antes da distribuição.
4. **MELHORIA FUTURA:** em B8.3, gerar SBOM/SCA por artefato a partir de versões release (sem SNAPSHOT), manter `skipPublishing=true` no exemplo e verificar a exclusão em uma preparação local sem credenciais; avaliar atualização isolada do exemplo e política de versão do Maven host; automatizar a associação de advisory, faixa fix e caminho Maven na CI. Não alterar Jackson 2.18.12 sem nova evidência.

### Limite do OSV-Scanner

OSV-Scanner v2.7.1 não conseguiu resolver satisfatoriamente todos os POMs com dependências intermodulares `1.0.0-SNAPSHOT`; por isso o relatório de POM direto continua marcado **partial**. O scan SBOM reconheceu 109 componentes e retornou 50 registros em 14 componentes, mas o SBOM agregado não codifica sozinho todos os scopes Maven nem prova que cada item será distribuído. A análise complementar usou `dependency:tree -Dverbose` e `effective-pom` após `install` local do reactor para identificar versão mediada, caminhos e scopes; CycloneDX/OSV foram cruzados com esse grafo. Não foi possível alegar cobertura total do POM OSV e não se declara ausência global de vulnerabilidades. Nenhum serviço externo recebeu código-fonte.

### Matriz de versões dos advisories HIGH/CRITICAL do snapshot OSV

As versões “corrigidas” abaixo vêm dos eventos `fixed`/`last_affected` dos registros no arquivo OSV citado, não de uma atualização aplicada nesta etapa. Para caminhos multi-branch, indica-se a correção aplicável à linha observada ou o primeiro ramo corrigido publicado pelo advisory. CVSS/score pode variar entre aliases e bases; a coluna severidade segue o resultado OSV exportado.

| Componente observado | Advisory (OSV/GHSA/CVE) | Severidade OSV | Versão observada e faixa afetada relevante | Correção indicada no advisory | Caminho e escopo no projeto |
|---|---|---|---|---|---|
| AssertJ | GHSA-rqfh-9r24-8c9r / CVE-2026-24400 | HIGH | 3.26.3; afetado `1.4.0` até antes de 3.27.7 | 3.27.7 | Teste; não distribuído como runtime |
| Guava | CVE-2026-102554 | HIGH | 33.2.1-jre; afetado desde 4.0 até antes de 33.7.2 | 33.7.2 | Plugin → Maven Core 3.9.9 `provided` → Resolver → Guice → Guava `provided`; risco do host Maven |
| Spring Boot | CVE-2026-40973 | HIGH | 3.4.13; branch 3.4 com último afetado 3.4.15 | Sem fix posterior na branch 3.4 no registro; branches corrigidas 3.5.14 e 4.0.6 | Exemplo Spring Boot, runtime excluído |
| Spring Expression | CVE-2026-41850 | HIGH | 6.2.15; 6.2.0 até antes de 6.2.19 | 6.2.19 | Exemplo runtime; também aparece via Spring Security Test `provided` no testkit |
| Spring WebMVC | CVE-2026-41845 | HIGH | 6.2.15; 6.2.0 até antes de 6.2.19 | 6.2.19 | Exemplo runtime |
| Spring WebMVC | CVE-2026-41842 | HIGH | 6.2.15; 6.2.0 até antes de 6.2.19 | 6.2.19 | Exemplo runtime |
| Spring Security Web | CVE-2026-22732 | CRITICAL | 6.4.13; 6.4.0–6.4.13 afetados | 6.5.9 ou 7.0.4; 6.4.13 é último afetado da linha | Exemplo runtime e testkit `provided`; exemplo excluído e testkit não empacota runtime desta dependência |
| Spring WebMVC | CVE-2026-47890 | CRITICAL | 6.2.15; 6.2.x até 6.2.19 afetado conforme OSV | 7.0.9 no ramo 7.0; registro sem fix indicado para 6.2.x | Exemplo runtime; branch observada sem correção indicada no registro |
| Spring WebMVC | CVE-2026-47884 | CRITICAL | 6.2.15; 6.2.x até 6.2.19 afetado conforme OSV | 7.0.9 no ramo 7.0; registro sem fix indicado para 6.2.x | Exemplo runtime; branch observada sem correção indicada no registro |
| Tomcat Embed Core | CVE-2026-24880 | HIGH | 10.1.50; 10.1.x anterior a 10.1.52 | 10.1.52 | Exemplo runtime |
| Tomcat Embed Core | CVE-2026-43515 | CRITICAL | 10.1.50; 10.1.x anterior a 10.1.55 | 10.1.55 | Exemplo runtime |
| Tomcat Embed Core | CVE-2026-43513 | HIGH | 10.1.50; 10.1.x anterior a 10.1.55 | 10.1.55 | Exemplo runtime |
| Tomcat Embed Core | CVE-2026-42498 | HIGH | 10.1.50; 10.1.x anterior a 10.1.55 | 10.1.55 | Exemplo runtime |
| Tomcat Embed Core | CVE-2026-65182 | CRITICAL | 10.1.50; 10.1.x anterior a 10.1.58 | 10.1.58 | Exemplo runtime |
| Tomcat Embed Core | CVE-2026-41284 | HIGH | 10.1.50; 10.1.x anterior a 10.1.55 | 10.1.55 | Exemplo runtime |
| Tomcat Embed Core | CVE-2026-68525 | CRITICAL | 10.1.50; 10.1.x anterior a 10.1.58 | 10.1.58 | Exemplo runtime |
| Tomcat Embed Core | CVE-2026-43512 | CRITICAL | 10.1.50; 10.1.x anterior a 10.1.55 | 10.1.55 | Exemplo runtime |
| Tomcat Embed Core | CVE-2026-53404 | HIGH | 10.1.50; 10.1.x anterior a 10.1.56 | 10.1.56 | Exemplo runtime |
| Tomcat Embed Core | CVE-2026-55276 | CRITICAL | 10.1.50; 10.1.x anterior a 10.1.56 | 10.1.56 | Exemplo runtime |
| Tomcat Embed Core | CVE-2026-24734 | HIGH | 10.1.50; 10.1.x anterior a 10.1.52 | 10.1.52 | Exemplo runtime |
| Tomcat Embed Core | CVE-2026-41293 | CRITICAL | 10.1.50; 10.1.x anterior a 10.1.55 | 10.1.55 | Exemplo runtime |
| Tomcat Embed Core | CVE-2026-34483 | HIGH | 10.1.50; 10.1.x anterior a 10.1.54 | 10.1.54 | Exemplo runtime |
| Tomcat Embed Core | CVE-2026-34487 | HIGH | 10.1.50; 10.1.x anterior a 10.1.54 | 10.1.54 | Exemplo runtime |
| Tomcat Embed Core | CVE-2026-65905 | CRITICAL | 10.1.50; 10.1.x anterior a 10.1.58 | 10.1.58 | Exemplo runtime |

O exemplo não precisa ser atualizado para incluir os artefatos Central, desde que permaneça excluído. Se for distribuído por qualquer canal, o example é bloqueado até uma migração coerente: o OSV não mostra fix na linha Spring Boot 3.4 nem nas linhas Spring Framework 6.2 para `CVE-2026-47890` e `CVE-2026-47884`; a versão Spring 7.0.9 aparece corrigida, mas compatibilidade com a baseline e a aplicação ainda não foi validada. Impacto potencial: requisições externas que atinjam renderização de SSE/fragments ou `XsltView` podem afetar a integridade do fluxo/limites de caminho conforme a funcionalidade usada; não há evidência de que o exemplo exponha essas rotas por padrão. Mitigação imediata é manter o módulo fora de qualquer distribuição/exposição; para distribuição futura, migrar o BOM Boot/Spring para ramo corrigido e compatível, alinhar Tomcat a pelo menos 10.1.58 e reexecutar build, testes, SBOM e OSV. Não aplicar overrides independentes.

## B8.3, Parte 1 — Licenças, notices e conteúdo dos artefatos (2026-11-19)

### Resultado e escopo verificado

Foram lidos este documento, `docs/release-readiness.md`, os POMs/effective POMs e as árvores B8.2 em `docs/release-evidence/b8.2-*`, além de `LICENSE`, `NOTICE`, `README.md`, CycloneDX e os metadados/licenças dos componentes. Os fontes oficiais consultados incluem os requisitos de [metadados e sources/Javadoc do Maven Central](https://central.sonatype.org/publish/requirements/), a [Apache License 2.0](https://www.apache.org/licenses/LICENSE-2.0), a [EPL 2.0](https://www.eclipse.org/legal/epl-2.0/), a [licença oficial JUnit 5.14.3](https://github.com/junit-team/junit-framework/blob/r5.14.3/LICENSE.md), os repositórios oficiais [Sisu](https://github.com/eclipse-sisu/sisu-project) e [Logback 1.5.22](https://github.com/qos-ch/logback/blob/v_1.5.22/LICENSE.txt), os POMs oficiais de [Jakarta Servlet 6.0.0](https://central.sonatype.com/artifact/jakarta.servlet/jakarta.servlet-api/6.0.0) e [Jakarta Annotations 2.1.1](https://central.sonatype.com/artifact/jakarta.annotation/jakarta.annotation-api/2.1.1), a [NOTICE upstream Servlet](https://github.com/jakartaee/servlet/blob/6.0.0/NOTICE.md), a [LGPL 2.1](https://www.gnu.org/licenses/old-licenses/lgpl-2.1.html) e a [GNU Classpath Exception](https://www.gnu.org/software/classpath/license.html). Os POMs/árvores de B8.2 estabelecem as versões e scopes; fontes upstream estabelecem os avisos/licenças. Nenhuma dessas fontes é parecer jurídico sobre compatibilidade.

Foi executado `./mvnw.cmd -B -ntp -Pcentral-release -DskipTests package` para gerar localmente os arquivos que seriam inspecionados. Resultado: `BUILD SUCCESS`; o log registrou `Tests are skipped`. **Não** foi executado `verify`, `deploy`, GPG, upload ou staging. O parent possui packaging `pom`, portanto é distribuído como POM, sem JAR de classes. Os cinco artefatos `jar` geraram binário, `-sources.jar` e `-javadoc.jar` no `target/` de cada módulo. O exemplo também foi empacotado localmente por fazer parte do reactor, mas o effective POM B8.2 mantém `skipPublishing=true`; a existência de arquivos no `target` local não o inclui no bundle.

Inspeção com `jar tf` dos 15 arquivos (cinco binários, cinco sources e cinco Javadocs): os binários contêm apenas classes sob `io/github/dhionson2/cig/...`, descritores/metadata do projeto e os arquivos de licença; nenhum pacote de Jackson, JUnit, Spring, Servlet ou Sisu está incorporado. Os sources contêm fontes do projeto e metadata/licenças, sem `.class`, JAR de dependência ou fonte de dependência. Cada binário e sources JAR inclui `META-INF/LICENSE` e `META-INF/NOTICE`; cada Javadoc JAR contém `resources/LICENSE` e `resources/NOTICE`. Os arquivos de licença copiados para os recursos de Javadoc têm o mesmo SHA-256 dos arquivos autoritativos da raiz.

Os Javadocs gerados pelo JDK incluem `script-dir/jquery-3.7.1.min.js`, jQuery UI 1.13.2 (`jquery-ui.min.js`, CSS e imagens) e arquivos em `legal/`. As notas MIT completas estão em `legal/jquery.md` e `legal/jqueryUI.md`; esta última também informa CC0 para código de demonstração, que não aparece nos assets distribuídos. Os arquivos JDK `legal/LICENSE`, `legal/ADDITIONAL_LICENSE_INFO` e `legal/ASSEMBLY_EXCEPTION` são apenas ponteiros relativos (`..\\java.base\\...`); os destinos não estão no Javadoc JAR. São conteúdo gerado/redistribuído pelo doclet, não dependências Maven. A suficiência do aviso/pacote legal do JDK continua **REQUER REVISÃO HUMANA**; não se presume que os ponteiros incompletos resolvam obrigações de redistribuição.

### Matriz de artefatos, licenças e avisos

| Artefato | Dependência/licença | Forma de distribuição | Obrigação considerada | Evidência | Status |
|---|---|---|---|---|---|
| Parent `code-integrity-guard-parent` | Código de empacotamento `pom`; Apache-2.0 declarada no POM | POM apenas; não contém classes nem fontes de terceiros | Manter license metadata e parent POM disponível; não exige sources/Javadoc JAR por ser `pom` segundo requisitos Central | Raiz `pom.xml`; packaging `pom`; POM no bundle separado dos JARs | **CORRIGIDO/OK TECNICAMENTE**; identidade SCM continua a confirmar por responsável humano |
| Core | Código CIG sob Apache-2.0 | Binário e sources JAR; LICENSE/NOTICE em `META-INF` | Entregar cópia da licença, preservar avisos aplicáveis e indicar alterações em arquivos modificados | JAR inspeccionado: classes apenas `io.github...cig.core`; `META-INF/LICENSE`, `META-INF/NOTICE`; sources sem bibliotecas externas | **CORRIGIDO**; inclusão feita pelo resource configuration herdado do parent |
| Core | JUnit 5.14.3 e JUnit Platform 1.14.3, EPL-2.0 | Somente dependência de `test`; não aparece no binário/sources nem é transitiva em runtime | EPL exige disponibilizar fonte do Programa EPL e preservar licença/avisos caso esse Programa seja redistribuído; não redistribuímos JUnit no artefato CIG | Árvore B8.2 `core` com JUnit `test`; POM oficial JUnit 5.14.3; jar sem pacote `org/junit` | **NÃO REDISTRIBUÍDA** |
| Testkit | Código CIG sob Apache-2.0 | Binário e sources JAR; JUnit, Spring Test, Spring Security Core, Servlet API e Hamcrest `provided`; Spring Security Test não é dependência do artefato | Manter cópia Apache no artifact; `provided` não é propagado; consumidor declara JUnit/Spring/Security Test conforme APIs que seu teste usa | Árvore de teste independente `docs/release-evidence/b8.3-consumer-dependency-tree.txt`; POM final; fontes `AuthorizationChecks`/`EvidenceExtension`; README atualizado | **CORRIGIDO TECNICAMENTE**; revisão humana do alcance das APIs `provided` continua pendente |
| Testkit | `junit-jupiter-api` 5.14.3, EPL-2.0 | `provided` no POM do testkit; JUnit não embutido | Obrigações EPL só se JUnit for redistribuído como programa; consumidores que adicionarem JUnit devem respeitar seus termos upstream | POM oficial JUnit 5.14.3; árvore B8.2 mostra `provided`; nenhum `org/junit` no JAR | **NÃO REDISTRIBUÍDA PELO CIG**; o consumidor seleciona a licença do JUnit |
| Testkit | `jakarta.servlet-api` 6.0.0, EPL-2.0 OR GPL-2.0-with-classpath-exception; NOTICE upstream também declara partes Apache-2.0 | `provided`, só referenciada pelo testkit; JAR upstream não embutido | Se redistribuir API, preservar licença/avisos upstream e selecionar/interpretar a alternativa conforme os termos e conteúdo coberto; exceção de classpath não é licença geral para código GPL | POM central oficial; `NOTICE.md` oficial Servlet; árvore B8.2 `testkit`; JAR CIG sem `jakarta/servlet` | **REQUER REVISÃO HUMANA** sobre metadado de múltiplas licenças se distribuição/sombreamento do Servlet API mudar; hoje não redistribuído pelo CIG |
| PIT Adapter | Código CIG Apache-2.0; JUnit 5.14.3/Platform 1.14.3 EPL-2.0 `test` | Binário/sources só incluem projeto; JUnit usado em testes | Preservar LICENSE/NOTICE do projeto; nenhuma obrigação EPL de JUnit é incorporada ao artefato quando só há uso em teste | Árvore B8.2 `pit-adapter`; JAR sem `org/junit`; LICENSE/NOTICE em `META-INF` | **CORRIGIDO / JUnit NÃO REDISTRIBUÍDO** |
| Report | Código CIG e Jackson 2.18.12 sob Apache-2.0; JUnit EPL-2.0 `test` | Jackson é dependência Maven runtime separada; nenhuma classe Jackson no report JAR; JUnit somente teste | Licença/NOTICE de Jackson acompanha o próprio artefato Jackson; cópia Apache CIG acompanha o JAR CIG | Árvore B8.2 `report`; inspeção sem `com/fasterxml/jackson`; JUnit em `test` | **CORRIGIDO / SEM NOTICE upstream adicional no JAR CIG** |
| Maven Plugin | Código CIG/Jackson Apache-2.0; JUnit EPL-2.0 `test`; Sisu 0.9.0.M3 EPL-2.0 no caminho Maven Core 3.9.9 `provided` | Plugin contém apenas classes CIG; Maven/Sisu são fornecidos pelo Maven host, não pelo JAR do plugin | Se uma distribuição do produto passasse a empacotar Sisu/JUnit, preservar licença/fonte e avisos aplicáveis; atualmente o POM plugin marca Maven API/Core como `provided` | Árvore B8.2 plugin (`maven-plugin-api`, `maven-core`, `org.eclipse.sisu.*`, JUnit); POM oficial Sisu declara EPL-2.0; JAR sem pacotes Maven/Sisu/JUnit | **NÃO REDISTRIBUÍDOS PELO PLUGIN**; compatibilidade de host Maven permanece revisão técnica separada |
| Cinco classifiers `*-javadoc.jar` | jQuery 3.7.1/MIT; jQuery UI 1.13.2/MIT; o aviso jQuery UI também trata código demo sob CC0, ausente dos JARs; arquivos legais do JDK referenciam licença/arquivos do módulo `java.base` | Javadoc JAR anexado a cada módulo `jar` | Preservar os textos MIT incluídos; decidir se referências relativas JDK são suficientes ou se o JDK legal completo precisa acompanhar os classifiers | `jar tf` e leitura dos cinco JARs: `script-dir/jquery-3.7.1.min.js`, `jquery-ui.min.js`, CSS, `resources/glass.png`, `resources/x.png`; `legal/jquery.md` e `legal/jqueryUI.md` contêm licença/copyright. `legal/LICENSE`, `ADDITIONAL_LICENSE_INFO` e `ASSEMBLY_EXCEPTION` só dizem `Please see ..\\java.base\\...`; esses destinos não existem no JAR. Conferido também com o [javadoc do JDK 21](https://docs.oracle.com/en/java/javase/21/docs/specs/man/javadoc.html) e [licença upstream jQuery UI](https://github.com/jquery/jquery-ui/blob/1.13.2/LICENSE.txt). | MIT: avisos presentes. **REQUER REVISÃO HUMANA** para os pointers legais JDK; sem aprovação jurídica automática |
| Exemplo `spring-boot-authorization-example` (fora dos seis) | Logback 1.5.22 EPL-1.0 OR LGPL-2.1; Jakarta Annotations API 2.1.1 EPL-2.0 OR GPL-2.0-with-classpath-exception; demais dependências Spring/Boot | Runtime do exemplo local; exemplo não publicado por `skipPublishing=true` | Se o exemplo for distribuído separadamente, incluir licenças/avisos próprios e avaliar obrigações de Logback/Jakarta conforme o conteúdo/alternativa efetivamente distribuída | B8.2 effective POM mostra `skipPublishing=true`; árvore/SBOM identificam as versões; fontes upstream Logback/Jakarta | **NÃO APLICÁVEL À DISTRIBUIÇÃO CENTRAL**; manter exclusão e não distribuir o demo até reavaliação |

### Verificação de escopos `provided` e documentação de consumidores

Os scopes do testkit permanecem `provided` porque as classes públicas referenciam JUnit, Spring MVC/Security Core e Servlet, mas o testkit é um utilitário de teste e não deve impor Spring no runtime da aplicação consumidora. `provided` evita a propagação transitiva e não empacota os JARs. A API não foi modificada. O POM final declara `spring-security-core` (usado pelo código) no lugar de `spring-security-test` (não usado pelo código); Hamcrest permanece `provided` porque uma tentativa de removê-lo falhou na compilação com `cannot access org.hamcrest.Matcher` ao chamar `MockMvcResultMatchers.status().isUnauthorized()`. README identifica JUnit Jupiter API para `EvidenceExtension`/`ContractTest`; Spring Test, Spring MVC, Servlet API para o helper `AuthorizationChecks`; Spring Security Core para ler principal autenticado; Hamcrest para os métodos `ResultMatcher`; e Spring Security Test, declarada diretamente pelo consumidor, quando usar seus post-processors. O teste independente usa explicitamente `spring-security-test` via `.with(user("alice"))`.

### NOTICE, THIRD-PARTY-NOTICES e metadados Central

A raiz já continha `LICENSE` Apache-2.0 e `NOTICE`; as cópias do NOTICE agora esclarecem que binário/source JAR contêm somente conteúdo CIG, enquanto os Javadocs também carregam os assets legais padrão do doclet. O parent POM distribui a licença em metadata. Para cinco módulos JAR, recursos herdados colocam cópias do `LICENSE` e `NOTICE` sob `META-INF` nos binários e sources; recursos do Javadoc colocam cópias sob `resources/`. A inspeção não encontrou dependência Maven sombreada nem código de terceiro incorporado no binário ou no source jar. Portanto, **não foi criado `THIRD-PARTY-NOTICES` agregado**: dependências Maven são artefatos separados com seus próprios POMs/licenças; os avisos de assets de Javadoc já acompanham aquele classifier. Se for adicionado shading, assembly ou cópia de dependência, esta conclusão deixa de valer.

Os seis POMs candidatos (parent + cinco módulos) agora declaram diretamente `name`, `description`, `url`, `licenses`, `developers` e `scm`, além do parent e packaging adequado. Os cinco módulos repetem os dados Apache-2.0/developer/SCM para que a validação do POM individual não dependa apenas da herança do parent. Os valores preenchem os campos técnicos exigidos pelo [Central](https://central.sonatype.org/publish/requirements/). O URL/SCM corresponde ao [repositório público](https://github.com/dhionson2/code-integrity-guard) e ao remote local. O maintainer deve decidir se o campo de developer deve usar o nome de equipe atual ou o nome público exibido no [perfil GitHub](https://github.com/dhionson2).

**Obrigações de licença resumidas, sem conclusão jurídica:** Apache-2.0: fornecer cópia da licença, preservar atribuições/avisos existentes e marcar arquivos alterados; NOTICE deve ser preservado quando já acompanha o trabalho. EPL-2.0: inclui disponibilização de source do programa EPL e licença/avisos quando o próprio programa é distribuído, sob condições do EPL. LGPL-2.1 requer análise de redistribuição da biblioteca/obra combinada e disponibilização de material de origem/condições relevantes quando aplicável. GPL-2.0-with-classpath-exception tem exceção de linking para módulos independentes, mantendo obrigações sobre a biblioteca coberta e seus avisos. O projeto não está redistribuindo atualmente os JARs dessas licenças; um advogado/responsável de compliance deve revisar qualquer mudança de embalagem, shading, escopo ou distribuição externa do exemplo.

### Classificação e pendências B8.3 Parte 1

- **CORRIGIDO TECNICAMENTE:** cópia de LICENSE/NOTICE adicionada aos cinco binários, cinco source jars e cinco Javadocs; metadados completos inseridos diretamente nos cinco POMs de módulo; README explica dependências `provided` do testkit.
- **NÃO REDISTRIBUÍDO PELO CIG:** JUnit EPL de teste/provided, Servlet API `provided` e Sisu EPL do Maven host; as classes/licenças permanecem nos artefatos dos respectivos fornecedores.
- **NÃO APLICÁVEL À DISTRIBUIÇÃO CENTRAL:** Logback EPL/LGPL e Jakarta Annotations EPL/GPL-CE pertencem ao runtime do exemplo, que está excluído pelo effective `skipPublishing=true`.
- **STATUS HISTÓRICO — Javadoc:** a suficiência técnica das notas JDK/doclet foi aprovada em B8.9 por comparação com as fontes Temurin e inspeção dos cinco classifiers. Permanecem em análise humana os scopes `provided`/interfaces públicas se houver redistribuição de runtime, identidade do desenvolvedor/ownership SCM e qualquer alteração futura de shading, bundle ou distribuição do exemplo.
- **THIRD-PARTY-NOTICES:** arquivo separado não necessário segundo o conteúdo atualmente inspecionado; reabrir se componentes de terceiros forem incorporados.

## B8.3 Parte 2 — gates técnicos e avisos Javadoc (2026-11-19)

**Snapshot histórico:** a composição JBR/Windows e os notices incompletos descritos nesta parte foram substituídos pela geração Temurin/Linux e pela decisão técnica B8.9.

### Metadados e consistência com GitHub

O `LICENSE` não tem diff nesta etapa e continua Apache-2.0. O `NOTICE` foi corrigido para identificar os assets reais do Javadoc e a limitação dos arquivos legais JDK. O SCM em todos os POMs aponta para `https://github.com/dhionson2/code-integrity-guard`; `git remote -v` aponta para `https://github.com/dhionson2/code-integrity-guard.git`, e o repositório público existe sob a mesma conta. O nome/descrição públicos do GitHub são “Code Integrity Guard” e “Evidence-based security contract and test integrity verification for Java projects.”; a descrição do POM parent foi alinhada. A identidade `<developer><name>Code Integrity Guard maintainers</name>` não é o nome exibido publicamente pelo perfil GitHub `DHIONSON PLASTER MOREIRA`; decidir humanamente se a listagem de equipe ou pessoa é a atribuição desejada. A declaração antiga de README que dizia namespace e SCM não verificados foi substituída pela situação de verificação já registrada pelo projeto. Ownership individual e metadados de autor ainda exigem revisão do mantenedor.

### Avisos incorporados nos cinco Javadocs

| Asset presente em cada `*-javadoc.jar` | Licença/origem observada | Aviso incluído | Resultado |
|---|---|---|---|
| `script-dir/jquery-3.7.1.min.js` | jQuery 3.7.1, MIT | `legal/jquery.md` inclui copyright OpenJS Foundation/contributors e texto MIT completo; corresponde ao [LICENSE oficial da tag 3.7.1](https://github.com/jquery/jquery/blob/3.7.1/LICENSE.txt) | Aviso presente |
| `script-dir/jquery-ui.min.js`, `jquery-ui.min.css`, `resources/glass.png`, `resources/x.png` | jQuery UI 1.13.2, MIT; nota também registra CC0 para código de demos | `legal/jqueryUI.md` inclui copyright e texto MIT completo, além da nota CC0; corresponde à [LICENSE oficial da tag 1.13.2](https://github.com/jquery/jquery-ui/blob/1.13.2/LICENSE.txt) | Aviso presente; não há arquivos de demos nos JARs |
| `legal/LICENSE`, `legal/ADDITIONAL_LICENSE_INFO`, `legal/ASSEMBLY_EXCEPTION` | Arquivos legais emitidos pelo standard doclet/JDK; o JDK local aponta para licença GPLv2 e OpenJDK Assembly Exception | Cada arquivo no JAR contém somente ponteiro `Please see ..\\java.base\\...`; os destinos do módulo java.base não estão dentro do JAR | **REQUER REVISÃO HUMANA** sobre suficiência da atribuição/pacote JDK para distribuição do classifier |

Os cinco Javadocs foram regenerados com `mvnw.cmd -B -ntp -Pcentral-release -DskipTests package` em JDK 21.0.12.1. Enumeração e leitura dos cinco classifiers confirmaram as mesmas paths de assets/notices. Uma comparação SHA-256 final encontrou `src/main/javadoc/resources/NOTICE` desatualizado; a cópia foi sincronizada com o NOTICE raiz, o package foi reexecutado, e cada classifier agora contém NOTICE com o mesmo hash do arquivo raiz. A opção oficial Javadoc [`--legal-notices`](https://docs.oracle.com/en/java/javase/21/docs/specs/man/javadoc.html) permite selecionar a origem dos avisos; nenhuma alteração de geração dos arquivos legais do JDK foi feita nesta parte porque a escolha entre remover os notices do JDK ou empacotar arquivos do runtime JDK deve ser revisada pelo mantenedor/compliance. Os avisos MIT incluídos cobrem os assets jQuery; o tratamento dos arquivos legais JDK/java.base fica aberto e não há declaração de aprovação jurídica.

### Consumidor independente e scopes

Comandos executados localmente, sem `central-release` e sem deploy:

```text
mvnw.cmd -B -ntp clean verify
mvnw.cmd -B -ntp install
integration-consumer> ..\\mvnw.cmd -B -ntp verify
integration-consumer> ..\\mvnw.cmd -B -ntp org.apache.maven.plugins:maven-dependency-plugin:3.8.1:tree -Dverbose -Dscope=test
mvnw.cmd -B -ntp -Pcentral-release -pl examples/spring-boot-authorization help:effective-pom -Doutput=target/effective-central-pom
```

`clean verify` passou em todos os sete projetos do reactor (parent, cinco bibliotecas/plugin e exemplo): 44 testes reportados, zero falhas/erros; o testkit não tem testes próprios. O exemplo passou cinco testes, incluindo perfis `vulnerable`/`fixed`, e validou seu contrato local. `install` passou para permitir consumo por coordenadas Maven fora do reactor; instalou snapshots somente no `.m2` local. O `integration-consumer` não é módulo do reactor: seu `verify` executou dois testes Surefire (um intencionalmente disabled/skipped) e um teste Failsafe, todos os executados passaram; o plugin `prepare-evidence`, `seal-evidence` e `audit` funcionou no consumidor e o contrato ficou `VERIFIED`.

A árvore `test` final do consumidor mostra `code-integrity-guard-testkit` e `core`, mas não traz JUnit, Spring Test, Spring Security Core/Test, Servlet API ou Hamcrest por transitividade do testkit. O consumidor declara explicitamente JUnit 5.14.3, Spring Test/Spring MVC 7.0.10, Spring Security Test 7.0.7 e Servlet API 6.1.0 no scope `test`; o teste usa `SecurityMockMvcRequestPostProcessors.user`. Esses componentes pertencem exclusivamente ao classpath do consumidor de validação e não são dependências transitivas do testkit. A árvore Maven separa conflitos por versão no classpath de teste; não foi executado OSV específico para esse consumidor e não se declara ausência de advisories nesse classpath. Os `provided` do testkit não quebram a API: os tipos Spring permanecem na assinatura Java, e o consumidor fornece as bibliotecas ao compilar/executar os testes. O plugin funciona sem dependências JUnit/Spring no classpath da aplicação; JUnit no POM do plugin é `test` e APIs Maven são `provided`.

O `effective-pom` com perfil `central-release` do exemplo contém `<skipPublishing>true</skipPublishing>` tanto na configuração efetiva quanto na configuração do perfil. Foi gerado apenas o POM efetivo; não houve geração/upload de bundle nem execução de goal `publish`/`deploy`. Isso confirma a configuração de exclusão, sem alegar que um bundle real foi criado ou validado. Para o consumer fixture, Spring Framework 7.0.10 e Spring Security Test 7.0.7 foram escolhidos como versões publicadas e alinhadas; a versão 7.0.7 corrige CVE-2026-47877/CVE-2026-59276 segundo os [advisories oficiais Spring Security](https://spring.io/security/). Não houve OSV scan dedicado para o classpath test do fixture; portanto isso não afirma ausência de outros advisories.

### Gating atualizado

- **PASSOU:** `clean verify` do reactor; instalação local; consumidor Maven independente e integração dos três goals do plugin; compilação/execução de testes usando JUnit, Spring Test, Spring Security Test, Spring MVC e Servlet no classpath test do consumidor; os seis POMs têm os metadados Central esperados; exemplo com `skipPublishing=true` no POM efetivo; cinco classifiers Javadoc incluem avisos MIT de jQuery/jQuery UI.
- **STATUS ATUALIZADO EM B8.9:** a pendência técnica de referências JDK/java.base incompletas nos Javadocs foi encerrada; decisões humanas sobre developer e revisão jurídica geral continuam fora desse gate. Nenhum parecer ou aprovação jurídica automática foi emitido.
- **FORA DE ESCOPO/SEM AÇÃO:** não foi alterada versão SNAPSHOT, dependência, funcionalidade Java, API pública, Jira ou publicação. Não houve deploy, Sonatype, credencial, GPG, commit, push ou tag.

Não há aprovação jurídica automática nesta conclusão. A publicação continua bloqueada até as revisões humanas e os gates finais de `release-readiness.md` serem aprovados. A versão SNAPSHOT, API pública e funcionalidades Java foram preservadas. A única correção de dependência no testkit foi substituir `spring-security-test` por `spring-security-core` em `provided`, alinhada às importações; Hamcrest foi mantido após o compilador demonstrar que é requerido. POM e teste do consumidor independente declaram Spring Security Test diretamente em `test` ao usar `.with(user(...))`.

## B8.3 Parte 3 — fechamento técnico dos avisos legais (2026-11-19)

Registro histórico da execução com JBR/Windows em 2026-11-19. A pendência técnica de avisos incompletos e a versão específica de jQuery UI abaixo foram substituídas pela validação Temurin/Linux em B8.9. O status vigente do gate técnico de avisos Javadoc é **APROVADO** em B8.9; isso não é certificação jurídica independente.

### Developer metadata

O proprietário confirmou a identidade pública. O parent e os cinco POMs dos módulos publicáveis declaram, de forma consistente, `id=dhionson2`, `name=Dhionson Plaster Moreira` e `url=https://github.com/dhionson2`. Os `help:effective-pom` foram gerados separadamente para os seis POMs e os seis resultados foram conferidos por XML; todos mostram os três mesmos valores. A versão continua `1.0.0-SNAPSHOT`.

### Causa e correção dos avisos JDK

O Maven do ambiente executa no JBR incluído no Android Studio: `C:\Program Files\Android\Android Studio\jbr`, Java 21.0.8 (Standard Doclet 21.0.8+-14018985-b1038.68). O JDK Temurin 21.0.12.1 no PATH não é o JDK que gerou os artefatos Maven. A fonte efetiva dos arquivos é:

- `.../jbr/legal/jdk.javadoc/LICENSE`, `ADDITIONAL_LICENSE_INFO` e `ASSEMBLY_EXCEPTION`: cada um continha somente `Please see ..\java.base\<arquivo>`.
- `.../jbr/legal/java.base/LICENSE`, `ADDITIONAL_LICENSE_INFO` e `ASSEMBLY_EXCEPTION`: textos completos correspondentes aos destinos relativos dos três ponteiros.
- `.../jbr/legal/jdk.javadoc/jquery.md` e `jqueryUI.md`: avisos upstream para os assets padrão do doclet.

O comportamento default do Javadoc copiava os três ponteiros da pasta `jdk.javadoc`, mas não a pasta irmã `java.base`, deixando referências sem destino nos classifiers. A opção oficial do Javadoc 21 `--legal-notices <directory>` seleciona os arquivos de aviso de uma pasta; ela não resolve por si só esses ponteiros entre módulos. A correção mínima no perfil Maven `central-release` usa `maven-resources-plugin:3.4.0:copy-resources`, na fase `initialize`, para copiar sem filtragem os três alvos de `java.base` e os dois avisos jQuery de `jdk.javadoc` para `${project.build.directory}/javadoc-legal`. O Javadoc recebe `--legal-notices ../../javadoc-legal` relativo ao diretório de saída `target/reports/apidocs`. Não há licença copiada de outra versão do JDK, texto editado ou aviso suprimido. O stage é local e é recriado a cada build.

### Verificação dos cinco Javadoc JARs

`mvnw.cmd -B -ntp -Pcentral-release -DskipTests package` — `BUILD SUCCESS`, sem `verify`, assinatura, deploy ou publicação; regenerou os cinco classifiers candidatos (core, testkit, PIT adapter, report e Maven Plugin). Cada JAR foi aberto e estes arquivos foram encontrados em `legal/`:

| Arquivo no classifier | Origem do JBR executante | SHA-256 (origem e JAR iguais) | Resultado técnico |
|---|---|---|---|
| `LICENSE` | `legal/java.base/LICENSE` | `4b9abebc4338048a7c2dc184e9f800deb349366bdf28eb23c2677a77b4c87726` | Texto integral GPLv2; não é mais ponteiro |
| `ADDITIONAL_LICENSE_INFO` | `legal/java.base/ADDITIONAL_LICENSE_INFO` | `a69bce275ba7a3570af6579cb0f55682cd75fedfcd49e0e8e9022270c447c916` | Texto integral da informação/exception de licenciamento |
| `ASSEMBLY_EXCEPTION` | `legal/java.base/ASSEMBLY_EXCEPTION` | `75292f03bf23d3db7c985aecc191029b93883200721ed23ed34a2e601463df33` | Texto integral da OpenJDK Assembly Exception |
| `jquery.md` | `legal/jdk.javadoc/jquery.md` | `b68f454c2bd58959c862d94bf0f16c3f78a2d537388ca060d354344db80ee695` | Copyright e texto MIT presentes |
| `jqueryUI.md` | `legal/jdk.javadoc/jqueryUI.md` | `bb0a0e89ebd824df714516bf64b9101c62081e4b376f00f929a58c09555bf111` | Copyright, MIT e a seção CC0 do aviso presentes |

Em todos os cinco JARs, os hashes desses cinco assets coincidem com os arquivos de origem do JBR; `LICENSE`, `ADDITIONAL_LICENSE_INFO` e `ASSEMBLY_EXCEPTION` não contêm `Please see ..\java.base`. Os avisos jQuery/jQuery UI permaneceram incluídos. Isso verifica conteúdo e integridade de cópia, não decide se cada obrigação legal de JDK, jQuery ou jQuery UI está satisfeita em todos os contextos. A suficiência da atribuição e a interpretação do escopo desses avisos permanecem **REQUER REVISÃO HUMANA**; não se declara aprovação jurídica automática.

`NOTICE` e `src/main/javadoc/resources/NOTICE` foram atualizados para descrever o conteúdo efetivo e a origem dos arquivos, preservando a ressalva de revisão humana. Seus conteúdos foram mantidos sincronizados.

### Validação e status

- `mvnw.cmd -B -ntp clean verify` — `BUILD SUCCESS` nos sete projetos do reactor; 44 testes passaram sem falha/erro; o contrato do exemplo resultou `VERIFIED`.
- `mvnw.cmd -B -ntp -Pcentral-release -DskipTests package` — `BUILD SUCCESS`, cinco Javadoc JARs regenerados conforme inspeção acima. Testes foram pulados nesse comando; a validação de testes é a execução separada `clean verify`.
- `mvnw.cmd -B -ntp -f <cada POM publicável> help:effective-pom -Doutput=<arquivo local>` — seis POMs conferidos, todos com os dados do developer confirmados.

| Gate | Status | Observação |
|---|---|---|
| Developer metadata nos seis POMs candidatos | **PASSOU tecnicamente** | Identidade confirmada pelo proprietário; herança/efetivo conferidos. |
| Avisos JDK sem ponteiros quebrados nos cinco Javadocs | **PASSOU tecnicamente** | Textos exatos do JBR executante incluídos e verificados por hash. |
| Avisos MIT de jQuery e jQuery UI | **PASSOU tecnicamente** | Arquivos originais do JBR incluídos sem alteração; aviso jQuery UI contém seção CC0 de demo. |
| Build/testes do reactor | **PASSOU** | `clean verify` passou; pacote central local também passou com testes pulados nesse comando. |
| Suficiência técnica dos avisos JDK/jQuery em cada classifier | **APROVADO TECNICAMENTE EM B8.9** | A antiga pendência foi encerrada com comparação byte a byte contra Temurin 21.0.13.1+2 e a validação dos cinco classifiers; a seção B8.9 não emite certificação jurídica independente. |

Não houve alteração de dependências, código funcional ou versão. Não houve deploy/publicação, GPG, credenciais Sonatype, commit, push, tag ou Jira. A release 1.0.0 não foi preparada; esta etapa conclui apenas a auditoria técnica B8.3 Parte 3.
## RC 1.0.0 — OSV-Scanner v2 final SBOM gate (2026-11-20)

### Identidade dos SBOMs e método

Validei seis CycloneDX 1.6 JSON em `docs/release-evidence/sbom-v1.0.0/`. Cada `metadata.component` corresponde ao GAV e à versão 1.0.0 publicável: parent POM (0 componentes), core (8), testkit (18), PIT adapter (9), report (15) e Maven Plugin (46). O exemplo Spring Boot não aparece em nenhum dos seis SBOMs. SHA-256 da entrada escaneada é registrado junto ao caminho do SBOM em cada saída JSON.

OSV-Scanner não estava instalado no PATH. Usei a release oficial Windows v2.7.1, commit `02f96a4`, com SHA-256 conferido contra o arquivo oficial `osv-scanner_SHA256SUMS`: `7121dcf420aefbbf89a7f0050c4d3094e079ff93afd334dfc96f16b02ec85800`. A sintaxe da versão instalada foi `osv-scanner scan source --lockfile <arquivo.cdx.json> --format json --all-packages --all-vulns --output-file <resultado>`. O JSON integral por SBOM, incluindo exit code, stdout/stderr e qualquer erro de parse/consulta, está em `docs/release-evidence/osv-v1.0.0/sbom-*.osv-scan.json`; os achados consolidados estão em `osv-v1.0.0-consolidated.json`. Cópias de entrada com extensão CycloneDX foram comparadas por SHA-256 aos SBOMs fonte; os SBOMs não foram alterados.

| Artefato | Pacotes extraídos | Exit code | Estado | Resultado |
|---|---:|---:|---|---|
| Parent POM | 0 | 128 | **INCONCLUSIVO** | BOM não tem componentes; scanner reportou “No package sources found”. |
| Core | 8 | 0 | Concluído | Nenhum advisory. |
| Testkit | 18 | 1 | Concluído com achados | 6 advisories em 3 componentes; 1 HIGH, 2 MODERATE, 3 LOW. |
| PIT adapter | 9 | 0 | Concluído | Nenhum advisory. |
| Report | 15 | 0 | Concluído | Nenhum advisory; Jackson 2.18.12 sem finding nesta consulta. |
| Maven Plugin | 46 | 1 | Concluído com achados | 2 advisories HIGH. |

Exit code `1` acompanha achados de vulnerabilidade e veio com JSON válido. Os demais cinco scans, exceto o parent, concluíram sem erro de consulta visível em stderr. O parent permanece inconclusivo: não converto “zero pacotes extraídos” em aprovação ou evidência de ausência de vulnerabilidades.

### Achados por componente e escopo Maven atual

Os scopes abaixo foram verificados em árvores `dependency:tree -Dverbose` novas, geradas na versão 1.0.0 para os cinco módulos; não inferi o scope a partir do CycloneDX, pois o plugin gerador marcou os componentes como `required` e não preservou a distinção Maven.

| Artefato | Componente e versão | CVE / GHSA | Severidade OSV | Escopo Maven e aplicabilidade |
|---|---|---|---|---|
| Testkit | `org.springframework:spring-expression:6.2.15` | CVE-2026-41850 / GHSA-r5w3-xv2f-j59q | **HIGH** | `provided`, transitivo de Spring Security Core; aparece na superfície de classpath de testes do consumer, não é empacotado nem propagado transitivamente pelo testkit. |
| Maven Plugin | `com.google.guava:guava:33.2.1-jre` | CVE-2026-102554 / GHSA-xxph-c9ww-hj94 | **HIGH** | `provided`, transitivo de Maven Core/Guice; fornecido pelo host Maven, não incluído no JAR do plugin. |
| Maven Plugin | `org.codehaus.plexus:plexus-utils:3.5.1` | CVE-2025-67030 / GHSA-6fmv-xxpf-w3cw | **HIGH** | `provided`, transitivo das APIs/Core Maven; fornecido pelo host Maven, não incluído no JAR do plugin. |
| Testkit | `org.springframework:spring-expression:6.2.15` | CVE-2026-41851 / GHSA-wxpp-56q6-5pcg | MODERATE | `provided`, transitivo de Spring Security Core; classpath de testes do consumer. |
| Testkit | `org.springframework.security:spring-security-core:6.4.13` | CVE-2026-22751 / GHSA-x2wq-9x2f-fhj7 | MODERATE | `provided` direto; classpath de testes do consumer. |
| Testkit | `org.springframework:spring-core:6.2.15` | CVE-2026-41848 / GHSA-659m-px2c-25wj | LOW | `provided`, transitivo de Spring Test/Security. |
| Testkit | `org.springframework:spring-expression:6.2.15` | CVE-2026-41852 / GHSA-9f52-rjqv-25qv | LOW | `provided`, transitivo de Spring Security Core. |
| Testkit | `org.springframework.security:spring-security-core:6.4.13` | CVE-2026-22746 / GHSA-vxf7-qj7q-83fh | LOW | `provided` direto; classpath de testes do consumer. |

Total: **3 HIGH, 2 MODERATE, 3 LOW, 0 CRITICAL**. Nenhum advisory foi encontrado nos componentes `compile` dos cinco JARs library/plugin; os testes JUnit/Gson dos SBOMs não tiveram advisory. O parent POM não tem runtime, mas seu scan vazio permanece inconclusivo.

### Comparação com B8.2

A comparação usa os resultados recém-gerados da composição 1.0.0 e os novos dependency trees. Guava 33.2.1-jre e Plexus Utils 3.5.1 no caminho Maven `provided` foram reportados novamente; agora os vínculos ao artefato `code-integrity-guard-maven-plugin:1.0.0` e aos scopes atuais estão confirmados diretamente. Jackson 2.18.12 no `report` e Maven Plugin não teve finding nesta execução. A conclusão B8.2 de não encontrar HIGH/CRITICAL em runtime compile dos módulos library/plugin permanece consistente, mas não basta para o gate: o scan do SBOM do testkit 1.0.0 revelou CVE-2026-41850 HIGH no Spring Expression 6.2.15 `provided`, e o plugin conserva dois HIGH no ambiente host.

B8.2 também continha achados de Spring/Tomcat no exemplo; esses não contam como achados dos seis artefatos porque o exemplo foi corretamente excluído destes SBOMs. O exemplo continua fora da distribuição Central planejada e seus riscos permanecem aplicáveis se alguém o distribuir separadamente. A contagem agregada B8.2 de 50 advisory records/109 componentes não foi reutilizada como resultado desta RC.

### Parecer técnico de segurança da RC

**Não há HIGH/CRITICAL identificados em dependências compile/runtime empacotadas dos cinco JARs de biblioteca/plugin nos SBOMs examinados.** Há, contudo, três HIGH nos SBOMs distribuídos em componentes `provided`: um no classpath de testes previsto para o testkit e dois no host Maven que executa o plugin. Esses riscos não são conteúdo empacotado dos JARs, mas não devem ser omitidos: consumidores do testkit escolhem suas próprias versões das APIs `provided`, e o risco do plugin depende das versões efetivas disponíveis no Maven host.

O gate final de segurança permanece **BLOQUEADO PARA PUBLICAÇÃO** até: (1) a análise do parent POM ser fechada com evidência adequada, sem converter zero componentes em clean; (2) o mantenedor decidir se os três HIGH em escopos `provided`/host são aceitos ou exigir remediação em outra etapa; e (3) preservar a exclusão do exemplo. Esta auditoria não atualizou dependências, Java ou versão, e não é parecer de segurança abrangente do produto.

Nenhuma credencial, GPG, alteração de versão, commit, push, tag, deploy ou publicação foi usada/executada. Artefatos da auditoria e árvores atuais ficam sob `docs/release-evidence/osv-v1.0.0/`.

## B8.6 — Decisão técnica dos findings da RC 1.0.0 (2026-11-20)

**Parecer: gate ainda bloqueado para publicação.** Preservam-se os resultados OSV B8.5 (3 HIGH, 2 MODERATE, 3 LOW, 0 CRITICAL); não houve alteração de dependências/código e, conforme o critério, não foram executados testes. O snapshot OSV não substitui a consulta de advisories oficiais.

### Spring — testkit

O testkit seleciona Spring Framework 6.2.15 e Spring Security Core 6.4.13, ambos `provided`. O source usa `Authentication`, `SecurityContext` e `SecurityContextHolder`; não foram encontrados parser/avaliador SpEL, `@PreAuthorize`, `DaoAuthenticationProvider`, `JdbcOneTimeTokenService`, `DataBinder` ou APIs de binding afetadas. Essas dependências não são empacotadas no JAR. O testkit não avalia expressões de usuários nem implementa autenticação da aplicação.

| Finding | Avaliação e decisão |
|---|---|
| Spring Expression 6.2.15 — CVE-2026-41850 HIGH | Spring define como pré-condição aceitar e avaliar SpEL não confiável; 6.2.19 corrige essa CVE. **RISCO RESIDUAL JUSTIFICADO** para CIG: API não usada no testkit e dependência `provided`, não empacotada. Consumidores devem avaliar suas próprias expressões e usar Spring corrigido. |
| Spring Core 6.2.15 — CVE-2026-41848 LOW; Spring Expression 6.2.15 — CVE-2026-41851 MODERATE e CVE-2026-41852 LOW | 6.2.19 corrige esses findings do snapshot, mas advisory oficial posterior CVE-2026-59282 afeta 6.2.15–6.2.19 em cenário de data binding com lista auto-populável; fix 6.2.20 é Enterprise Only, e a linha OSS corrigida inclui 7.0.9. CVE-2026-59282 não consta da consolidação OSV B8.5. **RISCO RESIDUAL JUSTIFICADO** para CIG pela ausência das APIs vulneráveis no testkit. Não fixar 6.2.19 como solução geral nem declarar o SBOM atual corrigido. |
| Spring Security Core 6.4.13 — CVE-2026-22746 LOW e CVE-2026-22751 MODERATE | Os cenários oficiais exigem `DaoAuthenticationProvider` com atributos de conta ou configuração explícita de `JdbcOneTimeTokenService`, ausentes no testkit. 6.4.16 é Enterprise Only; Spring indica versões OSS corrigidas 6.5.10/7.0.5. **RISCO RESIDUAL JUSTIFICADO** no escopo do CIG; consumidor escolhe a versão de Spring Security para sua aplicação e classpath de testes. |

Override isolado `spring-framework.version=6.2.19` é plausível contra o BOM Boot 3.4.13 e corrige CVE-2026-41850, mas não fecha advisories oficiais atuais. O POM mantém o gerenciamento atual do BOM; a revisão deve decidir entre atualizar a matriz Spring inteira e manter a versão efetiva sob controle do consumidor. Não declarar esses findings “corrigidos”.

### Maven host — Guava e Plexus Utils

O POM do plugin declara `maven-plugin-api`/`maven-core` 3.9.9 em `provided`. A árvore efetiva mostra Guava 33.2.1-jre por `maven-core → Guice 5.1.0 → Guava`; CIG não declara Guava. Plexus Utils 3.5.1 vem das APIs/Core do Maven. Inventário do JAR do plugin não encontrou classes Guava, `org/codehaus/plexus/util/Expand` ou Spring. São bibliotecas do host, não empacotadas pelo plugin; hosts vulneráveis ainda são relevantes para execução do plugin.

| Finding | Avaliação e decisão |
|---|---|
| Guava 33.2.1-jre — CVE-2026-102554 HIGH | GHSA descreve alocação sem limite no `readObject` de `CompactHashMap`, `CompactHashSet` e `MapMakerInternalMap`. O plugin não usa/desserializa esses tipos. Sem divergência de severidade: OSV HIGH/8.2 coincide com GHSA HIGH, CVSS v4 8.2. Maven 3.9.16 inclui Guava 33.6.0-jre (afetada); 3.10.1 inclui 33.8.0-jre (corrigida, fix 33.7.2). **BLOQUEADOR** para suporte `Maven 3.9+`: wrapper atual 3.9.16 ainda tem Guava afetada. Alterar versão `provided` no POM do plugin não substitui a do host. Exige elevar mínimo para ≥3.10.1 ou mitigação do host demonstrada/testada. |
| Plexus Utils 3.5.1 — CVE-2025-67030 HIGH | Advisory indica traversal em `Expand.extractFile`; plugin não importa/chama a API. Maven 3.9.16 inclui Plexus Utils 3.6.1 (fix) e 3.10.1 inclui 3.6.2. A árvore POM compilada contra Maven 3.9.9 seleciona a versão afetada. **BLOQUEADOR** para suporte `Maven 3.9+` sem limite corrigido: embora não alcançada pelo código CIG, a biblioteca do host não é controlada pelo plugin e versões antigas estão na faixa anunciada. Mínimo ≥3.10.1 resolve os dois findings de host sem empacotar Plexus no plugin. |

As composições foram conferidas nas [dependências oficiais do Maven 3.9.16](https://maven.apache.org/ref/3.9.16/apache-maven/dependencies.html) e [3.10.1](https://maven.apache.org/ref/3.10.1/apache-maven/dependencies.html). README declara 3.9+ e o wrapper usa 3.9.16; a versão 3.9.16 corrige Plexus, não Guava. Recomendar 3.10.1 sem restringir formalmente a matriz e executar build/execução do plugin nessa versão não libera o gate.

### Parent POM e inventário

O parent tem packaging `pom`, módulos e `dependencyManagement`, mas nenhuma seção própria `<dependencies>`. CycloneDX zero-component levou OSV exit 128 (`No package sources found`). **NÃO APLICÁVEL AO PARENT POM**, justificado pela ausência de dependências próprias; o scan continua inconclusivo, nunca “limpo”. Os cinco módulos continuam avaliados por SBOM individual. B8.2 serve apenas como referência histórica: composição 1.0.0 e scopes foram reavaliados; Spring surgiu no testkit; nenhum risco antigo foi aceito automaticamente.

**Ação para reabrir o gate:** decisão humana sobre Maven mínimo ≥3.10.1, atualização formal de matriz/wrapper/metadados e validação nesse host; novo scan OSV para incluir CVE-2026-59282; manter orientação Spring a consumidores e revisar os riscos residuais. Isso não constitui aprovação jurídica nem autorização de publicação.

Fontes: [Spring CVE-2026-41850](https://spring.io/security/cve-2026-41850/), [Spring CVE-2026-59282](https://spring.io/security/cve-2026-59282/), [Spring Security CVE-2026-22746](https://spring.io/security/cve-2026-22746/), [Spring Security CVE-2026-22751](https://spring.io/security/cve-2026-22751/), [GHSA Guava](https://github.com/advisories/GHSA-xxph-c9ww-hj94), [GHSA Plexus](https://github.com/advisories/GHSA-6fmv-xxpf-w3cw).

## B8.6 — Validação candidata do Maven Wrapper 3.10.1 (2026-11-20)

O Wrapper foi migrado localmente de Maven 3.9.16 para 3.10.1, mantendo `wrapperVersion=3.3.4`, `distributionType=bin` e o `wrapperUrl`; atualizei `distributionUrl` e adicionei `distributionSha256Sum`. O ZIP foi baixado do [diretório oficial de binários Apache](https://downloads.apache.org/maven/maven-3/3.10.1/binaries/) e o SHA-512 local conferiu com o checksum oficial. O SHA-256 calculado sobre esse mesmo arquivo verificado foi fixado no Wrapper; `mvnw.cmd -version` baixou/extraiu a distribuição e reportou Apache Maven 3.10.1 (`3afd8084b5629f80b97750906a719879ed4cd283`) com Java 21.0.8.

### Host utilizado e grafo publicado do plugin

O inventário do Maven efetivamente carregado contém `guava-33.8.0-jre.jar` e `plexus-utils-3.6.2.jar`. São versões posteriores aos fixes oficiais Guava 33.7.2 e Plexus 3.6.1; portanto **CVE-2026-102554 e CVE-2025-67030 não se aplicam a esses dois componentes do host 3.10.1** conforme seus ranges/advisories. A página oficial de dependências confirma Maven 3.10.1 e esses componentes [Guava 33.8.0](https://maven.apache.org/ref/3.10.1/apache-maven/dependencies.html) / [Plexus Utils 3.6.2](https://maven.apache.org/ref/3.10.1/apache-maven/dependencies.html).

Há uma distinção importante: a árvore do módulo `code-integrity-guard-maven-plugin` ainda resolve `maven-core`/`maven-plugin-api` 3.9.9 em `provided`, logo mostra Guava 33.2.1-jre e Plexus Utils 3.5.1. Isso é a declaração atualmente publicada pelo plugin, não o classpath da distribuição Wrapper 3.10.1 em execução. Não alterei o POM do plugin nem o descritor, porque fazê-lo elevaria a baseline de compatibilidade de consumidores e requer decisão do mantenedor. Assim, SBOM OSV B8.5 continua evidenciando os componentes da árvore declarada, enquanto a evidência desta seção demonstra que o host local atual está corrigido para esses dois CVEs. Os achados não foram suprimidos nem declarados corrigidos no SBOM anterior.

### Execução e resultados

| Execução Maven 3.10.1 | Resultado |
|---|---|
| `mvnw.cmd -B -ntp clean install` | **BUILD SUCCESS**, os sete projetos; 44 testes, 0 falhas, 0 erros, 0 skips. Sem `-DskipTests`. |
| `mvnw.cmd -B -ntp -f integration-consumer\pom.xml clean verify` | **BUILD SUCCESS**; Surefire 2 (1 passou, 1 skip intencional), Failsafe 1 passou; contrato `CONSUMER-OWNER-CHECK` `VERIFIED`. |
| Example `clean verify -Ppit-validation -Dcig.minimumMutationPercent=60` | **BUILD SUCCESS**; PIT `AVAILABLE`, 7/11 = 63.636%, `partialCoverage=true`, contrato `CUSTOMER-OWNER-ISOLATION` `VERIFIED`. Um sobrevivente e três mutações sem cobertura. |
| Mesmo comando com limite 70% | **BUILD FAILURE esperado** no quality gate CIG; PIT continuou `AVAILABLE`, 7/11 = 63.636%, contrato permaneceu `VERIFIED`. A falha é o score abaixo de 70%, não falha de execução PIT. |

Os goals do plugin funcionaram na versão candidata do host: `prepare-evidence`, `seal-evidence` e `audit` rodaram no exemplo do reactor e no consumidor externo. Os relatórios JSON selecionados registram `buildStatus=FAILED` somente no cenário PIT de 70% e mantêm PIT disponível/contrato verificado; o resumo de testes, a árvore do plugin e os dados PIT necessários estão em `docs/release-evidence/b8.6-maven-3.10.1/`. XMLs Surefire/Failsafe e logs brutos foram mantidos localmente porque carregam classpaths, diretórios temporários, caminhos do usuário e variáveis de ambiente da máquina.

### Declarar/garantir mínimo para usuários

O Wrapper só escolhe o Maven usado para desenvolver/construir este repositório; **não restringe o Maven instalado pelos usuários do plugin**. Para um plugin Maven, a referência oficial do POM diz que `<prerequisites><maven>3.10.1</maven></prerequisites>` verifica o mínimo ao executar o plugin. A configuração `requiredMavenVersion` do descritor do `maven-plugin-plugin` é avaliada pelo Maven 4 e ignorada por versões anteriores, portanto sozinha não protege consumidores que ainda executam Maven 3. [POM Prerequisites](https://maven.apache.org/pom.html#Prerequisites), [plugin descriptor](https://maven.apache.org/components/plugin-tools/maven-plugin-plugin/descriptor-mojo.html).

Exigir 3.10.1 retiraria suporte anunciado a hosts Maven 3.9.x e 3.10.0: nesses ambientes os goals deixariam de poder ser executados, o que é uma mudança de compatibilidade para consumidores. Enforcer na build do projeto valida apenas esta build e não substitui uma verificação de runtime do plugin. **Não implementei a restrição nem elevei `maven-plugin-api`/`maven-core` no POM do plugin.** Recomendação para decisão: adotar mínimo 3.10.1, alinhar as dependências `provided` do plugin a 3.10.1, declarar o prerequisite e validar o descritor em Maven 3.10.1; depois regenerar SBOM e OSV. Se o mantenedor quiser preservar Maven 3.9, os findings de Guava continuam bloqueando a baseline declarada; a execução local segura no Wrapper não basta.

### Spring e gate remanescente

Nenhuma dependência Spring foi alterada. Permanecem 6.2.15 / 6.4.13 nos escopos `provided` do testkit; as análises de SpEL, Spring Security e CVE-2026-59282 registradas acima continuam como riscos residuais a revisar pelo mantenedor/consumidor, não como corrigidas pelo Maven 3.10.1. O scan OSV B8.5 não foi reexecutado nesta etapa; seu total continua 3 HIGH, 2 MODERATE, 3 LOW, mas a nova consulta oficial de CVE-2026-59282 ainda precisa ser incorporada ao próximo scan.

**Bloqueador/decisão humana:** aprovar ou rejeitar a quebra de compatibilidade Maven 3.9→3.10.1 e autorizar alinhamento dos requisitos POM/descritor; então gerar SBOM/OSV novo. Não houve deploy/publicação, commit, push, tag, Sonatype ou GPG. A versão do projeto permaneceu 1.0.0.

## B8.7 — Baseline Maven 3.10.1 (2026-11-20)

A decisão proposta foi implementada no POM do Maven Plugin: `<prerequisites><maven>3.10.1</maven></prerequisites>`. `maven-core` e `maven-plugin-api` estão alinhados em 3.10.1 com escopo `provided`; não foram adicionadas dependências diretas Guava ou Plexus Utils. O README e a instalação agora exigem Java 21+ e Maven 3.10.1+. O POM instalado localmente contém o prerequisite e o descritor gerado contém `<requiredMavenVersion>3.10.1</requiredMavenVersion>`.

O teste negativo executou o consumidor independente com Maven 3.9.16 e foi rejeitado antes do goal: `Required Maven version 3.10.1 is not met by current version 3.9.16`. Com Maven 3.10.1, a árvore efetiva confirma `maven-core:3.10.1:provided` e `maven-plugin-api:3.10.1:provided`; o host oficial inclui Guava 33.8.0-jre e Plexus Utils 3.6.2, acima dos fixes 33.7.2 e 3.6.1, respectivamente.

O SBOM CycloneDX do Maven Plugin foi regenerado com 55 componentes e substituiu o SBOM candidato em `docs/release-evidence/sbom-v1.0.0/`. OSV-Scanner 2.7.1 concluiu a consulta (exit code 0; 55 pacotes; JSON sem falha de parse/consulta e sem findings) para esse SBOM. Guava 33.8.0-jre e Plexus Utils 3.6.2 estão presentes e os advisories CVE-2026-102554 e CVE-2025-67030 não se aplicam à baseline efetiva 3.10.1. Este resultado descreve o host mínimo documentado; consumidores que executarem Maven mais recente devem avaliar a distribuição instalada.

O scan de dependências não substitui a revisão Spring. O testkit permanece em Spring Framework 6.2.15 e Spring Security 6.4.13 em escopos `provided`; CVE-2026-41850 e demais findings previamente documentados continuam como risco residual. A página oficial confirma que CVE-2026-59282 afeta Spring Framework 6.2.0–6.2.19 sob a condição de data binding com uma lista auto-populável e propriedades aninhadas; correção OSS publicada em Spring 7.0.9, enquanto 6.2.20 é Enterprise Support Only. O testkit não foi atualizado e a condição de exploração não foi demonstrada como alcançável nele. Portanto CVE-2026-59282 permanece **RISCO RESIDUAL**, não corrigido nem automaticamente aceito. A revisão de advisory não implica que OSV o tenha reportado.

Evidências B8.7 selecionadas: árvore, descritor, SBOM final e relatório JSON completo OSV com exit code/scanner em `docs/release-evidence/b8.7-maven-baseline/`; a rejeição de Maven 3.9.16 foi reduzida a `maven-3.9.16-rejection.txt`, sem caminho local. Logs, versões de host com diretório do usuário, POM instalado redundante, SBOM pre-baseline e cópia duplicada do scanner foram mantidos localmente. A baseline Maven resolve os findings Guava/Plexus no host testado. Fontes oficiais: [dependências do Maven 3.10.1](https://maven.apache.org/ref/3.10.1/apache-maven/dependencies.html), [POM prerequisites](https://maven.apache.org/pom.html#Prerequisites), [CVE-2026-59282](https://spring.io/security/cve-2026-59282/).

## B8.8 — Fechamento incremental de riscos Spring (2026-11-20)

Esta etapa verificou somente advisories Spring novos e os pontos de composição/fluxo relevantes; não repetiu os scans nem alterou dependências ou Java. Evidência de árvore atual do testkit e do exemplo, inventário do JAR e escopo da inspeção estão em `docs/release-evidence/b8.8-spring-risk-review/`.

### CVE novas

| Advisory e condição oficial | Testkit / seis artefatos | Exemplo e decisão |
|---|---|---|
| CVE-2026-59273 — CRITICAL; Spring Security 6.4.0–6.4.19 e 7.0.0–7.0.8 afetados. O exploit exige que entrada externa seja vinculada a uma propriedade de `ELRequestMatcher`, disparando a avaliação SpEL irrestrita. | Entre os componentes relevantes, os SBOMs do testkit identificam `spring-security-core:6.4.13` e `spring-expression:6.2.15`, mas não `spring-security-web`. A árvore Maven atual do testkit não resolve `spring-security-web`; o JAR não contém essa classe nem classes Spring. As APIs do testkit leem `Authentication`/`SecurityContext` e executam requests MockMvc recebidos do consumidor; não instanciam matcher nem vinculam entrada a matcher. **NÃO APLICÁVEL COM EVIDÊNCIA ao testkit publicado.** | O exemplo excluído resolve `spring-security-web:6.4.13`, dentro do intervalo vulnerável, mas `DemoSecurity` usa `anyRequest().authenticated()` e não `ELRequestMatcher`; não há binding externo para propriedade desse matcher. **NÃO APLICÁVEL COM EVIDÊNCIA à configuração atual do exemplo**; sua dependência permanece vulnerável por versão e o exemplo não deve ser distribuído. Consumidores que incluam Security vulnerável e exponham binding de `ELRequestMatcher` têm risco próprio. |
| CVE-2026-59398 — MEDIUM; Spring Framework 6.2.0–6.2.20 afetado. Requer MVC/WebFlux e negociação de conteúdo por parâmetro ativada; para MVC, a configuração ainda precisa permitir extensões não registradas. | `spring-webmvc` e `spring-webflux` não aparecem em nenhum dos seis SBOMs; a árvore atual do testkit só resolve `spring-test`, `spring-core`, Spring Security Core e transitivos. **NÃO APLICÁVEL COM EVIDÊNCIA ao artefato**. Para consumidores, permanece dependente da própria stack/configuração MVC ou WebFlux. | O exemplo é MVC com Framework 6.2.15, mas não configura `favor-parameter`; o default documentado do Spring Boot é `false`, e não há configurer/estratégia de content negotiation no código. **NÃO APLICÁVEL COM EVIDÊNCIA à configuração atual**. Se alguém ativar negociação por parâmetro, reavaliar. |
| CVE-2026-59389 — MEDIUM; Spring Framework 6.2.0–6.2.20 afetado. Advisory exige WebFlux, multipart de fonte não confiável, verificação do conteúdo e relay a serviço downstream. | `spring-webflux` não aparece nos SBOMs nem na árvore do testkit; a API não recebe multipart ou relaya conteúdo. **NÃO APLICÁVEL COM EVIDÊNCIA ao artefato**; é risco do consumidor que implemente aquele fluxo WebFlux. | O exemplo resolve MVC, não WebFlux, e não tem rota multipart, inspeção de partes nem relay downstream. **NÃO APLICÁVEL COM EVIDÊNCIA ao exemplo atual**, além de estar excluído da distribuição. |

A fixture `integration-consumer` usa `spring-security-test:7.0.7` e Spring MVC 7.0.10 apenas em escopo de teste. O teste usa `.with(user(...))`; não instancia ELRequestMatcher nem vincula dados externos a matcher. Não é um dos artefatos distribuídos.

### Riscos anteriores consolidados e aceite

| Grupo | Classificação final para distribuição do CIG |
|---|---|
| Spring Expression 6.2.15: CVE-2026-41850 HIGH, CVE-2026-41851 MODERATE, CVE-2026-41852 LOW; Spring Core 6.2.15: CVE-2026-41848 LOW | Dependências `provided` do testkit, sem parser/evaluador SpEL nem uso das APIs vulneráveis no código CIG. **RESIDUAL DEPENDENTE DO CONSUMIDOR**: aplicações que avaliem expressões controladas por usuário devem escolher Spring corrigido e não tratar `provided` como mitigação. |
| Spring Security Core 6.4.13: CVE-2026-22751 MODERATE e CVE-2026-22746 LOW | `provided` do testkit; APIs/fluxos específicos do advisory (provider com atributos de conta / serviço de one-time-token) não aparecem no testkit. **RESIDUAL DEPENDENTE DO CONSUMIDOR**, que seleciona a versão e configuração efetivas. |
| Spring Framework 6.2.15: CVE-2026-59282 MEDIUM | A condição exige binding de caminhos controlados pelo usuário sobre lista auto-populável com subpropriedades; não há `DataBinder` nem esse modelo no código do testkit ou exemplo. **RESIDUAL DEPENDENTE DO CONSUMIDOR**, não corrigido por ausência de uso local. |
| CVE-2026-59273, CVE-2026-59398 e CVE-2026-59389, recém-analisadas | **NÃO APLICÁVEIS COM EVIDÊNCIA** aos seis artefatos/configuração atual, pelas composições e ausência das condições de entrada documentadas acima. As versões Spring que aparecem isoladas em escopo provided não demonstram por si um caminho explorável. |

Não foi confirmado caminho de exploração CRITICAL/HIGH nos seis artefatos distribuídos nem na fixture/exemplo nas configurações atuais. Isso não elimina os advisories de versões `provided` já identificados: **aceite explícito do mantenedor ainda é necessário** para os riscos Spring residuais do testkit antes do GO de distribuição. Para os consumidores, a aceitação/mitigação depende da stack e dos fluxos de cada aplicação. Parecer final: **NO-GO até o mantenedor registrar o aceite explícito dos riscos residuais anteriores**; após esse aceite, os três CVEs novos desta etapa não constituem bloqueador técnico da distribuição 1.0.0. A decisão não é automática nem equivale a ausência geral de vulnerabilidades.

Fontes oficiais: [CVE-2026-59273](https://spring.io/security/cve-2026-59273/), [CVE-2026-59398](https://spring.io/security/cve-2026-59398/), [CVE-2026-59389](https://spring.io/security/cve-2026-59389/), [CVE-2026-59282](https://spring.io/security/cve-2026-59282/), [Spring Boot 3.4 application properties](https://docs.spring.io/spring-boot/3.4/appendix/application-properties/index.html).

## B8.9 — Fechamento técnico do licenciamento dos Javadocs (2026-11-20)

### Decisão

**Gate técnico de avisos/licenciamento dos cinco Javadoc JARs: APROVADO.** Esta aprovação significa que os materiais de terceiros verificados foram identificados, seus avisos correspondentes estão completos/acessíveis e as cópias JDK foram reproduzidas do mesmo JDK utilizado para gerar os Javadocs. Não constitui certificação ou parecer jurídico independente.

### Solução reproduzível

O workflow `.github/workflows/ci.yml` usa `ubuntu-latest` e `actions/setup-java` com `distribution: temurin`, `java-version: '21'`. A verificação equivalente executou em Ubuntu Linux x86_64 sob WSL com Eclipse Temurin `21.0.13.1+2`, release oficial de 17/11/2026. O arquivo Linux foi validado pelo SHA-256 `bb8a93f9ae6e7a693b2b721e85dbeff26ae1a77447985a8d7afcdd9c7cf6222e`; a URL e o checksum estão fixados em `validate-temurin-linux.sh`, e o resultado está em `temurin-archive-checksum.txt` no diretório de evidências B8.9.

O padrão do Javadoc 21 copia três ponteiros de `legal/jdk.javadoc` sem trazer os textos irmãos de `java.base`. Isso foi observado com uma execução padrão do Javadoc Temurin; o resultado está resumido em `temurin-default-javadoc-pointer-summary.txt`. A saída integral de notices e o arquivo de hashes com diretório temporário do probe permanecem locais. A opção oficial `--legal-notices` aceita um diretório fonte de avisos. O perfil `central-release` usa `maven-resources-plugin:3.4.0:copy-resources` para copiar sem filtro os três documentos de `${java.home}/legal/java.base` e os dois avisos de `${java.home}/legal/jdk.javadoc` para `${project.build.directory}/javadoc-legal`; o argumento Javadoc usa o caminho Maven absoluto `${project.build.directory}/javadoc-legal`. Não há caminho específico de JBR, Windows, nem supressão de aviso.

### Inventário por material e obrigação

| Arquivos incluídos por classifier | Licença/aviso | Local no JAR e resultado |
|---|---|---|
| `script-dir/jquery-3.7.1.min.js` | MIT; copyright OpenJS Foundation e aviso de permissão devem acompanhar cópias/substantial portions | `legal/jquery.md`, texto integral copiado sem edição; versão verificada no cabeçalho do JS |
| `script-dir/jquery-ui.min.js`, `script-dir/jquery-ui.min.css`, `resources/glass.png`, `resources/x.png` | jQuery UI 1.14.1, MIT; nota upstream concede CC0 apenas ao código no diretório `demos` | `legal/jqueryUI.md`, aviso MIT completo e nota CC0 original; nenhum conteúdo de `demos/` consta nos JARs |
| `script.js`, `search.js`, `search-page.js`, `copy.svg`, `link.svg`, `stylesheet.css` | Recursos do OpenJDK standard doclet; JS/SVG auditados preservam os cabeçalhos GPLv2-only com Classpath Exception | Na raiz do classifier; hashes batem com `target/reports/apidocs` da execução Temurin. Nenhum arquivo foi editado |
| Licenças do material JDK/doclet | GPLv2, informação da Classpath Exception e OpenJDK Assembly Exception | `legal/LICENSE`, `legal/ADDITIONAL_LICENSE_INFO` e `legal/ASSEMBLY_EXCEPTION`, textos completos da distribuição JDK |
| Conteúdo CIG no Javadoc | Apache License 2.0; fornecer uma cópia da licença e manter avisos de NOTICE aplicáveis | `resources/LICENSE` e `resources/NOTICE`; hashes iguais aos arquivos da raiz em cada classifier |

Os seguintes hashes coincidem entre cada fonte Temurin/projeto e os cinco classifiers:

| Arquivo | SHA-256 |
|---|---|
| JDK `LICENSE` | `4b9abebc4338048a7c2dc184e9f800deb349366bdf28eb23c2677a77b4c87726` |
| `ADDITIONAL_LICENSE_INFO` | `a69bce275ba7a3570af6579cb0f55682cd75fedfcd49e0e8e9022270c447c916` |
| `ASSEMBLY_EXCEPTION` | `75292f03bf23d3db7c985aecc191029b93883200721ed23ed34a2e601463df33` |
| `jquery.md` | `b68f454c2bd58959c862d94bf0f16c3f78a2d537388ca060d354344db80ee695` |
| `jqueryUI.md` | `7854d2d568992ae25ff65867bd9021d655f70670142f0796b29a3b0621f19530` |
| LICENSE do projeto | `cfc7749b96f63bd31c3c42b5c471bf756814053e847c10f3eb003417bc523d30` |
| NOTICE do projeto | `a1b723d9e939ea5ed87a0db2e38e36af7113a160fa3a6d72288951859675b8aa` |

### Geração e artefatos candidatos

`./mvnw -B -ntp -Pcentral-release -DskipTests package` terminou `BUILD SUCCESS` no ambiente Linux/Temurin. Os cinco marcadores incrementais do Maven Javadoc foram invalidados individualmente; a geração dos classifiers foi confirmada pela auditoria. Para os cinco JARs, os sete arquivos JDK/Apache/NOTICE batem byte a byte com suas fontes; não há ponteiros `java.base`; seis recursos estáticos coincidem com a saída de Javadoc e mantêm os cabeçalhos GPLv2/Classpath; os notices MIT/CC0 estão presentes e não há `demos/`. Evidência, incluindo SHA de cada JAR: `docs/release-evidence/javadoc-license-b8.9/final-javadoc-audit.txt`; comando, versão, checksums e scripts de reprodução permanecem na mesma pasta. Logs de build e arquivos com caminhos temporários foram mantidos localmente.

Os cinco candidatos publicáveis de Javadoc são:

- `code-integrity-guard-core/target/code-integrity-guard-core-1.0.0-javadoc.jar`
- `code-integrity-guard-testkit/target/code-integrity-guard-testkit-1.0.0-javadoc.jar`
- `code-integrity-guard-pit-adapter/target/code-integrity-guard-pit-adapter-1.0.0-javadoc.jar`
- `code-integrity-guard-report/target/code-integrity-guard-report-1.0.0-javadoc.jar`
- `code-integrity-guard-maven-plugin/target/code-integrity-guard-maven-plugin-1.0.0-javadoc.jar`

O Spring Boot example também recebe um Javadoc JAR local por ser módulo do reactor, mas o effective POM gerado com `central-release` mantém `skipPublishing=true` no `central-publishing-maven-plugin`: `docs/release-evidence/javadoc-license-b8.9/example-central-exclusion.txt` registra o resultado. O POM efetivo completo permanece local; Maven registrou uma advertência de resolução do plugin CIG `1.0.0` ainda não publicado/instalado, mas a avaliação reteve a configuração de exclusão.

### Conclusão objetiva

Não foi identificada obrigação técnica de avisos/licenças dos materiais acima em falta nos cinco Javadocs: os avisos têm acesso dentro do JAR, as cópias são completas e sem modificação, e o empacotamento usa `${java.home}` e uma opção oficial do JDK. A licença Apache 2.0 do projeto permanece junto ao conteúdo CIG; os assets de licença distinta mantêm seus próprios avisos no mesmo classifier, sem tentativa de relabeling. A revisão técnica não atesta interpretação de compatibilidade jurídica entre GPLv2/Classpath Exception, OpenJDK Assembly Exception, MIT e Apache-2.0.

**Reprodutível em Linux/CI:** sim, para a distribuição JDK e hash registrados. Os scripts de evidência não dependem de caminhos Windows/JBR. O workflow do CI continua selecionando a linha Temurin 21; builds futuros acompanham os textos da instalação efetivamente resolvida, então uma mudança futura de patch pode mudar versões/assets e deve gerar novos hashes. Na data da B8.9, a publicação ainda dependia do aceite humano dos riscos Spring. A decisão posterior B8.10 registra esse aceite limitado, sem alterar os findings nem classificá-los como corrigidos e sem abranger aplicações consumidoras. O gate aprovado é técnico, não certificação jurídica independente. Não houve assinatura, deploy, commit, push, tag, credenciais ou publicação.

Fontes oficiais: [Temurin 21.0.13.1+2](https://github.com/adoptium/temurin21-binaries/releases/tag/jdk-21.0.13.1%2B2), [licença dos binários Temurin](https://adoptium.net/docs/faq), [opção `--legal-notices` do Javadoc 21](https://docs.oracle.com/en/java/javase/21/docs/specs/man/javadoc.html), [LICENSE jQuery 3.7.1](https://github.com/jquery/jquery/blob/3.7.1/LICENSE.txt), [LICENSE jQuery UI 1.14.1](https://github.com/jquery/jquery-ui/blob/1.14.1/LICENSE.txt), [Apache License 2.0 §4](https://www.apache.org/licenses/LICENSE-2.0.html).

## B8.10 — Aceite limitado de riscos Spring e fechamento da RC (2026-11-20)

O mantenedor registra o aceite explícito e limitado dos riscos Spring residuais avaliados para a composição e os fluxos da RC 1.0.0. Findings são **RISCO RESIDUAL ACEITO NO ESCOPO**, não corrigidos. Este aceite não se estende a aplicações consumidoras, outras combinações de dependências, alterações futuras do testkit nem ao exemplo Spring Boot, que permanece excluído do perfil `central-release`.

| Achado Spring remanescente | Estado para os seis artefatos | Orientação de escopo |
|---|---|---|
| Spring Expression 6.2.15 — CVE-2026-41850 HIGH; CVE-2026-41851 MODERATE; CVE-2026-41852 LOW; Spring Core 6.2.15 — CVE-2026-41848 LOW | **RISCO RESIDUAL ACEITO**, sem parser/evaluador SpEL vulnerável identificado no código CIG; dependências `provided` não são correção. | Consumidores escolhem a versão Spring e devem evitar avaliar expressões controladas por usuário sem mitigação e versão corrigida suportada. |
| Spring Security Core 6.4.13 — CVE-2026-22751 MODERATE e CVE-2026-22746 LOW | **RISCO RESIDUAL ACEITO**, APIs/fluxos específicos do advisory não identificados no testkit. | Consumidor deve alinhar Spring Security e revisar seus providers/fluxos e configuração. |
| Spring Framework 6.2.15 — CVE-2026-59282 MEDIUM | **RISCO RESIDUAL ACEITO**, sem `DataBinder`/modelo auto-populável correspondente nos caminhos examinados. | Consumidores devem avaliar binding de propriedades não confiáveis e componentes aninhados em sua aplicação. |
| CVE-2026-59273 CRITICAL, CVE-2026-59398 MEDIUM e CVE-2026-59389 MEDIUM | **NÃO APLICÁVEIS COM EVIDÊNCIA** à composição/configuração atual dos seis artefatos; condições de exploração não encontradas. | Reavaliar se consumidor ativar matcher EL com binding externo, negociação por parâmetro ou fluxo WebFlux multipart/relay, conforme cada advisory. |

Os achados do exemplo, incluindo vulnerabilidades Spring/Tomcat que lá existem por versão, não foram aceitos como risco de distribuição porque o exemplo não faz parte dos seis artefatos e `skipPublishing=true` está presente no effective POM `central-release`. Não atribuir ao exemplo status de correção: ele continua apenas como fixture educacional local.

**Parecer de segurança da RC:** GO para a distribuição restrita dos seis artefatos 1.0.0 após o aceite limitado acima, sem alegar ausência geral de vulnerabilidades. Para aplicações consumidoras, o parecer depende da stack, versões, configuração e fluxos locais. O parent POM não tem dependências próprias; o scan sem pacotes é não aplicável por evidência do POM e não deve ser reportado como resultado OSV “limpo”.

Este aceite fecha a decisão de risco de B8.8 e permite revisão do commit no GitHub. Não autoriza `push`, tag, deploy ou publicação Maven Central. A aprovação B8.9 dos avisos é apenas técnica e não equivale a certificação jurídica independente. A RC continua em `1.0.0`; os POMs, seis SBOMs e metadados foram conferidos nesta revisão.
