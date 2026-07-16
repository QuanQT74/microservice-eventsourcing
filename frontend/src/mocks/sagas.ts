import type { SagaExecution } from "./schema";

export const MOCK_SAGAS: SagaExecution[] = [
  {
    id: "saga-001",
    bookId: "book-123",
    userId: "user-456",
    status: "COMPLETED",
    startTime: "2026-07-10T15:20:00Z",
    endTime: "2026-07-10T15:20:05Z",
    steps: [
      { name: "Reserve Book", status: "SUCCESS", duration: 150 },
      { name: "Check Availability", status: "SUCCESS", duration: 80 },
      { name: "Create Borrowing", status: "SUCCESS", duration: 200 },
    ],
  },
  {
    id: "saga-002",
    bookId: "book-789",
    userId: "user-101",
    status: "ROLLED_BACK",
    startTime: "2026-07-10T15:25:00Z",
    endTime: "2026-07-10T15:25:03Z",
    steps: [
      { name: "Reserve Book", status: "SUCCESS", duration: 120 },
      { name: "Check Availability", status: "FAILED", duration: 90, error: "Book unavailable" },
      { name: "Rollback Reservation", status: "SUCCESS", duration: 100 },
    ],
  },
  {
    id: "saga-003",
    bookId: "book-321",
    userId: "user-789",
    status: "IN_PROGRESS",
    startTime: "2026-07-10T16:10:00Z",
    endTime: "2026-07-10T16:10:00Z",
    steps: [
      { name: "Reserve Book", status: "SUCCESS", duration: 130 },
      { name: "Check Availability", status: "SUCCESS", duration: 70 },
      { name: "Create Borrowing", status: "PENDING", duration: 0 },
    ],
  },
];

export const fetchSagas = (): Promise<SagaExecution[]> =>
  new Promise((resolve) => setTimeout(() => resolve(MOCK_SAGAS), 500));
