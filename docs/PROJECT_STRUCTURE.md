# Estrutura do Projeto - APM Platform

## Visão Geral da Arquitetura Multi-Module Maven

```
apm-platform/
├── pom.xml                          # Parent POM (multi-module)
├── docker-compose.yml               # Ambiente local
├── Dockerfile                       # Build da imagem
├── README.md                        # Documentação principal
├── .gitignore                       # Arquivos ignorados
│
├── docs/                            # Documentação técnica
│   ├── NAMING_CONVENTIONS.md        # Padrões de nomenclatura
│   ├── COMMIT_CONVENTIONS.md        # Convenções de commit
│   ├── VERSIONING_STRATEGY.md       # Estratégia de versionamento
│   └── PROJECT_STRUCTURE.md         # Este documento
│
├── apm-domain/                      # 📦 Módulo de Domínio
│   ├── pom.xml
│   └── src/
│       ├── main/java/com/apm/platform/domain/
│       │   ├── entity/              # Entidades de domínio
│       │   │   ├── MonitoredSystem.java
│       │   │   ├── Metric.java
│       │   │   ├── Alert.java
│       │   │   ├── Incident.java
│       │   │   └── AlertRule.java
│       │   │
│       │   ├── valueobject/         # Value Objects imutáveis
│       │   │   ├── SystemStatus.java
│       │   │   ├── AlertSeverity.java
│       │   │   ├── HealthCheckResult.java
│       │   │   ├── MetricSnapshot.java
│       │   │   ├── UptimePercentage.java
│       │   │   └── LatencyThreshold.java
│       │   │
│       │   ├── service/             # Domain Services
│       │   │   ├── HealthEvaluationDomainService.java
│       │   │   ├── UptimeCalculationDomainService.java
│       │   │   └── AlertRuleDomainService.java
│       │   │
│       │   ├── port/                # Portas (Hexagonal Architecture)
│       │   │   ├── incoming/        # Use Case Interfaces
│       │   │   │   ├── RegisterMonitoredSystem.java
│       │   │   │   ├── CollectSystemMetrics.java
│       │   │   │   ├── EvaluateSystemHealth.java
│       │   │   │   ├── TriggerAlert.java
│       │   │   │   └── QueryHistoricalMetrics.java
│       │   │   │
│       │   │   └── outgoing/        # Repository/Gateway Interfaces
│       │   │       ├── MonitoredSystemRepository.java
│       │   │       ├── MetricRepository.java
│       │   │       ├── AlertRepository.java
│       │   │       ├── MetricCollectorGateway.java
│       │   │       └── AlertNotifierGateway.java
│       │   │
│       │   ├── event/               # Domain Events
│       │   │   ├── AlertTriggeredEvent.java
│       │   │   ├── SystemHealthDegradedEvent.java
│       │   │   ├── MetricCollectedEvent.java
│       │   │   └── IncidentResolvedEvent.java
│       │   │
│       │   └── exception/           # Domain Exceptions
│       │       ├── MonitoredSystemNotFoundException.java
│       │       ├── InvalidMetricDataException.java
│       │       ├── DuplicateSystemException.java
│       │       └── AlertRuleViolationException.java
│       │
│       └── test/java/com/apm/platform/domain/
│           ├── entity/              # Testes unitários de entidades
│           ├── valueobject/         # Testes de value objects
│           └── service/             # Testes de domain services
│
├── apm-application/                 # 📦 Módulo de Aplicação
│   ├── pom.xml
│   └── src/
│       ├── main/java/com/apm/platform/application/
│       │   ├── usecase/             # Implementação de Use Cases
│       │   │   ├── RegisterMonitoredSystemUseCase.java
│       │   │   ├── CollectSystemMetricsUseCase.java
│       │   │   ├── EvaluateSystemHealthUseCase.java
│       │   │   ├── TriggerAlertUseCase.java
│       │   │   ├── QueryHistoricalMetricsUseCase.java
│       │   │   └── GenerateUptimeReportUseCase.java
│       │   │
│       │   ├── dto/                 # Data Transfer Objects
│       │   │   ├── request/         # DTOs de entrada
│       │   │   │   ├── RegisterMonitoredSystemRequest.java
│       │   │   │   ├── UpdateAlertRuleRequest.java
│       │   │   │   ├── QueryMetricsRequest.java
│       │   │   │   └── CollectMetricRequest.java
│       │   │   │
│       │   │   └── response/        # DTOs de saída
│       │   │       ├── MonitoredSystemResponse.java
│       │   │       ├── MetricResponse.java
│       │   │       ├── AlertResponse.java
│       │   │       ├── DashboardSummaryResponse.java
│       │   │       └── UptimeReportResponse.java
│       │   │
│       │   └── mapper/              # Mapeadores (MapStruct)
│       │       ├── MonitoredSystemMapper.java
│       │       ├── MetricMapper.java
│       │       └── AlertMapper.java
│       │
│       └── test/java/com/apm/platform/application/
│           └── usecase/             # Testes de use cases (unitários)
│
├── apm-infrastructure/              # 📦 Módulo de Infraestrutura
│   ├── pom.xml
│   └── src/
│       ├── main/java/com/apm/platform/infrastructure/
│       │   ├── persistence/         # Camada de persistência
│       │   │   ├── entity/          # JPA Entities
│       │   │   │   ├── MonitoredSystemEntity.java
│       │   │   │   ├── MetricEntity.java
│       │   │   │   ├── AlertEntity.java
│       │   │   │   └── AlertRuleEntity.java
│       │   │   │
│       │   │   ├── repository/      # Spring Data JPA Repositories
│       │   │   │   ├── MonitoredSystemEntityJpaRepository.java
│       │   │   │   ├── MetricEntityJpaRepository.java
│       │   │   │   └── AlertEntityJpaRepository.java
│       │   │   │
│       │   │   └── adapter/         # Adapters (Ports → JPA)
│       │   │       ├── JpaMonitoredSystemRepositoryAdapter.java
│       │   │       ├── JpaMetricRepositoryAdapter.java
│       │   │       └── JpaAlertRepositoryAdapter.java
│       │   │
│       │   ├── scheduler/           # Scheduled Tasks
│       │   │   ├── MetricCollectionScheduler.java
│       │   │   ├── HealthCheckScheduler.java
│       │   │   └── AlertEvaluationScheduler.java
│       │   │
│       │   ├── collector/           # Metric Collectors (Strategy Pattern)
│       │   │   ├── MetricCollector.java (interface)
│       │   │   ├── ActuatorMetricCollector.java
│       │   │   ├── HttpHealthCheckCollector.java
│       │   │   └── CustomEndpointCollector.java
│       │   │
│       │   ├── notifier/            # Alert Notifiers (Strategy Pattern)
│       │   │   ├── AlertNotifier.java (interface)
│       │   │   ├── LogAlertNotifier.java
│       │   │   ├── WebSocketAlertNotifier.java
│       │   │   └── EmailAlertNotifier.java
│       │   │
│       │   └── config/              # Configurações técnicas
│       │       ├── JpaConfig.java
│       │       ├── SchedulerConfig.java
│       │       └── ShedLockConfig.java
│       │
│       ├── main/resources/
│       │   └── db/migration/        # Flyway migrations
│       │       ├── V1__create_monitored_systems_table.sql
│       │       ├── V2__create_metrics_table.sql
│       │       ├── V3__create_alerts_table.sql
│       │       └── V4__create_shedlock_table.sql
│       │
│       └── test/java/com/apm/platform/infrastructure/
│           └── persistence/         # Testes de integração (Testcontainers)
│
├── apm-interface/                   # 📦 Módulo de Interface
│   ├── pom.xml
│   └── src/
│       ├── main/java/com/apm/platform/interfaces/
│       │   ├── rest/                # REST API
│       │   │   ├── controller/      # Controllers
│       │   │   │   ├── MonitoredSystemController.java
│       │   │   │   ├── MetricController.java
│       │   │   │   ├── AlertController.java
│       │   │   │   └── DashboardController.java
│       │   │   │
│       │   │   └── exception/       # Exception Handlers
│       │   │       ├── GlobalRestExceptionHandler.java
│       │   │       └── ApiError.java
│       │   │
│       │   ├── websocket/           # WebSocket
│       │   │   ├── WebSocketHandler.java
│       │   │   ├── MetricBroadcaster.java
│       │   │   └── AlertBroadcaster.java
│       │   │
│       │   ├── security/            # Security (JWT - opcional)
│       │   │   ├── JwtAuthenticationFilter.java
│       │   │   ├── JwtTokenProvider.java
│       │   │   └── SecurityUserDetails.java
│       │   │
│       │   └── config/              # Configurações Web
│       │       ├── WebSocketConfig.java
│       │       ├── SecurityConfig.java
│       │       └── OpenApiConfig.java
│       │
│       └── test/java/com/apm/platform/interfaces/
│           └── rest/                # Testes de API (MockMvc)
│
└── apm-starter/                     # 📦 Módulo Starter
    ├── pom.xml
    └── src/
        ├── main/java/com/apm/platform/
        │   └── ApmPlatformApplication.java  # Main class
        │
        └── main/resources/
            ├── application.yml              # Config base
            ├── application-local.yml        # Config local
            ├── application-test.yml         # Config test
            └── application-prod.yml         # Config produção
```

