export interface Metrics {
  totalServices: number;
  servicesUp: number;
  servicesDown: number;
  totalRequests: number;
  successRate: number;
  avgResponseTime: number;
}

export const MOCK_METRICS: Metrics = {
  totalServices: 5,
  servicesUp: 4,
  servicesDown: 1,
  totalRequests: 1250000,
  successRate: 99.2,
  avgResponseTime: 120,
};

export const fetchMetrics = (): Promise<Metrics> =>
  new Promise((resolve) => setTimeout(() => resolve(MOCK_METRICS), 500));
