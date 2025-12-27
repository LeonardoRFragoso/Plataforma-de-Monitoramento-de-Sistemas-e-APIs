import type { AlertSeverity } from '@/types/alert';

interface SeverityBadgeProps {
  severity: AlertSeverity;
}

export const SeverityBadge = ({ severity }: SeverityBadgeProps) => {
  const variants = {
    INFO: 'bg-blue-900/50 border-blue-700 text-blue-300',
    WARNING: 'bg-orange-900/50 border-orange-700 text-orange-300',
    CRITICAL: 'bg-red-900/50 border-red-700 text-red-300',
  };

  return (
    <span className={`inline-flex items-center px-3 py-1 rounded-lg border text-xs font-semibold uppercase ${variants[severity]}`}>
      {severity}
    </span>
  );
};
