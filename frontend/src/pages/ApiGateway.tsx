import { useQuery } from "@tanstack/react-query";
import { fetchServices } from "@/mocks/services";
import { Card, CardContent, CardHeader, CardTitle } from "@/components/ui/card";
import MetricCard from "@/components/shared/MetricCard";

export default function ApiGateway() {
  const { data: services = [] } = useQuery({ queryKey: ["services"], queryFn: fetchServices });
  const gw = services.find((s) => s.id === "api-gateway");
  const totalRequests = "1 250 000";
  const successRate = "99.2%";
  const latency = "120 ms";

  return (
    <div className="space-y-4">
      <div className="grid grid-cols-1 gap-4 sm:grid-cols-3">
        <MetricCard title="Total Requests" value={totalRequests} />
        <MetricCard title="Success Rate" value={successRate} />
        <MetricCard title="Avg Latency" value={latency} />
      </div>
      <Card>
        <CardHeader>
          <CardTitle>Gateway Status</CardTitle>
        </CardHeader>
        <CardContent>
          <p className="text-sm text-muted-foreground">
            Gateway: <span className="font-medium">{gw?.name || "API Gateway"}</span> • Status{" "}
            <span className="font-medium">{gw?.status || "UP"}</span>
          </p>
        </CardContent>
      </Card>
    </div>
  );
}
