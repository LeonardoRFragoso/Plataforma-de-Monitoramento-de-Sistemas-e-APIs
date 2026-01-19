import { useState } from 'react';
import { Plus, Trash2, Power, PowerOff, Bell, AlertTriangle, Info } from 'lucide-react';
import { useAllAlertRules, useCreateAlertRule, useDeleteAlertRule, useEnableAlertRule, useDisableAlertRule } from '@/hooks/useAlerts';
import { useSystems } from '@/hooks/useSystems';
import { LoadingSpinner } from '@/components/LoadingSpinner';
import { EmptyState } from '@/components/EmptyState';
import { ConfirmDialog } from '@/components/ConfirmDialog';
import { AlertRuleFormModal } from '@/components/AlertRuleFormModal';
import type { AlertRule, CreateAlertRuleRequest } from '@/types/alert';

const SeverityBadge = ({ severity }: { severity: string }) => {
  const config = {
    INFO: { icon: Info, bg: 'bg-blue-500/20', text: 'text-blue-400', border: 'border-blue-500/30' },
    WARNING: { icon: AlertTriangle, bg: 'bg-yellow-500/20', text: 'text-yellow-400', border: 'border-yellow-500/30' },
    CRITICAL: { icon: Bell, bg: 'bg-red-500/20', text: 'text-red-400', border: 'border-red-500/30' },
  }[severity] || { icon: Info, bg: 'bg-slate-500/20', text: 'text-slate-400', border: 'border-slate-500/30' };

  const Icon = config.icon;

  return (
    <span className={`inline-flex items-center gap-1.5 px-2.5 py-1 rounded-full text-xs font-medium ${config.bg} ${config.text} border ${config.border}`}>
      <Icon className="w-3 h-3" />
      {severity}
    </span>
  );
};

