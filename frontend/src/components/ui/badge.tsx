import * as React from "react";
import { cn } from "@/lib/utils";

function Badge({ className, variant = "default", ...props }: React.HTMLAttributes<HTMLDivElement> & { variant?: "default" | "success" | "warning" | "danger" }) {
  const styles: Record<string, string> = {
    default: "border-transparent bg-primary text-primary-foreground",
    success: "border-transparent bg-green-600 text-white",
    warning: "border-transparent bg-yellow-600 text-white",
    danger: "border-transparent bg-red-600 text-white",
  };
  return <div className={cn("inline-flex items-center rounded-full border px-2.5 py-0.5 text-xs font-semibold transition-colors", styles[variant], className)} {...props} />;
}
export { Badge };
