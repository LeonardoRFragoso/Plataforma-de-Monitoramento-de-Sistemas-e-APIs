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

export const useAllAlertRules = () => {
  return useQuery({
    queryKey: ['alert-rules', 'all'],
    queryFn: () => alertsApi.getAllRules(),
  });
};

export const useCreateAlertRule = () => {
  const queryClient = useQueryClient();

  return useMutation({
    mutationFn: ({ systemId, data }: { systemId: string; data: CreateAlertRuleRequest }) =>
      alertsApi.createRule(systemId, data),
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ['alert-rules'] });
      queryClient.invalidateQueries({ queryKey: ['alerts'] });
    },
  });
};

export const useDeleteAlertRule = () => {
  const queryClient = useQueryClient();

  return useMutation({
    mutationFn: (ruleId: string) => alertsApi.deleteRule(ruleId),
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ['alert-rules'] });
    },
  });
};

export const useEnableAlertRule = () => {
  const queryClient = useQueryClient();

  return useMutation({
    mutationFn: (ruleId: string) => alertsApi.enableRule(ruleId),
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ['alert-rules'] });
    },
  });
};

export const useDisableAlertRule = () => {
  const queryClient = useQueryClient();

  return useMutation({
    mutationFn: (ruleId: string) => alertsApi.disableRule(ruleId),
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ['alert-rules'] });
    },
  });
};
