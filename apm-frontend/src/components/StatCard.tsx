interface StatCardProps {
  title: string;
  value: number;
  subtitle?: string;
  variant?: 'default' | 'success' | 'warning' | 'danger';
}

const variantStyles = {
  default: 'bg-slate-800 border-slate-700',
  success: 'bg-green-900/20 border-green-700',
  warning: 'bg-yellow-900/20 border-yellow-700',
  danger: 'bg-red-900/20 border-red-700',
};

export const StatCard = ({ title, value, subtitle, variant = 'default' }: StatCardProps) => {
  return (
    <div className={`rounded-lg border p-6 ${variantStyles[variant]}`}>
      <h3 className="text-sm font-medium text-slate-400 mb-2">{title}</h3>
      <p className="text-3xl font-bold text-white">{value}</p>
      {subtitle && <p className="text-xs text-slate-500 mt-1">{subtitle}</p>}
    </div>
  );
};
