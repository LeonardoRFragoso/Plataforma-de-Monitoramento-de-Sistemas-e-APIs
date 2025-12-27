import { useState } from 'react';
import { useAllActiveAlerts } from '@/hooks/useAlerts';
import { SeverityBadge } from '@/components/SeverityBadge';
import { LoadingSpinner } from '@/components/LoadingSpinner';
import { EmptyState } from '@/components/EmptyState';
import { ErrorState } from '@/components/ErrorState';
import type { AlertSeverity } from '@/types/alert';

export const AlertsPage = () => {
  const { data: alerts, isLoading, error, refetch } = useAllActiveAlerts();
  const [severityFilter, setSeverityFilter] = useState<AlertSeverity | 'ALL'>('ALL');

  const formatTimestamp = (dateString: string) => {
    return new Date(dateString).toLocaleString();
  };

  const filteredAlerts = alerts?.filter((alert) => {
    if (severityFilter === 'ALL') return true;
    return alert.severity === severityFilter;
  });

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
        <ErrorState message="Failed to load alerts" onRetry={() => refetch()} />
      </div>
    );
  }

  return (
    <div className="p-8">
      <div className="max-w-7xl mx-auto">
        <div className="mb-8">
          <h1 className="text-3xl font-bold text-white mb-2">Active Alerts</h1>
          <p className="text-slate-400">Monitor all active alerts across your systems</p>
        </div>

        {/* Filters */}
        <div className="bg-slate-800 rounded-lg border border-slate-700 p-4 mb-6">
          <div className="flex items-center gap-4">
            <span className="text-sm font-medium text-slate-300">Filter by Severity:</span>
            <div className="flex gap-2">
              {(['ALL', 'INFO', 'WARNING', 'CRITICAL'] as const).map((severity) => (
                <button
                  key={severity}
                  onClick={() => setSeverityFilter(severity)}
                  className={`px-4 py-2 rounded-lg text-sm font-medium transition-colors ${
                    severityFilter === severity
                      ? 'bg-blue-600 text-white'
                      : 'bg-slate-700 text-slate-300 hover:bg-slate-600'
                  }`}
                >
                  {severity}
                </button>
              ))}
            </div>
          </div>
        </div>

        {/* Alerts List */}
        {!filteredAlerts || filteredAlerts.length === 0 ? (
          <div className="bg-slate-800 rounded-lg border border-slate-700 p-6">
            <EmptyState
              icon="✅"
              title={severityFilter === 'ALL' ? 'No Active Alerts' : `No ${severityFilter} Alerts`}
              description={
                severityFilter === 'ALL'
                  ? 'All systems are running smoothly. No active alerts to display.'
                  : `There are no active ${severityFilter.toLowerCase()} alerts at the moment.`
              }
            />
          </div>
        ) : (
          <div className="space-y-4">
            {filteredAlerts.map((alert) => (
              <div
                key={alert.id}
                className="bg-slate-800 rounded-lg border border-slate-700 p-6 hover:border-slate-600 transition-colors"
              >
                <div className="flex items-start justify-between mb-4">
                  <div className="flex-1">
                    <div className="flex items-center gap-3 mb-2">
                      <SeverityBadge severity={alert.severity} />
                      {alert.resolved && (
                        <span className="px-3 py-1 bg-green-900/50 border border-green-700 text-green-300 rounded-lg text-xs font-semibold uppercase">
                          Resolved
                        </span>
                      )}
                    </div>
                    <p className="text-lg text-white mb-2">{alert.message}</p>
                    <div className="flex items-center gap-4 text-sm text-slate-400">
                      <span>Alert ID: {alert.id.substring(0, 8)}</span>
                      <span>System ID: {alert.systemId.substring(0, 8)}</span>
                      <span>Rule ID: {alert.ruleId.substring(0, 8)}</span>
                    </div>
                  </div>
                </div>

                <div className="flex items-center justify-between pt-4 border-t border-slate-700">
                  <div className="text-sm">
                    <span className="text-slate-400">Triggered at: </span>
                    <span className="text-white">{formatTimestamp(alert.triggeredAt)}</span>
                  </div>
                  {alert.resolved && alert.resolvedAt && (
                    <div className="text-sm">
                      <span className="text-slate-400">Resolved at: </span>
                      <span className="text-white">{formatTimestamp(alert.resolvedAt)}</span>
                    </div>
                  )}
                </div>
              </div>
            ))}
          </div>
        )}

        {/* Summary Stats */}
        {alerts && alerts.length > 0 && (
          <div className="mt-8 grid grid-cols-1 md:grid-cols-3 gap-6">
            <div className="bg-slate-800 rounded-lg border border-slate-700 p-6">
              <div className="text-sm text-slate-400 mb-1">Total Active</div>
              <div className="text-3xl font-bold text-white">{alerts.length}</div>
            </div>
            <div className="bg-slate-800 rounded-lg border border-slate-700 p-6">
              <div className="text-sm text-slate-400 mb-1">Critical</div>
              <div className="text-3xl font-bold text-red-400">
                {alerts.filter((a) => a.severity === 'CRITICAL').length}
              </div>
            </div>
            <div className="bg-slate-800 rounded-lg border border-slate-700 p-6">
              <div className="text-sm text-slate-400 mb-1">Warnings</div>
              <div className="text-3xl font-bold text-orange-400">
                {alerts.filter((a) => a.severity === 'WARNING').length}
              </div>
            </div>
          </div>
        )}
      </div>
    </div>
  );
};
