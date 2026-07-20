import { cn } from "@/lib/utils";

type FlowStep = {
  id: string;
  label: string;
  detail: string;
  kind: "start" | "query" | "command" | "event" | "end" | "compensate";
};

const BORROW_FLOW: FlowStep[] = [
  { id: "1", label: "BorrowingCreated", detail: "@StartSaga", kind: "start" },
  { id: "2", label: "Check Employee", detail: "GetDetailEmployeeQuery", kind: "query" },
  { id: "3", label: "Check Book", detail: "GetBookDetailQuery", kind: "query" },
  { id: "4", label: "Update Book Status", detail: "UpdateStatusBookCommand", kind: "command" },
  { id: "5", label: "BookUpdatedStatus", detail: "isReady = false", kind: "event" },
  { id: "6", label: "Saga End", detail: "COMPLETED", kind: "end" },
];

const COMPENSATE_FLOW: FlowStep[] = [
  { id: "c1", label: "Fail / Exception", detail: "validation or command error", kind: "compensate" },
  { id: "c2", label: "BookRollBack", detail: "BookRollBackStatusCommand", kind: "command" },
  { id: "c3", label: "Delete Borrowing", detail: "DeleteBorrowingCommand", kind: "command" },
  { id: "c4", label: "Saga End", detail: "ROLLED_BACK", kind: "end" },
];

const RETURN_FLOW: FlowStep[] = [
  { id: "r1", label: "BorrowingReturned", detail: "@StartSaga", kind: "start" },
  { id: "r2", label: "Update Book Status", detail: "isReady = true", kind: "command" },
  { id: "r3", label: "BookUpdatedStatus", detail: "event", kind: "event" },
  { id: "r4", label: "Saga End", detail: "COMPLETED", kind: "end" },
];

const KIND_STYLES: Record<FlowStep["kind"], string> = {
  start: "border-sky-600/40 bg-sky-500/10 text-sky-300",
  query: "border-violet-600/40 bg-violet-500/10 text-violet-300",
  command: "border-amber-600/40 bg-amber-500/10 text-amber-300",
  event: "border-cyan-600/40 bg-cyan-500/10 text-cyan-300",
  end: "border-emerald-600/40 bg-emerald-500/10 text-emerald-300",
  compensate: "border-red-600/40 bg-red-500/10 text-red-300",
};

function FlowRow({ steps, title }: { steps: FlowStep[]; title: string }) {
  return (
    <div className="space-y-3">
      <h3 className="text-sm font-semibold text-foreground">{title}</h3>
      <div className="flex flex-wrap items-stretch gap-2">
        {steps.map((step, index) => (
          <div key={step.id} className="flex items-center gap-2">
            <div
              className={cn(
                "min-w-[140px] rounded-lg border px-3 py-2",
                KIND_STYLES[step.kind]
              )}
            >
              <div className="text-xs font-semibold leading-tight">{step.label}</div>
              <div className="mt-0.5 text-[10px] opacity-70">{step.detail}</div>
            </div>
            {index < steps.length - 1 && (
              <span className="text-muted-foreground" aria-hidden>
                →
              </span>
            )}
          </div>
        ))}
      </div>
    </div>
  );
}

export default function SagaFlowDiagram() {
  return (
    <div className="space-y-6">
      <FlowRow title="Happy path — Borrow" steps={BORROW_FLOW} />
      <FlowRow title="Compensation — Rollback" steps={COMPENSATE_FLOW} />
      <FlowRow title="Happy path — Return" steps={RETURN_FLOW} />

      <div className="flex flex-wrap gap-3 border-t border-border pt-4 text-[11px] text-muted-foreground">
        {(
          [
            ["start", "Start Saga"],
            ["query", "Query"],
            ["command", "Command"],
            ["event", "Event"],
            ["compensate", "Compensate"],
            ["end", "End"],
          ] as const
        ).map(([kind, label]) => (
          <span key={kind} className="inline-flex items-center gap-1.5">
            <span className={cn("size-2 rounded-sm border", KIND_STYLES[kind])} />
            {label}
          </span>
        ))}
      </div>
    </div>
  );
}
