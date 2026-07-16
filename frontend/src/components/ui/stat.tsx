import * as React from "react";
import { cn } from "@/lib/utils";
import { Card } from "@/components/ui/card";
import { Skeleton } from "@/components/ui/skeleton";

interface StatProps {
  label: string;
  value: string | number;
  icon?: React.ReactNode;
  trend?: {
    value: number;
    isPositive: boolean;
  };
  description?: string;
  className?: string;
  isLoading?: boolean;
  variant?: "default" | "success" | "warning" | "danger";
}

const variantStyles = {
  default: "from-primary/5 to-emerald-50/50",
  success: "from-green-50 to-green-100/50",
  warning: "from-yellow-50 to-yellow-100/50",
  danger: "from-red-50 to-red-100/50",
};

export function Stat({
  label,
  value,
  icon,
  trend,
  description,
  className,
  isLoading,
  variant = "default",
}: StatProps) {
  if (isLoading) {
    return (
      <Card className={cn("p-5 shadow-md", className)}>
        <div className="flex items-start justify-between">
          <div className="space-y-2">
            <Skeleton className="h-4 w-24" />
            <Skeleton className="h-8 w-16" />
          </div>
          <Skeleton className="h-10 w-10 rounded-lg" />
        </div>
      </Card>
    );
  }

  return (
    <Card
      className={cn(
        "group relative overflow-hidden p-5 shadow-md transition-all duration-300 hover:shadow-xl",
        className
      )}
    >
      {/* Background gradient */}
      <div className={cn("absolute inset-0 bg-gradient-to-br opacity-60", variantStyles[variant])} />
      <div className="absolute -right-4 -top-4 h-24 w-24 rounded-full bg-primary/5 blur-2xl" />

      <div className="relative flex items-start justify-between">
        <div className="space-y-1">
          <p className="text-sm font-medium text-muted-foreground">{label}</p>
          <div className="flex items-baseline gap-2">
            <p className="text-3xl font-bold tracking-tight text-foreground">{value}</p>
            {trend && (
              <span
                className={cn(
                  "flex items-center text-xs font-semibold",
                  trend.isPositive ? "text-green-600" : "text-red-600"
                )}
              >
                <svg
                  className={cn("h-3 w-3", !trend.isPositive && "rotate-180")}
                  fill="none"
                  viewBox="0 0 24 24"
                  stroke="currentColor"
                >
                  <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M5 15l7-7 7 7" />
                </svg>
                {Math.abs(trend.value)}%
              </span>
            )}
          </div>
          {description && (
            <p className="text-xs text-muted-foreground">{description}</p>
          )}
        </div>

        {icon && (
          <div className="flex h-12 w-12 items-center justify-center rounded-xl bg-primary/10 text-primary shadow-sm transition-transform duration-300 group-hover:scale-110 group-hover:bg-primary group-hover:text-primary-foreground">
            {icon}
          </div>
        )}
      </div>

      {/* Bottom accent line */}
      <div className="absolute bottom-0 left-0 h-1 w-0 bg-gradient-to-r from-primary to-emerald-500 transition-all duration-500 group-hover:w-full" />
    </Card>
  );
}

interface StatGridProps {
  children: React.ReactNode;
  columns?: 1 | 2 | 3 | 4;
  className?: string;
}

export function StatGrid({ children, columns = 4, className }: StatGridProps) {
  const gridCols = {
    1: "grid-cols-1",
    2: "grid-cols-1 sm:grid-cols-2",
    3: "grid-cols-1 sm:grid-cols-2 lg:grid-cols-3",
    4: "grid-cols-1 sm:grid-cols-2 lg:grid-cols-4",
  };

  return (
    <div className={cn("grid gap-4", gridCols[columns], className)}>
      {React.Children.map(children, (child, index) => (
        <div
          key={index}
          className="animate-in fade-in-0 slide-in-from-bottom-4"
          style={{ animationDelay: `${index * 100}ms`, animationFillMode: "backwards" }}
        >
          {child}
        </div>
      ))}
    </div>
  );
}
