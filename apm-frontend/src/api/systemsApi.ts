import { apiClient } from './client';
import type { MonitoredSystem, RegisterSystemRequest, UpdateSystemRequest } from '@/types/system';

export const systemsApi = {
  list: async (params?: { active?: boolean; environment?: string }): Promise<MonitoredSystem[]> => {
    const response = await apiClient.get<MonitoredSystem[]>('/api/v1/systems', { params });
    return response.data;
  },

  getById: async (systemId: string): Promise<MonitoredSystem> => {
    const response = await apiClient.get<MonitoredSystem>(`/api/v1/systems/${systemId}`);
    return response.data;
  },

  register: async (data: RegisterSystemRequest): Promise<MonitoredSystem> => {
    const response = await apiClient.post<MonitoredSystem>('/api/v1/systems', data);
    return response.data;
  },

  update: async (systemId: string, data: UpdateSystemRequest): Promise<MonitoredSystem> => {
    const response = await apiClient.put<MonitoredSystem>(`/api/v1/systems/${systemId}`, data);
    return response.data;
  },

  activate: async (systemId: string): Promise<void> => {
    await apiClient.post(`/api/v1/systems/${systemId}/activate`);
  },

  deactivate: async (systemId: string): Promise<void> => {
    await apiClient.post(`/api/v1/systems/${systemId}/deactivate`);
  },
};
