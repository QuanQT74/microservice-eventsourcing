import { apiFetch } from "./client";
import type { BorrowingRequest } from "@/types";

export const borrowingApi = {
  create: (data: BorrowingRequest) => {
    console.log("[BORROWING API] Creating borrowing with data:", data);
    return apiFetch<{ success: boolean; borrowingId: string }>("/borrowing", {
      method: "POST",
      body: JSON.stringify(data),
    });
  },
  getMyBorrows: (employeeId: string) => 
    apiFetch<any[]>(`/borrowings/user/${employeeId}`),
  returnBook: (borrowingId: string, bookId: string) =>
    apiFetch<{ success: boolean }>("/borrowing/return", {
      method: "POST",
      body: JSON.stringify({ id: borrowingId, bookId }),
    }),
  delete: (borrowingId: string) =>
    apiFetch<{ success: boolean }>(`/borrowing/${borrowingId}`, {
      method: "DELETE",
    }),
};
