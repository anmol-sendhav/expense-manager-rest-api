import { useEffect, useState } from "react";
import {
  CalendarDays,
  RefreshCw,
  TrendingUp,
  TrendingDown,
  Wallet,
  PiggyBank,
  FileBarChart
} from "lucide-react";

import {
  ResponsiveContainer,
  BarChart,
  Bar,
  XAxis,
  YAxis,
  CartesianGrid,
  Tooltip,
  Legend,
  PieChart,
  Pie,
  Cell
} from "recharts";

import client from "../api/client";
import { API } from "../api/endpoints";
import PageHeader from "../components/PageHeader";
import { money, errorMessage } from "../utils/format";
import { Download, Printer } from "lucide-react";

const COLORS = [
  "#7057e8",
  "#24b891",
  "#f2a34a",
  "#e46c78",
  "#4c9ce8",
  "#a78bfa",
  "#48b5c8",
  "#d68bca"
];

function localDate(date) {
  const year = date.getFullYear();
  const month = String(date.getMonth() + 1).padStart(2, "0");
  const day = String(date.getDate()).padStart(2, "0");
  return `${year}-${month}-${day}`;
}

function getInitialDates() {
  const now = new Date();

  return {
    startDate: localDate(
      new Date(now.getFullYear(), now.getMonth() - 5, 1)
    ),
    endDate: localDate(now)
  };
}

function downloadReportsCsv(report, dates) {
  if (!report) return;

  const rows = [
    ["EXPENSEPILOT - FINANCIAL REPORT"],
    ["Start date", dates.startDate],
    ["End date", dates.endDate],
    [],
    ["FINANCIAL SUMMARY"],
    ["Metric", "Amount"],
    ["Total Income", report.totalIncome ?? 0],
    ["Total Expenses", report.totalExpenses ?? 0],
    ["Net Savings", report.savings ?? 0],
    ["Savings Rate (%)", report.savingsRate ?? 0],
    [],
    ["CATEGORY BREAKDOWN"],
    ["Category", "Amount"],
    ...(report.categoryBreakdown || []).map(item => [
      item.category,
      item.amount
    ]),
    [],
    ["MONTHLY TREND"],
    ["Month", "Income", "Expenses"],
    ...(report.monthlyTrend || []).map(item => [
      item.month,
      item.income,
      item.expenses
    ]),
    [],
    ["RECENT TRANSACTIONS"],
    ["Title", "Category / Source", "Date", "Type", "Amount"],
    ...(report.recentTransactions || []).map(item => [
      item.title,
      item.category,
      item.date,
      item.type,
      item.amount
    ])
  ];

  const escapeCsv = value => {
    const text = String(value ?? "");
    return `"${text.replace(/"/g, '""')}"`;
  };

  const csv = "\uFEFF" + rows
    .map(row => row.map(escapeCsv).join(","))
    .join("\r\n");

  const blob = new Blob([csv], {
    type: "text/csv;charset=utf-8;"
  });

  const url = URL.createObjectURL(blob);
  const link = document.createElement("a");

  link.href = url;
  link.download = `ExpensePilot_Report_${dates.startDate}_to_${dates.endDate}.csv`;

  document.body.appendChild(link);
  link.click();
  link.remove();

  URL.revokeObjectURL(url);
}

