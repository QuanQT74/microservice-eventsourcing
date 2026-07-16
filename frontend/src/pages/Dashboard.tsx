import { useQuery } from "@tanstack/react-query";
import { fetchServices } from "@/mocks/services";
import { fetchSagas } from "@/mocks/sagas";
import type { Service } from "@/mocks/schema";
import { Card, CardContent, CardHeader, CardTitle } from "@/components/ui/card";
import MetricCard from "@/components/shared/MetricCard";
import ServicesStatusChart from "@/components/shared/ServicesStatusChart";
import Timeline from "@/components/shared/Timeline";

export default function Dashboard() {
  const { data: services = [] } = useQuery({ queryKey: ["services"], queryFn: fetchServices });
  const { data: sagas = [] } = useQuery({ queryKey: ["sagas"], queryFn: fetchSagas });

  const up = services.filter((s: Service) => s.status === "UP").length;
  const down = services.filter((s: Service) => s.status !== "UP").length;

  return (
    <div className="space-y-4">
      <div className="grid grid-cols-1 gap-4 sm:grid-cols-2 lg:grid-cols-4">
        <MetricCard title="Total Services" value={String(services.length)} />
        <MetricCard title="Services Up" value={String(up)} />
        <MetricCard title="Services Down" value={String(down)} />
        <MetricCard title="Saga Executions" value={String(sagas.length)} />
      </div>

      <div className="grid grid-cols-1 gap-4 lg:grid-cols-2">
        <Card>
          <CardHeader>
            <CardTitle>Services Status</CardTitle>
          </CardHeader>
          <CardContent>
            <ServicesStatusChart data={[{ status: "UP", count: up }, { status: "DOWN", count: down }]} />
          </CardContent>
        </Card>
        <Card>
          <CardHeader>
            <CardTitle>Recent Kafka Events</CardTitle>
          </CardHeader>
          <CardContent>
            <Timeline events={sagas.flatMap((s) => s.steps.map((step) => ({ time: s.startTime, event: step.name })))} />
          </CardContent>
        </Card>
      </div>
    </div>
  );
}
