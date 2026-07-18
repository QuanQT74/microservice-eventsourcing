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

  if (!token) {
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
