import { useMemo, useState } from "react";
import { Link } from "react-router-dom";
import { useQuery, useMutation, useQueryClient } from "@tanstack/react-query";
import {
  ArrowRight,
  BookMarked,
  CalendarDays,
  CheckCircle2,
  Library,
  Loader2,
  RotateCcw,
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
  return s === "BORROWED" || s === "1" || s === "ACTIVE" || s === "NULL" || s === "";
}

function isReturnedStatus(status: string | null | undefined) {
  const s = String(status ?? "").toUpperCase();
  return s === "RETURNED" || s === "0";
}

function formatDate(value?: string | null) {
  if (!value) return "—";
  const d = new Date(value);
  if (Number.isNaN(d.getTime())) return "—";
  return d.toLocaleDateString(undefined, { month: "short", day: "numeric", year: "numeric" });
}

function daysSince(value?: string | null) {
  if (!value) return 0;
  const start = new Date(value).getTime();
  if (Number.isNaN(start)) return 0;
  return Math.max(0, Math.floor((Date.now() - start) / (1000 * 60 * 60 * 24)));
}

export default function MyBorrowings() {
  const { employeeId, email } = useUser();
  const queryClient = useQueryClient();
  const [returningId, setReturningId] = useState<string | null>(null);
  const [tab, setTab] = useState<"shelf" | "history">("shelf");

  const queryKey = ["my-borrowings", employeeId] as const;

  const { data: borrowings = [], isLoading } = useQuery<BorrowingResponse[]>({
    queryKey,
    queryFn: () => borrowingApi.getMyBorrows(employeeId!),
    enabled: !!employeeId,
    refetchInterval: 15000,
  });

  const returnMutation = useMutation({
    mutationFn: ({ borrowingId, bookId }: { borrowingId: string; bookId: string }) =>
      borrowingApi.returnBook(borrowingId, bookId),
    onMutate: async ({ borrowingId }) => {
      await queryClient.cancelQueries({ queryKey });
      const previous = queryClient.getQueryData<BorrowingResponse[]>(queryKey);

      queryClient.setQueryData<BorrowingResponse[]>(queryKey, (old = []) =>
        old.map((b) =>
          b.id === borrowingId
            ? { ...b, status: "RETURNED", returnData: new Date().toISOString() }
            : b
        )
      );

      return { previous };
    },
    onSuccess: (_, variables) => {
      const name =
        borrowings.find((b) => b.id === variables.borrowingId)?.bookName ?? "Book";
      toast.success("Returned to the shelf", {
        description: email
          ? `"${name}" returned. Confirmation sent to ${email}.`
          : `"${name}" is available again. Check your inbox for confirmation.`,
      });
      queryClient.invalidateQueries({ queryKey: ["books"] });
      queryClient.invalidateQueries({ queryKey });
      // Saga cập nhật book status async — refetch thêm lần nữa
      window.setTimeout(() => {
        queryClient.invalidateQueries({ queryKey });
        queryClient.invalidateQueries({ queryKey: ["books"] });
      }, 1200);
    },
    onError: (err: Error, _vars, context) => {
      if (context?.previous) {
        queryClient.setQueryData(queryKey, context.previous);
      }
      toast.error("Could not return book", { description: err.message });
    },
    onSettled: () => setReturningId(null),
  });

  const active = useMemo(
    () => borrowings.filter((b) => isActiveStatus(b.status) && !isReturnedStatus(b.status)),
    [borrowings]
  );
  const history = useMemo(
    () => borrowings.filter((b) => isReturnedStatus(b.status)),
    [borrowings]
  );
  const readingDays = useMemo(
    () => borrowings.reduce((sum, b) => sum + daysSince(b.borrwingDate), 0),
    [borrowings]
  );

  const handleReturn = (id: string, bookId: string) => {
    setReturningId(id);
    returnMutation.mutate({ borrowingId: id, bookId });
  };

  if (!employeeId) {
    return (
      <div className="flex min-h-[60vh] items-center justify-center">
        <EmptyState
          icon={<Library className="h-8 w-8" />}
          title="Sign in to open your shelf"
          description="Connect your member account to track what you're reading."
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
        <Skeleton className="h-44 w-full rounded-3xl" />
        <Skeleton className="h-64 w-full rounded-3xl" />
      </div>
    );
  }

  return (
    <div className="space-y-8 animate-in fade-in-0 duration-500">
      {/* Atmosphere banner — one composition */}
      <section className="relative overflow-hidden rounded-[1.75rem] border border-border/60 bg-[#0B1F33] px-6 py-10 text-white shadow-xl sm:px-10 sm:py-12">
        <div
          className="pointer-events-none absolute inset-0 opacity-40"
          style={{
            backgroundImage:
              "radial-gradient(circle at 15% 20%, rgba(0,184,148,0.35), transparent 42%), radial-gradient(circle at 85% 10%, rgba(0,102,255,0.28), transparent 40%), linear-gradient(135deg, rgba(255,255,255,0.04), transparent 50%)",
          }}
        />
        <div
          className="pointer-events-none absolute inset-0 opacity-[0.07]"
          style={{
            backgroundImage:
              "repeating-linear-gradient(90deg, transparent, transparent 47px, rgba(255,255,255,0.35) 48px)",
          }}
        />

        <div className="relative grid gap-8 lg:grid-cols-[1.4fr_1fr] lg:items-end">
          <div className="space-y-4">
            <p className="text-xs font-semibold uppercase tracking-[0.22em] text-emerald-300/90">
              Your shelf
            </p>
            <h1 className="max-w-xl text-4xl font-extrabold tracking-tight sm:text-5xl">
              My Books
            </h1>
            <p className="max-w-lg text-sm leading-relaxed text-white/70 sm:text-base">
              What you&apos;re holding now, and what you&apos;ve already returned to the library.
            </p>
            <div className="flex flex-wrap gap-3 pt-1">
              <Link to="/">
                <Button
                  size="sm"
                  className="bg-white text-[#0B1F33] hover:bg-emerald-50"
                >
                  Browse catalog
                  <ArrowRight className="ml-1.5 h-3.5 w-3.5" />
                </Button>
              </Link>
            </div>
          </div>

          <div className="grid grid-cols-3 gap-3">
            {[
              { label: "On loan", value: active.length },
              { label: "Returned", value: history.length },
              { label: "Days read", value: readingDays },
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
        </div>
      </section>

      {/* Tabs */}
      <div className="flex items-center gap-2 border-b border-border/70 pb-px">
        {(
          [
            { id: "shelf" as const, label: "On loan", count: active.length },
            { id: "history" as const, label: "History", count: history.length },
          ]
        ).map((t) => (
          <button
            key={t.id}
            type="button"
            onClick={() => setTab(t.id)}
            className={cn(
              "relative -mb-px px-4 py-2.5 text-sm font-semibold transition-colors",
              tab === t.id
                ? "text-foreground"
                : "text-muted-foreground hover:text-foreground"
            )}
          >
            {t.label}
            <span className="ml-2 inline-flex min-w-5 items-center justify-center rounded-md bg-muted px-1.5 py-0.5 text-[11px] font-bold tabular-nums">
              {t.count}
            </span>
            {tab === t.id && (
              <span className="absolute inset-x-2 -bottom-px h-0.5 rounded-full bg-primary" />
            )}
          </button>
        ))}
      </div>

      {tab === "shelf" ? (
        active.length === 0 ? (
          <div className="rounded-[1.5rem] border border-dashed border-border bg-surface-solid/60 px-6 py-16 text-center">
            <BookMarked className="mx-auto mb-3 h-8 w-8 text-primary" />
            <p className="text-base font-semibold">Nothing on loan</p>
            <p className="mt-1 text-sm text-muted-foreground">
              Pick something from the catalog when you&apos;re ready.
            </p>
            <Link to="/" className="mt-6 inline-block">
              <Button size="sm">Find a book</Button>
            </Link>
          </div>
        ) : (
          <ul className="space-y-4">
            {active.map((b, index) => (
              <li
                key={b.id}
                className="group relative overflow-hidden rounded-[1.35rem] border border-border/70 bg-white shadow-sm transition-all duration-300 hover:-translate-y-0.5 hover:shadow-md animate-in fade-in-0 slide-in-from-bottom-2"
                style={{ animationDelay: `${index * 70}ms`, animationFillMode: "backwards" }}
              >
                <div className="absolute inset-y-0 left-0 w-1.5 bg-gradient-to-b from-primary to-emerald-500" />
                <div className="flex flex-col gap-5 p-5 pl-6 sm:flex-row sm:items-center sm:justify-between sm:p-6 sm:pl-7">
                  <div className="flex min-w-0 items-start gap-4">
                    <div className="flex h-16 w-12 shrink-0 items-center justify-center rounded-md bg-gradient-to-br from-[#123047] to-[#1B4D6E] text-[10px] font-bold uppercase tracking-wide text-emerald-200 shadow-inner">
                      On
                      <br />
                      loan
                    </div>
                    <div className="min-w-0 space-y-2">
                      <div>
                        <h2 className="truncate text-lg font-bold tracking-tight text-foreground">
                          {b.bookName || "Untitled"}
                        </h2>
                        <p className="text-sm text-muted-foreground">
                          {b.bookAuthor || "Unknown author"}
                        </p>
                      </div>
                      <div className="flex flex-wrap items-center gap-x-4 gap-y-1 text-xs text-muted-foreground">
                        <span className="inline-flex items-center gap-1.5">
                          <CalendarDays className="h-3.5 w-3.5" />
                          Borrowed {formatDate(b.borrwingDate)}
                        </span>
                        <span className="inline-flex items-center gap-1.5">
                          <Library className="h-3.5 w-3.5" />
                          {daysSince(b.borrwingDate)} day
                          {daysSince(b.borrwingDate) === 1 ? "" : "s"} with you
                        </span>
                      </div>
                    </div>
                  </div>

                  <div className="flex items-center gap-3 sm:shrink-0">
                    <span className="rounded-full bg-emerald-50 px-3 py-1 text-[11px] font-bold uppercase tracking-wider text-emerald-700 ring-1 ring-emerald-200/80">
                      Active
                    </span>
                    <Button
                      size="sm"
                      onClick={() => handleReturn(b.id, b.bookId)}
                      disabled={returningId === b.id || returnMutation.isPending}
                      className="min-w-[7.5rem]"
                    >
                      {returningId === b.id ? (
                        <>
                          <Loader2 className="mr-1.5 h-3.5 w-3.5 animate-spin" />
                          Returning
                        </>
                      ) : (
                        <>
                          <RotateCcw className="mr-1.5 h-3.5 w-3.5" />
                          Return
                        </>
                      )}
                    </Button>
                  </div>
                </div>
              </li>
            ))}
          </ul>
        )
      ) : history.length === 0 ? (
        <div className="rounded-[1.5rem] border border-dashed border-border bg-surface-solid/60 px-6 py-14 text-center">
          <CheckCircle2 className="mx-auto mb-3 h-7 w-7 text-muted-foreground" />
          <p className="text-sm font-medium text-muted-foreground">No returned books yet</p>
        </div>
      ) : (
        <ul className="divide-y divide-border/70 overflow-hidden rounded-[1.35rem] border border-border/70 bg-white">
          {history.map((b) => (
            <li
              key={b.id}
              className="flex items-center justify-between gap-4 px-5 py-4 sm:px-6"
            >
              <div className="min-w-0">
                <p className="truncate font-semibold text-foreground">
                  {b.bookName || "Untitled"}
                </p>
                <p className="mt-0.5 text-xs text-muted-foreground">
                  {formatDate(b.borrwingDate)} → {formatDate(b.returnData)}
                </p>
              </div>
              <span className="shrink-0 rounded-full bg-slate-100 px-2.5 py-1 text-[11px] font-bold uppercase tracking-wider text-slate-600">
                Returned
              </span>
            </li>
          ))}
        </ul>
      )}
    </div>
  );
}
