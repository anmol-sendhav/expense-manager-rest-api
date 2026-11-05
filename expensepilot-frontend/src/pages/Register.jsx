
import { useState } from "react";
import { Link, useNavigate } from "react-router-dom";
import { ArrowRight, LoaderCircle } from "lucide-react";
import AuthLayout from "../components/AuthLayout";
import { useAuth } from "../context/AuthContext";

export default function Register() {
  const navigate = useNavigate();
  const { register } = useAuth();

  const [form, setForm] = useState({
    name: "",
    email: "",
    password: "",
  });

  const [error, setError] = useState("");
  const [success, setSuccess] = useState("");
  const [loading, setLoading] = useState(false);

  const handleChange = (e) => {
    setForm({ ...form, [e.target.name]: e.target.value });
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    setError("");
    setSuccess("");

    if (form.password.length < 8) {
      setError("Password must contain at least 8 characters.");
      return;
    }

    try {
      setLoading(true);

      await register(form.name, form.email, form.password);

      setSuccess("Account created successfully. Redirecting to login...");

      setTimeout(() => navigate("/login"), 1200);
    } catch (err) {
      setError(
        err.response?.data?.message ||
        err.response?.data?.error ||
        err.message ||
        "Registration failed. Please try again."
      );
    } finally {
      setLoading(false);
    }
  };

  return (
    <AuthLayout mode="register">
      <div className="auth-badge">✦ GET STARTED — IT'S FREE</div>

      <h2>Create your account</h2>
      <p className="auth-description">
        Start your journey towards smarter money management.
      </p>

      <form onSubmit={handleSubmit} className="auth-form">
        <label htmlFor="name">Full name</label>
        <input
          id="name"
          type="text"
          name="name"
          placeholder="Enter your full name"
          value={form.name}
          onChange={handleChange}
          autoComplete="name"
          required
        />

        <label htmlFor="register-email">Email address</label>
        <input
          id="register-email"
          type="email"
          name="email"
          placeholder="you@example.com"
          value={form.email}
          onChange={handleChange}
          autoComplete="email"
          required
        />

        <label htmlFor="register-password">Password</label>
        <input
          id="register-password"
          type="password"
          name="password"
          placeholder="Create a strong password"
          value={form.password}
          onChange={handleChange}
          autoComplete="new-password"
          minLength={8}
          required
        />

        <small className="password-hint">Use at least 8 characters.</small>

        {error && <div className="form-error">{error}</div>}
        {success && <div className="form-success">{success}</div>}

        <button className="auth-submit" type="submit" disabled={loading}>
          {loading ? (
            <><LoaderCircle className="spin" size={18} /> Creating account...</>
          ) : (
            <>Create Account <ArrowRight size={18} /></>
          )}
        </button>
      </form>

      <div className="auth-divider">
        <span>YOUR FINANCIAL JOURNEY STARTS HERE</span>
      </div>

      <p className="auth-switch">
        Already have an account?
        <Link to="/login">Log in <ArrowRight size={15} /></Link>
      </p>

      <p className="auth-note">
        Your account information is handled by ExpensePilot's authentication service.
      </p>
    </AuthLayout>
  );
}
