import { useQuery, useMutation, useQueryClient } from '@tanstack/react-query';
import { systemsApi } from '@/api/systemsApi';
import type { RegisterSystemRequest, UpdateSystemRequest } from '@/types/system';

export const useSystems = (params?: { active?: boolean; environment?: string }) => {
  return useQuery({
    queryKey: ['systems', params],
    queryFn: () => systemsApi.list(params),
  });
};

export const useSystem = (systemId: string) => {
  return useQuery({
    queryKey: ['systems', systemId],
    queryFn: () => systemsApi.getById(systemId),
    enabled: !!systemId,
  });
};

export const useRegisterSystem = () => {
  const queryClient = useQueryClient();

  return useMutation({
    mutationFn: (data: RegisterSystemRequest) => systemsApi.register(data),
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ['systems'] });
      queryClient.invalidateQueries({ queryKey: ['dashboard'] });
    },
  });
};

export const useUpdateSystem = () => {
  const queryClient = useQueryClient();

  return useMutation({
    mutationFn: ({ systemId, data }: { systemId: string; data: UpdateSystemRequest }) =>
      systemsApi.update(systemId, data),
    onSuccess: (_, variables) => {
      queryClient.invalidateQueries({ queryKey: ['systems'] });
      queryClient.invalidateQueries({ queryKey: ['systems', variables.systemId] });
      queryClient.invalidateQueries({ queryKey: ['dashboard'] });
    },
  });
};

export const useActivateSystem = () => {
  const queryClient = useQueryClient();

  return useMutation({
    mutationFn: (systemId: string) => systemsApi.activate(systemId),
    onSuccess: (_, systemId) => {
      queryClient.invalidateQueries({ queryKey: ['systems'] });
      queryClient.invalidateQueries({ queryKey: ['systems', systemId] });
      queryClient.invalidateQueries({ queryKey: ['dashboard'] });
    },
  });
};

export const useDeactivateSystem = () => {
  const queryClient = useQueryClient();

  return useMutation({
    mutationFn: (systemId: string) => systemsApi.deactivate(systemId),
    onSuccess: (_, systemId) => {
      queryClient.invalidateQueries({ queryKey: ['systems'] });
      queryClient.invalidateQueries({ queryKey: ['systems', systemId] });
      queryClient.invalidateQueries({ queryKey: ['dashboard'] });
    },
  });
};
