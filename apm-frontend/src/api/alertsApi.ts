import { apiClient } from './client';
import type { Alert, AlertRule, CreateAlertRuleRequest } from '@/types/alert';

export const alertsApi = {
  getAllActive: async (): Promise<Alert[]> => {
    const response = await apiClient.get<Alert[]>('/api/v1/alerts/active');
    return response.data;
  },

  getBySystemId: async (systemId: string, params?: { resolved?: boolean }): Promise<Alert[]> => {
    const response = await apiClient.get<Alert[]>(`/api/v1/systems/${systemId}/alerts`, { params });
    return response.data;
  },

  getRules: async (systemId: string): Promise<AlertRule[]> => {
    const response = await apiClient.get<AlertRule[]>(`/api/v1/systems/${systemId}/alert-rules`);
    return response.data;
  },

  createRule: async (systemId: string, data: CreateAlertRuleRequest): Promise<AlertRule> => {
    const response = await apiClient.post<AlertRule>(`/api/v1/systems/${systemId}/alert-rules`, data);
    return response.data;
  },
};
