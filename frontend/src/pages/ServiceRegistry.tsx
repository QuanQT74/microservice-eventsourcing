import { useQuery } from "@tanstack/react-query";
import { CheckCircle, Clock, XCircle, HelpCircle, MoreHorizontal } from "lucide-react";
import { fetchServices, type Service } from "@/mocks/services";
import { ArrowRight } from "lucide-react";
import { Skeleton } from "@/components/ui/skeleton";

// Icon + Label badge with proper semantics
function StatusBadge({ status }: { status: Service["status"] }) {
  const config = {
    UP: {
      icon: CheckCircle,
      label: "UP",
      colorClass: "bg-success/10 text-success border-success/20",
    },
    STARTING: {
      icon: Clock,
      label: "STARTING",
      colorClass: "bg-warning/10 text-warning border-warning/20",
    },
    DOWN: {
      icon: XCircle,
      label: "DOWN",
      colorClass: "bg-danger/10 text-danger border-danger/20",
    },
    UNKNOWN: {
      icon: HelpCircle,
      label: "UNKNOWN",
      colorClass: "bg-muted/10 text-muted border-muted/20",
    },
    STOPPING: {
      icon: Clock,
      label: "STOPPING",
      colorClass: "bg-warning/10 text-warning border-warning/20",
    },
  };

  const { icon: Icon, label, colorClass } = config[status];

  return (
    <span
      className={`inline-flex items-center gap-1.5 px-3 py-1 rounded-full text-xs font-semibold border ${colorClass}`}
      role="status"
      aria-label={`Service status: ${status}`}
    >
      <Icon strokeWidth={2.5} size={14} />
      <span>{label}</span>
    </span>
  );
}

// Table column action button with proper styling
function ActionButton() {
  return (
    <button
      className="inline-flex items-center gap-1.5 px-3 py-1.5 text-xs font-medium text-primary border border-primary/20 rounded-md hover:bg-primary/5 hover:border-primary/30 transition-all duration-150"
      aria-label="View service details"
    >
      Details
      <ArrowRight strokeWidth={2} size={14} />
    </button>
  );
}

// Service row component with hover states
function ServiceRow({ svc }: { svc: Service }) {
  return (
    <tr
      key={svc.id}
      className="hover:bg-primary/5 transition-colors duration-200 group"
    >
      <td className="py-4 pl-4 pr-3">
        <span className="font-semibold text-foreground group-hover:text-primary transition-colors">
          {svc.name}
        </span>
        {svc.namespace && (
          <span className="ml-2 text-xs text-muted ml-2">
            <code className="bg-muted px-1.5 py-0.5 rounded text-muted-foreground/70">
              {svc.namespace}
            </code>
          </span>
        )}
      </td>
      <td className="py-4">
        <StatusBadge status={svc.status} />
      </td>
      <td className="py-4 text-muted font-mono text-sm">{svc.instances}</td>
      <td className="py-4 text-muted">{svc.health}</td>
      <td className="py-4 text-muted text-sm">
        {new Date(svc.lastHeartbeat).toLocaleString()}
      </td>
      <td className="py-4 pr-6 text-right">
        <ActionButton />
      </td>
    </tr>
  );
}

// Skeleton row component
function ServiceRowSkeleton() {
  return (
    <tr>
      <td className="py-4 pl-4 pr-3">
        <Skeleton className="h-4 w-32 mb-2" />
        <Skeleton className="h-3 w-40" />
      </td>
      <td className="py-4">
        <Skeleton className="h-6 w-16 rounded-full" />
      </td>
      <td className="py-4">
        <Skeleton className="h-4 w-8" />
      </td>
      <td className="py-4">
        <Skeleton className="h-4 w-16" />
      </td>
      <td className="py-4">
        <Skeleton className="h-4 w-40" />
      </td>
      <td className="py-4 pr-6 text-right">
        <Skeleton className="h-7 w-16 mx-auto" />
      </td>
    </tr>
  );
}

