interface AlertBadgeProps {
  severity: 'INFO' | 'WARNING' | 'CRITICAL';
  message: string;
  timestamp: string;
}

const severityStyles = {
  INFO: 'bg-blue-900/50 text-blue-300 border-blue-700',
  WARNING: 'bg-yellow-900/50 text-yellow-300 border-yellow-700',
  CRITICAL: 'bg-red-900/50 text-red-300 border-red-700',
};

export const AlertBadge = ({ severity, message, timestamp }: AlertBadgeProps) => {
  const time = new Date(timestamp).toLocaleTimeString();

  return (
    <div className={`rounded-lg border p-4 ${severityStyles[severity]}`}>
      <div className="flex items-center justify-between mb-2">
        <span className="text-xs font-semibold uppercase">{severity}</span>
        <span className="text-xs opacity-75">{time}</span>
      </div>
      <p className="text-sm">{message}</p>
    </div>
  );
};
