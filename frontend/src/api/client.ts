import type { ApiError } from "@/types";
import { getToken } from "@/context/UserContext";

const API_BASE = "http://localhost:8085/api/v1";

export async function apiFetch<T>(
  path: string,
  options: RequestInit & { auth?: boolean; apiKey?: boolean } = {}
): Promise<T> {
  const { auth = true, apiKey = false, headers: customHeaders, ...rest } = options;

  const headers: HeadersInit = {
    "Content-Type": "application/json",
    ...customHeaders,
  };

  // Add Bearer token if auth is required
  if (auth) {
    const token = getToken();
    if (token) {
      (headers as Record<string, string>)["Authorization"] = `Bearer ${token}`;
    }
  }

  // Add API Key as fallback
  if (apiKey) {
    const apiKeyValue = import.meta.env.VITE_API_KEY ?? "ltfullstack";
    (headers as Record<string, string>)["apiKey"] = apiKeyValue;
  }

  const url = path.startsWith("http") ? path : `${API_BASE}${path}`;
  const response = await fetch(url, { headers, ...rest });

  if (!response.ok) {
    let message = `Request failed (${response.status})`;
    try {
      const body = await response.json();
      message = body.error ?? body.message ?? body.meessage ?? JSON.stringify(body);
    } catch {
      // ignore parse errors
    }
    const error: ApiError = { message, status: response.status };
    throw error;
  }

  const text = await response.text();
  if (!text) return undefined as T;

  try {
    return JSON.parse(text) as T;
  } catch {
    return text as T;
  }
}
