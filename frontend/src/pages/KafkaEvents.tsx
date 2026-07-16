import { useQuery } from "@tanstack/react-query";
import { fetchEvents } from "@/mocks/events";
import { Card, CardContent, CardHeader, CardTitle } from "@/components/ui/card";
import { Badge } from "@/components/ui/badge";
import { Button } from "@/components/ui/button";

export default function KafkaEvents() {
  const { data: events = [], isLoading, refetch } = useQuery({ queryKey: ["events"], queryFn: fetchEvents });

  return (
    <Card>
      <CardHeader className="flex flex-row items-center justify-between">
        <CardTitle>Kafka Events</CardTitle>
        <Button variant="outline" size="sm" onClick={() => refetch()}>Refresh</Button>
      </CardHeader>
      <CardContent>
        {isLoading ? (
          <p className="text-sm text-muted-foreground">Loading…</p>
        ) : events.length === 0 ? (
          <p className="text-sm text-muted-foreground">No events</p>
        ) : (
          <div className="space-y-3">
            {events.map((event) => (
              <div key={event.id} className="flex flex-col gap-1 rounded-md border p-3">
                <div className="flex items-center justify-between">
                  <div className="text-sm font-medium">{event.topic}</div>
                  <Badge variant="default">{event.key}</Badge>
                </div>
                <pre className="overflow-auto rounded-md bg-muted p-2 text-xs">
                  {JSON.stringify(event.payload, null, 2)}
                </pre>
                <div className="flex items-center justify-between text-xs text-muted-foreground">
                  <span>{event.timestamp}</span>
                  <span>offset {event.offset} • partition {event.partition}</span>
                </div>
              </div>
            ))}
          </div>
        )}
      </CardContent>
    </Card>
  );
}
