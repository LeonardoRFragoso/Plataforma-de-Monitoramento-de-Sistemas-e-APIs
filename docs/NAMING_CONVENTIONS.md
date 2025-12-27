# Padrões de Nomenclatura - APM Platform

## 1. PACOTES

### Estrutura Base
```
com.apm.platform.<camada>.<conceito>
```

### Convenções por Camada

#### Domain (apm-domain)
```
com.apm.platform.domain.entity          - Entidades de domínio
com.apm.platform.domain.valueobject     - Value Objects
com.apm.platform.domain.service         - Domain Services
com.apm.platform.domain.port.incoming   - Use Case Interfaces
com.apm.platform.domain.port.outgoing   - Repository/Gateway Interfaces
com.apm.platform.domain.event           - Domain Events
com.apm.platform.domain.exception       - Domain Exceptions
```

#### Application (apm-application)
```
com.apm.platform.application.usecase    - Implementações de Use Cases
com.apm.platform.application.dto.request   - DTOs de entrada
com.apm.platform.application.dto.response  - DTOs de saída
com.apm.platform.application.mapper     - Mapeadores DTO <-> Domain
```

#### Infrastructure (apm-infrastructure)
```
com.apm.platform.infrastructure.persistence.entity     - JPA Entities
com.apm.platform.infrastructure.persistence.repository - Spring Data Repositories
com.apm.platform.infrastructure.persistence.adapter    - Adaptadores de Repository
com.apm.platform.infrastructure.scheduler              - Scheduled Tasks
com.apm.platform.infrastructure.collector              - Metric Collectors
com.apm.platform.infrastructure.notifier               - Alert Notifiers
com.apm.platform.infrastructure.config                 - Configurações técnicas
```

#### Interface (apm-interface)
```
com.apm.platform.interfaces.rest.controller   - REST Controllers
com.apm.platform.interfaces.rest.exception    - Exception Handlers
com.apm.platform.interfaces.websocket         - WebSocket Handlers
com.apm.platform.interfaces.security          - Security Components
com.apm.platform.interfaces.config            - Web Configurations
```

---

## 2. CLASSES

### 2.1 Entidades de Domínio
**Padrão**: Substantivo no singular, PascalCase  
**Exemplos**:
- `MonitoredSystem`
- `Metric`
- `Alert`
- `Incident`
- `AlertRule`

### 2.2 Value Objects
**Padrão**: Substantivo descritivo, PascalCase  
**Exemplos**:
- `SystemStatus`
- `AlertSeverity`
- `HealthCheckResult`
- `MetricSnapshot`
- `UptimePercentage`
- `LatencyThreshold`

### 2.3 Domain Services
**Padrão**: `<Conceito>DomainService`  
**Exemplos**:
- `HealthEvaluationDomainService`
- `UptimeCalculationDomainService`
- `AlertRuleDomainService`

### 2.4 JPA Entities (Infrastructure)
**Padrão**: `<Nome>Entity`  
**Justificativa**: Diferenciação clara da entidade de domínio  
**Exemplos**:
- `MonitoredSystemEntity`
- `MetricEntity`
- `AlertEntity`

### 2.5 Controllers
**Padrão**: `<Recurso>Controller`  
**Exemplos**:
- `MonitoredSystemController`
- `MetricController`
- `AlertController`
- `DashboardController`

### 2.6 Exception Handlers
**Padrão**: `Global<Tipo>ExceptionHandler`  
**Exemplos**:
- `GlobalRestExceptionHandler`
- `GlobalWebSocketExceptionHandler`

---

## 3. INTERFACES

### 3.1 Ports (Domain)
**Incoming (Use Cases)**:
- **Padrão**: Verbo no infinitivo + Complemento
- **Exemplos**:
  - `RegisterMonitoredSystem`
  - `CollectSystemMetrics`
  - `EvaluateSystemHealth`
  - `TriggerAlert`
  - `QueryHistoricalMetrics`

**Outgoing (Repositories/Gateways)**:
- **Padrão**: `<Entidade>Repository` ou `<Conceito>Gateway`
- **Exemplos**:
  - `MonitoredSystemRepository`
  - `MetricRepository`
  - `AlertRepository`
  - `MetricCollectorGateway`
  - `AlertNotifierGateway`

### 3.2 Spring Data Repositories
**Padrão**: `<Entity>JpaRepository`  
**Exemplos**:
- `MonitoredSystemEntityJpaRepository`
- `MetricEntityJpaRepository`
- `AlertEntityJpaRepository`

