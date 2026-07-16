const COLORS = ["bg-green-500", "bg-yellow-500", "bg-red-500", "bg-blue-500"];

export default function Timeline({ events }: { events: { time: string; event: string }[] }) {
  return (
    <div className="space-y-4">
      {events.map((event, index) => (
        <div key={index} className="flex items-start gap-3">
          <span className={`mt-1 size-2 rounded-full ${COLORS[index % COLORS.length]}`} />
          <div>
            <div className="text-sm font-medium">{event.event}</div>
            <div className="text-xs text-muted-foreground">{event.time}</div>
          </div>
        </div>
      ))}
      {events.length === 0 && <p className="text-sm text-muted-foreground">No events</p>}
    </div>
  );
}
