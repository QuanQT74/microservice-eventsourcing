import { Card, CardContent, CardHeader, CardTitle } from "@/components/ui/card";

interface MetricCardProps {
  title: string;
  value: string;
  icon?: React.ReactNode;
  trend?: {
    value: number;
    isPositive: boolean;
  };
  description?: string;
  className?: string;
  isLoading?: boolean;
  variant?: "default" | "success" | "warning" | "danger";
}

const variantStyles = {
  default: "from-primary/5 to-emerald-50/50",
  success: "from-green-50 to-green-100/50",
  warning: "from-yellow-50 to-yellow-100/50",
  danger: "from-red-50 to-red-100/50",
};

export default function MetricCard({
  title,
  value,
  icon,
  trend,
  description,
  className,
  variant = "default",
}: MetricCardProps) {
  return (
    <Card
      className={`group relative overflow-hidden shadow-md transition-all duration-300 hover:shadow-xl ${className || ""}`}
    >
      {/* Background gradient */}
      <div className={`absolute inset-0 bg-gradient-to-br ${variantStyles[variant]} opacity-60`} />
      <div className="absolute -right-4 -top-4 h-24 w-24 rounded-full bg-primary/5 blur-2xl" />

      <CardHeader className="relative pb-2">
        <CardTitle className="flex items-center justify-between text-sm font-medium text-muted-foreground">
          <span>{title}</span>
          {icon && (
            <div className="flex h-9 w-9 items-center justify-center rounded-lg bg-primary/10 text-primary shadow-sm transition-transform group-hover:scale-110">
              {icon}
            </div>
          )}
        </CardTitle>
      </CardHeader>

      <CardContent className="relative">
        <div className="flex items-baseline gap-2">
          <div className="text-3xl font-bold tracking-tight">{value}</div>
          {trend && (
            <span
              className={`flex items-center text-xs font-semibold ${
                trend.isPositive ? "text-green-600" : "text-red-600"
              }`}
            >
              <svg
                className={`h-3 w-3 ${!trend.isPositive ? "rotate-180" : ""}`}
                fill="none"
                viewBox="0 0 24 24"
                stroke="currentColor"
              >
                <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M5 15l7-7 7 7" />
              </svg>
              {Math.abs(trend.value)}%
            </span>
          )}
        </div>
        {description && (
          <p className="mt-1 text-xs text-muted-foreground">{description}</p>
        )}
      </CardContent>

      {/* Bottom accent line */}
      <div className="absolute bottom-0 left-0 h-1 w-0 bg-gradient-to-r from-primary to-emerald-500 transition-all duration-500 group-hover:w-full" />
    </Card>
  );
}