### 3.3 Strategy Interfaces
**Padrão**: `<Conceito>Strategy` ou `<Conceito>Collector`  
**Exemplos**:
- `MetricCollector`
- `AlertNotifier`
- `HealthCheckStrategy`

---

## 4. USE CASES (Application Layer)

### Padrão de Nomenclatura
**Classe**: `<Verbo><Complemento>UseCase`  
**Método principal**: `execute()`

### Exemplos Completos

```java
// Use Case de Registro
public class RegisterMonitoredSystemUseCase 
    implements RegisterMonitoredSystem {
    
    @Override
    public RegisterMonitoredSystemResponse execute(
        RegisterMonitoredSystemRequest request
    ) { ... }
}

// Use Case de Consulta
public class QueryHistoricalMetricsUseCase 
    implements QueryHistoricalMetrics {
    
    @Override
    public QueryHistoricalMetricsResponse execute(
        QueryHistoricalMetricsRequest request
    ) { ... }
}

// Use Case de Operação
public class EvaluateSystemHealthUseCase 
    implements EvaluateSystemHealth {
    
    @Override
    public EvaluateSystemHealthResponse execute(
        EvaluateSystemHealthRequest request
    ) { ... }
}
```

---

## 5. DTOs (Data Transfer Objects)

### 5.1 Request DTOs
**Padrão**: `<Verbo><Complemento>Request`  
**Exemplos**:
- `RegisterMonitoredSystemRequest`
- `UpdateAlertRuleRequest`
- `QueryMetricsRequest`
- `CollectMetricRequest`

### 5.2 Response DTOs
**Padrão**: `<Verbo><Complemento>Response` ou `<Entidade>Response`  
**Exemplos**:
- `RegisterMonitoredSystemResponse`
- `MonitoredSystemResponse`
- `MetricResponse`
- `AlertResponse`
- `DashboardSummaryResponse`

### 5.3 DTOs de Lista
**Padrão**: `<Entidade>ListResponse` ou `Paginated<Entidade>Response`  
**Exemplos**:
- `MonitoredSystemListResponse`
- `PaginatedMetricResponse`

---

## 6. DOMAIN EVENTS

**Padrão**: `<Entidade><Ação>Event` (tempo passado)  
**Exemplos**:
- `AlertTriggeredEvent`
- `SystemHealthDegradedEvent`
- `MetricCollectedEvent`
- `IncidentResolvedEvent`
- `SystemRegisteredEvent`

---

## 7. EXCEPTIONS

### 7.1 Domain Exceptions
**Padrão**: `<Motivo>DomainException`  
**Exemplos**:
- `MonitoredSystemNotFoundException`
- `InvalidMetricDataException`
- `DuplicateSystemException`
- `AlertRuleViolationException`

### 7.2 Application Exceptions
**Padrão**: `<Motivo>ApplicationException`  
**Exemplos**:
- `MetricCollectionFailedException`
- `UseCaseExecutionException`

### 7.3 Infrastructure Exceptions
**Padrão**: `<Tecnologia><Motivo>Exception`  
**Exemplos**:
- `DatabaseConnectionException`
- `HttpClientTimeoutException`
- `ActuatorEndpointUnreachableException`

---

## 8. CONFIGURAÇÕES

**Padrão**: `<Tecnologia>Config` ou `<Feature>Configuration`  
**Exemplos**:
- `JpaConfig`
- `WebSocketConfig`
- `SecurityConfig`
- `SchedulerConfig`
- `MetricCollectionConfiguration`

---

## 9. MAPPERS (MapStruct)

**Padrão**: `<Origem>To<Destino>Mapper`  
**Exemplos**:
- `MonitoredSystemToResponseMapper`
- `RequestToMonitoredSystemMapper`
- `EntityToDomainMapper`
- `DomainToEntityMapper`

---

## 10. SCHEDULERS

**Padrão**: `<Funcionalidade>Scheduler`  
**Exemplos**:
- `MetricCollectionScheduler`
- `HealthCheckScheduler`
- `AlertEvaluationScheduler`
- `MetricAggregationScheduler`

---

## 11. ADAPTERS (Infrastructure)

