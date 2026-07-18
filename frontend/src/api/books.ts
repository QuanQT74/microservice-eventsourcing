import { apiFetch } from "./client";
import type { Book } from "@/types";

export const booksApi = {
  getAll: () => apiFetch<Book[]>("/books"),
  getById: (id: string) => apiFetch<Book>(`/books/${id}`),
};
