
import { Link } from "react-router-dom";
import { ArrowUpRight, Target, Check } from "lucide-react";

export default function AuthLayout({ children, mode }) {
  const isRegister = mode === "register";

  return (
    <main className="auth-page">
      <div className="auth-shell">
        <section className="auth-brand">
          <Link to="/" className="brand-logo">
            <span className="brand-icon">E</span>
            <span>Expense<span className="brand-light">Pilot</span></span>
          </Link>

          <p className="brand-tagline">
            Manage your money, build your future.
          </p>

          <div className="brand-message">
            <span className="eyebrow">
              {isRegister
                ? "YOUR FINANCES, SIMPLIFIED"
                : "WELCOME BACK"}
            </span>

            <h1>
              {isRegister ? (
                <>Take control of your <span>finances.</span></>
              ) : (
                <>Your money.<br />Your goals.<br /><span>Your future.</span></>
              )}
            </h1>

            <p>
              Track expenses, manage income, and build better
              financial habits — all in one place.
            </p>
          </div>

          <div className="brand-features">
            <div className="brand-feature">
              <span className="feature-icon"><ArrowUpRight /></span>
              <div>
                <strong>Track every expense</strong>
                <small>Know where your money goes.</small>
              </div>
            </div>

            <div className="brand-feature">
              <span className="feature-icon"><Target /></span>
              <div>
                <strong>Plan your budgets</strong>
                <small>Stay focused on your goals.</small>
              </div>
            </div>

            <div className="brand-feature">
              <span className="feature-icon"><Check /></span>
              <div>
                <strong>Build better habits</strong>
                <small>Make smarter money decisions.</small>
              </div>
            </div>
          </div>

          <p className="brand-footer">
            Small steps. Big financial goals.
          </p>
        </section>

        <section className="auth-content">
          {children}
        </section>
      </div>
    </main>
  );
}
