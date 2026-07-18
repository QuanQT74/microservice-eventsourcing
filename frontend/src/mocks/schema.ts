export interface Service {
  id: string;
  name: string;
  status: "UP" | "DOWN" | "STARTING" | "STOPPING" | "UNKNOWN";
  instances: number;
  health: "HEALTHY" | "UNHEALTHY";
  port: number;
  lastHeartbeat: string;
  endpoints: string[];
  namespace?: string;
}

export interface SagaStep {
  name: string;
  status: "SUCCESS" | "FAILED" | "PENDING";
  duration: number;
  error?: string;
}

export interface SagaExecution {
  id: string;
  bookId: string;
  userId: string;
  status: "COMPLETED" | "ROLLED_BACK" | "IN_PROGRESS";
  startTime: string;
  endTime: string;
  steps: SagaStep[];
}

export interface KafkaEvent {
  id: string;
  topic: string;
  key: string;
  payload: Record<string, unknown>;
  timestamp: string;
  partition: number;
  offset: number;
}
