# Estratégia de Versionamento - APM Platform

## Padrão Adotado: Semantic Versioning 2.0.0

Este projeto segue rigorosamente o **Semantic Versioning** para garantir:
- Comunicação clara de mudanças
- Compatibilidade previsível entre versões
- Gestão eficiente de dependências
- Transparência para consumidores da API

**Referência oficial**: https://semver.org/

---

## 1. FORMATO DE VERSÃO

```
MAJOR.MINOR.PATCH[-PRERELEASE][+BUILD]

Exemplo: 1.2.3-beta.1+20231226
```

### Componentes

| Componente | Quando Incrementar | Exemplo |
|------------|-------------------|---------|
| **MAJOR** | Breaking changes (incompatibilidade com versão anterior) | 1.0.0 → 2.0.0 |
| **MINOR** | Nova funcionalidade (compatível com anterior) | 1.0.0 → 1.1.0 |
| **PATCH** | Bugfix (compatível com anterior) | 1.0.0 → 1.0.1 |
| **PRERELEASE** | Versão não estável (alpha, beta, rc) | 1.0.0-beta.1 |
| **BUILD** | Metadados de build (sem impacto semântico) | 1.0.0+20231226 |

---

## 2. REGRAS DE INCREMENTO

### 2.1 MAJOR (X.0.0)
**Quando**: Breaking changes - mudanças que quebram compatibilidade com versão anterior

**Exemplos**:
- Remoção de endpoints
- Mudança em contratos de API (estrutura de request/response)
- Alteração de enums públicos
- Remoção de campos obrigatórios
- Mudança de comportamento documentado

```
1.5.3 → 2.0.0

BREAKING CHANGE:
- Endpoint /api/systems agora retorna SystemResponseV2
- Campo 'interval' renomeado para 'collectionInterval'
- Enum AlertSeverity: WARNING substituído por WARN
```

**Ações Obrigatórias**:
1. Documentar TODAS as breaking changes no CHANGELOG
2. Fornecer guia de migração
3. Considerar período de deprecation em versão anterior
4. Atualizar documentação de API (OpenAPI/Swagger)

---

### 2.2 MINOR (x.Y.0)
**Quando**: Novas funcionalidades compatíveis com versão anterior

**Exemplos**:
- Novos endpoints
- Novos campos opcionais em responses
- Novos query parameters opcionais
- Novos valores em enums (não removendo existentes)
- Novas features que não afetam comportamento atual

```
1.5.3 → 1.6.0

NEW FEATURES:
- Add endpoint GET /api/v1/systems/{id}/uptime
- Add optional field 'tags' to MonitoredSystemResponse
- Add support for custom metric collectors
```

**Ações Obrigatórias**:
1. Documentar features no CHANGELOG
2. Atualizar documentação de API
3. Garantir backward compatibility

---

### 2.3 PATCH (x.y.Z)
**Quando**: Correções de bugs e melhorias de performance

**Exemplos**:
- Correção de bugs
- Melhorias de performance
- Correções de segurança
- Correções de documentação
- Refatorações internas

```
1.5.3 → 1.5.4

BUGFIXES:
- Fix null pointer exception in metric collection
- Fix incorrect uptime calculation for systems with gaps
- Improve database query performance for historical metrics
```

**Ações Obrigatórias**:
1. Documentar fixes no CHANGELOG
2. Referenciar issues corrigidas

---

## 3. VERSÕES DE DESENVOLVIMENTO

### 3.1 Versão 0.x.x (Desenvolvimento Inicial)
**Status**: Pré-produção, instável, sem garantias de compatibilidade

**Regras especiais**:
- MINOR pode conter breaking changes
- API pode mudar drasticamente
- Não recomendado para produção

```
0.1.0 → 0.2.0  (pode ter breaking changes)
0.2.0 → 0.2.1  (bugfixes)
0.9.0 → 1.0.0  (primeira versão estável)
```

**Duração esperada**: Até primeira release estável

---

### 3.2 Pre-releases
**Formato**: `X.Y.Z-<identifier>.<number>`

| Identifier | Significado | Estabilidade |
|-----------|-------------|--------------|
| `alpha` | Desenvolvimento inicial | Baixa |
| `beta` | Feature complete, em testes | Média |
| `rc` | Release Candidate, pronto para produção | Alta |

**Exemplos**:
```
1.0.0-alpha.1    (primeiro alpha da versão 1.0.0)
1.0.0-alpha.2    (segundo alpha)
1.0.0-beta.1     (primeiro beta)
1.0.0-rc.1       (release candidate)
1.0.0            (versão estável)
```

