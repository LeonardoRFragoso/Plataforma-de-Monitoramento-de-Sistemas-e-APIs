import { useState } from 'react';
import { Modal } from './Modal';
import { useCreateAlertRule } from '@/hooks/useAlerts';
import type { AlertType, AlertSeverity } from '@/types/alert';

interface CreateAlertRuleModalProps {
  isOpen: boolean;
  onClose: () => void;
  systemId: string;
}

export const CreateAlertRuleModal = ({ isOpen, onClose, systemId }: CreateAlertRuleModalProps) => {
  const createMutation = useCreateAlertRule();

  const [formData, setFormData] = useState({
    name: '',
    type: 'LATENCY' as AlertType,
    severity: 'WARNING' as AlertSeverity,
    thresholdValue: 1000,
    consecutiveViolations: 3,
  });

  const [errors, setErrors] = useState<Record<string, string>>({});

  const validate = () => {
    const newErrors: Record<string, string> = {};

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

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();

    if (!validate()) return;

    try {
      await createMutation.mutateAsync({ systemId, data: formData });
      setFormData({
        name: '',
        type: 'LATENCY',
        severity: 'WARNING',
        thresholdValue: 1000,
        consecutiveViolations: 3,
      });
      setErrors({});
      onClose();
    } catch (error) {
      console.error('Failed to create alert rule:', error);
    }
  };

  const handleClose = () => {
    setFormData({
      name: '',
      type: 'LATENCY',
      severity: 'WARNING',
      thresholdValue: 1000,
      consecutiveViolations: 3,
    });
    setErrors({});
    onClose();
  };

  return (
    <Modal isOpen={isOpen} onClose={handleClose} title="Create Alert Rule">
      <form onSubmit={handleSubmit} className="space-y-4">
        <div>
          <label className="block text-sm font-medium text-slate-300 mb-2">Rule Name</label>
          <input
            type="text"
            value={formData.name}
            onChange={(e) => setFormData({ ...formData, name: e.target.value })}
            className="w-full px-4 py-2 bg-slate-700 border border-slate-600 rounded-lg text-white placeholder-slate-400 focus:outline-none focus:ring-2 focus:ring-blue-500"
            placeholder="e.g., High Latency Alert"
          />
          {errors.name && <p className="mt-1 text-sm text-red-400">{errors.name}</p>}
        </div>

        <div>
          <label className="block text-sm font-medium text-slate-300 mb-2">Type</label>
          <select
            value={formData.type}
            onChange={(e) => setFormData({ ...formData, type: e.target.value as AlertType })}
            className="w-full px-4 py-2 bg-slate-700 border border-slate-600 rounded-lg text-white focus:outline-none focus:ring-2 focus:ring-blue-500"
          >
            <option value="LATENCY">Latency</option>
            <option value="ERROR_RATE">Error Rate</option>
            <option value="AVAILABILITY">Availability</option>
          </select>
        </div>

        <div>
          <label className="block text-sm font-medium text-slate-300 mb-2">Severity</label>
          <select
            value={formData.severity}
            onChange={(e) => setFormData({ ...formData, severity: e.target.value as AlertSeverity })}
            className="w-full px-4 py-2 bg-slate-700 border border-slate-600 rounded-lg text-white focus:outline-none focus:ring-2 focus:ring-blue-500"
          >
            <option value="INFO">Info</option>
            <option value="WARNING">Warning</option>
            <option value="CRITICAL">Critical</option>
          </select>
        </div>

        <div>
          <label className="block text-sm font-medium text-slate-300 mb-2">
            Threshold Value {formData.type === 'LATENCY' ? '(ms)' : '(%)'}
          </label>
          <input
            type="number"
            value={formData.thresholdValue}
            onChange={(e) => setFormData({ ...formData, thresholdValue: Number(e.target.value) })}
            className="w-full px-4 py-2 bg-slate-700 border border-slate-600 rounded-lg text-white placeholder-slate-400 focus:outline-none focus:ring-2 focus:ring-blue-500"
            min="1"
          />
          {errors.thresholdValue && <p className="mt-1 text-sm text-red-400">{errors.thresholdValue}</p>}
        </div>

        <div>
          <label className="block text-sm font-medium text-slate-300 mb-2">Consecutive Violations</label>
          <input
            type="number"
            value={formData.consecutiveViolations}
            onChange={(e) => setFormData({ ...formData, consecutiveViolations: Number(e.target.value) })}
            className="w-full px-4 py-2 bg-slate-700 border border-slate-600 rounded-lg text-white placeholder-slate-400 focus:outline-none focus:ring-2 focus:ring-blue-500"
            min="1"
          />
          {errors.consecutiveViolations && <p className="mt-1 text-sm text-red-400">{errors.consecutiveViolations}</p>}
          <p className="mt-1 text-xs text-slate-400">
            Number of consecutive violations before triggering the alert
          </p>
        </div>

        <div className="flex gap-3 justify-end pt-4 border-t border-slate-700">
          <button
            type="button"
            onClick={handleClose}
            disabled={createMutation.isPending}
            className="px-4 py-2 bg-slate-700 hover:bg-slate-600 text-white rounded-lg font-medium transition-colors disabled:opacity-50"
          >
            Cancel
          </button>
          <button
            type="submit"
            disabled={createMutation.isPending}
            className="px-4 py-2 bg-blue-600 hover:bg-blue-700 text-white rounded-lg font-medium transition-colors disabled:opacity-50"
          >
            {createMutation.isPending ? 'Creating...' : 'Create Rule'}
          </button>
        </div>
      </form>
    </Modal>
  );
};
