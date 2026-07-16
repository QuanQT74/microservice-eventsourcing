import { cn } from "@/lib/utils";

interface PageHeaderProps {
  title: string;
  description?: string;
  children?: React.ReactNode;
  className?: string;
  badge?: string;
}

export default function PageHeader({ title, description, children, className, badge }: PageHeaderProps) {
  return (
    <div className={cn("relative", className)}>
      <div className="flex flex-col gap-2 sm:flex-row sm:items-end sm:justify-between">
        <div className="space-y-1">
          {/* Title with gradient accent */}
          <div className="flex items-center gap-3">
            <div className="h-1 w-8 rounded-full bg-gradient-to-r from-primary to-emerald-500" />
            <h1 className="text-2xl font-bold tracking-tight text-foreground sm:text-3xl">
              {title}
            </h1>
            {badge && (
              <span className="inline-flex items-center rounded-full bg-primary/10 px-2.5 py-0.5 text-xs font-semibold text-primary">
                {badge}
              </span>
            )}
          </div>
          {description && (
            <p className="pl-11 text-sm text-muted-foreground sm:text-base">{description}</p>
          )}
        </div>
        {children && (
          <div className="flex shrink-0 items-center gap-2 pt-2 sm:pt-0">
            {children}
          </div>
        )}
      </div>
    </div>
  );
}
