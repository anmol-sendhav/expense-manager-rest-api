
import { useEffect, useMemo, useState } from "react";
import {
  Wallet,
  TrendingUp,
  TrendingDown,
  PiggyBank,
  Plus,
  ArrowUpRight,
  ArrowDownLeft,
  CalendarDays,
  RefreshCw,
  PieChart as PieIcon
} from "lucide-react";
import { Link } from "react-router-dom";

import {
  BarChart,
  Bar,
  XAxis,
  YAxis,
  CartesianGrid,
  Tooltip,
  ResponsiveContainer,
  Legend,
  PieChart,
  Pie,
  Cell
} from "recharts";

import client from "../api/client";
import { API } from "../api/endpoints";
import PageHeader from "../components/PageHeader";
import StatCard from "../components/StatCard";
import { money, getArray, errorMessage } from "../utils/format";

const COLORS = [
  "#7057e8",
  "#24b891",
  "#f2a34a",
  "#e879a5",
  "#429de0",
  "#9b79df",
  "#ed716e",
  "#48b9c7"
];

function parseDate(value) {
  if (!value) return null;

  const parts = String(value).split("-").map(Number);

  if (parts.length === 3 && parts.every(Number.isFinite)) {
    return new Date(parts[0], parts[1] - 1, parts[2]);
  }

  const date = new Date(value);
  return Number.isNaN(date.getTime()) ? null : date;
}

function formatDate(value) {
  const date = parseDate(value);

  if (!date) return "Date unavailable";

  return date.toLocaleDateString("en-IN", {
    day: "numeric",
    month: "short",
    year: "numeric"
  });
}