---

## Dependências Entre Módulos

```
         ┌─────────────────┐
         │  apm-starter    │ (execução)
         └────────┬────────┘
                  │
         ┌────────▼────────────────┐
         │                         │
    ┌────▼─────┐          ┌───────▼────────┐
    │ interface│          │ infrastructure │
    └────┬─────┘          └───────┬────────┘
         │                        │
         │    ┌──────────────┐    │
         └────►  application ◄────┘
              └──────┬───────┘
                     │
              ┌──────▼────┐
              │  domain   │ (núcleo)
              └───────────┘
```

**Regras**:
- `domain` não depende de ninguém
- `application` depende apenas de `domain`
- `infrastructure` depende de `domain`
- `interface` depende de `application`
- `starter` agrega todos os módulos

---

## Responsabilidades por Camada

### 🔵 Domain (Núcleo de Negócio)
**Responsabilidades**:
- Definir entidades com regras de negócio
- Definir value objects imutáveis
- Definir contratos (ports) para infraestrutura
- Publicar domain events
- Domain services para lógica complexa

**NÃO deve**:
- Conhecer framework
- Acessar banco de dados diretamente
- Fazer chamadas HTTP
- Depender de outras camadas

---

### 🟢 Application (Orquestração)
**Responsabilidades**:
- Implementar use cases
- Orquestrar domínio e infraestrutura
- Mapear entre domain e DTOs
- Validar entrada de dados
- Coordenar transações

