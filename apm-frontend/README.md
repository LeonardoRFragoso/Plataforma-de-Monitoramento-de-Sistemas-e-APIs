# APM Platform - Frontend (BETA v0.1)

A modern, portfolio-ready **Application Performance Monitoring** frontend built with React, TypeScript, and Tailwind CSS. This BETA version demonstrates professional-grade frontend development with real backend integration.

## 🎯 Project Overview

This is a **fully functional BETA** of an APM Platform frontend that:

✅ **Monitors systems in real-time** via WebSocket  
✅ **Manages monitored systems** (view, activate/deactivate)  
✅ **Displays active alerts** with severity filtering  
✅ **Configures alert rules** per system  
✅ **Shows real-time metrics** and dashboards  
✅ **Integrates with a production-ready backend** (Java 21 + Spring Boot + DDD)

---

## 🛠️ Tech Stack

### Core
- **React 19** - UI library
- **TypeScript** - Type safety
- **Tailwind CSS** - Estilização utility-first
- **Axios** - HTTP client
- **@tanstack/react-query** - Server state management
- **Recharts** - Gráficos responsivos
- **Zustand** - Client state (se necessário)
- **WebSocket nativo** - Real-time updates

## 📁 Project Structure

```
src/
├── api/              # API client configurations
│   ├── client.ts           # Axios instance
│   ├── systemsApi.ts       # Systems endpoints
│   ├── alertsApi.ts        # Alerts endpoints
│   └── dashboardApi.ts     # Dashboard endpoints
│
├── components/       # Reusable UI components
│   ├── StatusBadge.tsx     # System status (UP/DEGRADED/DOWN)
│   ├── SeverityBadge.tsx   # Alert severity (INFO/WARNING/CRITICAL)
│   ├── LoadingSpinner.tsx  # Loading state
│   ├── EmptyState.tsx      # Empty data state
│   ├── ErrorState.tsx      # Error state
│   ├── Modal.tsx           # Reusable modal
│   ├── ConfirmDialog.tsx   # Confirmation dialog
│   ├── StatCard.tsx        # Dashboard stat cards
│   ├── LatencyChart.tsx    # Real-time latency chart
│   ├── AlertBadge.tsx      # Alert display component
│   └── CreateAlertRuleModal.tsx  # Alert rule creation
│
├── hooks/            # React Query hooks & custom hooks
│   ├── useSystems.ts         # Systems CRUD operations
│   ├── useAlerts.ts          # Alerts & rules operations
│   ├── useDashboardOverview.ts  # Dashboard data
│   └── useDashboardWebSocket.ts # WebSocket connection
│
├── layouts/          # Layout components
│   └── MainLayout.tsx      # Sidebar + main content
│
├── pages/            # Page components
│   ├── Dashboard.tsx          # Dashboard overview
│   ├── SystemsPage.tsx        # Systems list
│   ├── SystemDetailPage.tsx   # System detail view
│   └── AlertsPage.tsx         # Alerts list
│
├── types/            # TypeScript types
│   ├── system.ts     # System-related types
│   ├── alert.ts      # Alert & rule types
│   ├── metric.ts     # Metric types
│   └── dashboard.ts  # Dashboard & WebSocket event types
│
├── App.tsx           # Routes configuration
└── main.tsx          # React Query provider setup
```

## 🚀 Quick Start

### Prerequisites
- **Node.js 18+**
- **Backend running** on `http://localhost:8080`

### Installation

```bash
npm install
```

### Development

```bash
npm run dev
```

The frontend will start at **http://localhost:3000**

### Build for Production

```bash
npm run build
```

### Preview Production Build

```bash
npm run preview
```

## 🐳 Docker

### Build da imagem

```bash
docker build -t apm-frontend .
```

### Executar container

```bash
docker run -p 3000:80 apm-frontend
```

## 🌐 Available Pages

| Route | Description |
|-------|-------------|
| `/` | **Dashboard** - Real-time overview with metrics and alerts |
| `/systems` | **Systems List** - Manage all monitored systems |
| `/systems/:id` | **System Detail** - View system info, alerts, and rules |
| `/alerts` | **Active Alerts** - Filter and monitor alerts |

---

## ✨ Key Features

### 1️⃣ Dashboard
- **Real-time metrics** via WebSocket
- **System health overview** (UP/DEGRADED/DOWN)
- **Active alerts summary**
- **Latest events display**

### 2️⃣ Systems Management
- **List all registered systems**
- **View system details** (type, environment, status)
- **Activate/Deactivate** systems
- **Navigate to system details**

### 3️⃣ System Detail View
- **System information panel**
- **Active alerts for the system**
- **Alert rules configuration**
- **Create new alert rules** via modal

