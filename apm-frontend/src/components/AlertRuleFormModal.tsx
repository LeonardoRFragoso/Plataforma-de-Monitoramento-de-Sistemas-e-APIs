import { useState, useEffect } from 'react';
import { X } from 'lucide-react';
import { useSystems } from '@/hooks/useSystems';
import type { CreateAlertRuleRequest, AlertType, AlertSeverity } from '@/types/alert';

interface AlertRuleFormModalProps {
  isOpen: boolean;
  onClose: () => void;
  onSubmit: (systemId: string, data: CreateAlertRuleRequest) => void;
  isLoading?: boolean;
}

const ALERT_TYPES: { value: AlertType; label: string; description: string }[] = [
  { value: 'LATENCY', label: 'Latency', description: 'Response time threshold' },
  { value: 'ERROR_RATE', label: 'Error Rate', description: 'Error percentage threshold' },
  { value: 'AVAILABILITY', label: 'Availability', description: 'Uptime percentage threshold' },
];

const SEVERITIES: { value: AlertSeverity; label: string; color: string }[] = [
  { value: 'INFO', label: 'Info', color: 'bg-blue-500' },
  { value: 'WARNING', label: 'Warning', color: 'bg-yellow-500' },
  { value: 'CRITICAL', label: 'Critical', color: 'bg-red-500' },
];

