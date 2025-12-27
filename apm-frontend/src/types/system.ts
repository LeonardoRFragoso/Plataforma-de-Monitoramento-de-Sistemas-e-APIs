export interface MonitoredSystem {
  id: string;
  name: string;
  baseUrl: string;
  type: string;
  environment: string;
  active: boolean;
  currentStatus: 'UP' | 'DEGRADED' | 'DOWN';
  collectionIntervalSeconds: number;
  lastCollectionTimestamp: string | null;
  createdAt: string;
  updatedAt: string;
}

export interface RegisterSystemRequest {
  name: string;
  baseUrl: string;
  type: string;
  environment: string;
  collectionIntervalSeconds: number;
}

export interface UpdateSystemRequest {
  name: string;
  baseUrl: string;
  type: string;
  environment: string;
  collectionIntervalSeconds: number;
}
