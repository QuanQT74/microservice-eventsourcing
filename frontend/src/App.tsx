import { RouterProvider, createBrowserRouter, Navigate } from "react-router-dom";
import { useEffect, useState } from "react";
import AppLayout from "@/components/layout/AppLayout";
import Books from "@/pages/Books";
import BookDetail from "@/pages/BookDetail";
import MyBorrowings from "@/pages/MyBorrowings";
import Profile from "@/pages/Profile";
import Welcome from "@/pages/Welcome";
import { getToken } from "@/context/UserContext";
import { Loader2 } from "lucide-react";
import { parseJwt } from "@/api/auth";

function isTokenValid(token: string | null): boolean {
  if (!token) return false;
  const payload = parseJwt(token);
  if (!payload) return false;
  // Check if token is expired (exp is in seconds)
  const now = Date.now() / 1000;
  return (payload.exp || 0) > now;
}

function AuthGuard({ children }: { children: React.ReactNode }) {
  const [checking, setChecking] = useState(true);
  const token = getToken();

  useEffect(() => {
    setChecking(false);
  }, []);

  if (checking) {
    return (
      <div className="flex h-screen items-center justify-center">
        <Loader2 className="h-8 w-8 animate-spin text-primary" />
      </div>
    );
  }

  // Check if token exists AND is valid (not expired)
  if (!token || !isTokenValid(token)) {
    // Clear invalid token from storage
    if (token && !isTokenValid(token)) {
      localStorage.removeItem("access_token");
      localStorage.removeItem("library_member_id");
      localStorage.removeItem("library_user_email");
    }
    return <Navigate to="/welcome" replace />;
  }

  return <>{children}</>;
}

const router = createBrowserRouter([
  {
    path: "/welcome",
    element: <Welcome />,
  },
  {
    path: "/",
    element: (
      <AuthGuard>
        <AppLayout />
      </AuthGuard>
    ),
    children: [
      { path: "/", element: <Books /> },
      { path: "/books/:id", element: <BookDetail /> },
      { path: "/my-borrowings", element: <MyBorrowings /> },
      { path: "/profile", element: <Profile /> },
    ],
  },
  { path: "*", element: <Navigate to="/" replace /> },
]);

export default function App() {
  return <RouterProvider router={router} />;
}
