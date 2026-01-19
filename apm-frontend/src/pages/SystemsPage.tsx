import { useState } from 'react';
import { useNavigate } from 'react-router-dom';
import { Plus, Pencil, Trash2, Power, PowerOff } from 'lucide-react';
import { useSystems, useActivateSystem, useDeactivateSystem, useRegisterSystem, useUpdateSystem, useDeleteSystem } from '@/hooks/useSystems';
import { StatusBadge } from '@/components/StatusBadge';
import { LoadingSpinner } from '@/components/LoadingSpinner';
import { EmptyState } from '@/components/EmptyState';
import { ConfirmDialog } from '@/components/ConfirmDialog';
import { SystemFormModal } from '@/components/SystemFormModal';
import type { MonitoredSystem, RegisterSystemRequest } from '@/types/system';

export const SystemsPage = () => {
  const navigate = useNavigate();
  const { data: systems, isLoading } = useSystems();
  const activateMutation = useActivateSystem();
  const deactivateMutation = useDeactivateSystem();
  const registerMutation = useRegisterSystem();
  const updateMutation = useUpdateSystem();
  const deleteMutation = useDeleteSystem();

  const [formModal, setFormModal] = useState<{
    isOpen: boolean;
    editingSystem: MonitoredSystem | null;
  }>({
    isOpen: false,
    editingSystem: null,
  });

  const [confirmDialog, setConfirmDialog] = useState<{
    isOpen: boolean;
    system: MonitoredSystem | null;
    action: 'activate' | 'deactivate' | 'delete';
  }>({
    isOpen: false,
    system: null,
    action: 'activate',
  });

  const handleOpenCreateModal = () => {
    setFormModal({ isOpen: true, editingSystem: null });
  };

  const handleOpenEditModal = (system: MonitoredSystem) => {
    setFormModal({ isOpen: true, editingSystem: system });
  };

  const handleCloseModal = () => {
    setFormModal({ isOpen: false, editingSystem: null });
  };

  const handleSubmitForm = async (data: RegisterSystemRequest) => {
    try {
      if (formModal.editingSystem) {
        await updateMutation.mutateAsync({
          systemId: formModal.editingSystem.id,
          data,
        });
      } else {
        await registerMutation.mutateAsync(data);
      }
      handleCloseModal();
    } catch (error) {
      console.error('Failed to save system:', error);
    }
  };

  const handleDelete = (system: MonitoredSystem) => {
    setConfirmDialog({ isOpen: true, system, action: 'delete' });
  };

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
      } else if (confirmDialog.action === 'deactivate') {
        await deactivateMutation.mutateAsync(confirmDialog.system.id);
      } else if (confirmDialog.action === 'delete') {
        await deleteMutation.mutateAsync(confirmDialog.system.id);
      }
      setConfirmDialog({ isOpen: false, system: null, action: 'activate' });
    } catch (error) {
      console.error('Failed to perform action:', error);
    }
  };

  const formatDate = (dateString: string | null) => {
    if (!dateString) return 'Never';
    return new Date(dateString).toLocaleString();
  };

  const getConfirmDialogProps = () => {
    switch (confirmDialog.action) {
      case 'delete':
        return {
          title: 'Delete System',
          message: `Are you sure you want to delete "${confirmDialog.system?.name}"? This action cannot be undone.`,
          confirmLabel: 'Delete',
          confirmVariant: 'danger' as const,
        };
      case 'deactivate':
        return {
          title: 'Deactivate System',
          message: `Are you sure you want to deactivate "${confirmDialog.system?.name}"? Monitoring will be paused.`,
          confirmLabel: 'Deactivate',
          confirmVariant: 'danger' as const,
        };
      default:
        return {
          title: 'Activate System',
          message: `Are you sure you want to activate "${confirmDialog.system?.name}"? Monitoring will resume.`,
          confirmLabel: 'Activate',
          confirmVariant: 'primary' as const,
        };
    }
  };

  if (isLoading) {
    return (
      <div className="p-8">
        <LoadingSpinner />
      </div>
    );
  }

  return (
    <div className="p-8">
      <div className="max-w-7xl mx-auto">
        <div className="flex items-center justify-between mb-8">
          <div>
            <h1 className="text-3xl font-bold text-white mb-2">Monitored Systems</h1>
            <p className="text-slate-400">Manage and monitor all registered systems</p>
          </div>
          <button
            onClick={handleOpenCreateModal}
            className="flex items-center gap-2 px-5 py-2.5 bg-blue-600 hover:bg-blue-700 text-white rounded-lg font-medium transition-colors"
          >
            <Plus className="w-5 h-5" />
            Add System
          </button>
        </div>

        {!systems || systems.length === 0 ? (
          <div className="bg-slate-800 rounded-lg border border-slate-700 p-8">
            <EmptyState
              icon="🖥️"
              title="No Systems Registered"
              description="Start monitoring your applications by adding your first system."
            />
            <div className="flex justify-center mt-6">
              <button
                onClick={handleOpenCreateModal}
                className="flex items-center gap-2 px-5 py-2.5 bg-blue-600 hover:bg-blue-700 text-white rounded-lg font-medium transition-colors"
              >
                <Plus className="w-5 h-5" />
                Add Your First System
              </button>
            </div>
          </div>
        ) : (
          <div className="bg-slate-800 rounded-lg border border-slate-700 overflow-hidden">
            <div className="overflow-x-auto">
              <table className="w-full">
                <thead className="bg-slate-700/50 border-b border-slate-700">
                  <tr>
                    <th className="px-6 py-4 text-left text-xs font-semibold text-slate-300 uppercase tracking-wider">
                      System
                    </th>
                    <th className="px-6 py-4 text-left text-xs font-semibold text-slate-300 uppercase tracking-wider">
                      Status
                    </th>
                    <th className="px-6 py-4 text-left text-xs font-semibold text-slate-300 uppercase tracking-wider">
                      Type
                    </th>
                    <th className="px-6 py-4 text-left text-xs font-semibold text-slate-300 uppercase tracking-wider">
                      Environment
                    </th>
                    <th className="px-6 py-4 text-left text-xs font-semibold text-slate-300 uppercase tracking-wider">
                      Interval
                    </th>
                    <th className="px-6 py-4 text-left text-xs font-semibold text-slate-300 uppercase tracking-wider">
                      Last Collection
                    </th>
                    <th className="px-6 py-4 text-right text-xs font-semibold text-slate-300 uppercase tracking-wider">
                      Actions
                    </th>
                  </tr>
                </thead>
                <tbody className="divide-y divide-slate-700">
                  {systems.map((system) => (
                    <tr
                      key={system.id}
                      className="hover:bg-slate-700/30 transition-colors"
                    >
                      <td className="px-6 py-4">
                        <div
                          className="cursor-pointer"
                          onClick={() => navigate(`/systems/${system.id}`)}
                        >
                          <div className="font-medium text-white hover:text-blue-400 transition-colors">
                            {system.name}
                          </div>
                          <div className="text-sm text-slate-400 truncate max-w-xs">
                            {system.baseUrl}
                          </div>
                        </div>
                      </td>
                      <td className="px-6 py-4">
                        <div className="flex items-center gap-2">
                          <StatusBadge status={system.currentStatus} />
                          {!system.active && (
                            <span className="px-2 py-0.5 bg-slate-600 text-slate-300 rounded text-xs">
                              Paused
                            </span>
                          )}
                        </div>
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
                        <span className="text-slate-300">{system.collectionIntervalSeconds}s</span>
                      </td>
                      <td className="px-6 py-4">
                        <span className="text-sm text-slate-400">
                          {formatDate(system.lastCollectionTimestamp)}
                        </span>
                      </td>
                      <td className="px-6 py-4">
                        <div className="flex items-center justify-end gap-2">
                          <button
                            onClick={() => handleOpenEditModal(system)}
                            className="p-2 hover:bg-slate-600 text-slate-400 hover:text-white rounded-lg transition-colors"
                            title="Edit"
                          >
                            <Pencil className="w-4 h-4" />
                          </button>
                          {system.active ? (
                            <button
                              onClick={() => handleDeactivate(system)}
                              className="p-2 hover:bg-orange-600/20 text-orange-400 hover:text-orange-300 rounded-lg transition-colors"
                              title="Deactivate"
                            >
                              <PowerOff className="w-4 h-4" />
                            </button>
                          ) : (
                            <button
                              onClick={() => handleActivate(system)}
                              className="p-2 hover:bg-green-600/20 text-green-400 hover:text-green-300 rounded-lg transition-colors"
                              title="Activate"
                            >
                              <Power className="w-4 h-4" />
                            </button>
                          )}
                          <button
                            onClick={() => handleDelete(system)}
                            className="p-2 hover:bg-red-600/20 text-red-400 hover:text-red-300 rounded-lg transition-colors"
                            title="Delete"
                          >
                            <Trash2 className="w-4 h-4" />
                          </button>
                        </div>
                      </td>
                    </tr>
                  ))}
                </tbody>
              </table>
            </div>
          </div>
        )}
      </div>

      <SystemFormModal
        isOpen={formModal.isOpen}
        onClose={handleCloseModal}
        onSubmit={handleSubmitForm}
        initialData={formModal.editingSystem}
        isLoading={registerMutation.isPending || updateMutation.isPending}
        title={formModal.editingSystem ? 'Edit System' : 'Add New System'}
      />

      <ConfirmDialog
        isOpen={confirmDialog.isOpen}
        onClose={() => setConfirmDialog({ isOpen: false, system: null, action: 'activate' })}
        onConfirm={handleConfirm}
        isLoading={activateMutation.isPending || deactivateMutation.isPending || deleteMutation.isPending}
        {...getConfirmDialogProps()}
      />
    </div>
  );
};
