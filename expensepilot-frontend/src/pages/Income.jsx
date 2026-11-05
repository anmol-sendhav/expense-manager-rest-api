import { useEffect, useMemo, useState } from "react";
import {
  TrendingUp,
  Wallet,
  CalendarDays,
  ReceiptText,
  RefreshCw,
  Search,
  ArrowUpRight,
  ArrowDownRight,
  CircleDollarSign,
} from "lucide-react";

import client from "../api/client";
import { API } from "../api/endpoints";
import PageHeader from "../components/PageHeader";
import { getArray, money, errorMessage } from "../utils/format";

function getIncomeDate(row) {
  const value =
    row.date ||
    row.incomeDate ||
    row.createdAt ||
    row.transactionDate;

  if (!value) return null;

  const date = new Date(value);
  return Number.isNaN(date.getTime()) ? null : date;
}

function getIncomeTitle(row) {
  return (
    row.title ||
    row.description ||
    row.source ||
    row.category ||
    "Income"
  );
}

function getAmount(row) {
  const amount = Number(row.amount);
  return Number.isFinite(amount) ? amount : 0;
}

function formatDate(value) {
  if (!value) return "Date unavailable";

  return value.toLocaleDateString("en-IN", {
    day: "2-digit",
    month: "short",
    year: "numeric",
  });
}

