import { Link } from "react-router-dom";
import { useQuery } from "@tanstack/react-query";
import {
  AlertTriangle,
  ArrowRight,
  BookMarked,
  Copy,
  Hash,
  IdCard,
  Library,
  LogOut,
  Mail,
  ShieldCheck,
  UserRound,
} from "lucide-react";
import { toast } from "sonner";
import { useUser } from "@/context/UserContext";
import { borrowingApi } from "@/api/borrowing";
import EmptyState from "@/components/shared/EmptyState";
import { Button } from "@/components/ui/button";
import { Skeleton } from "@/components/ui/skeleton";
import type { BorrowingResponse } from "@/types";
import { cn } from "@/lib/utils";

function isActiveStatus(status: string | null | undefined) {
  const s = String(status ?? "").toUpperCase();
  return s === "BORROWED" || s === "1" || s === "ACTIVE";
}

function shortId(id?: string) {
  if (!id) return "—";
  return id.length > 12 ? `${id.slice(0, 8)}…${id.slice(-4)}` : id;
}

export default function Profile() {
  const { employee, employeeId, email, isLoading, logout } = useUser();

  const { data: borrowings = [] } = useQuery<BorrowingResponse[]>({
    queryKey: ["my-borrowings", employeeId],
    queryFn: () => borrowingApi.getMyBorrows(employeeId!),
    enabled: !!employeeId,
  });

  const activeCount = borrowings.filter((b) => isActiveStatus(b.status)).length;
  const returnedCount = borrowings.filter(
    (b) => String(b.status).toUpperCase() === "RETURNED"
  ).length;

  const memberLabel =
    employee?.memberCode || employee?.Kin || shortId(employee?.id) || "—";

  const copyId = async (value: string, label: string) => {
    try {
      await navigator.clipboard.writeText(value);
      toast.success(`${label} copied`);
    } catch {
      toast.error("Could not copy");
    }
  };

  if (!employeeId) {
    return (
      <div className="flex min-h-[60vh] items-center justify-center">
        <EmptyState
          icon={<UserRound className="h-8 w-8" />}
          title="Sign in to view profile"
          description="Connect your member account to see membership details."
          action={
            <Link to="/welcome">
              <Button>Sign In</Button>
            </Link>
          }
        />
      </div>
    );
  }

  if (isLoading) {
    return (
      <div className="space-y-6">
        <Skeleton className="h-52 w-full rounded-3xl" />
        <Skeleton className="h-64 w-full rounded-3xl" />
      </div>
    );
  }

  if (!employee) {
    return (
      <div className="flex min-h-[60vh] items-center justify-center">
        <EmptyState
          title="Member not found"
          description="We couldn't load your profile. Sign in again with a valid account."
          action={
            <Link to="/welcome">
              <Button variant="outline">Try again</Button>
            </Link>
          }
        />
      </div>
    );
  }

  const initials = `${employee.firstName?.[0] ?? ""}${employee.lastName?.[0] ?? ""}`.toUpperCase() || "M";
  const fullName = `${employee.firstName} ${employee.lastName}`.trim();

  return (
    <div className="space-y-8 animate-in fade-in-0 duration-500">
      {/* Hero */}
      <section className="relative overflow-hidden rounded-[1.75rem] border border-border/60 bg-[#0B1F33] px-6 py-10 text-white shadow-xl sm:px-10 sm:py-12">
        <div
          className="pointer-events-none absolute inset-0 opacity-45"
          style={{
            backgroundImage:
              "radial-gradient(circle at 12% 20%, rgba(0,184,148,0.35), transparent 42%), radial-gradient(circle at 88% 15%, rgba(0,102,255,0.28), transparent 40%)",
          }}
        />
        <div
          className="pointer-events-none absolute inset-0 opacity-[0.06]"
          style={{
            backgroundImage:
              "repeating-linear-gradient(90deg, transparent, transparent 47px, rgba(255,255,255,0.4) 48px)",
          }}
        />

        <div className="relative flex flex-col gap-8 lg:flex-row lg:items-end lg:justify-between">
          <div className="flex items-start gap-5">
            <div className="flex h-20 w-20 shrink-0 items-center justify-center rounded-2xl bg-gradient-to-br from-emerald-400/90 to-primary text-2xl font-extrabold tracking-tight text-white shadow-lg ring-4 ring-white/10">
              {initials}
            </div>
            <div className="space-y-3">
              <p className="text-xs font-semibold uppercase tracking-[0.22em] text-emerald-300/90">
                Library member
              </p>
              <h1 className="text-3xl font-extrabold tracking-tight sm:text-4xl">
                {fullName}
              </h1>
              <div className="flex flex-wrap items-center gap-2">
                <span
                  className={cn(
                    "inline-flex items-center gap-1.5 rounded-full px-3 py-1 text-[11px] font-bold uppercase tracking-wider ring-1",
                    employee.isDisciplined
                      ? "bg-red-500/15 text-red-200 ring-red-400/30"
                      : "bg-emerald-500/15 text-emerald-200 ring-emerald-400/30"
                  )}
                >
                  {employee.isDisciplined ? (
                    <>
                      <AlertTriangle className="h-3.5 w-3.5" />
                      Restricted
                    </>
                  ) : (
                    <>
                      <ShieldCheck className="h-3.5 w-3.5" />
                      Active
                    </>
                  )}
                </span>
                <button
                  type="button"
                  onClick={() => copyId(memberLabel, "Member ID")}
                  className="inline-flex items-center gap-1.5 rounded-full bg-white/10 px-3 py-1 text-[11px] font-semibold tracking-wide text-white/85 ring-1 ring-white/15 transition hover:bg-white/15"
                >
                  <Hash className="h-3.5 w-3.5" />
                  {memberLabel}
                  <Copy className="h-3 w-3 opacity-70" />
                </button>
              </div>
            </div>
          </div>

          <div className="flex flex-wrap gap-2">
            <Link to="/my-borrowings">
              <Button size="sm" className="bg-white text-[#0B1F33] hover:bg-emerald-50">
                My shelf
                <ArrowRight className="ml-1.5 h-3.5 w-3.5" />
              </Button>
            </Link>
            <Button
              size="sm"
              variant="outline"
              className="border-white/20 bg-transparent text-white hover:bg-white/10 hover:text-white"
              onClick={logout}
            >
              <LogOut className="mr-1.5 h-3.5 w-3.5" />
              Sign out
            </Button>
          </div>
        </div>

        <div className="relative mt-8 grid grid-cols-3 gap-3">
          {[
            { label: "On loan", value: activeCount },
            { label: "Returned", value: returnedCount },
            { label: "Standing", value: employee.isDisciplined ? "Hold" : "Good" },
          ].map((item) => (
            <div
              key={item.label}
              className="rounded-2xl border border-white/10 bg-white/5 px-3 py-4 backdrop-blur-sm"
            >
              <div className="text-2xl font-bold tabular-nums tracking-tight sm:text-3xl">
                {item.value}
              </div>
              <div className="mt-1 text-[11px] uppercase tracking-wider text-white/55">
                {item.label}
              </div>
            </div>
          ))}
        </div>
      </section>

      {/* Details — single composition, no card spam */}
      <section className="overflow-hidden rounded-[1.5rem] border border-border/70 bg-white">
        <div className="border-b border-border/60 px-6 py-4">
          <h2 className="text-sm font-bold uppercase tracking-[0.16em] text-muted-foreground">
            Membership
          </h2>
        </div>

        <dl className="divide-y divide-border/60">
          <div className="grid gap-1 px-6 py-4 sm:grid-cols-[10rem_1fr] sm:items-center">
            <dt className="flex items-center gap-2 text-xs font-semibold uppercase tracking-wider text-muted-foreground">
              <UserRound className="h-3.5 w-3.5" />
              Full name
            </dt>
            <dd className="text-base font-semibold text-foreground">{fullName}</dd>
          </div>

          {email && (
            <div className="grid gap-1 px-6 py-4 sm:grid-cols-[10rem_1fr] sm:items-center">
              <dt className="flex items-center gap-2 text-xs font-semibold uppercase tracking-wider text-muted-foreground">
                <Mail className="h-3.5 w-3.5" />
                Email
              </dt>
              <dd className="flex flex-wrap items-center gap-2">
                <span className="text-base font-semibold text-foreground">{email}</span>
                <button
                  type="button"
                  onClick={() => copyId(email, "Email")}
                  className="rounded-md p-1 text-muted-foreground transition hover:bg-muted hover:text-foreground"
                  aria-label="Copy email"
                >
                  <Copy className="h-3.5 w-3.5" />
                </button>
              </dd>
            </div>
          )}

          <div className="grid gap-1 px-6 py-4 sm:grid-cols-[10rem_1fr] sm:items-center">
            <dt className="flex items-center gap-2 text-xs font-semibold uppercase tracking-wider text-muted-foreground">
              <IdCard className="h-3.5 w-3.5" />
              Member ID
            </dt>
            <dd className="flex flex-wrap items-center gap-2">
              <span className="font-mono text-sm font-semibold tracking-wide">
                {memberLabel}
              </span>
              {(employee.memberCode || employee.Kin) && (
                <button
                  type="button"
                  onClick={() => copyId(memberLabel, "Member ID")}
                  className="rounded-md p-1 text-muted-foreground transition hover:bg-muted hover:text-foreground"
                  aria-label="Copy member ID"
                >
                  <Copy className="h-3.5 w-3.5" />
                </button>
              )}
            </dd>
          </div>

          <div className="grid gap-1 px-6 py-4 sm:grid-cols-[10rem_1fr] sm:items-center">
            <dt className="flex items-center gap-2 text-xs font-semibold uppercase tracking-wider text-muted-foreground">
              <Hash className="h-3.5 w-3.5" />
              Account ref
            </dt>
            <dd className="flex flex-wrap items-center gap-2">
              <span className="break-all font-mono text-xs text-muted-foreground sm:text-sm">
                {employee.id}
              </span>
              <button
                type="button"
                onClick={() => copyId(employee.id, "Account ref")}
                className="rounded-md p-1 text-muted-foreground transition hover:bg-muted hover:text-foreground"
                aria-label="Copy account ref"
              >
                <Copy className="h-3.5 w-3.5" />
              </button>
            </dd>
          </div>

          <div className="grid gap-1 px-6 py-4 sm:grid-cols-[10rem_1fr] sm:items-center">
            <dt className="flex items-center gap-2 text-xs font-semibold uppercase tracking-wider text-muted-foreground">
              <ShieldCheck className="h-3.5 w-3.5" />
              Privilege
            </dt>
            <dd>
              {employee.isDisciplined ? (
                <span className="inline-flex items-center gap-1.5 rounded-full bg-red-50 px-3 py-1 text-xs font-bold uppercase tracking-wider text-red-700 ring-1 ring-red-200">
                  <AlertTriangle className="h-3.5 w-3.5" />
                  Cannot borrow
                </span>
              ) : (
                <span className="inline-flex items-center gap-1.5 rounded-full bg-emerald-50 px-3 py-1 text-xs font-bold uppercase tracking-wider text-emerald-700 ring-1 ring-emerald-200">
                  <ShieldCheck className="h-3.5 w-3.5" />
                  Can borrow books
                </span>
              )}
            </dd>
          </div>
        </dl>
      </section>

      <section className="rounded-[1.35rem] border border-border/70 bg-white p-6">
        <div className="mb-3 flex items-center gap-2 text-xs font-bold uppercase tracking-[0.16em] text-muted-foreground">
          <Mail className="h-3.5 w-3.5" />
          Email notifications
        </div>
        <p className="text-sm leading-relaxed text-muted-foreground">
          Borrow and return confirmations are sent by Notification Service to{" "}
          <span className="font-semibold text-foreground">
            {email || "your registered email"}
          </span>
          .
        </p>
      </section>

      {/* Activity strip */}
      <section className="grid gap-4 sm:grid-cols-2">
        <div className="rounded-[1.35rem] border border-border/70 bg-white p-6">
          <div className="mb-4 flex items-center gap-2 text-xs font-bold uppercase tracking-[0.16em] text-muted-foreground">
            <BookMarked className="h-3.5 w-3.5" />
            Currently reading
          </div>
          <p className="text-4xl font-extrabold tabular-nums tracking-tight text-foreground">
            {activeCount}
          </p>
          <p className="mt-1 text-sm text-muted-foreground">
            {activeCount === 1 ? "book on loan" : "books on loan"}
          </p>
          <Link to="/my-borrowings" className="mt-5 inline-block">
            <Button size="sm" variant="outline">
              Open shelf
              <ArrowRight className="ml-1.5 h-3.5 w-3.5" />
            </Button>
          </Link>
        </div>

        <div className="rounded-[1.35rem] border border-border/70 bg-[#0B1F33] p-6 text-white">
          <div className="mb-4 flex items-center gap-2 text-xs font-bold uppercase tracking-[0.16em] text-white/55">
            <Library className="h-3.5 w-3.5" />
            Catalog
          </div>
          <p className="text-lg font-semibold leading-snug">
            Find your next title in the collection.
          </p>
          <Link to="/" className="mt-5 inline-block">
            <Button size="sm" className="bg-white text-[#0B1F33] hover:bg-emerald-50">
              Browse books
              <ArrowRight className="ml-1.5 h-3.5 w-3.5" />
            </Button>
          </Link>
        </div>
      </section>
    </div>
  );
}
