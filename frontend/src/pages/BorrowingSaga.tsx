import { useQuery } from "@tanstack/react-query";
import { fetchSagas } from "@/mocks/sagas";
import { Card, CardContent, CardHeader, CardTitle } from "@/components/ui/card";
import { Badge } from "@/components/ui/badge";

export default function BorrowingSaga() {
  const { data: sagas = [], isLoading } = useQuery({ queryKey: ["sagas"], queryFn: fetchSagas });

  return (
    <div className="space-y-4">
      <Card>
        <CardHeader>
          <CardTitle>Borrowing Saga Flow</CardTitle>
        </CardHeader>
        <CardContent>
          <div className="flex flex-wrap items-center gap-2">
            {["Reserve Book", "Check Availability", "Create Borrowing", "Rollback"].map((step, i) => (
              <span key={step} className="flex items-center gap-2">
                <span className="rounded-full border px-3 py-1 text-xs">{step}</span>
                {i < 3 && <span className="text-xs text-muted-foreground">→</span>}
              </span>
            ))}
          </div>
        </CardContent>
      </Card>

      <Card>
        <CardHeader>
          <CardTitle>Recent Executions</CardTitle>
        </CardHeader>
        <CardContent>
          {isLoading ? (
            <p className="text-sm text-muted-foreground">Loading…</p>
          ) : (
            <div className="space-y-3">
              {sagas.map((saga) => (
                <div key={saga.id} className="rounded-md border p-3">
                  <div className="flex items-center justify-between">
                    <div>
                      <div className="text-sm font-medium">{saga.id}</div>
                      <div className="text-xs text-muted-foreground">
                        Book: {saga.bookId} • User: {saga.userId}
                      </div>
                    </div>
                    <Badge variant={saga.status === "COMPLETED" ? "success" : saga.status === "ROLLED_BACK" ? "danger" : "warning"}>
                      {saga.status.replace("_", " ")}
                    </Badge>
                  </div>
                  <div className="mt-3 flex flex-wrap gap-2">
                    {saga.steps.map((step) => (
                      <span
                        key={step.name}
                        className={[
                          "rounded-full border px-2 py-0.5 text-xs",
                          step.status === "SUCCESS"
                            ? "border-green-700 text-green-400"
                            : step.status === "FAILED"
                            ? "border-red-700 text-red-400"
                            : "border-yellow-700 text-yellow-400",
                        ].join(" ")}
                      >
                        {step.name}: {step.status}
                        {step.error ? ` (${step.error})` : ""}
                      </span>
                    ))}
                  </div>
                </div>
              ))}
            </div>
          )}
        </CardContent>
      </Card>
    </div>
  );
}