export default function ServiceRegistry() {
  const { data: services = [], isLoading, isError, error } = useQuery({
    queryKey: ["services"],
    queryFn: fetchServices,
  });

  if (isError) {
    return (
      <div className="p-6 text-center border border-warning/30 rounded-xl bg-warning/5">
        <XCircle
          className="mx-auto mb-3 h-8 w-8 text-warning"
          strokeWidth={2}
        />
        <p className="text-warning font-medium">Failed to load services</p>
        <p className="text-sm text-muted mt-1">{String(error)}</p>
      </div>
    );
  }

  return (
    <div className="max-w-7xl mx-auto p-4 sm:p-6 lg:p-8">
      {/* Page header with glassmorphism */}
      <div className="mb-8">
        <h1 className="text-3xl font-display font-bold text-foreground mb-2">
          Service Registry
        </h1>
        <p className="text-muted text-sm">
          Monitor and manage all microservices in the EventSourcing system
        </p>
      </div>

      {/* Bento grid layout - all services in one card for this view */}
      <div className="glass-card glass-card-elevated overflow-hidden">
        {/* Card header with gradient */}
        <div className="glass card-header">
          <div className="flex items-center justify-between">
            <div>
              <h2 className="card-title mb-1">All Services</h2>
              <p className="text-xs text-muted max-w-md">
                Displays real-time status, health metrics, and instance count for
                each service. Hover rows for quick interactions.
              </p>
            </div>
            <div className="flex items-center gap-2 flex-shrink-0">
              <div className="flex -space-x-2" aria-label="Service count">
                <div className="w-7 h-7 rounded-full bg-primary/10 border-2 border-background flex items-center justify-center text-xs font-semibold text-primary">
                  {services.length}
                </div>
              </div>
              <button
                className="p-2 text-muted hover:text-foreground hover:bg-muted/50 rounded-md transition-colors"
                aria-label="Refresh services"
              >
                <MoreHorizontal size={16} strokeWidth={2} />
              </button>
            </div>
          </div>
        </div>

        {/* Loading state with skeleton */}
        {isLoading ? (
          <div className="p-6">
            <div className="space-y-4">
              {Array.from({ length: 5 }).map((_, i) => (
                <ServiceRowSkeleton key={i} />
              ))}
            </div>
          </div>
        ) : (
          /* Table container with scroll support */
          <div className="table-container">
            <table className="table">
              <thead>
                <tr>
                  <th className="pl-4">Service Name</th>
                  <th>Status</th>
                  <th>Instances</th>
                  <th>Health</th>
                  <th>Last Heartbeat</th>
                  <th className="pr-6 text-right">Actions</th>
                </tr>
              </thead>
              <tbody>
                {services.length === 0 ? (
                  <tr>
                    <td
                      colSpan={6}
                      className="py-12 text-center text-muted"
                      role="alert"
                    >
                      <HelpCircle className="mx-auto mb-2 h-8 w-8 opacity-50" />
                      <p className="text-sm font-medium">No services found</p>
                    </td>
                  </tr>
                ) : (
                  services.map((svc) => (
                    <ServiceRow
                      svc={svc}
                      key={svc.id}
                    />
                  ))
                )}
              </tbody>
            </table>
          </div>
        )}

        {/* Card footer with info */}
        <div className="px-6 py-3 bg-muted/30 border-t border-border flex items-center justify-between text-xs text-muted">
          <span>Last updated: {new Date().toLocaleTimeString()}</span>
          <span className="flex items-center gap-1">
            <div className="w-2 h-2 rounded-full bg-success animate-pulse" />
            2 services pinned
          </span>
        </div>
      </div>

      {/* Blur hint for mobile */}
      <p className="text-center text-xs text-muted mt-4 opacity-60 pt-4 border-t border-border/50">
        For best experience on mobile, use landscape mode
      </p>
    </div>
  );
}