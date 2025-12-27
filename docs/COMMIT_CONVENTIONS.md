# Convenções de Commit - APM Platform

## Padrão Adotado: Conventional Commits 1.0.0

Este projeto segue rigorosamente o padrão **Conventional Commits** para garantir:
- Histórico de commits legível e semântico
- Geração automática de CHANGELOGs
- Versionamento semântico automatizado
- Facilidade em code reviews
- Rastreabilidade de mudanças

**Referência oficial**: https://www.conventionalcommits.org/

---

## Estrutura Básica

```
<tipo>[escopo opcional]: <descrição>

[corpo opcional]

[rodapé(s) opcional(is)]
```

---

## 1. TIPOS DE COMMIT (OBRIGATÓRIOS)

### 1.1 Tipos Principais

| Tipo | Quando Usar | Impacto no Versionamento |
|------|-------------|--------------------------|
| `feat` | Nova funcionalidade | MINOR (0.x.0) |
| `fix` | Correção de bug | PATCH (0.0.x) |
| `docs` | Alterações em documentação | - |
| `style` | Formatação, lint, sem mudança lógica | - |
| `refactor` | Refatoração sem alterar comportamento | - |
| `perf` | Melhorias de performance | PATCH (0.0.x) |
| `test` | Adição/correção de testes | - |
| `build` | Mudanças em build/dependencies | - |
| `ci` | Mudanças em CI/CD | - |
| `chore` | Tarefas auxiliares (configs, scripts) | - |
| `revert` | Reversão de commit anterior | Depende do revertido |

### 1.2 Breaking Changes
**Sufixo**: `!` após o tipo/escopo OU `BREAKING CHANGE:` no rodapé  
**Impacto**: MAJOR (x.0.0)

---

## 2. ESCOPOS (OPCIONAIS MAS RECOMENDADOS)

Escopos devem mapear **módulos ou domínios** do projeto:

### Escopos por Módulo
- `domain` - Mudanças na camada de domínio
- `application` - Mudanças em use cases
- `infrastructure` - Mudanças em persistência/schedulers/collectors
- `interface` - Mudanças em controllers/websocket/security
- `starter` - Mudanças na aplicação principal

### Escopos por Conceito
- `system` - Funcionalidades relacionadas a MonitoredSystem
- `metric` - Funcionalidades relacionadas a Metrics
- `alert` - Funcionalidades relacionadas a Alerts
- `health` - Funcionalidades de health check
- `dashboard` - Funcionalidades de visualização
- `security` - Autenticação/autorização
- `websocket` - Comunicação em tempo real

### Escopos Técnicos
- `deps` - Dependências
- `config` - Configurações
- `db` - Database/migrations
- `docker` - Containerização
- `api` - Contratos de API

---

## 3. DESCRIÇÃO

### Regras
1. **Minúscula** no início
2. **Máximo 72 caracteres**
3. **Imperativo** (como se fosse uma ordem)
4. **Sem ponto final**
5. **Em inglês** (padrão da indústria)

### ✅ Bom
```
add metric collection scheduler
fix null pointer in health check
update PostgreSQL to version 16
```

### ❌ Ruim
```
Added metric collection scheduler.  (passado + ponto final)
Fix NullPointer                      (não explica o contexto)
Atualiza PostgreSQL                  (português)
```

---

## 4. CORPO (OPCIONAL)

Use o corpo para:
- Explicar **O QUÊ** e **POR QUÊ** (não o COMO)
- Contexto adicional
- Motivação da mudança
- Comparação com comportamento anterior

**Formato**:
- Quebra de linha após descrição
- Parágrafos com linha em branco entre eles
- Máximo 100 caracteres por linha

---

## 5. RODAPÉ (OPCIONAL)

### 5.1 Breaking Changes
```
BREAKING CHANGE: endpoint /api/systems agora retorna SystemResponse ao invés de SystemDTO
```

### 5.2 Issues Relacionadas
```
Closes #123
Refs #456
Fixes #789
```

### 5.3 Co-autoria
```
Co-authored-by: Nome <email@example.com>
```

---

## 6. EXEMPLOS REAIS

### 6.1 Feature Simples
```
feat(system): add endpoint to register monitored system

Implements POST /api/v1/systems endpoint to allow
registration of new systems for monitoring.

Closes #12
```

### 6.2 Bugfix
```
fix(metric): prevent duplicate metrics on concurrent collection

Add database unique constraint on (system_id, timestamp)
to avoid duplicate entries when multiple schedulers run.

Fixes #45
```

### 6.3 Breaking Change
```
feat(api)!: change alert severity enum values

BREAKING CHANGE: AlertSeverity enum now uses WARN/ERROR/CRITICAL
instead of WARNING/CRITICAL. Clients must update their integrations.

Migration guide: replace "WARNING" with "WARN" in all API calls.
```

