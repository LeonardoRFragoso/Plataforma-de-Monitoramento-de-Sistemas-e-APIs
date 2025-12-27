export interface Metric {
  id: string;
  systemId: string;
  latencyMs: number;
  statusCode: number;
  success: boolean;
  collectedAt: string;
}