**Ordem de precedência**:
```
1.0.0-alpha < 1.0.0-alpha.1 < 1.0.0-beta < 1.0.0-beta.2 < 1.0.0-rc.1 < 1.0.0
```

---

## 4. GESTÃO DE RELEASES

### 4.1 Branching Strategy

```
main (produção)
  ├── develop (desenvolvimento)
  ├── release/x.y.z (preparação de release)
  └── hotfix/x.y.z (correções urgentes)
```

### 4.2 Workflow de Release

#### Release Normal (Feature Release)
```bash
# 1. Criar branch de release a partir de develop
git checkout -b release/1.2.0 develop

# 2. Atualizar versão em pom.xml
mvn versions:set -DnewVersion=1.2.0

# 3. Atualizar CHANGELOG.md
# Documentar todas as features e fixes

# 4. Commit de versão
git commit -am "chore(release): bump version to 1.2.0"

# 5. Merge para main
git checkout main
git merge --no-ff release/1.2.0

# 6. Tag de release
git tag -a v1.2.0 -m "Release version 1.2.0"

# 7. Merge de volta para develop
git checkout develop
git merge --no-ff release/1.2.0

# 8. Próxima versão de desenvolvimento
mvn versions:set -DnewVersion=1.3.0-SNAPSHOT
git commit -am "chore: prepare for next development iteration"

# 9. Push
git push origin main develop --tags
```

#### Hotfix (Correção Urgente)
```bash
# 1. Criar branch a partir de main
git checkout -b hotfix/1.2.1 main

# 2. Corrigir bug

# 3. Atualizar versão
mvn versions:set -DnewVersion=1.2.1

# 4. Commit
git commit -am "fix(metric): prevent duplicate entries"
git commit -am "chore(release): bump version to 1.2.1"

# 5. Merge para main
git checkout main
git merge --no-ff hotfix/1.2.1
git tag -a v1.2.1 -m "Hotfix version 1.2.1"

# 6. Merge para develop
git checkout develop
git merge --no-ff hotfix/1.2.1

# 7. Push
git push origin main develop --tags
```

---

## 5. VERSIONAMENTO DE API

### 5.1 API Versioning Strategy
**Escolha**: URL Path Versioning

```
/api/v1/systems
/api/v2/systems
```

**Justificativa**:
- Clareza para consumidores
- Facilita documentação (Swagger UI separado por versão)
- Permite manutenção de múltiplas versões simultaneamente

### 5.2 Política de Suporte

| Versão | Suporte | Observação |
|--------|---------|------------|
| **Current** | Full support | Versão mais recente |
| **Current - 1** | Maintenance | Apenas bugfixes críticos |
| **Current - 2** | Deprecated | Aviso de descontinuação |
| **Older** | Unsupported | Removida |

**Exemplo**:
```
v3 (current)     → Full support
v2 (current - 1) → Maintenance only
v1 (current - 2) → Deprecated (6 meses para migração)
```

### 5.3 Deprecation Policy
1. Anunciar deprecation em release notes
2. Adicionar header de resposta: `Deprecation: true`
3. Período de 6 meses antes de remoção
4. Fornecer guia de migração

---

## 6. CHANGELOG

### 6.1 Formato
**Padrão**: Keep a Changelog 1.1.0

Arquivo: `CHANGELOG.md`

```markdown
# Changelog

All notable changes to this project will be documented in this file.

The format is based on [Keep a Changelog](https://keepachangelog.com/en/1.1.0/),
and this project adheres to [Semantic Versioning](https://semver.org/spec/v2.0.0.html).

## [Unreleased]
### Added
- Feature X em desenvolvimento

## [1.2.0] - 2025-12-26
### Added
- New endpoint GET /api/v1/systems/{id}/uptime
- Support for custom metric collectors
- WebSocket real-time dashboard updates

### Changed
- Improve database query performance for historical metrics
- Update PostgreSQL driver to 42.7.1

### Fixed
- Fix null pointer exception in metric collection (#45)
- Fix incorrect uptime calculation (#67)

## [1.1.0] - 2025-12-15
...

## [1.0.0] - 2025-12-01
### Added
- Initial stable release
- System registration and management
- Metric collection with schedulers
- Alert system with configurable rules
- RESTful API with OpenAPI documentation
```

### 6.2 Categorias de Mudanças
- **Added**: Novas features
- **Changed**: Mudanças em features existentes
- **Deprecated**: Features marcadas para remoção
- **Removed**: Features removidas
- **Fixed**: Bugfixes
- **Security**: Correções de segurança

---

## 7. TAGS GIT

### 7.1 Formato de Tag
```
v<MAJOR>.<MINOR>.<PATCH>[-<PRERELEASE>]

Exemplos:
v1.0.0
v1.2.3
v2.0.0-beta.1
v2.0.0-rc.2
```

