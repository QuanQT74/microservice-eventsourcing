import { useState } from "react";
import { useNavigate } from "react-router-dom";
import { Library, Loader2, ArrowRight, BookOpen, Sparkles, Shield, Eye, EyeOff, CheckCircle2 } from "lucide-react";
import { toast } from "sonner";
import { Button } from "@/components/ui/button";
import { Input } from "@/components/ui/input";
import { Card, CardContent, CardHeader, CardTitle, CardFooter } from "@/components/ui/card";
import { login, register } from "@/api/auth";

type AuthMode = "login" | "register";

export default function Welcome() {
  const [mode, setMode] = useState<AuthMode>("login");
  const [loading, setLoading] = useState(false);
  const [showPassword, setShowPassword] = useState(false);

  // Login form
  const [loginData, setLoginData] = useState({ username: "", password: "" });

  // Register form
  const [registerData, setRegisterData] = useState({
    username: "",
    email: "",
    password: "",
    fullName: "",
  });

  const navigate = useNavigate();

  const handleLogin = async (e: React.FormEvent) => {
    e.preventDefault();
    if (!loginData.username || !loginData.password) return;

    setLoading(true);
    try {
      const response = await login(loginData);
      localStorage.setItem("access_token", response.access_token);
      toast.success("Login successful!", { description: "Welcome back!" });
      navigate("/");
    } catch (err) {
      toast.error("Login failed", { description: (err as Error).message });
    } finally {
      setLoading(false);
    }
  };

  const handleRegister = async (e: React.FormEvent) => {
    e.preventDefault();
    if (!registerData.username || !registerData.email || !registerData.password || !registerData.fullName) {
      return;
    }

    setLoading(true);
    try {
      await register(registerData);
      toast.success("Registration successful!", { description: "Please login with your credentials." });
      setMode("login");
      setLoginData({ username: registerData.username, password: "" });
      setRegisterData({ username: "", email: "", password: "", fullName: "" });
    } catch (err) {
      toast.error("Registration failed", { description: (err as Error).message });
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className="relative flex min-h-screen items-center justify-center overflow-hidden bg-gradient-to-br from-background via-emerald-50/30 to-teal-50/30 p-4">
      {/* Animated background elements */}
      <div className="pointer-events-none absolute inset-0 overflow-hidden">
        <div className="absolute -left-10 -top-10 h-72 w-72 rounded-full bg-primary/10 blur-3xl animate-pulse" />
        <div className="absolute -bottom-20 -right-10 h-96 w-96 rounded-full bg-emerald-500/10 blur-3xl animate-pulse" style={{ animationDelay: "1s" }} />
        <div className="absolute left-1/2 top-1/2 h-64 w-64 -translate-x-1/2 -translate-y-1/2 rounded-full bg-teal-500/5 blur-3xl animate-pulse" style={{ animationDelay: "2s" }} />
      </div>

      {/* Floating decorative elements */}
      <div className="pointer-events-none absolute inset-0 overflow-hidden">
        <div className="absolute left-[10%] top-[20%] animate-float">
          <BookOpen className="h-8 w-8 text-primary/20" />
        </div>
        <div className="absolute right-[15%] top-[30%] animate-float" style={{ animationDelay: "0.5s" }}>
          <Sparkles className="h-6 w-6 text-emerald-500/20" />
        </div>
        <div className="absolute bottom-[30%] left-[20%] animate-float" style={{ animationDelay: "1s" }}>
          <Shield className="h-10 w-10 text-teal-500/20" />
        </div>
      </div>

      <div className="relative w-full max-w-md space-y-6 animate-in fade-in-0 slide-in-from-bottom-4 duration-700">
        {/* Logo and title */}
        <div className="text-center">
          <div className="relative mx-auto mb-4 flex h-16 w-16 items-center justify-center">
            <div className="absolute inset-0 rounded-2xl bg-gradient-to-br from-primary to-emerald-700 blur-lg opacity-50" />
            <div className="relative flex h-16 w-16 items-center justify-center rounded-2xl bg-gradient-to-br from-primary to-emerald-700 shadow-xl">
              <Library className="h-8 w-8 text-white" />
            </div>
          </div>
          <h1 className="text-3xl font-bold tracking-tight">
            <span className="gradient-text">LibraStack</span>
          </h1>
          <p className="mt-2 text-sm text-muted-foreground">
            Your modern digital library experience
          </p>
        </div>

        {/* Auth card */}
        <Card className="border-0 shadow-2xl shadow-primary/10">
          <CardHeader className="pb-2">
            <div className="flex items-center justify-center gap-2 mb-2">
              <div className="flex h-10 w-10 items-center justify-center rounded-xl bg-primary/10">
                {mode === "login" ? (
                  <svg className="h-5 w-5 text-primary" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                    <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M11 16l-4-4m0 0l4-4m-4 4h14m-5 4v1a3 3 0 01-3 3H6a3 3 0 01-3-3V7a3 3 0 013-3h7a3 3 0 013 3v1" />
                  </svg>
                ) : (
                  <svg className="h-5 w-5 text-primary" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                    <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M18 9v3m0 0v3m0-3h3m-3 0h-3m-2-5a4 4 0 11-8 0 4 4 0 018 0zM3 20a6 6 0 0112 0v1H3v-1z" />
                  </svg>
                )}
              </div>
            </div>
            <CardTitle className="text-xl text-center">
              {mode === "login" ? "Sign In" : "Create Account"}
            </CardTitle>
            <p className="text-center text-sm text-muted-foreground">
              {mode === "login"
                ? "Welcome back! Please enter your credentials"
                : "Join LibraStack to borrow books instantly"}
            </p>
          </CardHeader>

          {mode === "login" ? (
            <form onSubmit={handleLogin}>
              <CardContent className="space-y-4">
                <div className="space-y-2">
                  <label htmlFor="username" className="text-sm font-semibold">Username</label>
                  <Input
                    id="username"
                    placeholder="Enter your username"
                    value={loginData.username}
                    onChange={(e) => setLoginData({ ...loginData, username: e.target.value })}
                    disabled={loading}
                    className="h-11 rounded-xl border-2 transition-all focus:border-primary"
                  />
                </div>
                <div className="space-y-2">
                  <label htmlFor="password" className="text-sm font-semibold">Password</label>
                  <div className="relative">
                    <Input
                      id="password"
                      type={showPassword ? "text" : "password"}
                      placeholder="Enter your password"
                      value={loginData.password}
                      onChange={(e) => setLoginData({ ...loginData, password: e.target.value })}
                      disabled={loading}
                      className="h-11 rounded-xl border-2 pr-10 transition-all focus:border-primary"
                    />
                    <button
                      type="button"
                      onClick={() => setShowPassword(!showPassword)}
                      className="absolute right-3 top-1/2 -translate-y-1/2 text-muted-foreground hover:text-foreground"
                    >
                      {showPassword ? <EyeOff className="h-4 w-4" /> : <Eye className="h-4 w-4" />}
                    </button>
                  </div>
                </div>
              </CardContent>
              <CardFooter className="pt-2 flex-col gap-3">
                <Button
                  type="submit"
                  
                  size="lg"
                  className="w-full"
                  disabled={loading || !loginData.username || !loginData.password}
                  isLoading={loading}
                >
                  {loading ? "Signing in..." : (
                    <>
                      Sign In
                      <ArrowRight className="ml-2 h-4 w-4" />
                    </>
                  )}
                </Button>
                <p className="text-sm text-muted-foreground">
                  Don't have an account?{" "}
                  <button
                    type="button"
                    onClick={() => setMode("register")}
                    className="font-semibold text-primary hover:underline"
                  >
                    Sign up
                  </button>
                </p>
              </CardFooter>
            </form>
          ) : (
            <form onSubmit={handleRegister}>
              <CardContent className="space-y-4">
                <div className="space-y-2">
                  <label htmlFor="fullName" className="text-sm font-semibold">Full Name</label>
                  <Input
                    id="fullName"
                    placeholder="Enter your full name"
                    value={registerData.fullName}
                    onChange={(e) => setRegisterData({ ...registerData, fullName: e.target.value })}
                    disabled={loading}
                    className="h-11 rounded-xl border-2 transition-all focus:border-primary"
                  />
                </div>
                <div className="space-y-2">
                  <label htmlFor="regUsername" className="text-sm font-semibold">Username</label>
                  <Input
                    id="regUsername"
                    placeholder="Choose a username"
                    value={registerData.username}
                    onChange={(e) => setRegisterData({ ...registerData, username: e.target.value })}
                    disabled={loading}
                    className="h-11 rounded-xl border-2 transition-all focus:border-primary"
                  />
                </div>
                <div className="space-y-2">
                  <label htmlFor="email" className="text-sm font-semibold">Email</label>
                  <Input
                    id="email"
                    type="email"
                    placeholder="Enter your email"
                    value={registerData.email}
                    onChange={(e) => setRegisterData({ ...registerData, email: e.target.value })}
                    disabled={loading}
                    className="h-11 rounded-xl border-2 transition-all focus:border-primary"
                  />
                </div>
                <div className="space-y-2">
                  <label htmlFor="regPassword" className="text-sm font-semibold">Password</label>
                  <div className="relative">
                    <Input
                      id="regPassword"
                      type={showPassword ? "text" : "password"}
                      placeholder="Create a password (min 6 characters)"
                      value={registerData.password}
                      onChange={(e) => setRegisterData({ ...registerData, password: e.target.value })}
                      disabled={loading}
                      className="h-11 rounded-xl border-2 pr-10 transition-all focus:border-primary"
                    />
                    <button
                      type="button"
                      onClick={() => setShowPassword(!showPassword)}
                      className="absolute right-3 top-1/2 -translate-y-1/2 text-muted-foreground hover:text-foreground"
                    >
                      {showPassword ? <EyeOff className="h-4 w-4" /> : <Eye className="h-4 w-4" />}
                    </button>
                  </div>
                  {registerData.password && (
                    <div className="flex items-center gap-1 text-xs text-muted-foreground">
                      <CheckCircle2 className={`h-3 w-3 ${registerData.password.length >= 6 ? "text-green-500" : "text-muted-foreground"}`} />
                      At least 6 characters
                    </div>
                  )}
                </div>
              </CardContent>
              <CardFooter className="pt-2 flex-col gap-3">
                <Button
                  type="submit"
                  
                  size="lg"
                  className="w-full"
                  disabled={loading || !registerData.username || !registerData.email || !registerData.password || !registerData.fullName}
                  isLoading={loading}
                >
                  {loading ? "Creating account..." : (
                    <>
                      Create Account
                      <ArrowRight className="ml-2 h-4 w-4" />
                    </>
                  )}
                </Button>
                <p className="text-sm text-muted-foreground">
                  Already have an account?{" "}
                  <button
                    type="button"
                    onClick={() => setMode("login")}
                    className="font-semibold text-primary hover:underline"
                  >
                    Sign in
                  </button>
                </p>
              </CardFooter>
            </form>
          )}
        </Card>
      </div>

      <style>{`
        @keyframes float {
          0%, 100% { transform: translateY(0) rotate(0deg); }
          50% { transform: translateY(-20px) rotate(5deg); }
        }
        .animate-float {
          animation: float 6s ease-in-out infinite;
        }
      `}</style>
    </div>
  );
}
