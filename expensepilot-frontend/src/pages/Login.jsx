
import { useState } from "react";
import { Link, useNavigate } from "react-router-dom";
import { ArrowRight, LoaderCircle } from "lucide-react";
import AuthLayout from "../components/AuthLayout";
import { useAuth } from "../context/AuthContext";

export default function Login() {
  const navigate = useNavigate();
  const { login } = useAuth();

  const [form, setForm] = useState({
    email: "",
    password: "",
  });

  const [error, setError] = useState("");
  const [loading, setLoading] = useState(false);

  const handleChange = (e) => {
    setForm({ ...form, [e.target.name]: e.target.value });
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    setError("");

    if (!form.email || !form.password) {
      setError("Please enter your email and password.");
      return;
    }

    try {
      setLoading(true);
      await login(form.email, form.password);
      navigate("/dashboard", { replace: true });
    } catch (err) {
      setError(
        err.response?.data?.message ||
        err.response?.data?.error ||
        err.message ||
        "Unable to sign in. Please try again."
      );
    } finally {
      setLoading(false);
    }
  };

  return (
    <AuthLayout mode="login">
      <div className="auth-badge">✦ YOUR FINANCES, IN FOCUS</div>

      <h2>Welcome back</h2>
      <p className="auth-description">
        Sign in to continue to your ExpensePilot account.
      </p>

	  <form onSubmit={handleSubmit} className="auth-form">
	    <label htmlFor="email">Email address</label>
	    <input
	      id="email"
	      type="email"
	      name="email"
	      placeholder="you@example.com"
	      value={form.email}
	      onChange={handleChange}
	      autoComplete="email"
	      required
	    />

	    <label htmlFor="password">Password</label>
	    <input
	      id="password"
	      type="password"
	      name="password"
	      placeholder="Enter your password"
	      value={form.password}
	      onChange={handleChange}
	      autoComplete="current-password"
	      required
	    />

	    {error && <div className="form-error">{error}</div>}

	    <button className="auth-submit" type="submit" disabled={loading}>
	      {loading ? (
	        <>
	          <LoaderCircle className="spin" size={18} />
	          Signing in...
	        </>
	      ) : (
	        <>
	          Sign In <ArrowRight size={18} />
	        </>
	      )}
	    </button>
	  </form>


      <div className="auth-divider">
        <span>WELCOME TO EXPENSEPILOT</span>
      </div>

      <p className="auth-switch">
        New to ExpensePilot?
        <Link to="/register">Create account <ArrowRight size={15} /></Link>
      </p>

      <p className="auth-note">
        Sign in to access your personal expense management dashboard.
      </p>
    </AuthLayout>
  );
}
