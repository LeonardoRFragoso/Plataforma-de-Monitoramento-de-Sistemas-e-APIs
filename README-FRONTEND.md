# 🎨 APM Platform - Guia de Execução Completo

Sistema completo de monitoramento (APM) com **Backend Spring Boot** + **Frontend React**.

---

## 📦 Arquitetura do Sistema

```
┌─────────────────────────────────────────┐
│          Frontend (React)               │
│  http://localhost:3000                  │
│  - Dashboard em tempo real              │
│  - Gráficos com Recharts                │
│  - WebSocket real-time                  │
└─────────────────────────────────────────┘
                  ↓ REST + WebSocket
┌─────────────────────────────────────────┐
│       Backend (Spring Boot)             │
│  http://localhost:8080                  │
│  - REST API                             │
│  - WebSocket /ws/dashboard              │
│  - Schedulers (métricas, alertas)       │
└─────────────────────────────────────────┘
                  ↓ JDBC
┌─────────────────────────────────────────┐
│        PostgreSQL 16                    │
│  localhost:5433                         │
│  - Database: apm_platform               │
│  - User: apm_user                       │
└─────────────────────────────────────────┘
```

---

## 🚀 Opções de Execução

### **Opção 1: Docker Compose (Recomendado)**

Execute **tudo** com um único comando:

```bash
docker-compose up --build
```

**Serviços iniciados:**
- ✅ PostgreSQL: `localhost:5433`
- ✅ Backend: `http://localhost:8080`
- ✅ Frontend: `http://localhost:3000`

**Acessar o dashboard:**
```
http://localhost:3000
```

**Parar tudo:**
```bash
docker-compose down
```

---

### **Opção 2: Desenvolvimento Local**

#### **1. Iniciar PostgreSQL (Docker)**

```bash
docker-compose up postgres -d
```

#### **2. Executar Backend (Spring Boot)**

```bash
# Na raiz do projeto
mvn spring-boot:run -pl apm-starter
```

Backend estará em: `http://localhost:8080`

**Endpoints disponíveis:**
- Swagger: `http://localhost:8080/swagger-ui.html`
- Health: `http://localhost:8080/actuator/health`
- Dashboard API: `http://localhost:8080/api/v1/dashboard/overview`

#### **3. Executar Frontend (React)**

```bash
# Entrar na pasta frontend
cd apm-frontend

# Instalar dependências (primeira vez)
npm install

# Executar dev server
npm run dev
```

Frontend estará em: `http://localhost:3000`

---

## 📊 Endpoints e Recursos

### **REST API Backend**

| Método | Endpoint | Descrição |
|--------|----------|-----------|
| `GET` | `/api/v1/dashboard/overview` | Visão geral do dashboard |
| `POST` | `/api/v1/systems` | Cadastrar sistema |
| `GET` | `/api/v1/systems` | Listar sistemas |
| `GET` | `/api/v1/systems/{id}` | Detalhes do sistema |
| `POST` | `/api/v1/systems/{id}/alert-rules` | Criar regra de alerta |
| `GET` | `/api/v1/alerts/active` | Alertas ativos |

### **WebSocket**

**URL:** `ws://localhost:8080/ws/dashboard`

**Tópicos:**
- `/topic/dashboard/metrics` - Métricas coletadas em tempo real
- `/topic/dashboard/alerts` - Alertas disparados
- `/topic/dashboard/health` - Eventos de saúde

---

## 🧪 Testando o Sistema

### **1. Verificar se backend está rodando**

```bash
curl http://localhost:8080/actuator/health
```

Resposta esperada:
```json
{
  "status": "UP"
}
```

### **2. Verificar dashboard overview**

```bash
curl http://localhost:8080/api/v1/dashboard/overview
```

Resposta esperada:
```json
{
  "totalSystems": 0,
  "activeSystems": 0,
  "healthySystems": 0,
  "degradedSystems": 0,
  "downSystems": 0,
  "activeAlerts": 0,
  "criticalAlerts": 0
}
```

