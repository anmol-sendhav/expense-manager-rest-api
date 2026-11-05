
import { useEffect, useState } from "react";
import {
  Plus, Pencil, Trash2, RefreshCw, X, Target,
  AlertTriangle, CheckCircle2, TrendingUp
} from "lucide-react";

import client from "../api/client";
import { API } from "../api/endpoints";
import PageHeader from "../components/PageHeader";
import { money, errorMessage } from "../utils/format";

const categories = [
  "Shopping", "Entertaiment", "Rent", "Electricity_Bills",
  "Education", "Food", "Travel", "Medical", "Groceries",
  "Gym", "Recharge", "Diet", "Clothes",
  "PREMIUM_SUBSCRIPTION_MONTHLY", "Vegetables",
  "Freelance", "Petrol", "Transport", "Other"
];

const emptyForm = {
  budgetAmount: "",
  category: "Food",
  month: new Date().toISOString().slice(0, 7),
  description: ""
};

function monthToBackend(value) {
  const [year, month] = value.split("-");
  return `01-${month}-${year}`;
}

function backendToMonth(value) {
  if (!value) return new Date().toISOString().slice(0, 7);

  const parts = value.split("-");

  if (parts.length === 3 && parts[0].length === 2) {
    return `${parts[2]}-${parts[1]}`;
  }

  const date = new Date(value);
  if (Number.isNaN(date.getTime())) {
    return new Date().toISOString().slice(0, 7);
  }

  return `${date.getFullYear()}-${String(date.getMonth() + 1).padStart(2, "0")}`;
}

function unwrapList(data) {
  if (Array.isArray(data)) return data;
  if (Array.isArray(data?.data)) return data.data;
  if (Array.isArray(data?.content)) return data.content;
  return [];
}

function statusInfo(status) {
  if (status === "EXCEEDED") {
    return {
      label: "Budget exceeded",
      className: "exceeded",
      icon: AlertTriangle
    };
  }

  if (status === "WARNING") {
    return {
      label: "Near limit",
      className: "warning",
      icon: AlertTriangle
    };
  }

  return {
    label: "On track",
    className: "safe",
    icon: CheckCircle2
  };
}