export default function Income() {
  const [rows, setRows] = useState([]);
  const [busy, setBusy] = useState(true);
  const [error, setError] = useState("");
  const [search, setSearch] = useState("");
  const [category, setCategory] = useState("All");
  const [sortOrder, setSortOrder] = useState("newest");

  async function loadIncome() {
    setBusy(true);
    setError("");

    try {
      const response = await client.get(API.incomes);
      setRows(getArray(response.data));
    } catch (err) {
      setError(errorMessage(err));
    } finally {
      setBusy(false);
    }
  }

  useEffect(() => {
    loadIncome();
  }, []);

  const categories = useMemo(() => {
    const values = rows
      .map((row) => row.category)
      .filter(Boolean);

    return ["All", ...new Set(values)];
  }, [rows]);

  const filteredRows = useMemo(() => {
    let result = [...rows];

    if (search.trim()) {
      const term = search.toLowerCase();

      result = result.filter((row) =>
        [
          getIncomeTitle(row),
          row.category,
          row.description,
        ]
          .filter(Boolean)
          .some((value) => String(value).toLowerCase().includes(term))
      );
    }

    if (category !== "All") {
      result = result.filter((row) => row.category === category);
    }

    result.sort((a, b) => {
      const dateA = getIncomeDate(a)?.getTime() || 0;
      const dateB = getIncomeDate(b)?.getTime() || 0;

      return sortOrder === "newest"
        ? dateB - dateA
        : dateA - dateB;
    });

    return result;
  }, [rows, search, category, sortOrder]);

  const totalIncome = useMemo(
    () => rows.reduce((sum, row) => sum + getAmount(row), 0),
    [rows]
  );

  const now = new Date();

  const monthlyIncome = useMemo(
    () =>
      rows.reduce((sum, row) => {
        const date = getIncomeDate(row);

        if (
          date &&
          date.getMonth() === now.getMonth() &&
          date.getFullYear() === now.getFullYear()
        ) {
          return sum + getAmount(row);
        }

        return sum;
      }, 0),
    [rows]
  );

  const averageIncome = rows.length
    ? totalIncome / rows.length
    : 0;

  const chartData = useMemo(() => {
    return Array.from({ length: 6 }, (_, index) => {
      const date = new Date(
        now.getFullYear(),
        now.getMonth() - (5 - index),
        1
      );

      const total = rows.reduce((sum, row) => {
        const incomeDate = getIncomeDate(row);

        if (
          incomeDate &&
          incomeDate.getMonth() === date.getMonth() &&
          incomeDate.getFullYear() === date.getFullYear()
        ) {
          return sum + getAmount(row);
        }

        return sum;
      }, 0);

      return {
        label: date.toLocaleDateString("en-IN", {
          month: "short",
        }),
        total,
      };
    });
  }, [rows]);

  const maxChartValue = Math.max(
    ...chartData.map((item) => item.total),
    1
  );

  return (
    <div className="income-page">
      <PageHeader
        title="Income"
        subtitle="Track your earnings and understand where your money comes from."
        action={
          <button
            className="income-refresh"
            onClick={loadIncome}
            disabled={busy}
          >
            <RefreshCw size={16} className={busy ? "spin" : ""} />
            Refresh
          </button>
        }
      />

      {error && (
        <div className="income-error">
          <span>{error}</span>
          <button onClick={loadIncome}>Try again</button>
        </div>
      )}

      <section className="income-summary">
        <div className="income-stat-card primary">
          <div className="income-stat-top">
            <span>Total income</span>
            <div className="income-stat-icon">
              <Wallet size={20} />
            </div>
          </div>
          <h2>{money(totalIncome)}</h2>
          <p><TrendingUp size={14} /> All recorded income</p>
        </div>

        <div className="income-stat-card">
          <div className="income-stat-top">
            <span>This month</span>
            <div className="income-stat-icon purple">
              <CalendarDays size={20} />
            </div>
          </div>
          <h2>{money(monthlyIncome)}</h2>
          <p>Income received this month</p>
        </div>

        <div className="income-stat-card">
          <div className="income-stat-top">
            <span>Transactions</span>
            <div className="income-stat-icon orange">
              <ReceiptText size={20} />
            </div>
          </div>
          <h2>{rows.length}</h2>
          <p>Total income records</p>
        </div>

        <div className="income-stat-card">
          <div className="income-stat-top">
            <span>Average income</span>
            <div className="income-stat-icon green">
              <CircleDollarSign size={20} />
            </div>
          </div>
          <h2>{money(averageIncome)}</h2>
          <p>Average per transaction</p>
        </div>
      </section>

      <section className="income-chart-panel">
        <div className="income-section-heading">
          <div>
            <h3>Income overview</h3>
            <p>Your income activity over the last six months</p>
          </div>
          <span className="income-chart-label">
            <span /> Monthly income
          </span>
        </div>

        <div className="income-chart">
          {chartData.map((item, index) => {
            const height =
              item.total === 0
                ? 4
                : Math.max((item.total / maxChartValue) * 100, 8);

            return (
              <div className="income-chart-column" key={`${item.label}-${index}`}>
                <div className="income-bar-area">
                  <span className="income-bar-value">
                    {item.total > 0 ? money(item.total) : ""}
                  </span>
                  <div
                    className={`income-bar ${index === 5 ? "current" : ""}`}
                    style={{ height: `${height}%` }}
                    title={`${item.label}: ${money(item.total)}`}
                  />
                </div>
                <span className="income-month-label">{item.label}</span>
              </div>
            );
          })}
        </div>
      </section>

      <section className="income-transactions-panel">
        <div className="income-section-heading transactions-heading">
          <div>
            <h3>Income transactions</h3>
            <p>View and manage your recorded income</p>
          </div>
          <span className="income-record-count">
            {filteredRows.length} records
          </span>
        </div>

        <div className="income-filters">
          <div className="income-search">
            <Search size={17} />
            <input
              type="text"
              placeholder="Search income..."
              value={search}
              onChange={(e) => setSearch(e.target.value)}
            />
          </div>

          <select
            value={category}
            onChange={(e) => setCategory(e.target.value)}
          >
            {categories.map((item) => (
              <option key={item} value={item}>
                {item === "All" ? "All categories" : item}
              </option>
            ))}
          </select>

          <select
            value={sortOrder}
            onChange={(e) => setSortOrder(e.target.value)}
          >
            <option value="newest">Newest first</option>
            <option value="oldest">Oldest first</option>
          </select>
        </div>

        {busy ? (
          <div className="income-empty">
            <RefreshCw className="spin" size={24} />
            <p>Loading your income...</p>
          </div>
        ) : filteredRows.length === 0 ? (
          <div className="income-empty">
            <div className="income-empty-icon">
              <TrendingUp size={27} />
            </div>
            <h4>No income records found</h4>
            <p>
              {rows.length === 0
                ? "Your income records will appear here once you add them."
                : "Try changing your search or category filter."}
            </p>
          </div>
        ) : (
          <div className="income-table-scroll">
            <table className="income-table">
              <thead>
                <tr>
                  <th>Income source</th>
                  <th>Category</th>
                  <th>Date</th>
                  <th>Amount</th>
                  <th>Status</th>
                </tr>
              </thead>

              <tbody>
                {filteredRows.map((row, index) => {
                  const date = getIncomeDate(row);

                  return (
                    <tr key={row.id || row.incomeId || index}>
                      <td>
                        <div className="income-source-cell">
                          <div className="income-source-icon">
                            <ArrowDownRight size={18} />
                          </div>
                          <div>
                            <strong>{getIncomeTitle(row)}</strong>
                            <small>Income received</small>
                          </div>
                        </div>
                      </td>

                      <td>
                        <span className="income-category">
                          {row.category || "Uncategorized"}
                        </span>
                      </td>

                      <td>{formatDate(date)}</td>

                      <td className="income-amount">
                        <ArrowUpRight size={15} />
                        {money(getAmount(row))}
                      </td>

                      <td>
                        <span className="income-status">Recorded</span>
                      </td>
                    </tr>
                  );
                })}
              </tbody>
            </table>
          </div>
        )}
      </section>
    </div>
  );
}