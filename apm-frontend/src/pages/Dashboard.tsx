import { useDashboardOverview } from '@/hooks/useDashboardOverview';
import { useDashboardWebSocket } from '@/hooks/useDashboardWebSocket';
import { StatCard } from '@/components/StatCard';
import { LatencyChart } from '@/components/LatencyChart';
import { AlertBadge } from '@/components/AlertBadge';
import { LoadingSpinner } from '@/components/LoadingSpinner';
import { ErrorState } from '@/components/ErrorState';

export const Dashboard = () => {
  const { data: overview, isLoading, error, refetch } = useDashboardOverview();
  const { lastMetric, lastAlert, lastHealthEvent } = useDashboardWebSocket();

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
        <ErrorState message="Failed to load dashboard overview" onRetry={() => refetch()} />
      </div>
    );
  }

  return (
    <div className="p-8">
      <div className="max-w-7xl mx-auto">
        {/* Header */}
        <div className="mb-8">
          <h1 className="text-3xl font-bold text-white mb-2">Dashboard</h1>
          <p className="text-slate-400">Real-time overview of your monitored systems</p>
        </div>

        {/* Stats Grid */}
        {overview && (
          <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-4 gap-6 mb-8">
            <StatCard
              title="Total Systems"
              value={overview.totalSystems}
              subtitle={`${overview.activeSystems} active`}
            />
            <StatCard
              title="Healthy Systems"
              value={overview.healthySystems}
              variant="success"
            />
            <StatCard
              title="Degraded Systems"
              value={overview.degradedSystems}
              variant="warning"
            />
            <StatCard
              title="Down Systems"
              value={overview.downSystems}
              variant="danger"
            />
          </div>
        )}

        {/* Alerts Summary */}
        {overview && overview.activeAlerts > 0 && (
          <div className="grid grid-cols-1 md:grid-cols-2 gap-6 mb-8">
            <StatCard
              title="Active Alerts"
              value={overview.activeAlerts}
              variant="warning"
            />
            <StatCard
              title="Critical Alerts"
              value={overview.criticalAlerts}
              variant="danger"
            />
          </div>
        )}

        {/* Real-time Latency Chart */}
        <div className="mb-8">
          <LatencyChart lastMetric={lastMetric} />
        </div>

        {/* Recent Events */}
        <div className="grid grid-cols-1 lg:grid-cols-2 gap-6">
          {/* Latest Alert */}
          {lastAlert && (
            <div className="bg-slate-800 rounded-lg border border-slate-700 p-6">
              <h3 className="text-lg font-semibold text-white mb-4">Latest Alert</h3>
              <AlertBadge
                severity={lastAlert.severity}
                message={lastAlert.message}
                timestamp={lastAlert.timestamp}
              />
            </div>
          )}

          {/* Latest Health Event */}
          {lastHealthEvent && (
            <div className="bg-slate-800 rounded-lg border border-slate-700 p-6">
              <h3 className="text-lg font-semibold text-white mb-4">Latest Health Event</h3>
              <div className="rounded-lg border border-orange-700 bg-orange-900/50 text-orange-300 p-4">
                <div className="flex items-center justify-between mb-2">
                  <span className="text-xs font-semibold uppercase">Health Degraded</span>
                  <span className="text-xs opacity-75">
                    {new Date(lastHealthEvent.timestamp).toLocaleTimeString()}
                  </span>
                </div>
                <p className="text-sm mb-2">
                  {lastHealthEvent.previousStatus} → {lastHealthEvent.currentStatus}
                </p>
                <p className="text-xs opacity-75">{lastHealthEvent.reason}</p>
              </div>
            </div>
          )}
        </div>
      </div>
    </div>
  );
};
