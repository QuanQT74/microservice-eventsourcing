import { Link, useLocation } from "react-router-dom";
import { BookOpen, History, User, Library, LogOut, Menu, X } from "lucide-react";
import { Button } from "@/components/ui/button";
import { useUser } from "@/context/UserContext";
import { getUserInfo } from "@/api/auth";
import { cn } from "@/lib/utils";
import { useState } from "react";

const navLinks = [
  { to: "/", label: "Browse", icon: BookOpen },
  { to: "/my-borrowings", label: "My Books", icon: History },
  { to: "/profile", label: "Profile", icon: User },
];

export default function Header() {
  const { pathname } = useLocation();
  const { logout, employee } = useUser();
  const [mobileMenuOpen, setMobileMenuOpen] = useState(false);
  const userInfo = getUserInfo();
  const displayName =
    employee
      ? `${employee.firstName} ${employee.lastName}`.trim()
      : userInfo?.name || userInfo?.preferred_username || "Member";
  const initial = (displayName[0] || "M").toUpperCase();

  return (
    <>
      <header className="sticky top-0 z-50 border-b border-black/5 bg-[#0B1F33]/95 text-white backdrop-blur-md">
        <div className="mx-auto flex h-16 max-w-6xl items-center justify-between gap-4 px-4 sm:px-6">
          <Link to="/" className="flex items-center gap-2.5">
            <span className="flex h-9 w-9 items-center justify-center rounded-lg bg-emerald-400/20 ring-1 ring-emerald-300/30">
              <Library className="h-4 w-4 text-emerald-300" />
            </span>
            <span className="leading-tight">
              <span className="block text-sm font-extrabold tracking-tight">LibraStack</span>
              <span className="hidden text-[10px] uppercase tracking-[0.18em] text-white/45 sm:block">
                Digital library
              </span>
            </span>
          </Link>

          <nav className="hidden items-center gap-1 md:flex">
            {navLinks.map(({ to, label }) => {
              const active = pathname === to || (to !== "/" && pathname.startsWith(to));
              return (
                <Link
                  key={to}
                  to={to}
                  className={cn(
                    "rounded-lg px-3.5 py-2 text-sm font-semibold transition-colors",
                    active
                      ? "bg-white text-[#0B1F33]"
                      : "text-white/70 hover:bg-white/10 hover:text-white"
                  )}
                >
                  {label}
                </Link>
              );
            })}
          </nav>

          <div className="flex items-center gap-2">
            {userInfo || employee ? (
              <div className="hidden items-center gap-2.5 sm:flex">
                <Link
                  to="/profile"
                  className="flex items-center gap-2 rounded-lg px-2 py-1.5 transition hover:bg-white/10"
                >
                  <span className="flex h-8 w-8 items-center justify-center rounded-full bg-gradient-to-br from-emerald-400 to-primary text-xs font-bold">
                    {initial}
                  </span>
                  <span className="max-w-[9rem] truncate text-sm font-semibold">{displayName}</span>
                </Link>
                <Button
                  variant="ghost"
                  size="icon"
                  onClick={logout}
                  aria-label="Sign out"
                  className="rounded-lg text-white/70 hover:bg-white/10 hover:text-white"
                >
                  <LogOut className="h-4 w-4" />
                </Button>
              </div>
            ) : (
              <Link to="/welcome" className="hidden sm:block">
                <Button size="sm" className="bg-white text-[#0B1F33] hover:bg-emerald-50">
                  Sign in
                </Button>
              </Link>
            )}

            <Button
              variant="ghost"
              size="icon"
              className="rounded-lg text-white md:hidden hover:bg-white/10"
              onClick={() => setMobileMenuOpen((v) => !v)}
              aria-label="Menu"
            >
              {mobileMenuOpen ? <X className="h-5 w-5" /> : <Menu className="h-5 w-5" />}
            </Button>
          </div>
        </div>

        {mobileMenuOpen && (
          <div className="border-t border-white/10 bg-[#0B1F33] md:hidden">
            <nav className="mx-auto flex max-w-6xl flex-col gap-1 px-4 py-3">
              {navLinks.map(({ to, label, icon: Icon }) => {
                const active = pathname === to || (to !== "/" && pathname.startsWith(to));
                return (
                  <Link
                    key={to}
                    to={to}
                    onClick={() => setMobileMenuOpen(false)}
                    className={cn(
                      "flex items-center gap-3 rounded-lg px-3 py-3 text-sm font-semibold",
                      active ? "bg-white text-[#0B1F33]" : "text-white/80 hover:bg-white/10"
                    )}
                  >
                    <Icon className="h-4 w-4" />
                    {label}
                  </Link>
                );
              })}
            </nav>
          </div>
        )}
      </header>

      <nav className="fixed bottom-0 left-0 right-0 z-50 border-t border-black/5 bg-white/95 backdrop-blur-md md:hidden">
        <div className="mx-auto flex max-w-lg items-center justify-around px-2 py-2">
          {navLinks.map(({ to, label, icon: Icon }) => {
            const active = pathname === to || (to !== "/" && pathname.startsWith(to));
            return (
              <Link
                key={to}
                to={to}
                className={cn(
                  "flex flex-1 flex-col items-center gap-1 rounded-xl py-2 text-[11px] font-semibold",
                  active ? "text-primary" : "text-muted-foreground"
                )}
              >
                <Icon className={cn("h-5 w-5", active && "stroke-[2.5]")} />
                {label}
              </Link>
            );
          })}
        </div>
      </nav>
    </>
  );
}
