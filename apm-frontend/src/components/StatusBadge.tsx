interface StatusBadgeProps {
  status: 'UP' | 'DEGRADED' | 'DOWN';
}

export const StatusBadge = ({ status }: StatusBadgeProps) => {
  const variants = {
    UP: 'bg-green-900/50 border-green-700 text-green-300',
    DEGRADED: 'bg-orange-900/50 border-orange-700 text-orange-300',
    DOWN: 'bg-red-900/50 border-red-700 text-red-300',
  };

  const dots = {
    UP: 'bg-green-500',
    DEGRADED: 'bg-orange-500',
    DOWN: 'bg-red-500',
  };

  return (
    <span className={`inline-flex items-center gap-2 px-3 py-1 rounded-lg border text-xs font-semibold uppercase ${variants[status]}`}>
      <span className={`w-2 h-2 rounded-full ${dots[status]} animate-pulse`} />
      {status}
    </span>
  );
};