export default function Budgets() {
  const [budgets, setBudgets] = useState([]);
  const [loading, setLoading] = useState(true);
  const [saving, setSaving] = useState(false);
  const [error, setError] = useState("");
  const [success, setSuccess] = useState("");
  const [search, setSearch] = useState("");

  const [modalOpen, setModalOpen] = useState(false);
  const [editingId, setEditingId] = useState(null);
  const [form, setForm] = useState(emptyForm);

  async function loadBudgets() {
    setLoading(true);
    setError("");

    try {
      const response = await client.get(API.budgets);
      const list = unwrapList(response.data);

      const detailed = await Promise.all(
        list.map(async budget => {
          const id = budget.id ?? budget.budgetId;

          if (!id) return { ...budget };

          try {
            const usageResponse = await client.get(API.budgetUsage(id));

            return {
              ...budget,
              ...usageResponse.data,
              id
            };
          } catch {
            return {
              ...budget,
              id,
              usedAmount: Number(budget.totalSpent || 0),
              remainingAmount: Number(budget.remainingAmount ?? budget.budgetAmount),
              usagePercentage: 0,
              status: "SAFE"
            };
          }
        })
      );

      setBudgets(detailed);
    } catch (err) {
      setError(errorMessage(err));
    } finally {
      setLoading(false);
    }
  }

  useEffect(() => {
    loadBudgets();
  }, []);

  function openCreate() {
    setEditingId(null);
    setForm(emptyForm);
    setError("");
    setSuccess("");
    setModalOpen(true);
  }

  function openEdit(budget) {
    setEditingId(budget.id);
    setForm({
      budgetAmount: String(budget.budgetAmount ?? ""),
      category: budget.category || "Food",
      month: backendToMonth(budget.date || budget.month),
      description: budget.description || ""
    });

    setError("");
    setSuccess("");
    setModalOpen(true);
  }

  function updateField(event) {
    const { name, value } = event.target;
    setForm(previous => ({ ...previous, [name]: value }));
  }

  async function handleSubmit(event) {
    event.preventDefault();
    setError("");
    setSuccess("");

    if (!form.budgetAmount || Number(form.budgetAmount) <= 0) {
      setError("Enter a valid budget amount.");
      return;
    }

    if (!form.description.trim()) {
      setError("Please enter a description.");
      return;
    }

    const payload = {
      budgetAmount: Number(form.budgetAmount),
      category: form.category,
      month: monthToBackend(form.month),
      description: form.description.trim()
    };

    setSaving(true);

    try {
      if (editingId) {
        await client.put(API.updateBudget(editingId), payload);
        setSuccess("Budget updated successfully.");
      } else {
        await client.post(API.addBudget, payload);
        setSuccess("Budget created successfully.");
      }

      setModalOpen(false);
      await loadBudgets();
    } catch (err) {
      setError(errorMessage(err));
    } finally {
      setSaving(false);
    }
  }

  async function deleteBudget(id) {
    if (!window.confirm("Are you sure you want to delete this budget?")) {
      return;
    }

    setError("");
    setSuccess("");

    try {
      await client.delete(API.deleteBudget(id));
      setSuccess("Budget deleted successfully.");
      await loadBudgets();
    } catch (err) {
      setError(errorMessage(err));
    }
  }

  const filtered = budgets.filter(budget =>
    `${budget.category} ${budget.description}`
      .toLowerCase()
      .includes(search.toLowerCase())
  );

  const totalLimit = budgets.reduce(
    (sum, budget) => sum + Number(budget.budgetAmount || 0),
    0
  );

  const totalUsed = budgets.reduce(
    (sum, budget) => sum + Number(budget.usedAmount || 0),
    0
  );

  const totalRemaining = totalLimit - totalUsed;

  return (
    <>
      <PageHeader
        eyebrow="FINANCIAL PLANNING"
        title="Budget management"
        subtitle="Set spending limits and stay in control of your money."
        action={
          <button className="button button-primary" onClick={openCreate}>
            <Plus size={18} /> Create budget
          </button>
        }
      />

      {error && <div className="alert error">{error}</div>}
      {success && <div className="budget-success">{success}</div>}

      <section className="budget-overview">
        <article className="budget-overview-card">
          <span className="budget-overview-icon purple">
            <Target size={21} />
          </span>
          <div>
            <small>Total budget limit</small>
            <strong>{money(totalLimit)}</strong>
          </div>
        </article>

        <article className="budget-overview-card">
          <span className="budget-overview-icon orange">
            <TrendingUp size={21} />
          </span>
          <div>
            <small>Total amount spent</small>
            <strong>{money(totalUsed)}</strong>
          </div>
        </article>

        <article className="budget-overview-card">
          <span className="budget-overview-icon green">
            <CheckCircle2 size={21} />
          </span>
          <div>
            <small>Remaining amount</small>
            <strong>{money(totalRemaining)}</strong>
          </div>
        </article>
      </section>

      <section className="panel budgets-panel">
        <div className="budgets-toolbar">
          <div>
            <h2>Your budgets</h2>
            <p>Monitor your spending limits and progress.</p>
          </div>

          <div className="budgets-toolbar-actions">
            <input
              className="budget-search"
              placeholder="Search category..."
              value={search}
              onChange={event => setSearch(event.target.value)}
            />

            <button
              className="button button-outline"
              onClick={loadBudgets}
              disabled={loading}
            >
              <RefreshCw size={16} /> Refresh
            </button>
          </div>
        </div>

        {loading ? (
          <div className="budget-empty">Loading your budgets...</div>
        ) : filtered.length === 0 ? (
          <div className="budget-empty">
            <span className="budget-empty-icon">
              <Target size={30} />
            </span>
            <h3>No budgets found</h3>
            <p>Create a budget to start tracking your spending.</p>
            <button className="button button-primary" onClick={openCreate}>
              <Plus size={17} /> Create your first budget
            </button>
          </div>
        ) : (
          <div className="budget-grid">
            {filtered.map(budget => {
              const amount = Number(budget.budgetAmount || 0);
              const spent = Number(budget.usedAmount || 0);
              const remaining = Number(
                budget.remainingAmount ?? amount - spent
              );
              const percentage = Number(budget.usagePercentage || 0);
              const progress = Math.min(Math.max(percentage, 0), 100);
              const status = statusInfo(budget.status);
              const StatusIcon = status.icon;

              return (
                <article className="budget-card" key={budget.id}>
                  <div className="budget-card-top">
                    <div className="budget-category-icon">
                      <Target size={19} />
                    </div>

                    <div className="budget-card-actions">
                      <button
                        title="Edit budget"
                        onClick={() => openEdit(budget)}
                      >
                        <Pencil size={15} />
                      </button>

                      <button
                        title="Delete budget"
                        onClick={() => deleteBudget(budget.id)}
                      >
                        <Trash2 size={15} />
                      </button>
                    </div>
                  </div>

                  <div className="budget-category-name">
                    {String(budget.category || "Other").replaceAll("_", " ")}
                  </div>

                  <div className="budget-month">
                    {budget.date || budget.month || "Budget period"}
                  </div>

                  <div className="budget-amount-line">
                    <strong>{money(spent)}</strong>
                    <span>of {money(amount)}</span>
                  </div>

                  <div className="budget-progress-track">
                    <div
                      className={`budget-progress-fill ${status.className}`}
                      style={{ width: `${progress}%` }}
                    />
                  </div>

                  <div className="budget-progress-label">
                    <span>{percentage.toFixed(1)}% used</span>
                    <span>{money(remaining)} left</span>
                  </div>

                  <div className={`budget-status ${status.className}`}>
                    <StatusIcon size={15} />
                    {status.label}
                  </div>

                  {budget.description && (
                    <p className="budget-description">
                      {budget.description}
                    </p>
                  )}
                </article>
              );
            })}
          </div>
        )}
      </section>

      {modalOpen && (
        <div
          className="budget-modal-backdrop"
          onMouseDown={() => !saving && setModalOpen(false)}
        >
          <section
            className="budget-modal"
            onMouseDown={event => event.stopPropagation()}
          >
            <div className="budget-modal-heading">
              <div>
                <span>BUDGET PLANNER</span>
                <h2>{editingId ? "Edit budget" : "Create a budget"}</h2>
                <p>Set a spending limit for a category and month.</p>
              </div>

              <button
                className="budget-modal-close"
                onClick={() => setModalOpen(false)}
                disabled={saving}
              >
                <X size={20} />
              </button>
            </div>

            <form className="budget-form" onSubmit={handleSubmit}>
              <label>
                Budget amount (₹)
                <input
                  type="number"
                  name="budgetAmount"
                  min="0.01"
                  step="0.01"
                  placeholder="e.g. 10000"
                  value={form.budgetAmount}
                  onChange={updateField}
                  required
                />
              </label>

              <label>
                Category
                <select
                  name="category"
                  value={form.category}
                  onChange={updateField}
                >
                  {categories.map(category => (
                    <option key={category} value={category}>
                      {category.replaceAll("_", " ")}
                    </option>
                  ))}
                </select>
              </label>

              <label>
                Budget month
                <input
                  type="month"
                  name="month"
                  value={form.month}
                  onChange={updateField}
                  required
                />
              </label>

              <label>
                Description
                <textarea
                  name="description"
                  rows={3}
                  placeholder="e.g. Monthly food and grocery budget"
                  value={form.description}
                  onChange={updateField}
                  required
                />
              </label>

              {error && <div className="alert error">{error}</div>}

              <div className="budget-form-actions">
                <button
                  type="button"
                  className="button button-outline"
                  onClick={() => setModalOpen(false)}
                  disabled={saving}
                >
                  Cancel
                </button>

                <button
                  type="submit"
                  className="button button-primary"
                  disabled={saving}
                >
                  {saving
                    ? "Saving..."
                    : editingId ? "Update budget" : "Create budget"}
                </button>
              </div>
            </form>
          </section>
        </div>
      )}
    </>
  );
}