### 7.2 Annotated Tags (Obrigatório)
```bash
# Criar tag anotada
git tag -a v1.2.0 -m "Release version 1.2.0

Features:
- Add uptime monitoring
- Add WebSocket dashboard

Fixes:
- Fix metric collection race condition
"

# Push de tag
git push origin v1.2.0

# Push de todas as tags
git push origin --tags
```

### 7.3 Assinatura de Tags (Recomendado)
```bash
git tag -s v1.2.0 -m "Release version 1.2.0"
```

---

## 8. AUTOMAÇÃO

### 8.1 Maven Versions Plugin
```bash
# Atualizar versão
mvn versions:set -DnewVersion=1.2.0

# Confirmar
mvn versions:commit

# Reverter
mvn versions:revert
```

### 8.2 Release Plugin
```xml
<plugin>
    <groupId>org.apache.maven.plugins</groupId>
    <artifactId>maven-release-plugin</artifactId>
    <version>3.0.1</version>
    <configuration>
        <tagNameFormat>v@{project.version}</tagNameFormat>
    </configuration>
</plugin>
```

### 8.3 CI/CD Integration
```yaml
# GitHub Actions example
name: Release
on:
  push:
    tags:
      - 'v*'
jobs:
  release:
    runs-on: ubuntu-latest
    steps:
      - uses: actions/checkout@v3
      - name: Extract version
        run: echo "VERSION=${GITHUB_REF#refs/tags/v}" >> $GITHUB_ENV
      - name: Build and publish
        run: mvn clean deploy -DskipTests
```

---

## 9. ROADMAP DE VERSÕES (PLANEJAMENTO)

### Fase 0: MVP (0.x.x)
```
0.1.0 - Domain entities and repositories
0.2.0 - Metric collection infrastructure
0.3.0 - Alert system
0.4.0 - REST API
0.5.0 - WebSocket real-time updates
0.9.0 - Feature complete, preparing for 1.0.0
```

### Fase 1: Initial Release (1.x.x)
```
1.0.0 - First stable release
1.1.0 - Dashboard improvements
1.2.0 - Advanced alert rules
1.3.0 - Metric aggregation
```

### Fase 2: Enterprise Features (2.x.x)
```
2.0.0 - Multi-tenant support (breaking changes)
2.1.0 - RBAC implementation
2.2.0 - External integrations (Slack, Telegram)
```

---

## 10. COMPATIBILIDADE E DEPRECATION

### 10.1 Quando Deprecar
1. Feature será removida em próxima MAJOR
2. Feature tem alternativa melhor
3. Feature causa problemas de performance/segurança

### 10.2 Como Deprecar
```java
/**
 * @deprecated Since 1.5.0, use {@link #newMethod()} instead.
 * Will be removed in 2.0.0
 */
@Deprecated(since = "1.5.0", forRemoval = true)
public void oldMethod() {
    // implementação
}
```

### 10.3 Comunicação
1. Documentar no CHANGELOG
2. Adicionar warnings em logs
3. Adicionar headers HTTP de deprecação
4. Atualizar documentação

---

## 11. CHECKLIST DE RELEASE

### Pre-Release
- [ ] Todos os testes passando
- [ ] Cobertura de testes adequada
- [ ] Documentação atualizada
- [ ] CHANGELOG.md atualizado
- [ ] Versão atualizada em todos os pom.xml
- [ ] API documentation (OpenAPI) atualizada
- [ ] Guia de migração criado (se breaking changes)

### Release
- [ ] Branch de release criada
- [ ] Tag criada e assinada
- [ ] Build e testes em ambiente de staging
- [ ] Artifacts publicados (Maven Central/Registry)
- [ ] Docker images publicadas
- [ ] Release notes publicadas (GitHub Releases)

### Post-Release
- [ ] Merge de release para main e develop
- [ ] Próxima versão SNAPSHOT configurada
- [ ] Comunicação para stakeholders
- [ ] Monitoramento de erros em produção

---

## RESUMO DAS REGRAS

1. **MAJOR**: Breaking changes
2. **MINOR**: Novas features compatíveis
3. **PATCH**: Bugfixes e melhorias
4. **0.x.x**: Versão de desenvolvimento, instável
5. **Tags**: `v1.2.3` (anotadas)
6. **Pre-releases**: `alpha`, `beta`, `rc`
7. **CHANGELOG**: Manter sempre atualizado
8. **API versioning**: URL path (`/api/v1`)
9. **Deprecation**: 6 meses de aviso antes de remoção
10. **Suporte**: Current + (Current - 1)

---

**Data de criação**: 2025-12-26  
**Versão**: 1.0.0  
**Status**: Ativo
