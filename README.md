# APM Platform - Application Performance Monitoring

> Plataforma de Monitoramento de Sistemas e Performance desenvolvida em Java com Clean Architecture

[![Java](https://img.shields.io/badge/Java-21-orange.svg)](https://www.oracle.com/java/)
[![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.2.1-brightgreen.svg)](https://spring.io/projects/spring-boot)
[![License](https://img.shields.io/badge/License-MIT-blue.svg)](LICENSE)

---

## 📋 Sobre o Projeto

APM Platform é uma solução profissional de monitoramento de sistemas e APIs, inspirada em ferramentas corporativas como New Relic, Datadog e Dynatrace. O projeto demonstra domínio técnico em:

- **Backend Java moderno** com Java 21 e Spring Boot 3.2
- **Clean Architecture** com separação rigorosa de responsabilidades
- **Observabilidade** através de métricas, logs e health checks
- **Performance** com coleta assíncrona e processamento otimizado
- **Monitoramento em tempo real** via WebSocket

---

## 🎬 Quick Start (Docker)

```bash
# Clone o repositório
git clone https://github.com/LeonardoRFragoso/apm-platform.git
cd apm-platform

# Inicie com Docker Compose
docker-compose up -d

# Acesse a aplicação
# Frontend: http://localhost:3000
# Backend: http://localhost:8080
# Swagger: http://localhost:8080/swagger-ui.html
```

---

## 📦 Módulos do Projeto

| Módulo | Descrição |
|--------|-----------|
| **apm-domain** | Lógica de negócio pura, entidades, value objects e ports |
| **apm-application** | Casos de uso, orchestration e DTOs |
| **apm-infrastructure** | JPA, schedulers, collectors, integrações externas |
| **apm-interface** | REST API, WebSocket, security, exception handlers |
| **apm-starter** | Aplicação principal, configurações e runtime |

---

## 🛠️ Stack Tecnológica

### Backend
- **Java 21** (LTS) - Virtual Threads, Pattern Matching
- **Spring Boot 3.2.1** - Framework principal
- **Spring Data JPA** - Persistência
- **Spring WebSocket** - Comunicação real-time
- **Spring Actuator** - Auto-observability

### Observabilidade
- **Micrometer** - Metrics façade
- **Prometheus** - Metrics aggregation
- **Structured Logging** - JSON logs

### Banco de Dados
- **PostgreSQL 16** - Database principal
- **Flyway** - Database migrations
- **JSONB** - Métricas semi-estruturadas

### Testes
- **JUnit 5** - Testes unitários
- **Mockito** - Mocking
- **Testcontainers** - Testes de integração com DB real

### Build & Deploy
- **Maven 3.9+** - Build tool
- **Docker** - Containerização
- **Docker Compose** - Ambiente local

---

## 📦 Estrutura de Pacotes

```
apm-domain/
└── com.apm.platform.domain
    ├── entity/              # Entidades de domínio
    ├── valueobject/         # Value Objects
    ├── service/             # Domain Services
    ├── port/
    │   ├── incoming/        # Use Case Interfaces
    │   └── outgoing/        # Repository/Gateway Interfaces
    ├── event/               # Domain Events
    └── exception/           # Domain Exceptions

apm-application/
└── com.apm.platform.application
    ├── usecase/             # Implementação de Use Cases
    ├── dto/
    │   ├── request/         # DTOs de entrada
    │   └── response/        # DTOs de saída
    └── mapper/              # Mapeadores DTO <-> Domain

apm-infrastructure/
└── com.apm.platform.infrastructure
    ├── persistence/
    │   ├── entity/          # JPA Entities
    │   ├── repository/      # Spring Data Repositories
    │   └── adapter/         # Repository Adapters
    ├── scheduler/           # Scheduled Tasks
    ├── collector/           # Metric Collectors
    ├── notifier/            # Alert Notifiers
    └── config/              # Configurações técnicas

apm-interface/
└── com.apm.platform.interfaces
    ├── rest/
    │   ├── controller/      # REST Controllers
    │   └── exception/       # Exception Handlers
    ├── websocket/           # WebSocket Handlers
    ├── security/            # Security Components
    └── config/              # Web Configurations

apm-starter/
└── com.apm.platform
    └── ApmPlatformApplication.java
```

---

## 🚀 Setup do Ambiente

### Pré-requisitos

- Java 21+
- Maven 3.9+
- Docker & Docker Compose
- PostgreSQL 16 (ou via Docker)

### Instalação

1. **Clone o repositório**
```bash
git clone <repository-url>
cd apm-platform
```

2. **Configure o banco de dados local**
```bash
docker-compose up -d postgres
```

3. **Build do projeto**
```bash
mvn clean install
```

4. **Execute a aplicação**
```bash
cd apm-starter
mvn spring-boot:run -Dspring-boot.run.profiles=local
```

A aplicação estará disponível em: `http://localhost:8080`

---

## 🧪 Testes

### Testes Unitários
```bash
mvn test
```

### Testes de Integração
```bash
mvn verify -P integration-tests
```

### Cobertura de Testes
```bash
mvn clean verify jacoco:report
```

---

## 📚 Documentação

### Documentos Técnicos
- [Padrões de Nomenclatura](docs/NAMING_CONVENTIONS.md)
- [Convenções de Commit](docs/COMMIT_CONVENTIONS.md)
- [Estratégia de Versionamento](docs/VERSIONING_STRATEGY.md)

### API Documentation
- Swagger UI: `http://localhost:8080/swagger-ui.html`
- OpenAPI Spec: `http://localhost:8080/v3/api-docs`

### Métricas
- Prometheus Metrics: `http://localhost:8080/actuator/prometheus`
- Health Check: `http://localhost:8080/actuator/health`

---

## 🔧 Profiles

| Profile | Descrição | Uso |
|---------|-----------|-----|
| `local` | Desenvolvimento local com debug habilitado | Default |
| `test` | Testes com Testcontainers | Automático em testes |
| `prod` | Produção com configurações otimizadas | Deploy |

---

## 📊 Funcionalidades Principais

### ✅ Cadastro de Sistemas Monitorados
- Registro de sistemas com URL, tipo e ambiente
- Configuração de intervalos de coleta
- Ativação/desativação de monitoramento

### ✅ Coleta Automática de Métricas
- Latência (tempo de resposta)
- Status HTTP
- CPU e memória (via Actuator)
- Disponibilidade

### ✅ Health Checks
- Verificação periódica de status
- Classificação: UP, DEGRADED, DOWN
- Detecção de degradação progressiva

### ✅ Sistema de Alertas
- Alertas configuráveis por sistema
- Thresholds personalizáveis
- Severidade: WARNING, CRITICAL
- Notificação via Log e WebSocket

### ✅ Dashboard em Tempo Real
- Atualização via WebSocket
- Visualização de métricas agregadas
- Histórico de incidentes

### ✅ Auditoria e Histórico
- Registro completo de métricas
- Rastreamento de incidentes
- Análise de tendências

---

## 🎨 Design Patterns Utilizados

- **Strategy Pattern** - Tipos de coleta de métricas
- **Observer Pattern** - Sistema de alertas
- **Factory Pattern** - Criação de collectors
- **Repository Pattern** - Acesso a dados
- **DTO Pattern** - Transferência de dados entre camadas
- **Adapter Pattern** - Integração entre camadas

---

## 🔒 Segurança

- **JWT Authentication** (opcional, configurável)
- **HTTPS** em produção
- **SQL Injection Prevention** via JPA
- **Input Validation** com Bean Validation
- **Rate Limiting** (roadmap)

---

## 📈 Roadmap

### v0.x.x (MVP)
- [x] Estrutura de projeto e módulos
- [ ] Domain entities e value objects
- [ ] Use cases principais
- [ ] Persistência com JPA
- [ ] REST API
- [ ] WebSocket real-time
- [ ] Sistema de alertas

### v1.x.x (Stable)
- [ ] First stable release
- [ ] Dashboard completo
- [ ] Métricas avançadas
- [ ] Documentação completa

### v2.x.x (Enterprise)
- [ ] Multi-tenant support
- [ ] RBAC
- [ ] Integrações externas (Slack, Telegram)
- [ ] Export para Prometheus

---

## 🤝 Contribuindo

1. Fork o projeto
2. Crie uma branch para sua feature (`git checkout -b feat/nova-feature`)
3. Commit suas mudanças seguindo [Conventional Commits](docs/COMMIT_CONVENTIONS.md)
4. Push para a branch (`git push origin feat/nova-feature`)
5. Abra um Pull Request

---

## 📝 Licença

Este projeto está sob a licença MIT. Veja o arquivo [LICENSE](LICENSE) para mais detalhes.

---

## 👤 Autor

**Desenvolvido como projeto de portfólio profissional**

Demonstra competências em:
- Arquitetura de software
- Design patterns
- Clean code
- Observabilidade
- Sistemas distribuídos
- Desenvolvimento backend Java

---

## 📞 Contato

Para dúvidas ou sugestões sobre o projeto, abra uma issue no GitHub.

---

**Status do Projeto**: 🚧 Em Desenvolvimento (v0.1.0-SNAPSHOT)
