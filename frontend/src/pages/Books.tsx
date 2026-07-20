import { useState } from "react";
import { useQuery } from "@tanstack/react-query";
import { BookOpen, Search, X } from "lucide-react";
import { booksApi } from "@/api/books";
import BookCard from "@/components/books/BookCard";
import EmptyState from "@/components/shared/EmptyState";
import { Input } from "@/components/ui/input";
import { Skeleton } from "@/components/ui/skeleton";
import { Button } from "@/components/ui/button";
import { cn } from "@/lib/utils";

export default function Books() {
  const [search, setSearch] = useState("");
  const [filter, setFilter] = useState<"all" | "available" | "borrowed">("all");

  const { data: books = [], isLoading, isError, error } = useQuery({
    queryKey: ["books"],
    queryFn: booksApi.getAll,
    retry: 1,
  });

  const filtered = books.filter((book) => {
    const q = search.toLowerCase();
    const matchesSearch =
      book.name.toLowerCase().includes(q) || book.author.toLowerCase().includes(q);
    const matchesFilter =
      filter === "all" ||
      (filter === "available" && book.isReady) ||
      (filter === "borrowed" && !book.isReady);
    return matchesSearch && matchesFilter;
  });

  const availableCount = books.filter((b) => b.isReady).length;
  const borrowedCount = books.length - availableCount;

  return (
    <div className="space-y-8 animate-in fade-in-0 duration-500">
      <section className="relative overflow-hidden rounded-[1.75rem] border border-border/60 bg-[#0B1F33] px-6 py-10 text-white sm:px-10 sm:py-12">
        <div
          className="pointer-events-none absolute inset-0 opacity-45"
          style={{
            backgroundImage:
              "radial-gradient(circle at 12% 20%, rgba(0,184,148,0.35), transparent 42%), radial-gradient(circle at 88% 15%, rgba(0,102,255,0.25), transparent 40%)",
          }}
        />
        <div className="relative max-w-2xl space-y-4">
          <p className="text-xs font-semibold uppercase tracking-[0.22em] text-emerald-300/90">
            Catalog
          </p>
          <h1 className="text-4xl font-extrabold tracking-tight sm:text-5xl">
            Browse the shelf
          </h1>
          <p className="max-w-lg text-sm text-white/70 sm:text-base">
            Search the collection, check availability, and borrow in one place.
          </p>
          {!isLoading && !isError && (
            <div className="flex flex-wrap gap-3 pt-1">
              <div className="rounded-xl border border-white/10 bg-white/5 px-4 py-2.5">
                <span className="text-xl font-bold tabular-nums">{books.length}</span>
                <span className="ml-2 text-xs uppercase tracking-wider text-white/55">titles</span>
              </div>
              <div className="rounded-xl border border-white/10 bg-white/5 px-4 py-2.5">
                <span className="text-xl font-bold tabular-nums">{availableCount}</span>
                <span className="ml-2 text-xs uppercase tracking-wider text-white/55">available</span>
              </div>
            </div>
          )}
        </div>
      </section>

      <div className="flex flex-col gap-4 sm:flex-row sm:items-center sm:justify-between">
        <div className="relative w-full sm:max-w-md">
          <Search className="absolute left-3.5 top-1/2 h-4 w-4 -translate-y-1/2 text-muted-foreground" />
          <Input
            placeholder="Search title or author…"
            value={search}
            onChange={(e) => setSearch(e.target.value)}
            className="h-11 rounded-xl border-border/70 bg-white pl-10 pr-10"
          />
          {search && (
            <button
              type="button"
              onClick={() => setSearch("")}
              className="absolute right-3 top-1/2 -translate-y-1/2 rounded-md p-1 text-muted-foreground hover:bg-muted"
            >
              <X className="h-4 w-4" />
            </button>
          )}
        </div>

        <div className="flex items-center gap-1 rounded-xl border border-border/70 bg-white p-1">
          {(
            [
              { id: "all" as const, label: "All", count: books.length },
              { id: "available" as const, label: "Available", count: availableCount },
              { id: "borrowed" as const, label: "On loan", count: borrowedCount },
            ]
          ).map((f) => (
            <button
              key={f.id}
              type="button"
              onClick={() => setFilter(f.id)}
              className={cn(
                "rounded-lg px-3 py-2 text-sm font-semibold transition",
                filter === f.id
                  ? "bg-[#0B1F33] text-white"
                  : "text-muted-foreground hover:text-foreground"
              )}
            >
              {f.label}
              <span className="ml-1.5 tabular-nums opacity-70">{f.count}</span>
            </button>
          ))}
        </div>
      </div>

      {isLoading && (
        <div className="grid grid-cols-2 gap-4 sm:grid-cols-3 lg:grid-cols-4">
          {Array.from({ length: 8 }).map((_, i) => (
            <div key={i} className="space-y-3">
              <Skeleton className="aspect-[3/4] w-full rounded-2xl" />
              <Skeleton className="h-4 w-3/4" />
              <Skeleton className="h-3 w-1/2" />
            </div>
          ))}
        </div>
      )}

      {isError && (
        <EmptyState
          icon={<BookOpen className="h-8 w-8" />}
          title="Unable to load books"
          description={
            (error as Error)?.message ??
            "Make sure the API Gateway and Book Service are running."
          }
          action={
            <Button variant="outline" onClick={() => window.location.reload()}>
              Try again
            </Button>
          }
        />
      )}

      {!isLoading && !isError && filtered.length === 0 && (
        <EmptyState
          icon={<BookOpen className="h-8 w-8" />}
          title="No books found"
          description={
            search ? "Try another title or author." : "The catalog is empty."
          }
          action={
            search ? (
              <Button variant="outline" onClick={() => setSearch("")}>
                Clear search
              </Button>
            ) : undefined
          }
        />
      )}

      {!isLoading && !isError && filtered.length > 0 && (
        <div className="grid grid-cols-2 gap-4 sm:grid-cols-3 lg:grid-cols-4">
          {filtered.map((book, index) => (
            <div
              key={book.id}
              className="animate-in fade-in-0 slide-in-from-bottom-2"
              style={{
                animationDelay: `${Math.min(index * 40, 280)}ms`,
                animationFillMode: "backwards",
              }}
            >
              <BookCard book={book} />
            </div>
          ))}
        </div>
      )}
    </div>
  );
}
