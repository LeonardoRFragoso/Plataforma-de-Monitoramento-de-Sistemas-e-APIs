import { useEffect, useState } from 'react';
import { LineChart, Line, XAxis, YAxis, CartesianGrid, Tooltip, ResponsiveContainer } from 'recharts';
import type { MetricEvent } from '@/types/dashboard';

interface LatencyChartProps {
  lastMetric: MetricEvent | null;
}

interface DataPoint {
  time: string;
  latency: number;
}

const MAX_POINTS = 20;

export const LatencyChart = ({ lastMetric }: LatencyChartProps) => {
  const [data, setData] = useState<DataPoint[]>([]);

  useEffect(() => {
    if (lastMetric) {
      const timestamp = new Date(lastMetric.timestamp);
      const timeStr = `${timestamp.getHours()}:${timestamp.getMinutes()}:${timestamp.getSeconds()}`;

      setData((prev) => {
        const newData = [
          ...prev,
          {
            time: timeStr,
            latency: lastMetric.latencyMs,
          },
        ];

        // Keep only the last MAX_POINTS
        return newData.slice(-MAX_POINTS);
      });
    }
  }, [lastMetric]);

  return (
    <div className="bg-slate-800 rounded-lg border border-slate-700 p-6">
      <h3 className="text-lg font-semibold text-white mb-4">Real-time Latency (ms)</h3>
      
      {data.length === 0 ? (
        <div className="h-64 flex items-center justify-center text-slate-500">
          Waiting for metrics...
        </div>
      ) : (
        <ResponsiveContainer width="100%" height={300}>
          <LineChart data={data}>
            <CartesianGrid strokeDasharray="3 3" stroke="#374151" />
            <XAxis 
              dataKey="time" 
              stroke="#9ca3af"
              tick={{ fill: '#9ca3af' }}
            />
            <YAxis 
              stroke="#9ca3af"
              tick={{ fill: '#9ca3af' }}
            />
            <Tooltip
              contentStyle={{
                backgroundColor: '#1e293b',
                border: '1px solid #475569',
                borderRadius: '0.5rem',
              }}
              labelStyle={{ color: '#e2e8f0' }}
              itemStyle={{ color: '#22d3ee' }}
            />
            <Line
              type="monotone"
              dataKey="latency"
              stroke="#22d3ee"
              strokeWidth={2}
              dot={{ fill: '#22d3ee', r: 4 }}
              animationDuration={500}
            />
          </LineChart>
        </ResponsiveContainer>
      )}
    </div>
  );
};
