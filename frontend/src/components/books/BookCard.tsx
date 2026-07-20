import { Link } from "react-router-dom";
import { BookMarked, Clock } from "lucide-react";
import { useState } from "react";
import type { Book } from "@/types";
import { cn } from "@/lib/utils";

interface BookCardProps {
  book: Book;
}

const FALLBACKS = [
  "from-[#123047] to-[#1B6B5A]",
  "from-[#1A3A5C] to-[#0F6B8F]",
  "from-[#2A3A2A] to-[#4A6B3A]",
  "from-[#3A2A1A] to-[#8B5A2B]",
];

function fallbackFor(id: string) {
  const n = id.split("").reduce((a, c) => a + c.charCodeAt(0), 0);
  return FALLBACKS[n % FALLBACKS.length];
}

export default function BookCard({ book }: BookCardProps) {
  const [imageError, setImageError] = useState(false);
  const hasImage = Boolean(book.imageUrl) && !imageError;

  return (
    <Link
      to={`/books/${book.id}`}
      className="group flex flex-col overflow-hidden rounded-2xl border border-border/70 bg-white shadow-sm transition duration-300 hover:-translate-y-1 hover:shadow-md"
    >
      <div className="relative aspect-[3/4] overflow-hidden bg-slate-100">
        {hasImage ? (
          <img
            src={book.imageUrl}
            alt={book.name}
            className="h-full w-full object-cover transition duration-500 group-hover:scale-[1.03]"
            onError={() => setImageError(true)}
          />
        ) : (
          <div
            className={cn(
              "flex h-full w-full flex-col justify-between bg-gradient-to-br p-4 text-white",
              fallbackFor(book.id)
            )}
          >
            <BookMarked className="h-5 w-5 text-white/70" />
            <p className="line-clamp-4 text-lg font-bold leading-snug tracking-tight">
              {book.name}
            </p>
          </div>
        )}

        <span
          className={cn(
            "absolute right-2.5 top-2.5 inline-flex items-center gap-1 rounded-full px-2.5 py-1 text-[10px] font-bold uppercase tracking-wider text-white shadow-sm",
            book.isReady ? "bg-emerald-600" : "bg-slate-800/85"
          )}
        >
          {book.isReady ? (
            <>
              <span className="h-1.5 w-1.5 animate-pulse rounded-full bg-white" />
              Available
            </>
          ) : (
            <>
              <Clock className="h-3 w-3" />
              On loan
            </>
          )}
        </span>
      </div>

      <div className="flex flex-1 flex-col gap-1 p-3.5">
        <h3 className="line-clamp-2 text-[15px] font-bold leading-snug tracking-tight text-foreground">
          {book.name}
        </h3>
        <p className="truncate text-sm text-muted-foreground">{book.author}</p>
        <span
          className={cn(
            "mt-auto pt-3 text-xs font-semibold",
            book.isReady ? "text-primary" : "text-muted-foreground"
          )}
        >
          {book.isReady ? "View & borrow →" : "View details →"}
        </span>
      </div>
    </Link>
  );
}
