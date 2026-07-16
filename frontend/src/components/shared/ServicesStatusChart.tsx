import { PieChart, Pie, Cell, ResponsiveContainer, Legend } from "recharts";

export default function ServicesStatusChart({ data }: { data: { status: string; count: number }[] }) {
  const colors: Record<string, string> = { UP: "#16a34a", DOWN: "#dc2626" };
  return (
    <ResponsiveContainer width="100%" height={260}>
      <PieChart>
        <Pie dataKey="count" data={data} cx="50%" cy="50%" outerRadius={80} label>
          {data.map((entry) => (
            <Cell key={entry.status} fill={colors[entry.status] || "#94a3b8"} />
          ))}
        </Pie>
        <Legend />
      </PieChart>
    </ResponsiveContainer>
  );
}
