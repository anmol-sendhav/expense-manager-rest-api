import Brand from "./Brand";
import { ArrowUpRight, CircleCheck, Target, Wallet } from "lucide-react";
export default function AuthShell({ children, mode }) {
  return <main className="auth-page"><section className="auth-card">
    <aside className="auth-aside"><Brand light/><p className="aside-tag">YOUR FINANCES, SIMPLIFIED</p>
      <h1>{mode === "login" ? <>Your money.<br/>Your goals.<br/><span>Your future.</span></> : <>Take control of<br/>your <span>finances.</span></>}</h1>
      <p className="aside-copy">Track expenses, manage income, and build better financial habits — all in one place.</p>
      <div className="feature-list">
        <div><i><ArrowUpRight/></i><span><b>Track every expense</b><small>Know where your money goes.</small></span></div>
        <div><i><Target/></i><span><b>Plan your budgets</b><small>Stay focused on your goals.</small></span></div>
        <div><i><CircleCheck/></i><span><b>Build better habits</b><small>Make smarter money decisions.</small></span></div>
      </div>
      <p className="aside-foot">Small steps. Big financial goals.</p>
    </aside>
    <section className="auth-content">{children}</section>
  </section></main>;
}