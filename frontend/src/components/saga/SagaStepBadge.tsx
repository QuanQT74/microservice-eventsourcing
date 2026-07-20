import { cn } from "@/lib/utils";
import type { SagaStep } from "@/mocks/schema";

const STATUS_STYLES: Record<SagaStep["status"], string> = {
  SUCCESS: "border-emerald-600/50 bg-emerald-500/10 text-emerald-400",
  FAILED: "border-red-600/50 bg-red-500/10 text-red-400",
  PENDING: "border-amber-600/50 bg-amber-500/10 text-amber-400",
};

export default function SagaStepBadge({ step }: { step: SagaStep }) {
  return (
    <span
      className={cn(
        "inline-flex items-center gap-1.5 rounded-md border px-2.5 py-1 text-xs font-medium",
        STATUS_STYLES[step.status]
      )}
      title={step.error}
    >
      <span
        className={cn(
          "size-1.5 rounded-full",
          step.status === "SUCCESS" && "bg-emerald-400",
          step.status === "FAILED" && "bg-red-400",
          step.status === "PENDING" && "bg-amber-400 animate-pulse"
        )}
      />
      {step.name}
      {step.duration > 0 && (
        <span className="opacity-60">{step.duration}ms</span>
      )}
      {step.error && <span className="opacity-80">— {step.error}</span>}
    </span>
  );
}
