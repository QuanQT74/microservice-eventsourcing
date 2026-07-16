import type { LocalBorrowing } from "@/types";

const STORAGE_KEY = "library_borrowings";

export function getBorrowings(): LocalBorrowing[] {
  try {
    const raw = localStorage.getItem(STORAGE_KEY);
    return raw ? (JSON.parse(raw) as LocalBorrowing[]) : [];
  } catch {
    return [];
  }
}

export function saveBorrowing(borrowing: LocalBorrowing): void {
  const list = getBorrowings();
  list.unshift(borrowing);
  localStorage.setItem(STORAGE_KEY, JSON.stringify(list));
}

export function markReturned(id: string): void {
  const list = getBorrowings().map((b) =>
    b.id === id ? { ...b, status: "RETURNED" as const } : b
  );
  localStorage.setItem(STORAGE_KEY, JSON.stringify(list));
}

export function getActiveBorrowings(employeeId: string): LocalBorrowing[] {
  return getBorrowings().filter((b) => b.employeeId === employeeId && b.status === "ACTIVE");
}

export function getBorrowingHistory(employeeId: string): LocalBorrowing[] {
  return getBorrowings().filter((b) => b.employeeId === employeeId && b.status === "RETURNED");
}
