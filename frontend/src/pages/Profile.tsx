import { Link } from "react-router-dom";
import { User, Mail, Calendar, BookOpen, AlertTriangle, Bell, Settings, LogOut, Activity } from "lucide-react";
import { useUser } from "@/context/UserContext";
import { getActiveBorrowings } from "@/lib/borrowings-storage";
import PageHeader from "@/components/shared/PageHeader";
import EmptyState from "@/components/shared/EmptyState";
import { Card, CardContent, CardHeader, CardTitle } from "@/components/ui/card";
import { Badge } from "@/components/ui/badge";
import { Button } from "@/components/ui/button";
import { Skeleton } from "@/components/ui/skeleton";
import { Avatar } from "@/components/ui/avatar";
import { Stat } from "@/components/ui/stat";

export default function Profile() {
  const { employee, employeeId, isLoading, logout } = useUser();

  const activeCount = employeeId ? getActiveBorrowings(employeeId).length : 0;

  if (!employeeId) {
    return (
      <div className="flex min-h-[60vh] items-center justify-center">
        <EmptyState
          icon={<User className="h-8 w-8" />}
          title="Sign in to view profile"
          description="Enter your member ID to access your library account."
          action={
            <Link to="/welcome">
              <Button>Sign In</Button>
            </Link>
          }
        />
      </div>
    );
  }

  if (isLoading) {
    return (
      <div className="mx-auto max-w-4xl space-y-6">
        <Skeleton className="h-10 w-48" />
        <div className="grid gap-4 md:grid-cols-3">
          <Skeleton className="h-32 rounded-2xl" />
          <Skeleton className="h-32 rounded-2xl" />
          <Skeleton className="h-32 rounded-2xl" />
        </div>
        <div className="grid gap-4 md:grid-cols-2">
          <Skeleton className="h-64 rounded-2xl" />
          <Skeleton className="h-64 rounded-2xl" />
        </div>
      </div>
    );
  }

  if (!employee) {
    return (
      <div className="flex min-h-[60vh] items-center justify-center">
        <EmptyState
          title="Member not found"
          description="We couldn't load your profile. Check your member ID and try again."
          action={
            <Link to="/welcome">
              <Button variant="outline">Try again</Button>
            </Link>
          }
        />
      </div>
    );
  }

  return (
    <div className="mx-auto max-w-4xl space-y-6 animate-in fade-in-0 slide-in-from-bottom-4">
      <PageHeader title="My Profile" description="Your library membership details" />

      {/* Profile header card */}
      <div className="relative overflow-hidden rounded-2xl bg-gradient-to-br from-primary/5 via-emerald-50/50 to-teal-50/50 p-6 shadow-lg border border-primary/10">
        <div className="absolute -right-10 -top-10 h-40 w-40 rounded-full bg-primary/5 blur-3xl" />
        <div className="absolute -bottom-10 -left-10 h-32 w-32 rounded-full bg-emerald-500/5 blur-3xl" />
        
        <div className="relative flex flex-col gap-5 sm:flex-row sm:items-center sm:justify-between">
          <div className="flex items-center gap-4">
            <Avatar 
              fallback={`${employee.firstName[0]}${employee.lastName[0]}`} 
              size="xl"
              className="shadow-xl ring-4 ring-white"
            />
            <div>
              <h2 className="text-2xl font-bold tracking-tight">
                {employee.firstName} {employee.lastName}
              </h2>
              <p className="flex items-center gap-2 text-sm text-muted-foreground">
                <span className="inline-flex items-center gap-1 rounded-full bg-primary/10 px-2 py-0.5 text-xs font-medium text-primary">
                  <Activity className="h-3 w-3" />
                  Library Member
                </span>
              </p>
              {employee.isDisciplined && (
                <Badge variant="danger" className="mt-2">
                  <AlertTriangle className="mr-1 h-3 w-3" />
                  Account Restricted
                </Badge>
              )}
            </div>
          </div>

          <div className="flex gap-2">
            <Button variant="outline" size="sm">
              <Settings className="mr-2 h-4 w-4" />
              Settings
            </Button>
            <Button variant="ghost" size="sm" onClick={logout}>
              <LogOut className="mr-2 h-4 w-4" />
              Sign Out
            </Button>
          </div>
        </div>
      </div>

      {/* Stats row */}
      <div className="grid grid-cols-1 gap-4 sm:grid-cols-3">
        <Stat
          label="Active Borrowings"
          value={activeCount}
          icon={<BookOpen className="h-5 w-5" />}
          variant={activeCount > 0 ? "success" : "default"}
        />
        <Stat
          label="Member Status"
          value={employee.isDisciplined ? "Restricted" : "Active"}
          icon={<User className="h-5 w-5" />}
          variant={employee.isDisciplined ? "danger" : "success"}
        />
        <Stat
          label="Member ID"
          value={employee.Kin || "N/A"}
          icon={<Calendar className="h-5 w-5" />}
          description="Your membership number"
        />
      </div>

      {/* Info cards */}
      <div className="grid gap-4 md:grid-cols-2">
        <Card className="border-0 shadow-lg">
          <CardHeader className="border-b bg-muted/30 pb-3">
            <CardTitle className="flex items-center gap-2 text-base">
              <User className="h-4 w-4 text-primary" />
              Account Information
            </CardTitle>
          </CardHeader>
          <CardContent className="space-y-4 pt-4">
            <div className="flex items-start gap-3">
              <div className="flex h-9 w-9 items-center justify-center rounded-lg bg-primary/10">
                <User className="h-4 w-4 text-primary" />
              </div>
              <div>
                <p className="text-xs text-muted-foreground">Full Name</p>
                <p className="font-semibold">{employee.firstName} {employee.lastName}</p>
              </div>
            </div>
            <div className="flex items-start gap-3">
              <div className="flex h-9 w-9 items-center justify-center rounded-lg bg-primary/10">
                <Mail className="h-4 w-4 text-primary" />
              </div>
              <div>
                <p className="text-xs text-muted-foreground">Member ID (Kin)</p>
                <p className="font-mono font-medium">{employee.Kin || "N/A"}</p>
              </div>
            </div>
            <div className="flex items-start gap-3">
              <div className="flex h-9 w-9 items-center justify-center rounded-lg bg-primary/10">
                <Calendar className="h-4 w-4 text-primary" />
              </div>
              <div>
                <p className="text-xs text-muted-foreground">Employee ID</p>
                <p className="font-mono text-sm font-medium">{employee.id}</p>
              </div>
            </div>
          </CardContent>
        </Card>

        <Card className="border-0 shadow-lg">
          <CardHeader className="border-b bg-muted/30 pb-3">
            <CardTitle className="flex items-center gap-2 text-base">
              <BookOpen className="h-4 w-4 text-primary" />
              Reading Activity
            </CardTitle>
          </CardHeader>
          <CardContent className="space-y-4 pt-4">
            <div className="rounded-xl bg-gradient-to-br from-primary/5 to-emerald-50/50 p-4 text-center">
              <div className="text-4xl font-bold text-primary">{activeCount}</div>
              <div className="text-sm text-muted-foreground">Books Currently Borrowed</div>
            </div>
            <Link to="/my-borrowings" className="block">
              <Button  className="w-full">
                View All Borrowings
              </Button>
            </Link>
          </CardContent>
        </Card>
      </div>

      {/* Notifications card */}
      <Card className="border-0 shadow-lg">
        <CardHeader className="border-b bg-muted/30 pb-3">
          <CardTitle className="flex items-center gap-2 text-base">
            <Bell className="h-4 w-4 text-primary" />
            Notifications
          </CardTitle>
        </CardHeader>
        <CardContent>
          <div className="rounded-xl border border-dashed bg-muted/30 p-6 text-center">
            <div className="mx-auto mb-3 flex h-12 w-12 items-center justify-center rounded-full bg-primary/10">
              <Bell className="h-6 w-6 text-primary" />
            </div>
            <p className="text-sm text-muted-foreground">
              Email notifications are sent via the Notification Service when books are borrowed or overdue.
            </p>
            <p className="mt-2 text-xs text-muted-foreground">
              In-app notification API coming soon.
            </p>
          </div>
        </CardContent>
      </Card>
    </div>
  );
}
