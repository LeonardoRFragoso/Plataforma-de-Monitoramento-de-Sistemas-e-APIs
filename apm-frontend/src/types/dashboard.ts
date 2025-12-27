export interface DashboardOverview {
  totalSystems: number;
  activeSystems: number;
  healthySystems: number;
  degradedSystems: number;
  downSystems: number;
  activeAlerts: number;
  criticalAlerts: number;
}

export interface MetricEvent {
  type: 'METRIC_COLLECTED';
  metricId: string;
  systemId: string;
  latencyMs: number;
  statusCode: number;
  timestamp: string;
}

export interface AlertEvent {
  type: 'ALERT_TRIGGERED';
  alertId: string;
  systemId: string;
  severity: 'INFO' | 'WARNING' | 'CRITICAL';
  message: string;
  timestamp: string;
}

export interface HealthEvent {
  type: 'HEALTH_DEGRADED';
  systemId: string;
  previousStatus: string;
  currentStatus: string;
  reason: string;
  timestamp: string;
}

export type WebSocketEvent = MetricEvent | AlertEvent | HealthEvent;
