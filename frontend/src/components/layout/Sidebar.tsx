import { Link, useLocation } from "react-router-dom";
import { BookOpen, History, User, Library, HelpCircle, Sparkles } from "lucide-react";
import { cn } from "@/lib/utils";
import { useUser } from "@/context/UserContext";

const links = [
  { to: "/", label: "Browse", icon: BookOpen },
  { to: "/my-borrowings", label: "My Books", icon: History },
  { to: "/profile", label: "Profile", icon: User },
];

export default function Sidebar() {
  const { pathname } = useLocation();
  const { employee } = useUser();

  return (
    <aside className="hidden w-64 shrink-0 md:block">
      <div className="sticky top-16 flex h-[calc(100vh-4rem)] flex-col p-4">
        {/* Welcome card */}
        <div className="mb-6 relative overflow-hidden rounded-2xl bg-gradient-to-br from-primary via-emerald-600 to-teal-700 p-5 shadow-xl">
          {/* Decorative elements */}
          <div className="absolute -right-6 -top-6 h-24 w-24 rounded-full bg-white/10 blur-2xl" />
          <div className="absolute -bottom-4 -left-4 h-20 w-20 rounded-full bg-accent/20 blur-xl" />
          
          <div className="relative">
            <div className="mb-3 flex h-11 w-11 items-center justify-center rounded-xl bg-white/20 backdrop-blur-sm shadow-md">
              <Library className="h-5 w-5 text-white" />
            </div>
            <p className="text-xs font-medium text-white/80 uppercase tracking-wider">Member Portal</p>
            {employee ? (
              <div className="mt-2">
                <p className="text-base font-bold text-white">
                  Welcome back,
                </p>
                <p className="text-sm font-semibold text-white/90">
                  {employee.firstName} {employee.lastName}
                </p>
              </div>
            ) : (
              <p className="mt-2 text-sm text-white/80">
                Sign in to borrow books
              </p>
            )}
          </div>
        </div>

        {/* Navigation */}
        <nav className="flex flex-col gap-1">
          <p className="px-3 py-2 text-xs font-semibold uppercase tracking-wider text-muted-foreground">
            Navigation
          </p>
          {links.map(({ to, label, icon: Icon }) => {
            const active = pathname === to || (to !== "/" && pathname.startsWith(to));
            return (
              <Link
                key={to}
                to={to}
                className={cn(
                  "group flex items-center gap-3 rounded-xl px-4 py-3 text-sm font-semibold transition-all duration-200",
                  active
                    ? "bg-gradient-to-r from-primary to-emerald-600 text-primary-foreground shadow-lg shadow-primary/25"
                    : "text-muted-foreground hover:bg-muted hover:text-foreground"
                )}
              >
                <Icon className={cn("h-5 w-5 transition-transform group-hover:scale-110", active && "text-white")} />
                {label}
                {active && (
                  <div className="ml-auto h-1.5 w-1.5 rounded-full bg-white" />
                )}
              </Link>
            );
          })}
        </nav>

        {/* Help section */}
        <div className="mt-auto rounded-2xl border border-dashed bg-muted/50 p-4">
          <div className="flex items-center gap-3 mb-2">
            <div className="flex h-8 w-8 items-center justify-center rounded-lg bg-primary/10">
              <HelpCircle className="h-4 w-4 text-primary" />
            </div>
            <p className="text-sm font-semibold">Need help?</p>
          </div>
          <p className="text-xs text-muted-foreground leading-relaxed">
            Visit the front desk or contact library support for assistance.
          </p>
          <div className="mt-3 flex items-center gap-2">
            <Sparkles className="h-3 w-3 text-primary" />
            <p className="text-xs text-muted-foreground">Available 24/7</p>
          </div>
        </div>
      </div>
    </aside>
  );
}
