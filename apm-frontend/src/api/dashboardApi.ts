import { apiClient } from './client';
import type { DashboardOverview } from '@/types/dashboard';

export const dashboardApi = {
  getOverview: async (): Promise<DashboardOverview> => {
    const response = await apiClient.get<DashboardOverview>('/api/v1/dashboard/overview');
    return response.data;
  },
};