export const AlertRulesPage = () => {
  const { data: rules, isLoading } = useAllAlertRules();
  const { data: systems } = useSystems();
  const createMutation = useCreateAlertRule();
  const deleteMutation = useDeleteAlertRule();
  const enableMutation = useEnableAlertRule();
  const disableMutation = useDisableAlertRule();

  const [isFormOpen, setIsFormOpen] = useState(false);
  const [confirmDialog, setConfirmDialog] = useState<{
    isOpen: boolean;
    rule: AlertRule | null;
    action: 'delete' | 'enable' | 'disable';
  }>({
    isOpen: false,
    rule: null,
    action: 'delete',
  });

  const getSystemName = (systemId: string) => {
    return systems?.find((s) => s.id === systemId)?.name || 'Unknown System';
  };

  const handleCreate = async (systemId: string, data: CreateAlertRuleRequest) => {
    try {
      await createMutation.mutateAsync({ systemId, data });
      setIsFormOpen(false);
    } catch (error) {
      console.error('Failed to create rule:', error);
    }
  };

  const handleDelete = (rule: AlertRule) => {
    setConfirmDialog({ isOpen: true, rule, action: 'delete' });
  };

  const handleToggleEnabled = (rule: AlertRule) => {
    setConfirmDialog({
      isOpen: true,
      rule,
      action: rule.enabled ? 'disable' : 'enable',
    });
  };

  const handleConfirm = async () => {
    if (!confirmDialog.rule) return;

    try {
      if (confirmDialog.action === 'delete') {
        await deleteMutation.mutateAsync(confirmDialog.rule.id);
      } else if (confirmDialog.action === 'enable') {
        await enableMutation.mutateAsync(confirmDialog.rule.id);
      } else if (confirmDialog.action === 'disable') {
        await disableMutation.mutateAsync(confirmDialog.rule.id);
      }
      setConfirmDialog({ isOpen: false, rule: null, action: 'delete' });
    } catch (error) {
      console.error('Failed to perform action:', error);
    }
  };

  const getConfirmDialogProps = () => {
    switch (confirmDialog.action) {
      case 'delete':
        return {
          title: 'Delete Alert Rule',
          message: `Are you sure you want to delete "${confirmDialog.rule?.name}"? This action cannot be undone.`,
          confirmLabel: 'Delete',
          confirmVariant: 'danger' as const,
        };
      case 'disable':
        return {
          title: 'Disable Alert Rule',
          message: `Are you sure you want to disable "${confirmDialog.rule?.name}"? Alerts will not be triggered.`,
          confirmLabel: 'Disable',
          confirmVariant: 'danger' as const,
        };
      default:
        return {
          title: 'Enable Alert Rule',
          message: `Are you sure you want to enable "${confirmDialog.rule?.name}"? Alerts will be triggered when conditions are met.`,
          confirmLabel: 'Enable',
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
            <h1 className="text-3xl font-bold text-white mb-2">Alert Rules</h1>
            <p className="text-slate-400">Manage alert rules for your monitored systems</p>
          </div>
          <button
            onClick={() => setIsFormOpen(true)}
            className="flex items-center gap-2 px-5 py-2.5 bg-blue-600 hover:bg-blue-700 text-white rounded-lg font-medium transition-colors"
          >
            <Plus className="w-5 h-5" />
            Create Rule
          </button>
        </div>

        {!rules || rules.length === 0 ? (
          <div className="bg-slate-800 rounded-lg border border-slate-700 p-8">
            <EmptyState
              icon="🔔"
              title="No Alert Rules"
              description="Create alert rules to get notified when your systems need attention."
            />
            <div className="flex justify-center mt-6">
              <button
                onClick={() => setIsFormOpen(true)}
                className="flex items-center gap-2 px-5 py-2.5 bg-blue-600 hover:bg-blue-700 text-white rounded-lg font-medium transition-colors"
              >
                <Plus className="w-5 h-5" />
                Create Your First Rule
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
                      Rule
                    </th>
                    <th className="px-6 py-4 text-left text-xs font-semibold text-slate-300 uppercase tracking-wider">
                      System
                    </th>
                    <th className="px-6 py-4 text-left text-xs font-semibold text-slate-300 uppercase tracking-wider">
                      Type
                    </th>
                    <th className="px-6 py-4 text-left text-xs font-semibold text-slate-300 uppercase tracking-wider">
                      Severity
                    </th>
                    <th className="px-6 py-4 text-left text-xs font-semibold text-slate-300 uppercase tracking-wider">
                      Threshold
                    </th>
                    <th className="px-6 py-4 text-left text-xs font-semibold text-slate-300 uppercase tracking-wider">
                      Status
                    </th>
                    <th className="px-6 py-4 text-right text-xs font-semibold text-slate-300 uppercase tracking-wider">
                      Actions
                    </th>
                  </tr>
                </thead>
                <tbody className="divide-y divide-slate-700">
                  {rules.map((rule) => (
                    <tr key={rule.id} className="hover:bg-slate-700/30 transition-colors">
                      <td className="px-6 py-4">
                        <div className="font-medium text-white">{rule.name}</div>
                        <div className="text-xs text-slate-500">
                          {rule.consecutiveViolations} consecutive violations
                        </div>
                      </td>
                      <td className="px-6 py-4">
                        <span className="text-slate-300">{getSystemName(rule.systemId)}</span>
                      </td>
                      <td className="px-6 py-4">
                        <span className="px-3 py-1 bg-slate-700 text-slate-300 rounded-lg text-sm">
                          {rule.type}
                        </span>
                      </td>
                      <td className="px-6 py-4">
                        <SeverityBadge severity={rule.severity} />
                      </td>
                      <td className="px-6 py-4">
                        <span className="text-slate-300 font-mono">
                          {rule.thresholdValue}
                          {rule.type === 'LATENCY' ? 'ms' : '%'}
                        </span>
                      </td>
                      <td className="px-6 py-4">
                        {rule.enabled ? (
                          <span className="inline-flex items-center gap-1.5 px-2.5 py-1 rounded-full text-xs font-medium bg-green-500/20 text-green-400 border border-green-500/30">
                            <span className="w-1.5 h-1.5 bg-green-400 rounded-full" />
                            Enabled
                          </span>
                        ) : (
                          <span className="inline-flex items-center gap-1.5 px-2.5 py-1 rounded-full text-xs font-medium bg-slate-500/20 text-slate-400 border border-slate-500/30">
                            <span className="w-1.5 h-1.5 bg-slate-400 rounded-full" />
                            Disabled
                          </span>
                        )}
                      </td>
                      <td className="px-6 py-4">
                        <div className="flex items-center justify-end gap-2">
                          {rule.enabled ? (
                            <button
                              onClick={() => handleToggleEnabled(rule)}
                              className="p-2 hover:bg-orange-600/20 text-orange-400 hover:text-orange-300 rounded-lg transition-colors"
                              title="Disable"
                            >
                              <PowerOff className="w-4 h-4" />
                            </button>
                          ) : (
                            <button
                              onClick={() => handleToggleEnabled(rule)}
                              className="p-2 hover:bg-green-600/20 text-green-400 hover:text-green-300 rounded-lg transition-colors"
                              title="Enable"
                            >
                              <Power className="w-4 h-4" />
                            </button>
                          )}
                          <button
                            onClick={() => handleDelete(rule)}
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

      <AlertRuleFormModal
        isOpen={isFormOpen}
        onClose={() => setIsFormOpen(false)}
        onSubmit={handleCreate}
        isLoading={createMutation.isPending}
      />

      <ConfirmDialog
        isOpen={confirmDialog.isOpen}
        onClose={() => setConfirmDialog({ isOpen: false, rule: null, action: 'delete' })}
        onConfirm={handleConfirm}
        isLoading={deleteMutation.isPending || enableMutation.isPending || disableMutation.isPending}
        {...getConfirmDialogProps()}
      />
    </div>
  );
};