### 4️⃣ Alerts Monitoring
- **View all active alerts**
- **Filter by severity** (INFO, WARNING, CRITICAL)
- **Alert statistics**
- **Resolved/Active status**

### 5️⃣ Alert Rules
- **Create rules** for latency, error rate, availability
- **Set thresholds** and consecutive violations
- **Define severity levels**
- **Enable/disable rules**

---

## 🔌 Backend Integration

### REST API Endpoints Used

```
GET    /api/v1/dashboard/overview          # Dashboard stats
GET    /api/v1/systems                     # List systems
GET    /api/v1/systems/:id                 # Get system by ID
POST   /api/v1/systems/:id/activate        # Activate system
POST   /api/v1/systems/:id/deactivate      # Deactivate system
GET    /api/v1/systems/:id/alerts          # Get system alerts
GET    /api/v1/systems/:id/alert-rules     # Get alert rules
POST   /api/v1/systems/:id/alert-rules     # Create alert rule
GET    /api/v1/alerts/active               # Get all active alerts
```

### WebSocket Endpoint

```
WS     /ws/dashboard                       # Real-time events
```

**Event Types:**
- `METRIC_COLLECTED` - New metric data
- `ALERT_TRIGGERED` - New alert
- `HEALTH_DEGRADED` - System health change

## 🎨 Design Decisions

### Architecture
- **Separation of concerns** - API, hooks, components, pages
- **React Query for server state** - Automatic caching, refetching
- **WebSocket for real-time** - Not REST polling
- **Type-safe** - Full TypeScript coverage

### UX Principles
- **Loading states** - LoadingSpinner for all async operations
- **Empty states** - Clear messaging when no data
- **Error handling** - ErrorState with retry option
- **Confirmation dialogs** - For destructive actions
- **Responsive design** - Mobile-friendly layout

### Component Philosophy
- **Reusable** - StatusBadge, Modal, EmptyState
- **Composable** - Pages composed of small components
- **Accessible** - Semantic HTML, clear labels
- **Consistent** - Unified color scheme and spacing

---

## 🔧 Configuration

### Environment Variables

Create a `.env` file (optional):

```env
VITE_API_URL=http://localhost:8080
VITE_WS_URL=ws://localhost:8080/ws/dashboard
```

**Note:** The app auto-detects the backend URL based on environment.

## 📊 What's Implemented (BETA)

✅ **Complete navigation** - Sidebar with routing  
✅ **Dashboard** - Real-time overview  
✅ **Systems list** - Full CRUD UI  
✅ **System details** - Comprehensive view  
✅ **Alerts page** - Global alert monitoring  
✅ **Alert rules** - Creation and display  
✅ **WebSocket integration** - Real-time updates  
✅ **Loading/error/empty states** - All handled  
✅ **Responsive design** - Works on all devices  
✅ **Type safety** - No `any` types  
✅ **Clean code** - ESLint compliant  

---

## 🚫 What's NOT Included (BETA Scope)

❌ Authentication/Authorization  
❌ Multi-tenancy  
❌ User management  
❌ System registration UI (use backend API directly)  
❌ Metric detail pages  
❌ Advanced filtering  
❌ Export features  
❌ Mobile app  

---

## 🧪 Testing the Application

1. **Start the backend** (`apm-starter` module)
2. **Start the frontend** (`npm run dev`)
3. **Open** http://localhost:3000
4. **Navigate** through Dashboard, Systems, Alerts
5. **Test features**:
   - View dashboard metrics
   - Check systems list
   - Click a system to see details
   - Create an alert rule
   - View active alerts
   - Activate/deactivate a system

---

## 🎯 Portfolio Highlights

This project demonstrates:

- **Modern React patterns** (hooks, composition)
- **TypeScript proficiency** (types, interfaces, generics)
- **State management** (React Query, WebSocket)
- **API integration** (REST + WebSocket)
- **UI/UX best practices** (loading states, error handling)
- **Clean architecture** (separation of concerns)
- **Responsive design** (Tailwind CSS)
- **Production-ready code** (ESLint, TypeScript strict mode)

## 📝 Future Enhancements (Post-BETA)

- User authentication
- System registration form
- Advanced filtering and search
- Data export (CSV, JSON)
- Alert acknowledgment
- Metric history graphs
- Email/Slack notifications
- Dark/light theme toggle
- Internationalization (i18n)

---

## 👨‍💻 Author

Built as a portfolio project demonstrating full-stack development skills with a focus on:
- Clean, maintainable code
- Modern frontend architecture
- Real backend integration
- Professional UX

---

## 📄 License

This is a portfolio/learning project.

---

**Status:** BETA v0.1 - Fully functional and ready for demonstration in technical interviews.
