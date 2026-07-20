import { useState } from "react";
import { useParams, Link, useNavigate } from "react-router-dom";
import { useQuery, useMutation, useQueryClient } from "@tanstack/react-query";
import {
  ArrowLeft,
  ArrowRight,
  BookMarked,
  CheckCircle2,
  Clock,
  Loader2,
  Mail,
  User,
} from "lucide-react";
import { toast } from "sonner";
import { booksApi } from "@/api/books";
import { borrowingApi } from "@/api/borrowing";
import { useUser } from "@/context/UserContext";
import { Button } from "@/components/ui/button";
import { Skeleton } from "@/components/ui/skeleton";
import EmptyState from "@/components/shared/EmptyState";
import { cn } from "@/lib/utils";

export default function BookDetail() {
  const { id } = useParams<{ id: string }>();
  const navigate = useNavigate();
  const queryClient = useQueryClient();
  const { employee, employeeId, email } = useUser();
  const [borrowed, setBorrowed] = useState(false);

  const { data: book, isLoading, isError } = useQuery({
    queryKey: ["book", id],
    queryFn: () => booksApi.getById(id!),
    enabled: !!id,
  });

  const borrowMutation = useMutation({
    mutationFn: () => borrowingApi.create({ bookId: id!, employeeId: employeeId! }),
    onSuccess: () => {
      setBorrowed(true);
      queryClient.setQueryData(["book", id], (old: typeof book) =>
        old ? { ...old, isReady: false } : old
      );
      queryClient.invalidateQueries({ queryKey: ["books"] });
      queryClient.invalidateQueries({ queryKey: ["book", id] });
      queryClient.invalidateQueries({ queryKey: ["my-borrowings"] });
      toast.success("Borrow confirmed", {
        description: email
          ? `Confirmation email sent to ${email}`
          : `"${book?.name}" is on your shelf. Check your inbox for confirmation.`,
      });
    },
    onError: (err: Error) => {
      toast.error("Borrowing failed", {
        description: err.message || "This book may already be on loan.",
      });
    },
  });

  const handleBorrow = () => {
    if (!employeeId) {
      toast.info("Please sign in first", {
        description: "You need a member account to borrow books.",
      });
      navigate("/welcome");
      return;
    }
    if (employee?.isDisciplined) {
      toast.error("Account restricted", {
        description: "Your account is disciplined and cannot borrow books.",
      });
      return;
    }
    borrowMutation.mutate();
  };

  if (isLoading) {
    return (
      <div className="space-y-6">
        <Skeleton className="h-10 w-40" />
        <Skeleton className="h-72 w-full rounded-[1.75rem]" />
        <Skeleton className="h-40 w-full rounded-[1.5rem]" />
      </div>
    );
  }

  if (isError || !book) {
    return (
      <div className="flex min-h-[60vh] items-center justify-center">
        <EmptyState
          icon={<BookMarked className="h-8 w-8" />}
          title="Book not found"
          description="This book may have been removed from the catalog."
          action={
            <Link to="/">
              <Button variant="outline">
                <ArrowLeft className="mr-2 h-4 w-4" />
                Back to catalog
              </Button>
            </Link>
          }
        />
      </div>
    );
  }

  return (
    <div className="space-y-8 animate-in fade-in-0 duration-500">
      <Link
        to="/"
        className="inline-flex items-center gap-2 text-sm font-semibold text-muted-foreground transition hover:text-foreground"
      >
        <ArrowLeft className="h-4 w-4" />
        Back to catalog
      </Link>

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

        <div className="relative grid gap-8 lg:grid-cols-[1.4fr_auto] lg:items-end">
          <div className="space-y-4">
            <p className="text-xs font-semibold uppercase tracking-[0.22em] text-emerald-300/90">
              Title detail
            </p>
            <h1 className="max-w-2xl text-3xl font-extrabold tracking-tight sm:text-5xl">
              {book.name}
            </h1>
            <p className="flex items-center gap-2 text-base text-white/75 sm:text-lg">
              <User className="h-4 w-4 shrink-0" />
              {book.author}
            </p>
          </div>

          <span
            className={cn(
              "inline-flex w-fit items-center gap-1.5 rounded-full px-3.5 py-1.5 text-[11px] font-bold uppercase tracking-wider ring-1",
              book.isReady
                ? "bg-emerald-500/15 text-emerald-200 ring-emerald-400/30"
                : "bg-white/10 text-white/80 ring-white/15"
            )}
          >
            {book.isReady ? (
              <>
                <span className="h-1.5 w-1.5 animate-pulse rounded-full bg-emerald-300" />
                Available
              </>
            ) : (
              <>
                <Clock className="h-3.5 w-3.5" />
                On loan
              </>
            )}
          </span>
        </div>
      </section>

      <section className="overflow-hidden rounded-[1.5rem] border border-border/70 bg-white">
        <div className="border-b border-border/60 px-6 py-4">
          <h2 className="text-sm font-bold uppercase tracking-[0.16em] text-muted-foreground">
            Borrow
          </h2>
        </div>

        <div className="space-y-5 p-6">
          {borrowed ? (
            <div className="space-y-4">
              <div className="flex items-start gap-4 rounded-2xl border border-emerald-200/80 bg-emerald-50/80 p-5">
                <div className="flex h-11 w-11 shrink-0 items-center justify-center rounded-full bg-emerald-100">
                  <CheckCircle2 className="h-5 w-5 text-emerald-700" />
                </div>
                <div className="space-y-1">
                  <p className="font-semibold text-emerald-900">Added to your shelf</p>
                  <p className="text-sm text-emerald-800/80">
                    {email
                      ? `Confirmation mail is on the way to ${email}.`
                      : "A confirmation email is being sent to your account."}
                  </p>
                </div>
              </div>
              <div className="flex flex-col gap-2 sm:flex-row">
                <Button
                  size="lg"
                  className="flex-1"
                  onClick={() => navigate("/my-borrowings")}
                >
                  Go to My Books
                  <ArrowRight className="ml-1.5 h-4 w-4" />
                </Button>
                <Button
                  size="lg"
                  variant="outline"
                  className="flex-1"
                  onClick={() => navigate("/")}
                >
                  Keep browsing
                </Button>
              </div>
            </div>
          ) : (
            <>
              <Button
                size="lg"
                className="w-full text-base"
                disabled={!book.isReady || borrowMutation.isPending}
                onClick={handleBorrow}
                isLoading={borrowMutation.isPending}
              >
                {borrowMutation.isPending ? (
                  <>
                    <Loader2 className="mr-2 h-5 w-5 animate-spin" />
                    Processing…
                  </>
                ) : book.isReady ? (
                  "Borrow this book"
                ) : (
                  "Currently unavailable"
                )}
              </Button>

              <div className="flex items-start gap-3 rounded-2xl border border-dashed border-border bg-surface-solid/70 p-4">
                <Mail className="mt-0.5 h-4 w-4 shrink-0 text-primary" />
                <p className="text-sm text-muted-foreground">
                  After borrow, LibraStack emails a confirmation
                  {email ? (
                    <>
                      {" "}
                      to <span className="font-semibold text-foreground">{email}</span>
                    </>
                  ) : (
                    " to your registered account"
                  )}
                  . Loan period is up to 14 days.
                </p>
              </div>
            </>
          )}
        </div>
      </section>
    </div>
  );
}