export const AlertRuleFormModal = ({
  isOpen,
  onClose,
  onSubmit,
  isLoading = false,
}: AlertRuleFormModalProps) => {
  const { data: systems } = useSystems();
  
  const [formData, setFormData] = useState({
    systemId: '',
    name: '',
    type: 'LATENCY' as AlertType,
    severity: 'WARNING' as AlertSeverity,
    thresholdValue: 1000,
    consecutiveViolations: 3,
  });

  const [errors, setErrors] = useState<Record<string, string>>({});

  useEffect(() => {
    if (isOpen) {
      setFormData({
        systemId: systems?.[0]?.id || '',
        name: '',
        type: 'LATENCY',
        severity: 'WARNING',
        thresholdValue: 1000,
        consecutiveViolations: 3,
      });
      setErrors({});
    }
  }, [isOpen, systems]);

  const validate = (): boolean => {
    const newErrors: Record<string, string> = {};

    if (!formData.systemId) {
      newErrors.systemId = 'System is required';
    }

    if (!formData.name.trim()) {
      newErrors.name = 'Name is required';
    }

    if (formData.thresholdValue <= 0) {
      newErrors.thresholdValue = 'Threshold must be greater than 0';
    }

    if (formData.consecutiveViolations < 1) {
      newErrors.consecutiveViolations = 'Must be at least 1';
    }

    setErrors(newErrors);
    return Object.keys(newErrors).length === 0;
  };

  const handleSubmit = (e: React.FormEvent) => {
    e.preventDefault();
    if (validate()) {
      onSubmit(formData.systemId, {
        name: formData.name,
        type: formData.type,
        severity: formData.severity,
        thresholdValue: formData.thresholdValue,
        consecutiveViolations: formData.consecutiveViolations,
      });
    }
  };

  const handleChange = (field: string, value: string | number) => {
    setFormData((prev) => ({ ...prev, [field]: value }));
    if (errors[field]) {
      setErrors((prev) => {
        const newErrors = { ...prev };
        delete newErrors[field];
        return newErrors;
      });
    }
  };

  const getThresholdLabel = () => {
    switch (formData.type) {
      case 'LATENCY':
        return 'Threshold (ms)';
      case 'ERROR_RATE':
        return 'Threshold (%)';
      case 'AVAILABILITY':
        return 'Min Availability (%)';
      default:
        return 'Threshold';
    }
  };

  if (!isOpen) return null;

  return (
    <div className="fixed inset-0 z-50 flex items-center justify-center">
      <div className="absolute inset-0 bg-black/60 backdrop-blur-sm" onClick={onClose} />
      <div className="relative bg-slate-800 rounded-xl border border-slate-700 shadow-2xl w-full max-w-lg mx-4">
        <div className="flex items-center justify-between p-6 border-b border-slate-700">
          <h2 className="text-xl font-semibold text-white">Create Alert Rule</h2>
          <button
            onClick={onClose}
            className="p-2 hover:bg-slate-700 rounded-lg transition-colors"
          >
            <X className="w-5 h-5 text-slate-400" />
          </button>
        </div>

        <form onSubmit={handleSubmit} className="p-6 space-y-5">
          <div>
            <label className="block text-sm font-medium text-slate-300 mb-2">
              System *
            </label>
            <select
              value={formData.systemId}
              onChange={(e) => handleChange('systemId', e.target.value)}
              className={`w-full px-4 py-3 bg-slate-900 border rounded-lg text-white focus:outline-none focus:ring-2 focus:ring-blue-500 transition-colors ${
                errors.systemId ? 'border-red-500' : 'border-slate-600'
              }`}
            >
              <option value="">Select a system</option>
              {systems?.map((system) => (
                <option key={system.id} value={system.id}>
                  {system.name}
                </option>
              ))}
            </select>
            {errors.systemId && <p className="mt-1 text-sm text-red-400">{errors.systemId}</p>}
          </div>

          <div>
            <label className="block text-sm font-medium text-slate-300 mb-2">
              Rule Name *
            </label>
            <input
              type="text"
              value={formData.name}
              onChange={(e) => handleChange('name', e.target.value)}
              placeholder="e.g., High Latency Alert"
              className={`w-full px-4 py-3 bg-slate-900 border rounded-lg text-white placeholder-slate-500 focus:outline-none focus:ring-2 focus:ring-blue-500 transition-colors ${
                errors.name ? 'border-red-500' : 'border-slate-600'
              }`}
            />
            {errors.name && <p className="mt-1 text-sm text-red-400">{errors.name}</p>}
          </div>

          <div className="grid grid-cols-2 gap-4">
            <div>
              <label className="block text-sm font-medium text-slate-300 mb-2">
                Alert Type *
              </label>
              <select
                value={formData.type}
                onChange={(e) => handleChange('type', e.target.value)}
                className="w-full px-4 py-3 bg-slate-900 border border-slate-600 rounded-lg text-white focus:outline-none focus:ring-2 focus:ring-blue-500 transition-colors"
              >
                {ALERT_TYPES.map((type) => (
                  <option key={type.value} value={type.value}>
                    {type.label}
                  </option>
                ))}
              </select>
            </div>

            <div>
              <label className="block text-sm font-medium text-slate-300 mb-2">
                Severity *
              </label>
              <select
                value={formData.severity}
                onChange={(e) => handleChange('severity', e.target.value)}
                className="w-full px-4 py-3 bg-slate-900 border border-slate-600 rounded-lg text-white focus:outline-none focus:ring-2 focus:ring-blue-500 transition-colors"
              >
                {SEVERITIES.map((sev) => (
                  <option key={sev.value} value={sev.value}>
                    {sev.label}
                  </option>
                ))}
              </select>
            </div>
          </div>

          <div className="grid grid-cols-2 gap-4">
            <div>
              <label className="block text-sm font-medium text-slate-300 mb-2">
                {getThresholdLabel()} *
              </label>
              <input
                type="number"
                min={1}
                value={formData.thresholdValue}
                onChange={(e) => handleChange('thresholdValue', parseInt(e.target.value) || 0)}
                className={`w-full px-4 py-3 bg-slate-900 border rounded-lg text-white focus:outline-none focus:ring-2 focus:ring-blue-500 transition-colors ${
                  errors.thresholdValue ? 'border-red-500' : 'border-slate-600'
                }`}
              />
              {errors.thresholdValue && (
                <p className="mt-1 text-sm text-red-400">{errors.thresholdValue}</p>
              )}
            </div>

            <div>
              <label className="block text-sm font-medium text-slate-300 mb-2">
                Consecutive Violations *
              </label>
              <input
                type="number"
                min={1}
                max={10}
                value={formData.consecutiveViolations}
                onChange={(e) => handleChange('consecutiveViolations', parseInt(e.target.value) || 1)}
                className={`w-full px-4 py-3 bg-slate-900 border rounded-lg text-white focus:outline-none focus:ring-2 focus:ring-blue-500 transition-colors ${
                  errors.consecutiveViolations ? 'border-red-500' : 'border-slate-600'
                }`}
              />
              <p className="mt-1 text-xs text-slate-500">
                Times threshold must be exceeded
              </p>
            </div>
          </div>

          <div className="flex justify-end gap-3 pt-4 border-t border-slate-700">
            <button
              type="button"
              onClick={onClose}
              className="px-5 py-2.5 bg-slate-700 hover:bg-slate-600 text-white rounded-lg font-medium transition-colors"
            >
              Cancel
            </button>
            <button
              type="submit"
              disabled={isLoading}
              className="px-5 py-2.5 bg-blue-600 hover:bg-blue-700 disabled:bg-blue-800 disabled:cursor-not-allowed text-white rounded-lg font-medium transition-colors flex items-center gap-2"
            >
              {isLoading ? (
                <>
                  <div className="w-4 h-4 border-2 border-white/30 border-t-white rounded-full animate-spin" />
                  Creating...
                </>
              ) : (
                'Create Rule'
              )}
            </button>
          </div>
        </form>
      </div>
    </div>
  );
};
