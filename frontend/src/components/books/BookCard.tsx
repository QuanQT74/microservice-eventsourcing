import { Link } from "react-router-dom";
import { BookMarked, User, Star, Clock } from "lucide-react";
import type { Book } from "@/types";
import { Card, CardContent } from "@/components/ui/card";
import { Badge } from "@/components/ui/badge";
import { Button } from "@/components/ui/button";

interface BookCardProps {
  book: Book;
}

const COVER_GRADIENTS = [
  "from-emerald-600 to-teal-800",
  "from-indigo-600 to-violet-800",
  "from-amber-600 to-orange-800",
  "from-rose-600 to-pink-800",
  "from-cyan-600 to-blue-800",
  "from-fuchsia-600 to-purple-800",
];

const COVER_ACCENTS = [
  "bg-emerald-400/20",
  "bg-indigo-400/20",
  "bg-amber-400/20",
  "bg-rose-400/20",
  "bg-cyan-400/20",
  "bg-fuchsia-400/20",
];

function getGradient(id: string) {
  const index = id.split("").reduce((acc, c) => acc + c.charCodeAt(0), 0);
  return {
    gradient: COVER_GRADIENTS[index % COVER_GRADIENTS.length],
    accent: COVER_ACCENTS[index % COVER_ACCENTS.length],
  };
}

export default function BookCard({ book }: BookCardProps) {
  const { gradient, accent } = getGradient(book.id);

  return (
    <Card className="group overflow-hidden border-0 bg-card shadow-md transition-all duration-500 hover:-translate-y-2 hover:shadow-2xl hover:shadow-primary/10">
      {/* Cover section */}
      <div className={`relative h-44 bg-gradient-to-br ${gradient} p-5`}>
        {/* Overlay patterns */}
        <div className="absolute inset-0 bg-black/10" />
        <div className={`absolute -right-6 -top-6 h-32 w-32 rounded-full ${accent} blur-2xl`} />
        <div className="absolute -bottom-8 -left-8 h-24 w-24 rounded-full bg-white/10 blur-xl" />

        {/* Status badge */}
        <Badge
          variant={book.isReady ? "success" : "danger"}
          className="absolute right-3 top-3 z-10 shadow-lg"
        >
          {book.isReady ? (
            <span className="flex items-center gap-1">
              <span className="h-1.5 w-1.5 rounded-full bg-white animate-pulse" />
              Available
            </span>
          ) : (
            <span className="flex items-center gap-1">
              <Clock className="h-3 w-3" />
              Borrowed
            </span>
          )}
        </Badge>

        {/* Book icon */}
        <div className="relative flex h-full flex-col justify-between">
          <div className="flex items-start justify-between">
            <div className="flex h-10 w-10 items-center justify-center rounded-lg bg-white/20 backdrop-blur-sm">
              <BookMarked className="h-5 w-5 text-white" />
            </div>
            <div className="flex items-center gap-1 rounded-full bg-white/20 px-2 py-1 text-xs font-medium text-white backdrop-blur-sm">
              <Star className="h-3 w-3 fill-current" />
              4.8
            </div>
          </div>

          <div className="space-y-2">
            <p className="line-clamp-2 text-lg font-bold leading-tight text-white drop-shadow-sm">
              {book.name}
            </p>
            <p className="flex items-center gap-1.5 text-sm text-white/90">
              <User className="h-3.5 w-3.5" />
              {book.author}
            </p>
          </div>
        </div>
      </div>

      {/* Content section */}
      <CardContent className="p-4">
        <div className="mb-3 flex items-center justify-between">
          <div className="flex items-center gap-2">
            <span className="text-xs text-muted-foreground">ID:</span>
            <code className="rounded bg-muted px-1.5 py-0.5 text-xs font-mono text-muted-foreground">
              {book.id.slice(0, 8)}...
            </code>
          </div>
          {book.isReady && (
            <span className="text-xs font-medium text-green-600">Ready to borrow</span>
          )}
        </div>

        <div className="flex gap-2">
          <Link to={`/books/${book.id}`} className="flex-1">
            <Button
              size="sm"
              variant={book.isReady ? "gradient" : "outline"}
              className="w-full"
              disabled={!book.isReady}
            >
              {book.isReady ? "View & Borrow" : "View Details"}
            </Button>
          </Link>
        </div>
      </CardContent>

      {/* Hover reveal bar */}
      <div className="h-1 w-0 bg-gradient-to-r from-primary to-emerald-500 transition-all duration-500 group-hover:w-full" />
    </Card>
  );
}
