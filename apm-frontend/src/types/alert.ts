export type AlertSeverity = 'INFO' | 'WARNING' | 'CRITICAL';
export type AlertType = 'LATENCY' | 'ERROR_RATE' | 'AVAILABILITY';

export interface Alert {
  id: string;
  systemId: string;
  ruleId: string;
  severity: AlertSeverity;
  message: string;
  triggeredAt: string;
  resolved: boolean;
  resolvedAt: string | null;
}

export interface AlertRule {
  id: string;
  systemId: string;
  name: string;
  type: AlertType;
  severity: AlertSeverity;
  thresholdValue: number;
  consecutiveViolations: number;
  enabled: boolean;
  createdAt: string;
}

export interface CreateAlertRuleRequest {
  name: string;
  type: AlertType;
  severity: AlertSeverity;
  thresholdValue: number;
  consecutiveViolations: number;
}
