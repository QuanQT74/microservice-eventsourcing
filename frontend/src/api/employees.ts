import { apiFetch } from "./client";
import type { Employee } from "@/types";

export const employeesApi = {
  getById: (id: string) => apiFetch<Employee>(`/employees/${id}`),
  getAll: () => apiFetch<Employee[]>("/employees"),
};
