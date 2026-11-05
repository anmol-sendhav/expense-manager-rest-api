export default function StatCard({ label, value, hint, icon: Icon, tone = "violet" }) {
  return <article className="stat-card"><div className="stat-top"><span>{label}</span><i className={`stat-icon ${tone}`}>{Icon && <Icon size={20}/>}</i></div><strong>{value}</strong><small>{hint}</small></article>;
}