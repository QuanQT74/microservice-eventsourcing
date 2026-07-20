import { apiFetch } from "./client";
import type { BorrowingRequest, BorrowingResponse } from "@/types";

export const borrowingApi = {
  create: (data: BorrowingRequest) => {
    return apiFetch<{ success: boolean; borrowingId: string }>("/borrowing", {
      method: "POST",
      body: JSON.stringify(data),
    });
  },
  /** Chỉ lấy borrowings của user đang đăng nhập */
  getMyBorrows: (_employeeId?: string) => {
    const employeeId = _employeeId || localStorage.getItem("library_member_id") || "";
    return apiFetch<BorrowingResponse[]>("/borrowings/me", {
      headers: employeeId ? { "X-Employee-Id": employeeId } : {},
    });
  },
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
