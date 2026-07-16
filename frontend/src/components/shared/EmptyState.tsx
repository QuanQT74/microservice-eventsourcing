import { BookOpen } from "lucide-react";

interface EmptyStateProps {
  icon?: React.ReactNode;
  title: string;
  description?: string;
  action?: React.ReactNode;
}

export default function EmptyState({ icon, title, description, action }: EmptyStateProps) {
  return (
    <div className="glass-card glass-card-elevated px-6 py-16 text-center animate-in fade-in-0 slide-in-from-bottom-4 duration-500">
      <div className="mb-4 text-primary">
        {icon ?? <BookOpen className="h-7 w-7" />}
      </div>
      <h3 className="text-lg font-semibold text-foreground">{title}</h3>
      {description && (
        <p className="mt-2 max-w-sm mx-auto text-sm text-muted">{description}</p>
      )}
      {action && <div className="mt-6">{action}</div>}
    </div>
  );
}