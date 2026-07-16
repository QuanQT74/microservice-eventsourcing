import { Card, CardContent, CardHeader, CardTitle } from "@/components/ui/card";

export default function EmailTemplates() {
  const templates = [
    { id: "tmpl-1", name: "Borrowing Confirmation", subject: "Your book has been borrowed", variables: ["userName", "bookTitle", "dueDate"] },
    { id: "tmpl-2", name: "Overdue Notice", subject: "Your book is overdue", variables: ["userName", "bookTitle", "daysOverdue"] },
  ];

  return (
    <div className="grid grid-cols-1 gap-4 md:grid-cols-2 lg:grid-cols-3">
      {templates.map((tpl) => (
        <Card key={tpl.id}>
          <CardHeader>
            <CardTitle>{tpl.name}</CardTitle>
          </CardHeader>
          <CardContent className="space-y-2">
            <p className="text-sm text-muted-foreground">Subject: {tpl.subject}</p>
            <div className="text-xs text-muted-foreground">
              Variables: {tpl.variables.join(", ")}
            </div>
          </CardContent>
        </Card>
      ))}
    </div>
  );
}
