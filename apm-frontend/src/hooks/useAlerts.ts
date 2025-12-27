import { useQuery, useMutation, useQueryClient } from '@tanstack/react-query';
import { alertsApi } from '@/api/alertsApi';
import type { CreateAlertRuleRequest } from '@/types/alert';

export const useAllActiveAlerts = () => {
  return useQuery({
    queryKey: ['alerts', 'active'],
    queryFn: () => alertsApi.getAllActive(),
  });
};

export const useSystemAlerts = (systemId: string, params?: { resolved?: boolean }) => {
  return useQuery({
    queryKey: ['alerts', 'system', systemId, params],
    queryFn: () => alertsApi.getBySystemId(systemId, params),
    enabled: !!systemId,
  });
};

export const useAlertRules = (systemId: string) => {
  return useQuery({
    queryKey: ['alert-rules', systemId],
    queryFn: () => alertsApi.getRules(systemId),
    enabled: !!systemId,
  });
};

export const useCreateAlertRule = () => {
  const queryClient = useQueryClient();

  return useMutation({
    mutationFn: ({ systemId, data }: { systemId: string; data: CreateAlertRuleRequest }) =>
      alertsApi.createRule(systemId, data),
    onSuccess: (_, variables) => {
      queryClient.invalidateQueries({ queryKey: ['alert-rules', variables.systemId] });
      queryClient.invalidateQueries({ queryKey: ['alerts'] });
    },
  });
};