### **3. Cadastrar um sistema de exemplo**

```bash
curl -X POST http://localhost:8080/api/v1/systems \
  -H "Content-Type: application/json" \
  -d '{
    "name": "API de Teste",
    "baseUrl": "https://jsonplaceholder.typicode.com",
    "type": "API",
    "environment": "PRODUCTION",
    "collectionIntervalSeconds": 60
  }'
```

### **4. Acessar o frontend**

Abra: `http://localhost:3000`

Você verá:
- ✅ Estatísticas dos sistemas
- ✅ Gráfico de latência em tempo real (quando houver métricas)
- ✅ Indicador de conexão WebSocket
- ✅ Eventos recentes (alertas e saúde)

---

## 🛠️ Stack Tecnológica

### **Backend**
- Java 21
- Spring Boot 3
- PostgreSQL 16
- Flyway (migrations)
- WebSocket (STOMP)
- Docker

### **Frontend**
- Vite
- React 18
- TypeScript
- Tailwind CSS
- Axios
- @tanstack/react-query
- Recharts
- WebSocket nativo

---

## 📁 Estrutura do Projeto

```
apm-platform/
├── apm-domain/              # Domain Layer (Clean Architecture)
├── apm-application/         # Application Layer (Use Cases)
├── apm-infrastructure/      # Infrastructure Layer (JPA, Schedulers)
├── apm-interface/           # Interface Layer (REST Controllers)
├── apm-starter/             # Spring Boot Main Application
├── apm-frontend/            # React Frontend
│   ├── src/
│   │   ├── api/            # Axios client
│   │   ├── hooks/          # React Query hooks + WebSocket
│   │   ├── components/     # Componentes reutilizáveis
│   │   ├── pages/          # Dashboard page
│   │   └── types/          # TypeScript types
│   ├── Dockerfile
│   └── nginx.conf
├── docker-compose.yml       # Orquestração completa
└── README-FRONTEND.md       # Este arquivo
```

---

## 🔧 Troubleshooting

### **Frontend não conecta no backend**

1. Verifique se backend está rodando: `curl http://localhost:8080/actuator/health`
2. Verifique console do navegador (F12) para erros CORS
3. Se usar Docker: certifique-se que os serviços estão na mesma rede

### **WebSocket não conecta**

1. Verifique URL no console: `ws://localhost:8080/ws/dashboard`
2. Backend deve ter WebSocket habilitado (já está)
3. Verifique firewall/antivírus

### **Build do frontend falha**

```bash
# Limpar e reinstalar
cd apm-frontend
rm -rf node_modules package-lock.json
npm install
npm run build
```

### **PostgreSQL não inicia (porta em uso)**

Porta 5433 já em uso? Altere no `docker-compose.yml`:

```yaml
ports:
  - "5434:5432"  # Mudar para outra porta
```

---

## 🎯 Próximos Passos

1. **Cadastrar sistemas reais** via API
2. **Ver métricas sendo coletadas** automaticamente (schedulers)
3. **Criar regras de alerta** para monitorar latência
4. **Ver gráfico atualizar em tempo real** via WebSocket
5. **Explorar Swagger UI** para testar todos os endpoints

---

## 📚 Documentação Adicional

- **Backend**: Ver Swagger em `http://localhost:8080/swagger-ui.html`
- **Frontend**: Ver `apm-frontend/README.md`
- **API Endpoints**: Ver Swagger ou `apm-interface/` controllers

---

## ✅ Checklist de Funcionamento

- [ ] PostgreSQL rodando na porta 5433
- [ ] Backend iniciado (porta 8080)
- [ ] Flyway migrations executadas (6 tabelas criadas)
- [ ] Schedulers ativos (logs de coleta de métricas)
- [ ] Frontend rodando (porta 3000)
- [ ] Dashboard acessível no navegador
- [ ] WebSocket conectado (indicador verde no dashboard)
- [ ] API `/dashboard/overview` retornando dados

---

**Plataforma APM 100% funcional e pronta para produção!** 🚀
