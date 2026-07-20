import { setToken } from "@/context/UserContext";

const KEYCLOAK_URL = "http://localhost:8180/realms/laptrinhfullstack";
const CLIENT_ID = "ltfullstack_app";
const REDIRECT_URI = encodeURIComponent("http://localhost:5173");
const SCOPE = encodeURIComponent("openid profile email");

export interface LoginRequest {
  username: string;
  password: string;
}

export interface RegisterRequest {
  username: string;
  email: string;
  password: string;
  fullName: string;
}

export interface AuthResponse {
  access_token: string;
  token_type: string;
  expires_in: number;
  refresh_token?: string;
}

export interface UserResponse {
  id: string;
  username: string;
  email: string;
  fullName: string;
  isActive: boolean;
  createdAt: string;
  memberCode?: string;
  employeeId?: string;
}

export async function getUserByUsername(username: string): Promise<UserResponse> {
  const response = await fetch(`http://localhost:8085/api/v1/users/me/${username}`, {
    headers: {
      Authorization: `Bearer ${localStorage.getItem("access_token")}`,
    },
  });
  if (!response.ok) {
    throw new Error("Failed to get user info");
  }
  const user = await response.json();
  
  // Fallback: get employeeId separately if not in response
  if (!user.employeeId) {
    try {
      const empResponse = await fetch(`http://localhost:8085/api/v1/users/${user.id}/employee-id`, {
        headers: {
          Authorization: `Bearer ${localStorage.getItem("access_token")}`,
        },
      });
      if (empResponse.ok) {
        user.employeeId = await empResponse.text();
      }
    } catch (e) {
      console.error("[auth] Failed to get employeeId:", e);
    }
  }
  
  return user;
}

export function getKeycloakAuthUrl(): string {
  return `${KEYCLOAK_URL}/protocol/openid-connect/auth?client_id=${CLIENT_ID}&redirect_uri=${REDIRECT_URI}&response_type=code&scope=${SCOPE}`;
}

export function getLogoutUrl(): string {
  const redirect = encodeURIComponent("http://localhost:5173");
  return `${KEYCLOAK_URL}/protocol/openid-connect/logout?redirect_uri=${redirect}`;
}

export async function exchangeCodeForToken(code: string): Promise<AuthResponse> {
  const response = await fetch(`${KEYCLOAK_URL}/protocol/openid-connect/token`, {
    method: "POST",
    headers: { "Content-Type": "application/x-www-form-urlencoded" },
    body: new URLSearchParams({
      grant_type: "authorization_code",
      client_id: CLIENT_ID,
      code,
      redirect_uri: REDIRECT_URI,
    }),
  });

  if (!response.ok) {
    throw new Error("Failed to exchange code for token");
  }

  const data = await response.json();
  setToken(data.access_token);
  return data;
}

export async function login(data: LoginRequest): Promise<AuthResponse> {
  const response = await fetch(`${KEYCLOAK_URL}/protocol/openid-connect/token`, {
    method: "POST",
    headers: { "Content-Type": "application/x-www-form-urlencoded" },
    body: new URLSearchParams({
      grant_type: "password",
      client_id: CLIENT_ID,
      username: data.username,
      password: data.password,
      scope: "openid profile email",
    }),
  });

  if (!response.ok) {
    throw new Error("Invalid username or password");
  }

  const authData = await response.json();
  setToken(authData.access_token);
  return authData;
}

export async function register(data: RegisterRequest): Promise<UserResponse> {
  const response = await fetch(`http://localhost:8085/api/v1/users/register`, {
    method: "POST",
    headers: { "Content-Type": "application/json" },
    body: JSON.stringify({
      username: data.username,
      email: data.email,
      password: data.password,
      fullName: data.fullName,
    }),
  });

  if (!response.ok) {
    const error = await response.json().catch(() => ({ message: "Registration failed" }));
    throw new Error(error.message || "Registration failed");
  }

  const userResponse = await response.json();
  
  if (userResponse.employeeId) {
    localStorage.setItem("library_member_id", userResponse.employeeId);
  }
  
  return userResponse;
}

export function parseJwt(token: string) {
  try {
    const base64Url = token.split(".")[1];
    const base64 = base64Url.replace(/-/g, "+").replace(/_/g, "/");
    const jsonPayload = decodeURIComponent(
      atob(base64)
        .split("")
        .map((c) => "%" + ("00" + c.charCodeAt(0).toString(16)).slice(-2))
        .join("")
    );
    return JSON.parse(jsonPayload);
  } catch {
    return null;
  }
}

export function getUserInfo() {
  const token = localStorage.getItem("access_token");
  if (!token) return null;
  return parseJwt(token);
}
