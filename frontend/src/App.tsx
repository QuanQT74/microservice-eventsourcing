import { RouterProvider, createBrowserRouter, Navigate } from "react-router-dom";
import AppLayout from "@/components/layout/AppLayout";
import Books from "@/pages/Books";
import BookDetail from "@/pages/BookDetail";
import MyBorrowings from "@/pages/MyBorrowings";
import Profile from "@/pages/Profile";
import Welcome from "@/pages/Welcome";

const router = createBrowserRouter([
  {
    path: "/welcome",
    element: <Welcome />,
  },
  {
    path: "/",
    element: <AppLayout />,
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
