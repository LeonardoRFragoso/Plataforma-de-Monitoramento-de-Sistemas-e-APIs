import { useEffect, useRef, useState } from 'react';
import type { WebSocketEvent, MetricEvent, AlertEvent, HealthEvent } from '@/types/dashboard';

interface UseWebSocketReturn {
  connected: boolean;
  lastMetric: MetricEvent | null;
  lastAlert: AlertEvent | null;
  lastHealthEvent: HealthEvent | null;
}

// WebSocket URL configuration
const getWebSocketURL = (): string => {
  // Allow override via environment variable
  if (import.meta.env.VITE_WS_URL) {
    return import.meta.env.VITE_WS_URL;
  }

  // Development: connect directly to backend
  if (import.meta.env.DEV) {
    return 'ws://localhost:8080/ws/dashboard';
  }

  // Production: use relative WebSocket URL (nginx proxies /ws -> backend:8080)
  const protocol = window.location.protocol === 'https:' ? 'wss:' : 'ws:';
  return `${protocol}//${window.location.host}/ws/dashboard`;
};

const WS_URL = getWebSocketURL();

export const useDashboardWebSocket = (): UseWebSocketReturn => {
  const ws = useRef<WebSocket | null>(null);
  const [connected, setConnected] = useState(false);
  const [lastMetric, setLastMetric] = useState<MetricEvent | null>(null);
  const [lastAlert, setLastAlert] = useState<AlertEvent | null>(null);
  const [lastHealthEvent, setLastHealthEvent] = useState<HealthEvent | null>(null);

  useEffect(() => {
    const connect = () => {
      try {
        ws.current = new WebSocket(WS_URL);

        ws.current.onopen = () => {
          console.log('WebSocket connected');
          setConnected(true);
        };

        ws.current.onmessage = (event) => {
          try {
            const data: WebSocketEvent = JSON.parse(event.data);

            switch (data.type) {
              case 'METRIC_COLLECTED':
                setLastMetric(data);
                break;
              case 'ALERT_TRIGGERED':
                setLastAlert(data);
                break;
              case 'HEALTH_DEGRADED':
                setLastHealthEvent(data);
                break;
            }
          } catch (error) {
            console.error('Error parsing WebSocket message:', error);
          }
        };

        ws.current.onerror = (error) => {
          console.error('WebSocket error:', error);
        };

        ws.current.onclose = () => {
          console.log('WebSocket disconnected');
          setConnected(false);
          
          // Reconnect after 5 seconds
          setTimeout(() => {
            connect();
          }, 5000);
        };
      } catch (error) {
        console.error('Error connecting to WebSocket:', error);
      }
    };

    connect();

    return () => {
      if (ws.current) {
        ws.current.close();
      }
    };
  }, []);

  return {
    connected,
    lastMetric,
    lastAlert,
    lastHealthEvent,
  };
};
