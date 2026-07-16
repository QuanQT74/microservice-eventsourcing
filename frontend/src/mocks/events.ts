import type { KafkaEvent } from "./schema";

export const MOCK_EVENTS: KafkaEvent[] = [
  {
    id: "evt-001",
    topic: "book-events",
    key: "book-reserved",
    payload: { bookId: "book-123", userId: "user-456" },
    timestamp: "2026-07-10T16:30:00Z",
    partition: 0,
    offset: 12345,
  },
  {
    id: "evt-002",
    topic: "borrowing-events",
    key: "borrowing-created",
    payload: { borrowingId: "bor-789", bookId: "book-123", dueDate: "2026-07-24" },
    timestamp: "2026-07-10T16:30:05Z",
    partition: 1,
    offset: 6789,
  },
];

export const fetchEvents = (): Promise<KafkaEvent[]> =>
  new Promise((resolve) => setTimeout(() => resolve(MOCK_EVENTS), 500));
