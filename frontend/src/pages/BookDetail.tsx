import { useState } from "react";
import { useParams, Link, useNavigate } from "react-router-dom";
import { useQuery, useMutation, useQueryClient } from "@tanstack/react-query";
import { ArrowLeft, BookMarked, User, Loader2, CheckCircle2, Clock, Calendar, Info } from "lucide-react";
import { toast } from "sonner";
import { booksApi } from "@/api/books";
import { borrowingApi } from "@/api/borrowing";
import { useUser } from "@/context/UserContext";
import { saveBorrowing } from "@/lib/borrowings-storage";
import { Button } from "@/components/ui/button";
import { Badge } from "@/components/ui/badge";
import { Card, CardContent } from "@/components/ui/card";
import { Skeleton } from "@/components/ui/skeleton";
import EmptyState from "@/components/shared/EmptyState";
import PageHeader from "@/components/shared/PageHeader";

export default function BookDetail() {
  const { id } = useParams<{ id: string }>();
  const navigate = useNavigate();
  const queryClient = useQueryClient();
  const { employee, employeeId } = useUser();
  const [borrowed, setBorrowed] = useState(false);

  const { data: book, isLoading, isError } = useQuery({
    queryKey: ["book", id],
    queryFn: () => booksApi.getById(id!),
    enabled: !!id,
  });

  const borrowMutation = useMutation({
    mutationFn: () =>
      borrowingApi.create({ bookId: id!, employeeId: employeeId! }),
    onSuccess: (borrowingId) => {
      saveBorrowing({
        id: borrowingId,
        bookId: id!,
        bookName: book!.name,
        bookAuthor: book!.author,
        employeeId: employeeId!,
        borrowedDate: new Date().toISOString().split("T")[0],
        status: "ACTIVE",
      });
      setBorrowed(true);
      queryClient.invalidateQueries({ queryKey: ["books"] });
      queryClient.invalidateQueries({ queryKey: ["book", id] });
      toast.success("Book borrowed successfully!", {
        description: `"${book?.name}" has been added to your borrowings.`,
      });
    },
    onError: (err: Error) => {
      toast.error("Borrowing failed", { description: err.message });
    },
  });

  const handleBorrow = () => {
    if (!employeeId) {
      toast.info("Please sign in first", { description: "You need a member account to borrow books." });
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
      <div className="mx-auto max-w-2xl space-y-6">
        <Skeleton className="h-8 w-32" />
        <Skeleton className="h-80 w-full rounded-2xl" />
        <Skeleton className="h-32 w-full rounded-xl" />
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
    <div className="mx-auto max-w-2xl space-y-6 animate-in fade-in-0 slide-in-from-bottom-4">
      {/* Back link */}
      <Link
        to="/"
        className="inline-flex items-center gap-2 rounded-lg px-3 py-2 text-sm text-muted-foreground transition-all hover:bg-muted hover:text-foreground"
      >
        <ArrowLeft className="h-4 w-4" />
        Back to catalog
      </Link>

      {/* Main book card */}
      <Card className="overflow-hidden border-0 shadow-2xl">
        {/* Header with gradient */}
        <div className="relative bg-gradient-to-br from-primary via-emerald-600 to-teal-700 p-8">
          {/* Decorative elements */}
          <div className="absolute -right-8 -top-8 h-40 w-40 rounded-full bg-white/10 blur-2xl" />
          <div className="absolute -bottom-10 -left-10 h-32 w-32 rounded-full bg-accent/20 blur-xl" />
          <div className="absolute right-1/4 top-1/3 h-24 w-24 rounded-full bg-emerald-400/10 blur-3xl" />

          <div className="relative">
            <div className="mb-6 flex items-start justify-between gap-4">
              <div className="flex h-14 w-14 items-center justify-center rounded-xl bg-white/20 backdrop-blur-sm">
                <BookMarked className="h-7 w-7 text-white" />
              </div>
              <Badge 
                variant={book.isReady ? "success" : "danger"} 
                className="shrink-0 shadow-lg text-sm px-3 py-1"
              >
                {book.isReady ? (
                  <span className="flex items-center gap-1.5">
                    <span className="h-2 w-2 rounded-full bg-white animate-pulse" />
                    Available
                  </span>
                ) : (
                  <span className="flex items-center gap-1.5">
                    <Clock className="h-3.5 w-3.5" />
                    Currently Borrowed
                  </span>
                )}
              </Badge>
            </div>

            <h1 className="text-3xl font-bold text-white sm:text-4xl mb-3">
              {book.name}
            </h1>
            <p className="flex items-center gap-2 text-lg text-white/90">
              <User className="h-5 w-5" />
              {book.author}
            </p>
          </div>
        </div>

        {/* Book details */}
        <CardContent className="space-y-6 p-6">
          {/* Info grid */}
          <div className="grid grid-cols-2 gap-4 rounded-xl bg-muted/50 p-4">
            <div className="flex items-center gap-3">
              <div className="flex h-10 w-10 items-center justify-center rounded-lg bg-primary/10">
                <Info className="h-5 w-5 text-primary" />
              </div>
              <div>
                <p className="text-xs text-muted-foreground">Book ID</p>
                <p className="font-mono text-sm font-medium">{book.id.slice(0, 12)}...</p>
              </div>
            </div>
            <div className="flex items-center gap-3">
              <div className="flex h-10 w-10 items-center justify-center rounded-lg bg-primary/10">
                <Clock className="h-5 w-5 text-primary" />
              </div>
              <div>
                <p className="text-xs text-muted-foreground">Status</p>
                <p className="font-medium">{book.isReady ? "Ready to borrow" : "Unavailable"}</p>
              </div>
            </div>
          </div>

          {/* Success message */}
          {borrowed ? (
            <div className="space-y-4">
              <div className="flex items-center gap-4 rounded-xl border-2 border-green-200 bg-green-50 p-5">
                <div className="flex h-12 w-12 items-center justify-center rounded-full bg-green-100">
                  <CheckCircle2 className="h-6 w-6 text-green-600" />
                </div>
                <div>
                  <p className="font-semibold text-green-800">Successfully borrowed!</p>
                  <p className="text-sm text-green-700">View it in My Books.</p>
                </div>
              </div>
              <Button 
                 
                className="w-full" 
                size="lg"
                onClick={() => navigate("/my-borrowings")}
              >
                Go to My Books
              </Button>
            </div>
          ) : (
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
                  Processing...
                </>
              ) : book.isReady ? (
                "Borrow This Book"
              ) : (
                "Currently Unavailable"
              )}
            </Button>
          )}

          {/* Info notice */}
          <div className="rounded-xl border border-dashed bg-muted/30 p-4">
            <div className="flex items-start gap-3">
              <Info className="h-5 w-5 text-muted-foreground shrink-0 mt-0.5" />
              <p className="text-sm text-muted-foreground">
                Books can be borrowed for up to 14 days. Return on time to maintain good standing.
              </p>
            </div>
          </div>
        </CardContent>
      </Card>
    </div>
  );
}
