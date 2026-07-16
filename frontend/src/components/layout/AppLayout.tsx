import { Outlet } from "react-router-dom";
import Sidebar from "@/components/layout/Sidebar";
import Header from "@/components/layout/Header";

export default function AppLayout() {
  return (
    <div className="min-h-screen w-full bg-background">
      <Header />
      <div className="mx-auto flex max-w-screen-2xl">
        <Sidebar />
        <main className="min-h-[calc(100vh-3.5rem)] flex-1 p-4 pb-24 md:p-6 md:pb-6">
          <Outlet />
        </main>
      </div>
    </div>
  );
}
