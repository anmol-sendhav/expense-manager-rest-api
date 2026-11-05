
import { WalletCards } from "lucide-react";

export default function Brand({ light = false }) {
  return (
    <div className={`brand-logo ${light ? "brand-light" : ""}`}>
      <span className="brand-icon">
        <WalletCards size={23} strokeWidth={2.2} />
      </span>

      <span className="brand-name">
        Expense<span className="brand-accent">Pilot</span>
      </span>
    </div>
  );
}
