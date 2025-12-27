import { useState } from 'react';
import { useNavigate } from 'react-router-dom';
import { useSystems, useActivateSystem, useDeactivateSystem } from '@/hooks/useSystems';
import { StatusBadge } from '@/components/StatusBadge';
import { LoadingSpinner } from '@/components/LoadingSpinner';
import { EmptyState } from '@/components/EmptyState';
import { ErrorState } from '@/components/ErrorState';
import { ConfirmDialog } from '@/components/ConfirmDialog';
import type { MonitoredSystem } from '@/types/system';

export const SystemsPage = () => {
  const navigate = useNavigate();
  const { data: systems, isLoading, error, refetch } = useSystems();
  const activateMutation = useActivateSystem();
  const deactivateMutation = useDeactivateSystem();

  const [confirmDialog, setConfirmDialog] = useState<{
    isOpen: boolean;
    system: MonitoredSystem | null;
    action: 'activate' | 'deactivate';
  }>({
    isOpen: false,
    system: null,
    action: 'activate',
  });

  const handleActivate = (system: MonitoredSystem) => {
    setConfirmDialog({ isOpen: true, system, action: 'activate' });
  };

  const handleDeactivate = (system: MonitoredSystem) => {
    setConfirmDialog({ isOpen: true, system, action: 'deactivate' });
  };

  const handleConfirm = async () => {
    if (!confirmDialog.system) return;

    try {
      if (confirmDialog.action === 'activate') {
        await activateMutation.mutateAsync(confirmDialog.system.id);
      } else {
        await deactivateMutation.mutateAsync(confirmDialog.system.id);
      }
      setConfirmDialog({ isOpen: false, system: null, action: 'activate' });
    } catch (error) {
      console.error('Failed to update system:', error);
    }
  };

  const formatDate = (dateString: string | null) => {
    if (!dateString) return 'Never';
    return new Date(dateString).toLocaleString();
  };

  if (isLoading) {
    return (
      <div className="p-8">
        <LoadingSpinner />
      </div>
    );
  }

  if (error) {
    return (
      <div className="p-8">
        <ErrorState message="Failed to load systems" onRetry={() => refetch()} />
      </div>
    );
  }

  if (!systems || systems.length === 0) {
    return (
      <div className="p-8">
        <EmptyState
          icon="🖥️"
          title="No Systems Registered"
          description="Start monitoring your applications by registering your first system."
        />
      </div>
    );
  }

  return (
    <div className="p-8">
      <div className="max-w-7xl mx-auto">
        <div className="mb-8">
          <h1 className="text-3xl font-bold text-white mb-2">Monitored Systems</h1>
          <p className="text-slate-400">Manage and monitor all registered systems</p>
        </div>

        <div className="bg-slate-800 rounded-lg border border-slate-700 overflow-hidden">
          <div className="overflow-x-auto">
            <table className="w-full">
              <thead className="bg-slate-700/50 border-b border-slate-700">
                <tr>
                  <th className="px-6 py-3 text-left text-xs font-semibold text-slate-300 uppercase tracking-wider">
                    System
                  </th>
                  <th className="px-6 py-3 text-left text-xs font-semibold text-slate-300 uppercase tracking-wider">
                    Status
                  </th>
                  <th className="px-6 py-3 text-left text-xs font-semibold text-slate-300 uppercase tracking-wider">
                    Type
                  </th>
                  <th className="px-6 py-3 text-left text-xs font-semibold text-slate-300 uppercase tracking-wider">
                    Environment
                  </th>
                  <th className="px-6 py-3 text-left text-xs font-semibold text-slate-300 uppercase tracking-wider">
                    Last Collection
                  </th>
                  <th className="px-6 py-3 text-left text-xs font-semibold text-slate-300 uppercase tracking-wider">
                    Actions
                  </th>
                </tr>
              </thead>
              <tbody className="divide-y divide-slate-700">
                {systems.map((system) => (
                  <tr
                    key={system.id}
                    className="hover:bg-slate-700/30 transition-colors cursor-pointer"
                    onClick={() => navigate(`/systems/${system.id}`)}
                  >
                    <td className="px-6 py-4">
                      <div>
                        <div className="font-medium text-white">{system.name}</div>
                        <div className="text-sm text-slate-400 truncate max-w-xs">{system.baseUrl}</div>
                      </div>
                    </td>
                    <td className="px-6 py-4">
                      <StatusBadge status={system.currentStatus} />
                    </td>
                    <td className="px-6 py-4">
                      <span className="text-slate-300">{system.type}</span>
                    </td>
                    <td className="px-6 py-4">
                      <span className="px-3 py-1 bg-slate-700 text-slate-300 rounded-lg text-sm">
                        {system.environment}
                      </span>
                    </td>
                    <td className="px-6 py-4">
                      <span className="text-sm text-slate-400">{formatDate(system.lastCollectionTimestamp)}</span>
                    </td>
                    <td className="px-6 py-4">
                      <div className="flex items-center gap-2" onClick={(e) => e.stopPropagation()}>
                        {system.active ? (
                          <button
                            onClick={() => handleDeactivate(system)}
                            className="px-3 py-1 bg-red-600 hover:bg-red-700 text-white text-sm rounded-lg transition-colors"
                          >
                            Deactivate
                          </button>
                        ) : (
                          <button
                            onClick={() => handleActivate(system)}
                            className="px-3 py-1 bg-green-600 hover:bg-green-700 text-white text-sm rounded-lg transition-colors"
                          >
                            Activate
                          </button>
                        )}
                        <button
                          onClick={() => navigate(`/systems/${system.id}`)}
                          className="px-3 py-1 bg-blue-600 hover:bg-blue-700 text-white text-sm rounded-lg transition-colors"
                        >
                          View Details
                        </button>
                      </div>
                    </td>
                  </tr>
                ))}
              </tbody>
            </table>
          </div>
        </div>
      </div>

      <ConfirmDialog
        isOpen={confirmDialog.isOpen}
        onClose={() => setConfirmDialog({ isOpen: false, system: null, action: 'activate' })}
        onConfirm={handleConfirm}
        title={confirmDialog.action === 'activate' ? 'Activate System' : 'Deactivate System'}
        message={`Are you sure you want to ${confirmDialog.action} ${confirmDialog.system?.name}?`}
        confirmLabel={confirmDialog.action === 'activate' ? 'Activate' : 'Deactivate'}
        confirmVariant={confirmDialog.action === 'deactivate' ? 'danger' : 'primary'}
        isLoading={activateMutation.isPending || deactivateMutation.isPending}
      />
    </div>
  );
};
