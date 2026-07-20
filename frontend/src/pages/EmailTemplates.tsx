import { Mail, BookMarked, RotateCcw } from "lucide-react";

const templates = [
  {
    id: "borrow-success",
    name: "Borrow confirmation",
    subject: "LibraStack — Borrow confirmed",
    file: "borrow-success.ftl",
    trigger: "After BorrowingSaga updates book status (BORROWED)",
    variables: ["name", "bookName", "borrowingId", "date"],
    icon: BookMarked,
  },
  {
    id: "return-success",
    name: "Return confirmation",
    subject: "LibraStack — Book returned",
    file: "return-success.ftl",
    trigger: "After BorrowingSaga updates book status (RETURNED)",
    variables: ["name", "bookName", "borrowingId", "date"],
    icon: RotateCcw,
  },
];

export default function EmailTemplates() {
  return (
    <div className="space-y-8 animate-in fade-in-0 duration-500">
      <section className="relative overflow-hidden rounded-[1.75rem] border border-border/60 bg-[#0B1F33] px-6 py-10 text-white shadow-xl sm:px-10">
        <div
          className="pointer-events-none absolute inset-0 opacity-45"
          style={{
            backgroundImage:
              "radial-gradient(circle at 12% 20%, rgba(0,184,148,0.35), transparent 42%), radial-gradient(circle at 88% 15%, rgba(0,102,255,0.28), transparent 40%)",
          }}
        />
        <div className="relative max-w-xl space-y-3">
          <p className="text-xs font-semibold uppercase tracking-[0.22em] text-emerald-300/90">
            Notification Service
          </p>
          <h1 className="text-3xl font-extrabold tracking-tight sm:text-4xl">
            Email templates
          </h1>
          <p className="text-sm text-white/70 sm:text-base">
            Freemarker templates sent via EmailService when Kafka topic{" "}
            <span className="font-mono text-emerald-200">testMailTemplate</span> receives a
            borrowing notification.
          </p>
        </div>
      </section>

      <ul className="space-y-4">
        {templates.map((tpl) => {
          const Icon = tpl.icon;
          return (
            <li
              key={tpl.id}
              className="overflow-hidden rounded-[1.35rem] border border-border/70 bg-white"
            >
              <div className="flex items-start gap-4 border-b border-border/60 px-6 py-5">
                <div className="flex h-11 w-11 shrink-0 items-center justify-center rounded-xl bg-[#0B1F33] text-emerald-300">
                  <Icon className="h-5 w-5" />
                </div>
                <div className="min-w-0 space-y-1">
                  <h2 className="text-lg font-bold tracking-tight">{tpl.name}</h2>
                  <p className="text-sm text-muted-foreground">
                    Subject: <span className="font-medium text-foreground">{tpl.subject}</span>
                  </p>
                </div>
              </div>
              <dl className="divide-y divide-border/60 px-6">
                <div className="grid gap-1 py-4 sm:grid-cols-[8rem_1fr]">
                  <dt className="text-xs font-semibold uppercase tracking-wider text-muted-foreground">
                    File
                  </dt>
                  <dd className="font-mono text-sm">{tpl.file}</dd>
                </div>
                <div className="grid gap-1 py-4 sm:grid-cols-[8rem_1fr]">
                  <dt className="text-xs font-semibold uppercase tracking-wider text-muted-foreground">
                    Trigger
                  </dt>
                  <dd className="text-sm text-muted-foreground">{tpl.trigger}</dd>
                </div>
                <div className="grid gap-1 py-4 sm:grid-cols-[8rem_1fr]">
                  <dt className="flex items-center gap-1.5 text-xs font-semibold uppercase tracking-wider text-muted-foreground">
                    <Mail className="h-3.5 w-3.5" />
                    Variables
                  </dt>
                  <dd className="flex flex-wrap gap-2">
                    {tpl.variables.map((v) => (
                      <span
                        key={v}
                        className="rounded-md bg-muted px-2 py-1 font-mono text-xs font-semibold"
                      >
                        {"${" + v + "}"}
                      </span>
                    ))}
                  </dd>
                </div>
              </dl>
            </li>
          );
        })}
      </ul>
    </div>
  );
}
