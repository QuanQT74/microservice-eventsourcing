import { apiFetch } from "./client";
import type { Employee } from "@/types";

export const employeesApi = {
  getById: (id: string) => apiFetch<Employee>(`/api/v1/employees/${id}`, { auth: true }),
  getAll: () => apiFetch<Employee[]>("/api/v1/employees", { auth: true }),
};
