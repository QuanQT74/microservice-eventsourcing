import { Outlet } from "react-router-dom";
import Header from "@/components/layout/Header";

export default function AppLayout() {
  return (
    <div className="min-h-screen w-full bg-[#F4F7FA]">
      <Header />
      <main className="mx-auto min-h-[calc(100vh-4rem)] w-full max-w-6xl px-4 py-6 pb-24 sm:px-6 md:py-8 md:pb-8">
        <Outlet />
      </main>
    </div>
  );
}
