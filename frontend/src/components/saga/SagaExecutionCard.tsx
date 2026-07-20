import { Badge } from "@/components/ui/badge";
import SagaStepBadge from "@/components/saga/SagaStepBadge";
import type { SagaExecution } from "@/mocks/schema";

const STATUS_VARIANT: Record<
  SagaExecution["status"],
  "success" | "danger" | "warning"
> = {
  COMPLETED: "success",
  ROLLED_BACK: "danger",
  IN_PROGRESS: "warning",
};

function formatTime(iso: string) {
  try {
    return new Date(iso).toLocaleString();
  } catch {
    return iso;
  }
}

export default function SagaExecutionCard({ saga }: { saga: SagaExecution }) {
  const durationMs =
    saga.status !== "IN_PROGRESS"
      ? Math.max(0, new Date(saga.endTime).getTime() - new Date(saga.startTime).getTime())
      : null;

  return (
    <div className="rounded-lg border border-border bg-card/40 p-4 transition-colors hover:bg-card/70">
      <div className="flex flex-wrap items-start justify-between gap-3">
        <div className="min-w-0 space-y-1">
          <div className="flex flex-wrap items-center gap-2">
            <span className="truncate font-mono text-sm font-medium text-foreground">
              {saga.id}
            </span>
            <Badge variant={STATUS_VARIANT[saga.status]}>
              {saga.status.replace("_", " ")}
            </Badge>
          </div>
          <div className="text-xs text-muted-foreground">
            Book <span className="font-mono text-foreground/80">{saga.bookId}</span>
            {" · "}
            Employee <span className="font-mono text-foreground/80">{saga.userId}</span>
          </div>
          <div className="text-xs text-muted-foreground">
            Started {formatTime(saga.startTime)}
            {durationMs != null && <> · {durationMs}ms total</>}
          </div>
        </div>
      </div>

      <div className="mt-3 flex flex-wrap gap-2">
        {saga.steps.map((step) => (
          <SagaStepBadge key={`${saga.id}-${step.name}`} step={step} />
        ))}
      </div>
    </div>
  );
}