### 6.4 Refactoring
```
refactor(domain): extract health evaluation to domain service

Move health evaluation logic from use case to
HealthEvaluationDomainService following DDD patterns.
No behavior change.
```

### 6.5 Performance
```
perf(metric): optimize query for historical metrics

Replace N+1 query with single JOIN fetch.
Reduces average response time from 850ms to 120ms.

Refs #67
```

### 6.6 Documentation
```
docs(readme): add architecture diagram and setup instructions
```

### 6.7 Tests
```
test(usecase): add integration tests for metric collection

Add TestContainers-based tests covering:
- successful metric collection
- timeout handling
- invalid system scenarios
```

### 6.8 Build/Dependencies
```
build(deps): upgrade Spring Boot to 3.2.1

Includes security patches and performance improvements.
```

### 6.9 CI/CD
```
ci: add automated Docker image build on main branch
```

### 6.10 Chore
```
chore(config): update application-local.yml with new datasource
```

### 6.11 Revert
```
revert: feat(alert): add email notification

This reverts commit abc123.
Email integration causing SMTP connection pool exhaustion.
```

---

## 7. COMMITS COMPOSTOS (MÚLTIPLAS MUDANÇAS)

**❌ Evite commits "grande demais"**:
```
feat: add system registration and metric collection and alerts
```

**✅ Prefira commits atômicos**:
```
feat(system): add system registration endpoint
feat(metric): add metric collection scheduler
feat(alert): add alert triggering logic
```

---

## 8. COMMITS DE MERGE

### Pull Request Merges (Squash Merge - Recomendado)
```
feat(system): implement monitored system management (#23)

- Add registration endpoint
- Add update endpoint
- Add deletion endpoint
- Add list with pagination

Co-authored-by: Developer Name <dev@example.com>
```

### Branch Merges (Merge Commit)
```
merge: feat/alert-system into main
```

---

## 9. COMMITS DE INFRAESTRUTURA INICIAL

### Setup Inicial
```
chore: initialize multi-module Maven project structure

Create modules:
- apm-domain
- apm-application
- apm-infrastructure
- apm-interface
- apm-starter
```

### Database
```
chore(db): add initial database schema migration

Create tables:
- monitored_systems
- metrics
- alerts
- shedlock
```

---

## 10. MENSAGENS EM PORTUGUÊS VS INGLÊS

**Decisão do projeto**: **INGLÊS**

### Justificativa
1. Padrão da indústria
2. Facilita colaboração internacional
3. Compatibilidade com ferramentas de automação
4. Melhor para portfólio profissional

### Exceções Permitidas
- Nomes próprios de projetos/empresas
- Termos de domínio em português que não têm tradução adequada

---

## 11. FERRAMENTAS E AUTOMAÇÃO

### Commitlint (Recomendado)
Validação automática de commits:
```bash
npm install --save-dev @commitlint/cli @commitlint/config-conventional
```

### Commitizen (Recomendado)
CLI interativo para criar commits:
```bash
npm install --save-dev commitizen cz-conventional-changelog
```

### Husky (Git Hooks)
Validação pré-commit:
```bash
npm install --save-dev husky
```

---

## 12. ANTI-PATTERNS A EVITAR

### ❌ Descrições Vagas
```
fix: fix bug
feat: update code
chore: changes
```

### ❌ Mensagens Genéricas
```
WIP
temp
test
asdf
minor changes
```

### ❌ Commits Gigantes
```
feat: implement entire monitoring system
```

### ❌ Mixing Types
```
feat/fix: add feature and fix bug
```

---

## 13. WORKFLOW DE COMMIT RECOMENDADO

1. **Stage mudanças relacionadas**:
   ```bash
   git add src/main/java/com/apm/platform/domain/entity/MonitoredSystem.java
   ```

2. **Commit atômico**:
   ```bash
   git commit -m "feat(domain): add MonitoredSystem entity"
   ```

3. **Push incremental**:
   ```bash
   git push origin feature/monitored-system
   ```

---

## 14. EXEMPLOS DE SEQUÊNCIA LÓGICA

### Feature Completa (Do Início ao Fim)
```
feat(domain): add MonitoredSystem entity
feat(domain): add MonitoredSystemRepository port
feat(application): add RegisterMonitoredSystemUseCase
feat(infrastructure): implement JpaMonitoredSystemRepositoryAdapter
feat(interface): add MonitoredSystemController
test(usecase): add tests for system registration
docs(api): document system registration endpoint
```

---

## CHECKLIST ANTES DE COMITAR

- [ ] Tipo está correto?
- [ ] Escopo é apropriado?
- [ ] Descrição está no imperativo?
- [ ] Descrição tem menos de 72 caracteres?
- [ ] Mudança está bem delimitada (atômica)?
- [ ] Breaking change está documentado?
- [ ] Issues estão referenciadas no rodapé?
- [ ] Mensagem está em inglês?

---

**Data de criação**: 2025-12-26  
**Versão**: 1.0.0  
**Status**: Ativo