export default function Dashboard() {
  const [summary, setSummary] = useState(null);
  const [transactions, setTransactions] = useState([]);
  const [busy, setBusy] = useState(true);
  const [error, setError] = useState("");

  async function loadDashboard() {
    setBusy(true);
    setError("");

    try {
      const [summaryResponse, expenseResponse, incomeResponse] =
        await Promise.all([
          client.get(API.dashboard),
          client.get(API.expenses),
          client.get(API.incomes)
        ]);

      const expenses = getArray(expenseResponse.data).map(item => ({
        ...item,
        _type: "Expense",
        _date: item.date,
        _category: item.category || "Other"
      }));

      const incomes = getArray(incomeResponse.data).map(item => ({
        ...item,
        _type: "Income",
        _date: item.amountDate,
        _category: item.source || "Other"
      }));

      const combined = [...expenses, ...incomes].sort((a, b) => {
        const dateA = parseDate(a._date)?.getTime() || 0;
        const dateB = parseDate(b._date)?.getTime() || 0;
        return dateB - dateA;
      });

      setSummary(summaryResponse.data);
      setTransactions(combined);
    } catch (err) {
      setError(errorMessage(err));
    } finally {
      setBusy(false);
    }
  }

  useEffect(() => {
    loadDashboard();
  }, []);

  const d = summary || {};

  const income = Number(d.totalIncome || 0);
  const expenses = Number(d.totalExpenses || 0);
  const balance = Number(d.balance ?? income - expenses);

  const savings = Number(
    d.savingsRate ?? (income ? ((income - expenses) / income) * 100 : 0)
  );

  // Build the latest six calendar months.
  const monthlyData = useMemo(() => {
    const today = new Date();

    const months = Array.from({ length: 6 }, (_, index) => {
      const date = new Date(
        today.getFullYear(),
        today.getMonth() - 5 + index,
        1
      );

      return {
        key: `${date.getFullYear()}-${date.getMonth()}`,
        month: date.toLocaleDateString("en-IN", { month: "short" }),
        income: 0,
        expenses: 0
      };
    });

    transactions.forEach(item => {
      const date = parseDate(item._date);
      if (!date) return;

      const key = `${date.getFullYear()}-${date.getMonth()}`;
      const month = months.find(entry => entry.key === key);

      if (!month) return;

      const amount = Number(item.amount || 0);

      if (item._type === "Income") {
        month.income += amount;
      } else {
        month.expenses += amount;
      }
    });

    return months;
  }, [transactions]);

  // Group expense amounts by category.
  const categoryData = useMemo(() => {
    const totals = {};

    transactions
      .filter(item => item._type === "Expense")
      .forEach(item => {
        const category = item._category || "Other";
        totals[category] = (totals[category] || 0) + Number(item.amount || 0);
      });

    return Object.entries(totals)
      .map(([name, value]) => ({ name, value }))
      .sort((a, b) => b.value - a.value);
  }, [transactions]);

  const recentTransactions = transactions.slice(0, 7);

  const hasChartData = monthlyData.some(
    item => item.income > 0 || item.expenses > 0
  );

  return (
    <>
      <PageHeader
        eyebrow={new Date().toLocaleDateString("en-IN", {
          weekday: "long",
          day: "numeric",
          month: "long",
          year: "numeric"
        }).toUpperCase()}
        title="Your money, at a glance."
        subtitle="A little clarity goes a long way."
        action={
          <div className="dashboard-header-actions">
            <button
              className="button button-outline"
              onClick={loadDashboard}
              disabled={busy}
            >
              <RefreshCw size={16} /> Refresh
            </button>

            <Link className="button button-primary" to="/transactions">
              <Plus size={18} /> Add transaction
            </Link>
          </div>
        }
      />

      {error && <div className="alert error">{error}</div>}

      {busy ? (
        <div className="loading-card">Loading your financial summary...</div>
      ) : (
        <>
          <div className="stats-grid">
            <StatCard
              label="Available balance"
              value={money(balance)}
              hint="Income minus expenses"
              icon={Wallet}
            />

            <StatCard
              label="Income"
              value={money(income)}
              hint={`${d.incomeCount ?? 0} income records`}
              icon={TrendingUp}
              tone="green"
            />

            <StatCard
              label="Expenses"
              value={money(expenses)}
              hint={`${d.expenseCount ?? 0} expense records`}
              icon={TrendingDown}
              tone="orange"
            />

            <StatCard
              label="Savings rate"
              value={`${savings.toFixed(1)}%`}
              hint="Of your income retained"
              icon={PiggyBank}
              tone="blue"
            />
          </div>

          <section className="dashboard-analytics">
            <article className="panel dashboard-chart-panel">
              <div className="panel-heading">
                <div>
                  <h2>Monthly cash flow</h2>
                  <p>Income versus expenses over the last six months</p>
                </div>
                <span className="analytics-icon purple">
                  <TrendingUp size={18} />
                </span>
              </div>

              {hasChartData ? (
                <div className="dashboard-chart-area">
                  <ResponsiveContainer width="100%" height="100%">
                    <BarChart
                      data={monthlyData}
                      barGap={7}
                      barSize={17}
                    >
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
                        tickFormatter={value =>
                          value >= 1000
                            ? `₹${(value / 1000).toFixed(0)}k`
                            : `₹${value}`
                        }
                      />

                      <Tooltip
                        formatter={value => money(value)}
                        contentStyle={{
                          borderRadius: 12,
                          border: "1px solid #edf0f7",
                          fontSize: 12
                        }}
                      />

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
                <div className="analytics-empty">
                  <CalendarDays size={30} />
                  <b>No monthly activity yet</b>
                  <span>Your monthly comparison will appear here.</span>
                </div>
              )}
            </article>

            <article className="panel category-panel">
              <div className="panel-heading">
                <div>
                  <h2>Expense breakdown</h2>
                  <p>Spending by category</p>
                </div>

                <span className="analytics-icon orange">
                  <PieIcon size={18} />
                </span>
              </div>

              {categoryData.length > 0 ? (
                <>
                  <div className="category-chart-area">
                    <ResponsiveContainer width="100%" height="100%">
                      <PieChart>
                        <Pie
                          data={categoryData}
                          dataKey="value"
                          nameKey="name"
                          innerRadius={57}
                          outerRadius={85}
                          paddingAngle={3}
                        >
                          {categoryData.map((item, index) => (
                            <Cell
                              key={item.name}
                              fill={COLORS[index % COLORS.length]}
                            />
                          ))}
                        </Pie>

                        <Tooltip formatter={value => money(value)} />
                      </PieChart>
                    </ResponsiveContainer>
                  </div>

                  <div className="category-legend">
                    {categoryData.slice(0, 6).map((item, index) => (
                      <div className="category-legend-item" key={item.name}>
                        <span>
                          <i
                            style={{
                              background: COLORS[index % COLORS.length]
                            }}
                          />
                          {item.name.replaceAll("_", " ")}
                        </span>
                        <b>{money(item.value)}</b>
                      </div>
                    ))}
                  </div>
                </>
              ) : (
                <div className="analytics-empty">
                  <PieIcon size={30} />
                  <b>No expenses recorded</b>
                  <span>Add expenses to see your spending breakdown.</span>
                </div>
              )}
            </article>
          </section>

          <section className="panel recent-panel dashboard-recent">
            <div className="panel-heading">
              <div>
                <h2>Recent transactions</h2>
                <p>Your latest income and expenses</p>
              </div>

              <Link className="button button-outline" to="/transactions">
                View all <ArrowUpRight size={16} />
              </Link>
            </div>

            {recentTransactions.length > 0 ? (
              <div className="recent-list">
                {recentTransactions.map((item, index) => (
                  <div
                    className="recent-item"
                    key={`${item._type}-${item.expenseId || item.incomeId || index}`}
                  >
                    <div
                      className={`recent-item-icon ${
                        item._type === "Income" ? "recent-income" : "recent-expense"
                      }`}
                    >
                      {item._type === "Income"
                        ? <ArrowDownLeft size={19} />
                        : <ArrowUpRight size={19} />}
                    </div>

                    <div className="recent-item-info">
                      <b>{item.title || "Untitled transaction"}</b>
                      <span>
                        {item._type === "Income"
                          ? item.source || "Income"
                          : item.category || "Expense"}
                        {" · "}
                        {formatDate(item._date)}
                      </span>
                    </div>

                    <strong
                      className={
                        item._type === "Income"
                          ? "amount-positive"
                          : "amount-negative"
                      }
                    >
                      {item._type === "Income" ? "+" : "−"}
                      {money(item.amount)}
                    </strong>
                  </div>
                ))}
              </div>
            ) : (
              <div className="empty-state">
                No transactions yet. Add your first transaction to get started.
              </div>
            )}
          </section>
        </>
      )}
    </>
  );
}