**Padrão**: `<Interface>Adapter` ou `Jpa<Repository>Adapter`  
**Exemplos**:
- `JpaMonitoredSystemRepositoryAdapter`
- `JpaMetricRepositoryAdapter`
- `ActuatorMetricCollectorAdapter`
- `WebSocketAlertNotifierAdapter`

---

## 12. COLLECTORS

**Padrão**: `<Tipo><Tecnologia>Collector`  
**Exemplos**:
- `ActuatorMetricCollector`
- `HttpHealthCheckCollector`
- `CustomEndpointCollector`

---

## 13. NOTIFIERS

**Padrão**: `<Canal><Tipo>Notifier`  
**Exemplos**:
- `LogAlertNotifier`
- `WebSocketAlertNotifier`
- `EmailAlertNotifier`
- `SlackAlertNotifier`

---

## 14. VARIÁVEIS E MÉTODOS

### 14.1 Variáveis
- **Locais**: camelCase, descritivas
- **Constantes**: UPPER_SNAKE_CASE
- **Atributos**: camelCase

**Exemplos**:
```java
// Variáveis locais
MonitoredSystem monitoredSystem = ...;
List<Metric> collectedMetrics = ...;

// Constantes
private static final int DEFAULT_COLLECTION_INTERVAL = 60;
private static final String ACTUATOR_HEALTH_ENDPOINT = "/actuator/health";

// Atributos
private final MetricRepository metricRepository;
private final AlertNotifierGateway alertNotifier;
```

### 14.2 Métodos
- **Padrão**: verbo + complemento, camelCase
- **Booleanos**: prefixo `is`, `has`, `can`, `should`

**Exemplos**:
```java
// Métodos de ação
public void collectMetrics();
public Alert triggerAlert();
public void evaluateHealth();

// Métodos booleanos
public boolean isSystemHealthy();
public boolean hasActiveAlerts();
public boolean canCollectMetrics();
public boolean shouldTriggerAlert();

// Métodos de consulta
public List<Metric> findMetricsBySystemId();
public Optional<Alert> findActiveAlertBySystem();
```

---

## 15. TESTES

### 15.1 Classes de Teste
**Padrão**: `<ClasseTestar>Test` (unitário) ou `<ClasseTestar>IntegrationTest`  
**Exemplos**:
- `MonitoredSystemTest`
- `EvaluateSystemHealthUseCaseTest`
- `JpaMetricRepositoryAdapterIntegrationTest`
- `MonitoredSystemControllerIntegrationTest`

### 15.2 Métodos de Teste
**Padrão**: `should<Comportamento>When<Condição>` ou `given<Contexto>When<Acao>Then<Resultado>`  
**Exemplos**:
```java
@Test
void shouldReturnHealthyStatusWhenLatencyBelowThreshold() { ... }

@Test
void shouldTriggerCriticalAlertWhenSystemIsDown() { ... }

@Test
void givenInvalidSystemIdWhenCollectingMetricsThenThrowException() { ... }
```

---

## RESUMO DE PREFIXOS/SUFIXOS OBRIGATÓRIOS

| Tipo | Sufixo/Prefixo | Exemplo |
|------|----------------|---------|
| Domain Entity | - | `MonitoredSystem` |
| JPA Entity | `Entity` | `MonitoredSystemEntity` |
| Value Object | - | `SystemStatus` |
| Use Case (Interface) | Verbo infinitivo | `RegisterMonitoredSystem` |
| Use Case (Impl) | `UseCase` | `RegisterMonitoredSystemUseCase` |
| Request DTO | `Request` | `RegisterMonitoredSystemRequest` |
| Response DTO | `Response` | `MonitoredSystemResponse` |
| Domain Event | `Event` | `AlertTriggeredEvent` |
| Exception | `Exception` | `SystemNotFoundException` |
| Repository (Port) | `Repository` | `MetricRepository` |
| JPA Repository | `JpaRepository` | `MetricEntityJpaRepository` |
| Adapter | `Adapter` | `JpaMetricRepositoryAdapter` |
| Controller | `Controller` | `MetricController` |
| Config | `Config` / `Configuration` | `WebSocketConfig` |
| Mapper | `Mapper` | `EntityToDomainMapper` |
| Scheduler | `Scheduler` | `MetricCollectionScheduler` |
| Collector | `Collector` | `ActuatorMetricCollector` |
| Notifier | `Notifier` | `WebSocketAlertNotifier` |

---

**Data de criação**: 2025-12-26  
**Versão**: 1.0.0  
**Status**: Ativo
