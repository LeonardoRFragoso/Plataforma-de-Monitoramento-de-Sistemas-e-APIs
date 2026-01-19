import { useState, useEffect } from 'react';
import { X } from 'lucide-react';
import type { MonitoredSystem, RegisterSystemRequest } from '@/types/system';

interface SystemFormModalProps {
  isOpen: boolean;
  onClose: () => void;
  onSubmit: (data: RegisterSystemRequest) => void;
  initialData?: MonitoredSystem | null;
  isLoading?: boolean;
  title: string;
}

const SYSTEM_TYPES = ['API', 'SERVICE', 'JOB', 'MICROSERVICE', 'MONOLITH'];
const ENVIRONMENTS = ['DEVELOPMENT', 'STAGING', 'PRODUCTION'];

export const SystemFormModal = ({
  isOpen,
  onClose,
  onSubmit,
  initialData,
  isLoading = false,
  title,
}: SystemFormModalProps) => {
  const [formData, setFormData] = useState<RegisterSystemRequest>({
    name: '',
    baseUrl: '',
    type: 'API',
    environment: 'PRODUCTION',
    collectionIntervalSeconds: 30,
  });

  const [errors, setErrors] = useState<Record<string, string>>({});

  useEffect(() => {
    if (initialData) {
      setFormData({
        name: initialData.name,
        baseUrl: initialData.baseUrl,
        type: initialData.type,
        environment: initialData.environment,
        collectionIntervalSeconds: initialData.collectionIntervalSeconds,
      });
    } else {
      setFormData({
        name: '',
        baseUrl: '',
        type: 'API',
        environment: 'PRODUCTION',
        collectionIntervalSeconds: 30,
      });
    }
    setErrors({});
  }, [initialData, isOpen]);

  const validate = (): boolean => {
    const newErrors: Record<string, string> = {};

    if (!formData.name.trim()) {
      newErrors.name = 'Name is required';
    } else if (formData.name.length > 100) {
      newErrors.name = 'Name must not exceed 100 characters';
    }

    if (!formData.baseUrl.trim()) {
      newErrors.baseUrl = 'Base URL is required';
    } else if (!formData.baseUrl.startsWith('http://') && !formData.baseUrl.startsWith('https://')) {
      newErrors.baseUrl = 'URL must start with http:// or https://';
    }

    if (formData.collectionIntervalSeconds < 10) {
      newErrors.collectionIntervalSeconds = 'Interval must be at least 10 seconds';
    } else if (formData.collectionIntervalSeconds > 86400) {
      newErrors.collectionIntervalSeconds = 'Interval cannot exceed 24 hours';
    }

    setErrors(newErrors);
    return Object.keys(newErrors).length === 0;
  };

  const handleSubmit = (e: React.FormEvent) => {
    e.preventDefault();
    if (validate()) {
      onSubmit(formData);
    }
  };

  const handleChange = (field: keyof RegisterSystemRequest, value: string | number) => {
    setFormData((prev) => ({ ...prev, [field]: value }));
    if (errors[field]) {
      setErrors((prev) => {
        const newErrors = { ...prev };
        delete newErrors[field];
        return newErrors;
      });
    }
  };

  if (!isOpen) return null;

  return (
    <div className="fixed inset-0 z-50 flex items-center justify-center">
      <div className="absolute inset-0 bg-black/60 backdrop-blur-sm" onClick={onClose} />
      <div className="relative bg-slate-800 rounded-xl border border-slate-700 shadow-2xl w-full max-w-lg mx-4">
        <div className="flex items-center justify-between p-6 border-b border-slate-700">
          <h2 className="text-xl font-semibold text-white">{title}</h2>
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
              System Name *
            </label>
            <input
              type="text"
              value={formData.name}
              onChange={(e) => handleChange('name', e.target.value)}
              placeholder="e.g., Payment Service"
              className={`w-full px-4 py-3 bg-slate-900 border rounded-lg text-white placeholder-slate-500 focus:outline-none focus:ring-2 focus:ring-blue-500 transition-colors ${
                errors.name ? 'border-red-500' : 'border-slate-600'
              }`}
            />
            {errors.name && <p className="mt-1 text-sm text-red-400">{errors.name}</p>}
          </div>

          <div>
            <label className="block text-sm font-medium text-slate-300 mb-2">
              Base URL *
            </label>
            <input
              type="text"
              value={formData.baseUrl}
              onChange={(e) => handleChange('baseUrl', e.target.value)}
              placeholder="e.g., https://api.example.com"
              className={`w-full px-4 py-3 bg-slate-900 border rounded-lg text-white placeholder-slate-500 focus:outline-none focus:ring-2 focus:ring-blue-500 transition-colors ${
                errors.baseUrl ? 'border-red-500' : 'border-slate-600'
              }`}
            />
            {errors.baseUrl && <p className="mt-1 text-sm text-red-400">{errors.baseUrl}</p>}
          </div>

          <div className="grid grid-cols-2 gap-4">
            <div>
              <label className="block text-sm font-medium text-slate-300 mb-2">
                Type *
              </label>
              <select
                value={formData.type}
                onChange={(e) => handleChange('type', e.target.value)}
                className="w-full px-4 py-3 bg-slate-900 border border-slate-600 rounded-lg text-white focus:outline-none focus:ring-2 focus:ring-blue-500 transition-colors"
              >
                {SYSTEM_TYPES.map((type) => (
                  <option key={type} value={type}>
                    {type}
                  </option>
                ))}
              </select>
            </div>

            <div>
              <label className="block text-sm font-medium text-slate-300 mb-2">
                Environment *
              </label>
              <select
                value={formData.environment}
                onChange={(e) => handleChange('environment', e.target.value)}
                className="w-full px-4 py-3 bg-slate-900 border border-slate-600 rounded-lg text-white focus:outline-none focus:ring-2 focus:ring-blue-500 transition-colors"
              >
                {ENVIRONMENTS.map((env) => (
                  <option key={env} value={env}>
                    {env}
                  </option>
                ))}
              </select>
            </div>
          </div>

          <div>
            <label className="block text-sm font-medium text-slate-300 mb-2">
              Collection Interval (seconds) *
            </label>
            <input
              type="number"
              min={10}
              max={86400}
              value={formData.collectionIntervalSeconds}
              onChange={(e) => handleChange('collectionIntervalSeconds', parseInt(e.target.value) || 30)}
              className={`w-full px-4 py-3 bg-slate-900 border rounded-lg text-white focus:outline-none focus:ring-2 focus:ring-blue-500 transition-colors ${
                errors.collectionIntervalSeconds ? 'border-red-500' : 'border-slate-600'
              }`}
            />
            {errors.collectionIntervalSeconds && (
              <p className="mt-1 text-sm text-red-400">{errors.collectionIntervalSeconds}</p>
            )}
            <p className="mt-1 text-xs text-slate-500">
              How often to collect metrics (10s - 24h)
            </p>
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
                  Saving...
                </>
              ) : (
                'Save System'
              )}
            </button>
          </div>
        </form>
      </div>
    </div>
  );
};
