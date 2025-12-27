import { useState } from 'react';
import { useParams, useNavigate } from 'react-router-dom';
import { useSystem } from '@/hooks/useSystems';
import { useSystemAlerts, useAlertRules } from '@/hooks/useAlerts';
import { StatusBadge } from '@/components/StatusBadge';
import { SeverityBadge } from '@/components/SeverityBadge';
import { LoadingSpinner } from '@/components/LoadingSpinner';
import { EmptyState } from '@/components/EmptyState';
import { ErrorState } from '@/components/ErrorState';
import { CreateAlertRuleModal } from '@/components/CreateAlertRuleModal';

export const SystemDetailPage = () => {
  const { systemId } = useParams<{ systemId: string }>();
  const navigate = useNavigate();
  const [showCreateRuleModal, setShowCreateRuleModal] = useState(false);

  const { data: system, isLoading: systemLoading, error: systemError } = useSystem(systemId!);
  const { data: alerts, isLoading: alertsLoading } = useSystemAlerts(systemId!, { resolved: false });
  const { data: rules, isLoading: rulesLoading } = useAlertRules(systemId!);

  const formatDate = (dateString: string | null) => {
    if (!dateString) return 'Never';
    return new Date(dateString).toLocaleString();
  };

  const formatTimestamp = (dateString: string) => {
    return new Date(dateString).toLocaleString();
  };

  if (systemLoading) {
    return (
      <div className="p-8">
        <LoadingSpinner />
      </div>
    );
  }

  if (systemError || !system) {
    return (
      <div className="p-8">
        <ErrorState message="Failed to load system details" onRetry={() => navigate('/systems')} />
      </div>
    );
  }

  return (
    <div className="p-8">
      <div className="max-w-7xl mx-auto">
        {/* Header */}
        <div className="mb-8">
          <button
            onClick={() => navigate('/systems')}
            className="text-slate-400 hover:text-white mb-4 flex items-center gap-2 transition-colors"
          >
            <svg className="w-5 h-5" fill="none" viewBox="0 0 24 24" stroke="currentColor">
              <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M15 19l-7-7 7-7" />
            </svg>
            Back to Systems
          </button>
          <div className="flex items-start justify-between">
            <div>
              <h1 className="text-3xl font-bold text-white mb-2">{system.name}</h1>
              <p className="text-slate-400">{system.baseUrl}</p>
            </div>
            <StatusBadge status={system.currentStatus} />
          </div>
        </div>

        {/* System Information */}
        <div className="grid grid-cols-1 lg:grid-cols-2 gap-6 mb-8">
          <div className="bg-slate-800 rounded-lg border border-slate-700 p-6">
            <h2 className="text-xl font-semibold text-white mb-4">System Information</h2>
            <dl className="space-y-3">
              <div>
                <dt className="text-sm text-slate-400">Type</dt>
                <dd className="text-white font-medium">{system.type}</dd>
              </div>
              <div>
                <dt className="text-sm text-slate-400">Environment</dt>
                <dd className="text-white font-medium">
                  <span className="px-3 py-1 bg-slate-700 rounded-lg">{system.environment}</span>
                </dd>
              </div>
              <div>
                <dt className="text-sm text-slate-400">Collection Interval</dt>
                <dd className="text-white font-medium">{system.collectionIntervalSeconds}s</dd>
              </div>
              <div>
                <dt className="text-sm text-slate-400">Status</dt>
                <dd className="text-white font-medium">
                  {system.active ? (
                    <span className="text-green-400">Active</span>
                  ) : (
                    <span className="text-red-400">Inactive</span>
                  )}
                </dd>
              </div>
            </dl>
          </div>

          <div className="bg-slate-800 rounded-lg border border-slate-700 p-6">
            <h2 className="text-xl font-semibold text-white mb-4">Collection Status</h2>
            <dl className="space-y-3">
              <div>
                <dt className="text-sm text-slate-400">Last Collection</dt>
                <dd className="text-white font-medium">{formatDate(system.lastCollectionTimestamp)}</dd>
              </div>
              <div>
                <dt className="text-sm text-slate-400">Created At</dt>
                <dd className="text-white font-medium">{formatTimestamp(system.createdAt)}</dd>
              </div>
              <div>
                <dt className="text-sm text-slate-400">Updated At</dt>
                <dd className="text-white font-medium">{formatTimestamp(system.updatedAt)}</dd>
              </div>
            </dl>
          </div>
        </div>

        {/* Active Alerts */}
        <div className="bg-slate-800 rounded-lg border border-slate-700 p-6 mb-8">
          <div className="flex items-center justify-between mb-4">
            <h2 className="text-xl font-semibold text-white">Active Alerts</h2>
          </div>
          {alertsLoading ? (
            <LoadingSpinner />
          ) : !alerts || alerts.length === 0 ? (
            <EmptyState
              icon="✅"
              title="No Active Alerts"
              description="This system has no active alerts at the moment."
            />
          ) : (
            <div className="space-y-3">
              {alerts.map((alert) => (
                <div key={alert.id} className="p-4 bg-slate-700/50 rounded-lg border border-slate-600">
                  <div className="flex items-start justify-between mb-2">
                    <SeverityBadge severity={alert.severity} />
                    <span className="text-xs text-slate-400">{formatTimestamp(alert.triggeredAt)}</span>
                  </div>
                  <p className="text-white">{alert.message}</p>
                </div>
              ))}
            </div>
          )}
        </div>

        {/* Alert Rules */}
        <div className="bg-slate-800 rounded-lg border border-slate-700 p-6">
          <div className="flex items-center justify-between mb-4">
            <h2 className="text-xl font-semibold text-white">Alert Rules</h2>
            <button
              onClick={() => setShowCreateRuleModal(true)}
              className="px-4 py-2 bg-blue-600 hover:bg-blue-700 text-white rounded-lg font-medium transition-colors"
            >
              Create Rule
            </button>
          </div>
          {rulesLoading ? (
            <LoadingSpinner />
          ) : !rules || rules.length === 0 ? (
            <EmptyState
              icon="📋"
              title="No Alert Rules"
              description="Create alert rules to monitor this system."
              action={{
                label: 'Create First Rule',
                onClick: () => setShowCreateRuleModal(true),
              }}
            />
          ) : (
            <div className="overflow-x-auto">
              <table className="w-full">
                <thead className="border-b border-slate-700">
                  <tr>
                    <th className="px-4 py-2 text-left text-xs font-semibold text-slate-300 uppercase">Name</th>
                    <th className="px-4 py-2 text-left text-xs font-semibold text-slate-300 uppercase">Type</th>
                    <th className="px-4 py-2 text-left text-xs font-semibold text-slate-300 uppercase">Severity</th>
                    <th className="px-4 py-2 text-left text-xs font-semibold text-slate-300 uppercase">Threshold</th>
                    <th className="px-4 py-2 text-left text-xs font-semibold text-slate-300 uppercase">Status</th>
                  </tr>
                </thead>
                <tbody className="divide-y divide-slate-700">
                  {rules.map((rule) => (
                    <tr key={rule.id} className="hover:bg-slate-700/30 transition-colors">
                      <td className="px-4 py-3 text-white">{rule.name}</td>
                      <td className="px-4 py-3 text-slate-300">{rule.type}</td>
                      <td className="px-4 py-3">
                        <SeverityBadge severity={rule.severity} />
                      </td>
                      <td className="px-4 py-3 text-slate-300">
                        {rule.thresholdValue} ({rule.consecutiveViolations}x)
                      </td>
                      <td className="px-4 py-3">
                        {rule.enabled ? (
                          <span className="text-green-400">Enabled</span>
                        ) : (
                          <span className="text-red-400">Disabled</span>
                        )}
                      </td>
                    </tr>
                  ))}
                </tbody>
              </table>
            </div>
          )}
        </div>
      </div>

      <CreateAlertRuleModal
        isOpen={showCreateRuleModal}
        onClose={() => setShowCreateRuleModal(false)}
        systemId={systemId!}
      />
    </div>
  );
};
