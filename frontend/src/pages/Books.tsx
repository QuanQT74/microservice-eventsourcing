import { useState } from "react";
import { useQuery } from "@tanstack/react-query";
import { Search, BookOpen, Sparkles, Filter, X } from "lucide-react";
import { booksApi } from "@/api/books";
import BookCard from "@/components/books/BookCard";
import PageHeader from "@/components/shared/PageHeader";
import EmptyState from "@/components/shared/EmptyState";
import { Input } from "@/components/ui/input";
import { Skeleton } from "@/components/ui/skeleton";
import { Badge } from "@/components/ui/badge";
import { Button } from "@/components/ui/button";

export default function Books() {
  const [search, setSearch] = useState("");
  const [filter, setFilter] = useState<"all" | "available" | "borrowed">("all");
  const [showFilters, setShowFilters] = useState(false);

  const { data: books = [], isLoading, isError, error } = useQuery({
    queryKey: ["books"],
    queryFn: booksApi.getAll,
    retry: 1,
  });

  const filtered = books.filter((book) => {
    const matchesSearch =
      book.name.toLowerCase().includes(search.toLowerCase()) ||
      book.author.toLowerCase().includes(search.toLowerCase());
    const matchesFilter =
      filter === "all" ||
      (filter === "available" && book.isReady) ||
      (filter === "borrowed" && !book.isReady);
    return matchesSearch && matchesFilter;
  });

  const availableCount = books.filter((b) => b.isReady).length;
  const borrowedCount = books.length - availableCount;

  const clearSearch = () => setSearch("");

  return (
    <div className="space-y-8">
      {/* Hero Banner */}
      <section className="relative overflow-hidden rounded-3xl bg-gradient-to-br from-primary via-emerald-600 to-teal-700 px-6 py-12 text-white shadow-xl sm:px-10 sm:py-16">
        {/* Decorative elements */}
        <div className="absolute -right-12 -top-12 h-48 w-48 rounded-full bg-white/10 blur-2xl" />
        <div className="absolute -bottom-10 -left-10 h-40 w-40 rounded-full bg-accent/20 blur-xl" />
        <div className="absolute right-1/4 top-1/4 h-32 w-32 rounded-full bg-emerald-400/10 blur-3xl" />

        <div className="relative max-w-2xl">
          <div className="mb-4 inline-flex items-center gap-2 rounded-full bg-white/15 px-4 py-1.5 text-xs font-medium backdrop-blur-sm">
            <Sparkles className="h-3.5 w-3.5" />
            Digital Library Collection
          </div>
          <h1 className="text-4xl font-bold tracking-tight sm:text-5xl text-balance mb-4">
            Discover your next great read
          </h1>
          <p className="text-base text-white/80 sm:text-lg">
            Browse our catalog, borrow instantly, and manage your books — all in one place.
          </p>
          {!isLoading && !isError && (
            <div className="mt-6 flex items-center gap-4">
              <div className="rounded-xl bg-white/15 backdrop-blur-sm px-4 py-2">
                <span className="text-2xl font-bold">{books.length}</span>
                <span className="ml-2 text-sm text-white/80">Total Books</span>
              </div>
              <div className="rounded-xl bg-white/15 backdrop-blur-sm px-4 py-2">
                <span className="text-2xl font-bold">{availableCount}</span>
                <span className="ml-2 text-sm text-white/80">Available</span>
              </div>
            </div>
          )}
        </div>
      </section>

      {/* Search and Filters */}
      <div className="space-y-4">
        <PageHeader 
          title="Catalog" 
          description="Search and filter books in our collection"
        >
          <div className="flex items-center gap-2">
            {/* Search input */}
            <div className="relative w-full sm:w-80">
              <Search className="absolute left-4 top-1/2 h-4 w-4 -translate-y-1/2 text-muted-foreground" />
              <Input
                placeholder="Search by title or author..."
                value={search}
                onChange={(e) => setSearch(e.target.value)}
                className="h-11 pl-11 pr-10 rounded-xl"
              />
              {search && (
                <button
                  onClick={clearSearch}
                  className="absolute right-3 top-1/2 -translate-y-1/2 rounded-full p-1 hover:bg-muted"
                >
                  <X className="h-4 w-4 text-muted-foreground" />
                </button>
              )}
            </div>
          </div>
        </PageHeader>

        {/* Filter buttons */}
        <div className="flex flex-wrap items-center gap-3">
          <div className="flex items-center gap-1 rounded-xl bg-muted/50 p-1">
            {(["all", "available", "borrowed"] as const).map((f) => (
              <button
                key={f}
                onClick={() => setFilter(f)}
                className={[
                  "rounded-lg px-4 py-2 text-sm font-medium transition-all",
                  filter === f
                    ? "bg-primary text-primary-foreground shadow-md"
                    : "text-muted-foreground hover:bg-background",
                ].join(" ")}
              >
                {f === "all" ? "All Books" : f === "available" ? "Available" : "Borrowed"}
                {f === "all" && <span className="ml-1.5 text-xs opacity-70">({books.length})</span>}
                {f === "available" && <span className="ml-1.5 text-xs opacity-70">({availableCount})</span>}
                {f === "borrowed" && <span className="ml-1.5 text-xs opacity-70">({borrowedCount})</span>}
              </button>
            ))}
          </div>
          
          {!isLoading && (
            <Badge variant="default" className="text-sm px-3 py-1.5">
              {filtered.length} result{filtered.length !== 1 ? "s" : ""}
              {search && ` for "${search}"`}
            </Badge>
          )}
        </div>
      </div>

      {/* Loading state */}
      {isLoading && (
        <div className="grid grid-cols-1 gap-6 sm:grid-cols-2 lg:grid-cols-3">
          {Array.from({ length: 6 }).map((_, i) => (
            <div key={i} className="space-y-3 animate-pulse">
              <Skeleton className="h-44 w-full rounded-2xl" />
              <Skeleton className="h-5 w-3/4" />
              <Skeleton className="h-4 w-1/2" />
            </div>
          ))}
        </div>
      )}

      {/* Error state */}
      {isError && (
        <EmptyState
          icon={<BookOpen className="h-8 w-8" />}
          title="Unable to load books"
          description={
            (error as Error)?.message ??
            "Make sure the API Gateway (port 8085) and Book Service are running."
          }
          action={
            <Button variant="outline" onClick={() => window.location.reload()}>
              Try Again
            </Button>
          }
        />
      )}

      {/* Empty state */}
      {!isLoading && !isError && filtered.length === 0 && (
        <EmptyState
          icon={<BookOpen className="h-8 w-8" />}
          title="No books found"
          description={search ? "Try a different search term or clear your filters." : "The catalog is empty."}
          action={
            search ? (
              <Button variant="outline" onClick={clearSearch}>
                Clear Search
              </Button>
            ) : undefined
          }
        />
      )}

      {/* Books grid */}
      {!isLoading && !isError && filtered.length > 0 && (
        <div className="grid grid-cols-1 gap-6 sm:grid-cols-2 lg:grid-cols-3">
          {filtered.map((book, index) => (
            <div
              key={book.id}
              className="animate-in fade-in-0 slide-in-from-bottom-4"
              style={{ 
                animationDelay: `${Math.min(index * 50, 300)}ms`,
                animationFillMode: "backwards" 
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
