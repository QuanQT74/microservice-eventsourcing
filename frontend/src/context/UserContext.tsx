import { createContext, useContext, useState, useEffect, type ReactNode } from "react";
import type { Employee } from "@/types";
import { employeesApi } from "@/api/employees";
import { parseJwt } from "@/api/auth";

interface UserContextValue {
  employeeId: string | null;
  employee: Employee | null;
  email: string | null;
  isLoading: boolean;
  isAuthenticated: boolean;
  accessToken: string | null;
  setEmployeeId: (id: string) => Promise<void>;
  setEmail: (email: string | null) => void;
  logout: () => void;
}

const UserContext = createContext<UserContextValue | null>(null);

const STORAGE_KEY = "library_member_id";
const TOKEN_KEY = "access_token";
const EMAIL_KEY = "library_user_email";

function isTokenValid(token: string | null): boolean {
  if (!token) return false;
  const payload = parseJwt(token);
  if (!payload) return false;
  const now = Date.now() / 1000;
  return (payload.exp || 0) > now;
}

export function UserProvider({ children }: { children: ReactNode }) {
  const [employeeId, setEmployeeIdState] = useState<string | null>(() => {
    if (!isTokenValid(localStorage.getItem(TOKEN_KEY))) {
      localStorage.removeItem(TOKEN_KEY);
      return null;
    }
    return localStorage.getItem(STORAGE_KEY);
  });
  const [email, setEmailState] = useState<string | null>(() => {
    if (!isTokenValid(localStorage.getItem(TOKEN_KEY))) {
      localStorage.removeItem(EMAIL_KEY);
      return null;
    }
    return localStorage.getItem(EMAIL_KEY);
  });
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
      .catch(() => {
        setEmployee(null);
        localStorage.removeItem(STORAGE_KEY);
        setEmployeeIdState(null);
      })
      .finally(() => setIsLoading(false));
  }, [employeeId]);

  const setEmployeeId = async (id: string) => {
    const trimmed = id.trim();
    const emp = await employeesApi.getById(trimmed);
    localStorage.setItem(STORAGE_KEY, trimmed);
    setEmployeeIdState(trimmed);
    setEmployee(emp);
  };

  const setEmail = (value: string | null) => {
    if (value) {
      localStorage.setItem(EMAIL_KEY, value);
      setEmailState(value);
    } else {
      localStorage.removeItem(EMAIL_KEY);
      setEmailState(null);
    }
  };

  const logout = () => {
    localStorage.removeItem(STORAGE_KEY);
    localStorage.removeItem(TOKEN_KEY);
    localStorage.removeItem(EMAIL_KEY);
    localStorage.removeItem("library_borrowings");
    setEmployeeIdState(null);
    setEmailState(null);
    setEmployee(null);
  };

  return (
    <UserContext.Provider
      value={{
        employeeId,
        employee,
        email,
        isLoading,
        isAuthenticated: !!employeeId && !!employee,
        accessToken: localStorage.getItem(TOKEN_KEY),
        setEmployeeId,
        setEmail,
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
