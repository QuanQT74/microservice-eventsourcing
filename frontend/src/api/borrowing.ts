import { apiFetch } from "./client";
import type { BorrowingRequest } from "@/types";

export const borrowingApi = {
  create: (data: BorrowingRequest) =>
    apiFetch<string>("/borrowing-api/api/v1/borrowing", {
      method: "POST",
      body: JSON.stringify(data),
    }),
};
