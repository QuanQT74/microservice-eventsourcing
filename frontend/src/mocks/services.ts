import type { Service } from "./schema";

export type { Service };

export const MOCK_SERVICES: Service[] = [
  {
    id: "book-service",
    name: "Book Service",
    status: "UP",
    instances: 2,
    health: "HEALTHY",
    port: 8081,
    lastHeartbeat: "2026-07-10T16:30:00Z",
    endpoints: ["/api/books", "/api/books/{id}"],
  },
  {
    id: "borrowing-service",
    name: "Borrowing Service",
    status: "UP",
    instances: 3,
    health: "HEALTHY",
    port: 8082,
    lastHeartbeat: "2026-07-10T16:31:00Z",
    endpoints: ["/api/borrowing", "/api/borrowing/saga"],
  },
  {
    id: "notification-service",
    name: "Notification Service",
    status: "UP",
    instances: 1,
    health: "HEALTHY",
    port: 8083,
    lastHeartbeat: "2026-07-10T16:29:55Z",
    endpoints: ["/api/notifications"],
  },
  {
    id: "email-service",
    name: "Email Service",
    status: "UP",
    instances: 2,
    health: "HEALTHY",
    port: 8084,
    lastHeartbeat: "2026-07-10T16:31:10Z",
    endpoints: ["/api/email", "/api/email/templates"],
  },
  {
    id: "api-gateway",
    name: "API Gateway",
    status: "UP",
    instances: 2,
    health: "HEALTHY",
    port: 8080,
    lastHeartbeat: "2026-07-10T16:31:15Z",
    endpoints: ["/api/v1/*"],
  },
];

export const fetchServices = (): Promise<Service[]> =>
  new Promise((resolve) => setTimeout(() => resolve(MOCK_SERVICES), 500));