export default function Reports() {
  const [dates, setDates] = useState(getInitialDates);
  const [report, setReport] = useState(null);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState("");

  async function loadReport() {
    if (dates.startDate > dates.endDate) {
      setError("Start date cannot be after end date.");
      return;
    }

    setLoading(true);
    setError("");

    try {
      const response = await client.get(API.reports, {
        params: dates
      });

      setReport(response.data);
    } catch (err) {
      setError(errorMessage(err));
    } finally {
      setLoading(false);
    }
  }

  useEffect(() => {
    loadReport();
  }, []);

  const categories = report?.categoryBreakdown || [];
  const monthly = report?.monthlyTrend || [];
  const transactions = report?.recentTransactions || [];

  const summary = [
    {
      label: "Total income",
      value: money(report?.totalIncome),
      icon: TrendingUp,
      tone: "green"
    },
    {
      label: "Total expenses",
      value: money(report?.totalExpenses),
      icon: TrendingDown,
      tone: "orange"
    },
    {
      label: "Net savings",
      value: money(report?.savings),
      icon: Wallet,
      tone: "purple"
    },
    {
      label: "Savings rate",
      value: `${Number(report?.savingsRate || 0).toFixed(1)}%`,
      icon: PiggyBank,
      tone: "blue"
    }
  ];

  return (
    <>
	<PageHeader
	  title="Reports & Analytics"
	  subtitle="Understand your income, expenses and financial progress."
	  action={
	    <div className="report-header-actions">
	      <button
	        className="button button-outline"
	        onClick={() => window.print()}
	        disabled={!report || loading}
	      >
	        <Printer size={16} />
	        Export PDF
	      </button>

	      <button
	        className="button button-primary"
	        onClick={() => downloadReportsCsv(report, dates)}
	        disabled={!report || loading}
	      >
	        <Download size={16} />
	        Export CSV
	      </button>

	      <button
	        className="button button-outline"
	        onClick={loadReport}
	        disabled={loading}
	      >
	        <RefreshCw size={16} />
	        Refresh
	      </button>
	    </div>
	  }
	/>

      <section className="report-filter panel">
        <div className="report-filter-title">
          <CalendarDays size={18} />
          <div>
            <h3>Report period</h3>
            <p>Select a date range to analyse your finances.</p>
          </div>
        </div>

        <div className="report-filter-controls">
          <label>
            From
            <input
              type="date"
              value={dates.startDate}
              max={dates.endDate}
              onChange={e =>
                setDates({ ...dates, startDate: e.target.value })
              }
            />
          </label>

          <label>
            To
            <input
              type="date"
              value={dates.endDate}
              min={dates.startDate}
              onChange={e =>
                setDates({ ...dates, endDate: e.target.value })
              }
            />
          </label>

          <button
            className="button button-primary"
            onClick={loadReport}
            disabled={loading}
          >
            Apply filters
          </button>
        </div>
      </section>

      {error && <div className="alert error">{error}</div>}

      {loading ? (
        <div className="panel empty-state">Loading financial reports…</div>
      ) : report ? (
        <>
          <section className="report-summary-grid">
            {summary.map(item => {
              const Icon = item.icon;

              return (
                <article className="report-summary-card" key={item.label}>
                  <div className={`report-summary-icon ${item.tone}`}>
                    <Icon size={19} />
                  </div>
                  <span>{item.label}</span>
                  <strong>{item.value}</strong>
                </article>
              );
            })}
          </section>

          <section className="report-charts-grid">
            <article className="panel report-chart-panel">
              <div className="panel-heading">
                <div>
                  <h2>Income vs expenses</h2>
                  <p>Monthly financial comparison</p>
                </div>
                <TrendingUp size={19} />
              </div>

              {monthly.length ? (
                <div className="report-chart-area">
                  <ResponsiveContainer width="100%" height="100%">
                    <BarChart data={monthly} barGap={7}>
                      <CartesianGrid
                        strokeDasharray="3 3"
                        vertical={false}
                        stroke="#edf0f6"
                      />
                      <XAxis
                        dataKey="month"
                        axisLine={false}
                        tickLine={false}
                        tick={{ fill: "#929cb0", fontSize: 11 }}
                      />
                      <YAxis
                        axisLine={false}
                        tickLine={false}
                        tick={{ fill: "#929cb0", fontSize: 10 }}
                      />
                      <Tooltip formatter={value => money(value)} />
                      <Legend />
                      <Bar
                        dataKey="income"
                        name="Income"
                        fill="#24b891"
                        radius={[5, 5, 0, 0]}
                      />
                      <Bar
                        dataKey="expenses"
                        name="Expenses"
                        fill="#f2a34a"
                        radius={[5, 5, 0, 0]}
                      />
                    </BarChart>
                  </ResponsiveContainer>
                </div>
              ) : (
                <div className="empty-state">No monthly data available.</div>
              )}
            </article>

            <article className="panel report-chart-panel">
              <div className="panel-heading">
                <div>
                  <h2>Expense categories</h2>
                  <p>Where your money is going</p>
                </div>
                <FileBarChart size={19} />
              </div>

              {categories.length ? (
                <>
                  <div className="report-pie-area">
                    <ResponsiveContainer width="100%" height="100%">
                      <PieChart>
                        <Pie
                          data={categories}
                          dataKey="amount"
                          nameKey="category"
                          cx="50%"
                          cy="50%"
                          innerRadius={65}
                          outerRadius={100}
                          paddingAngle={3}
                        >
                          {categories.map((item, index) => (
                            <Cell
                              key={item.category}
                              fill={COLORS[index % COLORS.length]}
                            />
                          ))}
                        </Pie>
                        <Tooltip formatter={value => money(value)} />
                      </PieChart>
                    </ResponsiveContainer>
                  </div>

                  <div className="report-category-list">
                    {categories.map((item, index) => (
                      <div className="report-category-row" key={item.category}>
                        <span>
                          <i
                            style={{
                              background: COLORS[index % COLORS.length]
                            }}
                          />
                          {item.category}
                        </span>
                        <strong>{money(item.amount)}</strong>
                      </div>
                    ))}
                  </div>
                </>
              ) : (
                <div className="empty-state">No expense categories found.</div>
              )}
            </article>
          </section>

          <section className="panel report-transactions">
            <div className="panel-heading">
              <div>
                <h2>Recent transactions</h2>
                <p>Your latest income and expense records in this period</p>
              </div>
            </div>

            {transactions.length ? (
              <div className="table-scroll">
                <table>
                  <thead>
                    <tr>
                      <th>Transaction</th>
                      <th>Category / Source</th>
                      <th>Date</th>
                      <th>Type</th>
                      <th>Amount</th>
                    </tr>
                  </thead>
                  <tbody>
                    {transactions.map((item, index) => (
                      <tr key={`${item.type}-${item.title}-${index}`}>
                        <td><b>{item.title}</b></td>
                        <td>{item.category || "—"}</td>
                        <td>{item.date || "—"}</td>
                        <td>
                          <span className={`report-type ${item.type.toLowerCase()}`}>
                            {item.type}
                          </span>
                        </td>
                        <td className={
                          item.type === "INCOME"
                            ? "amount-positive"
                            : "amount-negative"
                        }>
                          {money(item.amount)}
                        </td>
                      </tr>
                    ))}
                  </tbody>
                </table>
              </div>
            ) : (
              <div className="empty-state">
                No transactions found for this date range.
              </div>
            )}
          </section>
        </>
      ) : null}
    </>
  );
}