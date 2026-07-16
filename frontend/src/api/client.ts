import type { ApiError } from "@/types";

const API_KEY = import.meta.env.VITE_API_KEY ?? "ltfullstack";

export async function apiFetch<T>(
  path: string,
  options: RequestInit & { auth?: boolean } = {}
): Promise<T> {
  const { auth = false, headers: customHeaders, ...rest } = options;

  const headers: HeadersInit = {
    "Content-Type": "application/json",
    ...customHeaders,
  };

  if (auth) {
    (headers as Record<string, string>)["apiKey"] = API_KEY;
  }

  const response = await fetch(path, { headers, ...rest });

  if (!response.ok) {
    let message = `Request failed (${response.status})`;
    try {
      const body = await response.json();
      message = body.message ?? body.meessage ?? JSON.stringify(body);
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
