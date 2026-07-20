import { apiFetch } from "./client";
import type { Employee } from "@/types";

function normalizeEmployee(raw: Record<string, unknown>): Employee {
  return {
    id: String(raw.id ?? ""),
    firstName: String(raw.firstName ?? ""),
    lastName: String(raw.lastName ?? ""),
    // Jackson serialize field "Kin" → JSON key "kin"
    Kin: String(raw.Kin ?? raw.kin ?? ""),
    isDisciplined: Boolean(raw.isDisciplined),
    memberCode: raw.memberCode != null ? String(raw.memberCode) : undefined,
  };
}

export const employeesApi = {
  getById: async (id: string) => {
    const raw = await apiFetch<Record<string, unknown>>(`/employees/${id}`);
    return normalizeEmployee(raw);
  },
  getAll: async () => {
    const list = await apiFetch<Record<string, unknown>[]>("/employees");
    return (list ?? []).map(normalizeEmployee);
  },
};
