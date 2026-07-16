import { apiFetch } from "./client";
import type { Book } from "@/types";

export const booksApi = {
  getAll: () => apiFetch<Book[]>("/api/v1/books"),
  getById: (id: string) => apiFetch<Book>(`/api/v1/books/${id}`),
};