**NÃO deve**:
- Conter regras de negócio complexas
- Acessar tecnologias diretamente
- Conhecer detalhes de persistência

---

### 🟡 Infrastructure (Implementação Técnica)
**Responsabilidades**:
- Implementar ports do domain
- Persistência (JPA, repositories)
- Integrações externas (HTTP clients)
- Schedulers e background jobs
- Coleta de métricas
- Envio de notificações

**NÃO deve**:
- Conter regras de negócio
- Chamar use cases diretamente (só via domain ports)

---

### 🔴 Interface (Entrada/Saída)
**Responsabilidades**:
- Expor REST API
- WebSocket para real-time
- Validação de entrada (Bean Validation)
- Exception handling global
- Segurança (autenticação/autorização)
- Documentação de API (OpenAPI)

**NÃO deve**:
- Conter lógica de negócio
- Acessar repositories diretamente
- Conhecer detalhes de domínio (apenas DTOs)

---

### ⚫ Starter (Runtime)
**Responsabilidades**:
- Bootstrapping da aplicação
- Configurações globais
- Profiles de ambiente
- Dependency injection setup

---

## Fluxo de Execução Típico

### 1. Requisição REST
```
1. Client → Controller (interface)
2. Controller → Use Case (application)
3. Use Case → Domain Service (domain)
4. Use Case → Repository Port (domain interface)
5. Repository Adapter → JPA Repository (infrastructure)
6. JPA Repository → Database
7. Database → JPA Repository
8. JPA Repository → Repository Adapter
9. Repository Adapter → Use Case
10. Use Case → Controller
11. Controller → Client (DTO)
```

### 2. Coleta Periódica
```
1. Scheduler (infrastructure) → trigger
2. Scheduler → Use Case (application)
3. Use Case → Collector Gateway Port (domain)
4. Collector Adapter → HTTP Client (infrastructure)
5. HTTP Client → External System
6. External System → HTTP Client
7. HTTP Client → Collector Adapter
8. Collector Adapter → Use Case
9. Use Case → Repository Port (domain)
10. Repository Adapter → Save to DB (infrastructure)
```

### 3. Alerta Disparado
```
1. Domain Service → avaliar métrica
2. Domain Service → trigger Domain Event
3. Event Publisher → listeners
4. Alert Use Case → processa evento
5. Alert Use Case → Notifier Gateway Port (domain)
6. Notifier Adapter → WebSocket broadcast (infrastructure)
7. WebSocket → Connected clients
```

---

## Convenções de Código

### Pacotes
- **Sempre** começam com `com.apm.platform.<module>`
- **Snake_case** para subpacotes com múltiplas palavras (evitar, preferir palavras compostas)
- **Singular** para pacotes conceituais (`entity`, não `entities`)

### Classes
- **PascalCase**
- **Sufixos claros**: `Entity`, `UseCase`, `Adapter`, `Controller`
- **Interfaces de port**: Verbo no infinitivo (`RegisterMonitoredSystem`)

### Métodos
- **camelCase**
- **Verbos**: `execute()`, `collect()`, `evaluate()`
- **Booleanos**: `isHealthy()`, `hasAlerts()`, `canCollect()`

### Constantes
- **UPPER_SNAKE_CASE**
- **Exemplos**: `DEFAULT_INTERVAL`, `MAX_RETRIES`, `API_VERSION`

---

## Padrões de Testes

### Unitários
```java
@Test
void shouldReturnHealthyStatusWhenLatencyBelowThreshold() {
    // given
    var system = MonitoredSystem.create(...);
    var metric = new Metric(...);
    
    // when
    var result = healthService.evaluate(system, metric);
    
    // then
    assertThat(result.getStatus()).isEqualTo(SystemStatus.HEALTHY);
}
```

### Integração
```java
@SpringBootTest
@Testcontainers
class JpaMetricRepositoryAdapterIntegrationTest {
    
    @Container
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:16");
    
    @Test
    void shouldPersistAndRetrieveMetric() {
        // test com banco real
    }
}
```

---

## Build e Deploy

### Build Local
```bash
mvn clean install
```

### Build com Testes de Integração
```bash
mvn clean verify -P integration-tests
```

### Docker Build
```bash
docker build -t apm-platform:latest .
```

### Docker Compose
```bash
docker-compose up -d
```

---

**Documento criado em**: 2025-12-26  
**Versão**: 1.0.0  
**Mantido por**: Equipe APM Platform
