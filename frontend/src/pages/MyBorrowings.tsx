import { useState } from "react";
import { Link } from "react-router-dom";
import { useQuery, useMutation, useQueryClient } from "@tanstack/react-query";
import { History, RotateCcw, BookOpen, Calendar, CheckCircle2, Clock, Sparkles } from "lucide-react";
import { toast } from "sonner";
import { useUser } from "@/context/UserContext";
import { borrowingApi } from "@/api/borrowing";
import PageHeader from "@/components/shared/PageHeader";
import EmptyState from "@/components/shared/EmptyState";
import { Card, CardContent, CardHeader, CardTitle } from "@/components/ui/card";
import { Badge } from "@/components/ui/badge";
import { Button } from "@/components/ui/button";
import { Stat } from "@/components/ui/stat";
import { Skeleton } from "@/components/ui/skeleton";
import type { BorrowingResponse } from "@/types";

export default function MyBorrowings() {
  const { employeeId } = useUser();
  const queryClient = useQueryClient();
  const [returningId, setReturningId] = useState<string | null>(null);

  const { data: borrowings, isLoading } = useQuery<BorrowingResponse[]>({
    queryKey: ["my-borrowings", employeeId],
    queryFn: () => borrowingApi.getMyBorrows(employeeId!),
    enabled: !!employeeId,
    refetchInterval: 30000,
  });

  const returnMutation = useMutation({
    mutationFn: ({ borrowingId, bookId }: { borrowingId: string; bookId: string }) =>
      borrowingApi.returnBook(borrowingId, bookId),
    onSuccess: (_, variables) => {
      const borrowing = borrowings?.find(b => b.id === variables.borrowingId);
      toast.success("Book returned successfully!", {
        description: borrowing ? `"${borrowing.bookName}" has been returned.` : "Book returned successfully.",
        icon: <CheckCircle2 className="h-4 w-4 text-green-500" />,
      });
      queryClient.invalidateQueries({ queryKey: ["my-borrowings"] });
      queryClient.invalidateQueries({ queryKey: ["books"] });
    },
    onError: (err: Error) => {
      toast.error("Failed to return book", { description: err.message });
    },
    onSettled: () => setReturningId(null),
  });

  const active = borrowings?.filter((b) => {
    const status = String(b.status).toUpperCase();
    return status === "BORROWED" || status === "1" || status === "ACTIVE";
  }) || [];

  const history = borrowings?.filter((b) => {
    const status = String(b.status).toUpperCase();
    return status === "RETURNED" || status === "0";
  }) || [];

  const handleReturn = (id: string, bookId: string) => {
    setReturningId(id);
    returnMutation.mutate({ borrowingId: id, bookId });
  };

  if (!employeeId) {
    return (
      <div className="flex min-h-[60vh] items-center justify-center">
        <EmptyState
          icon={<History className="h-8 w-8" />}
          title="Sign in to view borrowings"
          description="Connect your member account to see books you've borrowed."
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
        <PageHeader title="My Books" description="Loading..." />
        <div className="space-y-4">
          <Skeleton className="h-32 w-full" />
          <Skeleton className="h-48 w-full" />
        </div>
      </div>
    );
  }

  return (
    <div className="space-y-6">
      <PageHeader
        title="My Books"
        description="Track your active borrowings and reading history"
      />

      {/* Stats row */}
      <div className="grid grid-cols-1 gap-4 sm:grid-cols-3">
        <Stat
          label="Active Borrowings"
          value={active.length}
          icon={<BookOpen className="h-5 w-5" />}
          variant={active.length > 0 ? "success" : "default"}
        />
        <Stat
          label="Books Returned"
          value={history.length}
          icon={<CheckCircle2 className="h-5 w-5" />}
        />
        <Stat
          label="Reading Days"
          value={history.length > 0 ? `${history.length * 14}` : "0"}
          icon={<Clock className="h-5 w-5" />}
          description="Total days"
        />
      </div>

      {/* Active borrowings */}
      <Card className="border-0 shadow-lg overflow-hidden">
        <CardHeader className="border-b bg-gradient-to-r from-primary/5 to-emerald-50/50 py-4">
          <div className="flex items-center justify-between">
            <div className="flex items-center gap-3">
              <div className="flex h-10 w-10 items-center justify-center rounded-xl bg-primary/10">
                <BookOpen className="h-5 w-5 text-primary" />
              </div>
              <div>
                <CardTitle className="text-lg">Active Borrowings</CardTitle>
                <p className="text-xs text-muted-foreground">Books you currently have</p>
              </div>
            </div>
            {active.length > 0 && (
              <Badge variant="success" className="text-sm px-3 py-1">
                {active.length} book{active.length !== 1 ? "s" : ""}
              </Badge>
            )}
          </div>
        </CardHeader>
        <CardContent className="p-0">
          {active.length === 0 ? (
            <div className="flex flex-col items-center justify-center py-16 px-6 text-center animate-in fade-in-0 slide-in-from-bottom-4 duration-500">
              <div className="mb-4 text-primary">
                <BookOpen className="h-8 w-8" />
              </div>
              <p className="text-base font-medium text-foreground">No active borrowings</p>
              <p className="mt-1 text-sm text-muted">Start exploring our catalog to borrow books</p>
              <Link to="/" className="mt-6">
                <Button size="sm">
                  Browse Catalog
                </Button>
              </Link>
            </div>
          ) : (
            <div className="divide-y">
              {active.map((b, index) => (
                <div 
                  key={b.id} 
                  className="flex flex-col gap-4 p-5 sm:flex-row sm:items-center sm:justify-between animate-in fade-in-0 slide-in-from-bottom-2"
                  style={{ animationDelay: `${index * 100}ms`, animationFillMode: "backwards" }}
                >
                  <div className="flex items-start gap-4">
                    <div className="flex h-12 w-12 items-center justify-center rounded-xl bg-gradient-to-br from-primary/10 to-emerald-50 text-primary">
                      <BookOpen className="h-5 w-5" />
                    </div>
                    <div className="space-y-1">
                      <p className="font-semibold text-base">{b.bookName || 'Unknown Book'}</p>
                      <p className="text-sm text-muted-foreground">{b.bookAuthor || 'Unknown Author'}</p>
                      <div className="flex items-center gap-3 text-xs text-muted-foreground">
                        <span className="flex items-center gap-1">
                          <Calendar className="h-3 w-3" />
                          Borrowed {b.borrwingDate ? new Date(b.borrwingDate).toLocaleDateString() : 'N/A'}
                        </span>
                        <span className="flex items-center gap-1">
                          <Clock className="h-3 w-3" />
                          Due {b.returnData ? new Date(b.returnData).toLocaleDateString() : 'N/A'}
                        </span>
                      </div>
                    </div>
                  </div>
                  <div className="flex items-center gap-3 sm:ml-auto">
                    <Badge variant="success" className="text-sm px-3 py-1">ACTIVE</Badge>
                    <Button
                      size="sm"
                      onClick={() => handleReturn(b.id, b.bookId)}
                      disabled={returningId === b.id}
                    >
                      {returningId === b.id ? (
                        <>
                          <Clock className="mr-1.5 h-3.5 w-3.5 animate-spin" />
                          Returning...
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
              ))}
            </div>
          )}
        </CardContent>
      </Card>

      {/* Borrowing history */}
      <Card className="border-0 shadow-lg overflow-hidden">
        <CardHeader className="border-b bg-muted/30 py-4">
          <div className="flex items-center gap-3">
            <div className="flex h-10 w-10 items-center justify-center rounded-xl bg-muted">
              <History className="h-5 w-5 text-muted-foreground" />
            </div>
            <div>
              <CardTitle className="text-lg">Borrowing History</CardTitle>
              <p className="text-xs text-muted-foreground">Previously borrowed books</p>
            </div>
          </div>
        </CardHeader>
        <CardContent className="p-0">
          {history.length === 0 ? (
            <div className="flex flex-col items-center justify-center py-12 px-6 text-center animate-in fade-in-0 duration-500">
              <div className="mb-3 text-primary">
                <Sparkles className="h-5 w-5" />
              </div>
              <p className="text-sm text-muted">No borrowing history yet</p>
            </div>
          ) : (
            <div className="divide-y">
              {history.map((b, index) => (
                <div 
                  key={b.id} 
                  className="flex items-center justify-between p-5 animate-in fade-in-0 slide-in-from-bottom-2"
                  style={{ animationDelay: `${(active.length + index) * 50}ms`, animationFillMode: "backwards" }}
                >
                  <div className="flex items-center gap-4">
                    <div className="flex h-10 w-10 items-center justify-center rounded-lg bg-green-100 text-green-600">
                      <CheckCircle2 className="h-4 w-4" />
                    </div>
                    <div className="space-y-0.5">
                      <p className="font-medium">{b.bookName || 'Unknown Book'}</p>
                      <p className="text-xs text-muted-foreground">
                        Borrowed: {b.borrwingDate ? new Date(b.borrwingDate).toLocaleDateString() : 'N/A'}
                      </p>
                    </div>
                  </div>
                  <Badge variant="default" className="text-xs">RETURNED</Badge>
                </div>
              ))}
            </div>
          )}
        </CardContent>
      </Card>
    </div>
  );
}
