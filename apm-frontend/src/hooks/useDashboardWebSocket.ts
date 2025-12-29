import { useEffect, useRef, useState } from 'react';
import { Client, type IMessage } from '@stomp/stompjs';
import SockJS from 'sockjs-client';
import type { MetricEvent, AlertEvent, HealthEvent } from '@/types/dashboard';

interface UseWebSocketReturn {
  connected: boolean;
  lastMetric: MetricEvent | null;
  lastAlert: AlertEvent | null;
  lastHealthEvent: HealthEvent | null;
}

const getWebSocketUrl = (): string => {
  if (import.meta.env.VITE_WS_URL) {
    return import.meta.env.VITE_WS_URL;
  }
  
  // Use relative path - nginx will proxy to backend
  const protocol = window.location.protocol === 'https:' ? 'https:' : 'http:';
  const host = window.location.host;
  return `${protocol}//${host}/ws/dashboard`;
};

export const useDashboardWebSocket = (): UseWebSocketReturn => {
  const stompClient = useRef<Client | null>(null);
  const [connected, setConnected] = useState(false);
  const [lastMetric, setLastMetric] = useState<MetricEvent | null>(null);
  const [lastAlert, setLastAlert] = useState<AlertEvent | null>(null);
  const [lastHealthEvent, setLastHealthEvent] = useState<HealthEvent | null>(null);

  useEffect(() => {
    const connect = () => {
      const client = new Client({
        webSocketFactory: () => new SockJS(getWebSocketUrl()),
        reconnectDelay: 5000,
        heartbeatIncoming: 4000,
        heartbeatOutgoing: 4000,
        debug: (str: string) => {
          console.log('STOMP: ' + str);
        },
        onConnect: () => {
          console.log('STOMP connected');
          setConnected(true);

          client.subscribe('/topic/dashboard/metrics', (message: IMessage) => {
            try {
              const data: MetricEvent = JSON.parse(message.body);
              setLastMetric(data);
            } catch (error) {
              console.error('Error parsing metric message:', error);
            }
          });

          client.subscribe('/topic/dashboard/alerts', (message: IMessage) => {
            try {
              const data: AlertEvent = JSON.parse(message.body);
              setLastAlert(data);
            } catch (error) {
              console.error('Error parsing alert message:', error);
            }
          });

          client.subscribe('/topic/dashboard/health', (message: IMessage) => {
            try {
              const data: HealthEvent = JSON.parse(message.body);
              setLastHealthEvent(data);
            } catch (error) {
              console.error('Error parsing health message:', error);
            }
          });
        },
        onStompError: (frame: any) => {
          console.error('STOMP error:', frame.headers['message']);
          console.error('Details:', frame.body);
        },
        onWebSocketClose: () => {
          console.log('WebSocket disconnected');
          setConnected(false);
        },
        onWebSocketError: (error: any) => {
          console.error('WebSocket error:', error);
        },
      });

      stompClient.current = client;
      client.activate();
    };

    connect();

    return () => {
      if (stompClient.current) {
        stompClient.current.deactivate();
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
