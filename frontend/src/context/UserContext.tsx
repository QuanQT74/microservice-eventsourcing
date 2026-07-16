import { createContext, useContext, useState, useEffect, type ReactNode } from "react";
import type { Employee } from "@/types";
import { employeesApi } from "@/api/employees";

interface UserContextValue {
  employeeId: string | null;
  employee: Employee | null;
  isLoading: boolean;
  isAuthenticated: boolean;
  setEmployeeId: (id: string) => Promise<void>;
  login: (userId: string) => void;
  logout: () => void;
}

const UserContext = createContext<UserContextValue | null>(null);

const STORAGE_KEY = "library_member_id";
const TOKEN_KEY = "access_token";

export function UserProvider({ children }: { children: ReactNode }) {
  const [employeeId, setEmployeeIdState] = useState<string | null>(() =>
    localStorage.getItem(STORAGE_KEY)
  );
  const [employee, setEmployee] = useState<Employee | null>(null);
  const [isLoading, setIsLoading] = useState(!!employeeId);

  useEffect(() => {
    if (!employeeId) {
      setEmployee(null);
      setIsLoading(false);
      return;
    }

    setIsLoading(true);
    employeesApi
      .getById(employeeId)
      .then(setEmployee)
      .catch(() => setEmployee(null))
      .finally(() => setIsLoading(false));
  }, [employeeId]);

  const login = (userId: string) => {
    localStorage.setItem(STORAGE_KEY, userId);
    setEmployeeIdState(userId);
  };

  const setEmployeeId = async (id: string) => {
    const trimmed = id.trim();
    const emp = await employeesApi.getById(trimmed);
    localStorage.setItem(STORAGE_KEY, trimmed);
    setEmployeeIdState(trimmed);
    setEmployee(emp);
  };

  const logout = () => {
    localStorage.removeItem(STORAGE_KEY);
    localStorage.removeItem(TOKEN_KEY);
    setEmployeeIdState(null);
    setEmployee(null);
  };

  return (
    <UserContext.Provider
      value={{
        employeeId,
        employee,
        isLoading,
        isAuthenticated: !!employeeId && !!employee,
        setEmployeeId,
        login,
        logout,
      }}
    >
      {children}
    </UserContext.Provider>
  );
}

export function useUser() {
  const ctx = useContext(UserContext);
  if (!ctx) throw new Error("useUser must be used within UserProvider");
  return ctx;
}

export function getToken(): string | null {
  return localStorage.getItem(TOKEN_KEY);
}

export function setToken(token: string): void {
  localStorage.setItem(TOKEN_KEY, token);
}
