
import { useEffect, useState } from "react";
import client from "../api/client";
import { API } from "../api/endpoints";
import PageHeader from "../components/PageHeader";
import { getArray, money, errorMessage } from "../utils/format";
import {
  Plus,
  Search,
  RefreshCw,
  X,
  Trash2,
  Pencil,
  ArrowDownLeft,
  ArrowUpRight,
  Wallet,
  CalendarDays,
  LoaderCircle
} from "lucide-react";

const categories = [
  "Shopping", "Entertaiment", "Rent", "Electricity_Bills",
  "Education", "Food", "Travel", "Medical", "Groceries",
  "Gym", "Recharge", "Diet", "Clothes",
  "PREMIUM_SUBSCRIPTION_MONTHLY", "Vegetables",
  "Freelance", "Petrol", "Transport", "Other"
];

const paymentMethods = [
  "UPI", "NET_BANKING", "CREDIT_CARD", "CASH"
];

const paymentWays = [
  "BankTransfer", "Upi", "NetBanking", "Card", "DebitCard"
];

const initialForm = {
  title: "",
  amount: "",
  description: "",
  category: "Food",
  paymentMethod: "UPI",
  source: "",
  paymentWay: "Upi"
};

export default function Transactions() {
  const [items, setItems] = useState([]);
  const [busy, setBusy] = useState(true);
  const [saving, setSaving] = useState(false);
  const [error, setError] = useState("");
  const [success, setSuccess] = useState("");
  const [search, setSearch] = useState("");
  const [modalOpen, setModalOpen] = useState(false);
  const [editItem, setEditItem] = useState(null);
  const [type, setType] = useState("Expense");
  const [form, setForm] = useState(initialForm);

  async function load() {
    setBusy(true);
    setError("");

    try {
      const [expenseResponse, incomeResponse] = await Promise.all([
        client.get(API.expenses),
        client.get(API.incomes)
      ]);

      const expenses = getArray(expenseResponse.data).map(item => ({
        ...item,
        _type: "Expense",
        _date: item.date
      }));

      const incomes = getArray(incomeResponse.data).map(item => ({
        ...item,
        _type: "Income",
        _date: item.amountDate
      }));

      const combined = [...expenses, ...incomes].sort(
        (a, b) => new Date(b._date || 0) - new Date(a._date || 0)
      );

      setItems(combined);
    } catch (err) {
      setError(errorMessage(err));
    } finally {
      setBusy(false);
    }
  }

  useEffect(() => {
    load();
  }, []);

  function openModal() {
    setEditItem(null);
    setForm(initialForm);
    setType("Expense");
    setError("");
    setSuccess("");
    setModalOpen(true);
  }
  
  function editTransaction(item) {
    setEditItem(item);
    setType(item._type);

    setForm({
      title: item.title || "",
      amount: item.amount ?? "",
      description: item.description || "",
      category: item.category || "Food",
      paymentMethod: item.paymentMethod || "UPI",
      source: item.source || "",
      paymentWay: item.paymentWay || "Upi"
    });

    setError("");
    setSuccess("");
    setModalOpen(true);
  }

  function closeModal() {
    if (!saving) setModalOpen(false);
  }

  function updateField(event) {
    const { name, value } = event.target;
    setForm(previous => ({ ...previous, [name]: value }));
  }

  async function handleSubmit(event) {
    event.preventDefault();
    setError("");
    setSuccess("");

    if (!form.title.trim() || !form.amount || Number(form.amount) <= 0) {
      setError("Enter a valid title and amount.");
      return;
    }

    if (!form.description.trim()) {
      setError("Please enter a description.");
      return;
    }

    if (type === "Income" && !form.source.trim()) {
      setError("Please enter the income source.");
      return;
    }

    const amount = Number(form.amount);

    const payload = type === "Expense"
      ? {
          title: form.title.trim(),
          amount,
          description: form.description.trim(),
          category: form.category,
          paymentMethod: form.paymentMethod
        }
      : {
          title: form.title.trim(),
          source: form.source.trim(),
          amount,
          description: form.description.trim(),
          paymentWay: form.paymentWay
        };

    setSaving(true);

	try {
	  if (editItem) {
	    const id = editItem._type === "Expense"
	      ? editItem.expenseId
	      : editItem.incomeId;

	    const endpoint = editItem._type === "Expense"
	      ? API.updateExpense(id)
	      : API.updateIncome(id);

	    await client.put(endpoint, payload);

	    setSuccess(`${type} updated successfully.`);
	  } else {
	    if (type === "Expense") {
	      await client.post(API.addExpense, payload);
	    } else {
	      await client.post(API.addIncome, payload);
	    }

	    setSuccess(`${type} added successfully.`);
	  }

	  setModalOpen(false);
	  setEditItem(null);
	  setForm(initialForm);

	  await load();

	} catch (err) {
	  setError(errorMessage(err));
	} finally {
	  setSaving(false);
	}
	}

  async function deleteTransaction(item) {
    const id = item._type === "Expense"
      ? item.expenseId
      : item.incomeId;

    if (!id) {
      setError("Transaction ID was not returned by the server.");
      return;
    }

    const confirmed = window.confirm(
      `Are you sure you want to delete this ${item._type.toLowerCase()}?`
    );

    if (!confirmed) return;

    setError("");
    setSuccess("");

    try {
      const endpoint = item._type === "Expense"
        ? API.deleteExpense(id)
        : API.deleteIncome(id);

      await client.delete(endpoint);
      setSuccess(`${item._type} deleted successfully.`);
      await load();
    } catch (err) {
      setError(errorMessage(err));
    }
  }

  const visible = items.filter(item => {
    const text = [
      item.title,
      item.description,
      item.category,
      item.source,
      item._type
    ].join(" ").toLowerCase();

    return text.includes(search.toLowerCase());
  });

  const totalIncome = items
    .filter(item => item._type === "Income")
    .reduce((sum, item) => sum + Number(item.amount || 0), 0);

  const totalExpenses = items
    .filter(item => item._type === "Expense")
    .reduce((sum, item) => sum + Number(item.amount || 0), 0);

  return (
    <>
      <PageHeader
        eyebrow="MONEY MANAGEMENT"
        title="Transactions"
        subtitle="Track every rupee coming in and going out."
        action={
          <button className="button button-primary" onClick={openModal}>
            <Plus size={18} /> Add transaction
          </button>
        }
      />

      {error && <div className="alert error">{error}</div>}
      {success && <div className="transaction-success">{success}</div>}

      <section className="transaction-summary">
        <article className="transaction-summary-card">
          <div className="transaction-summary-icon income-icon">
            <ArrowDownLeft size={21} />
          </div>
          <div>
            <span>Total income</span>
            <strong className="amount-positive">{money(totalIncome)}</strong>
          </div>
        </article>

        <article className="transaction-summary-card">
          <div className="transaction-summary-icon expense-icon">
            <ArrowUpRight size={21} />
          </div>
          <div>
            <span>Total expenses</span>
            <strong className="amount-negative">{money(totalExpenses)}</strong>
          </div>
        </article>

        <article className="transaction-summary-card">
          <div className="transaction-summary-icon balance-icon">
            <Wallet size={21} />
          </div>
          <div>
            <span>Net balance</span>
            <strong>{money(totalIncome - totalExpenses)}</strong>
          </div>
        </article>
      </section>

      <section className="panel transaction-panel">
        <div className="transaction-toolbar">
          <div>
            <h2>All transactions</h2>
            <p>Your personal financial activity</p>
          </div>

          <div className="transaction-actions">
            <div className="search-box">
              <Search size={17} />
              <input
                placeholder="Search transactions..."
                value={search}
                onChange={event => setSearch(event.target.value)}
              />
            </div>

            <button
              className="button button-outline"
              onClick={load}
              disabled={busy}
            >
              <RefreshCw size={16} /> Refresh
            </button>
          </div>
        </div>

        {busy ? (
          <div className="transaction-loading">
            <LoaderCircle className="spin" size={25} />
            Loading your transactions...
          </div>
        ) : visible.length === 0 ? (
          <div className="transaction-empty">
            <div className="transaction-empty-icon">
              <Wallet size={30} />
            </div>
            <h3>No transactions found</h3>
            <p>Add your first income or expense to get started.</p>
            <button className="button button-primary" onClick={openModal}>
              <Plus size={17} /> Add transaction
            </button>
          </div>
        ) : (
          <div className="table-scroll">
            <table className="transaction-table">
              <thead>
                <tr>
                  <th>TRANSACTION</th>
                  <th>TYPE</th>
                  <th>CATEGORY / SOURCE</th>
                  <th>DATE</th>
                  <th>AMOUNT</th>
                  <th>ACTION</th>
                </tr>
              </thead>

              <tbody>
                {visible.map((item, index) => (
                  <tr key={`${item._type}-${item.expenseId || item.incomeId || index}`}>
                    <td>
                      <div className="transaction-title">
                        <div className={`transaction-row-icon ${item._type === "Income" ? "income-icon" : "expense-icon"}`}>
                          {item._type === "Income"
                            ? <ArrowDownLeft size={17} />
                            : <ArrowUpRight size={17} />}
                        </div>
                        <div>
                          <b>{item.title || "Untitled"}</b>
                          <small>{item.description || "No description"}</small>
                        </div>
                      </div>
                    </td>

                    <td>
                      <span className={`type-pill ${item._type.toLowerCase()}`}>
                        {item._type}
                      </span>
                    </td>

                    <td>
                      {item._type === "Income"
                        ? item.source || "—"
                        : item.category || "—"}
                    </td>

                    <td>
                      <span className="transaction-date">
                        <CalendarDays size={14} />
                        {item._date || "—"}
                      </span>
                    </td>

                    <td className={item._type === "Income" ? "amount-positive" : "amount-negative"}>
                      {item._type === "Income" ? "+" : "−"}{money(item.amount)}
                    </td>

					<td>
					  <div className="transaction-row-actions">
					    <button
					      className="transaction-edit"
					      title="Edit transaction"
					      onClick={() => editTransaction(item)}
					    >
					      <Pencil size={15} />
					    </button>

					    <button
					      className="transaction-delete"
					      title="Delete transaction"
					      onClick={() => deleteTransaction(item)}
					    >
					      <Trash2 size={15} />
					    </button>
					  </div>
					</td>
                  </tr>
                ))}
              </tbody>
            </table>
          </div>
        )}
      </section>

      {modalOpen && (
        <div className="transaction-modal-backdrop" onMouseDown={closeModal}>
          <section
            className="transaction-modal"
            onMouseDown={event => event.stopPropagation()}
          >
            <div className="transaction-modal-header">
              <div>
                <span>{editItem ? "UPDATE TRANSACTION" : "NEW TRANSACTION"}</span>
                <h2>{editItem ? "Edit transaction" : "Add transaction"}</h2>
                <p>Enter the details of your money activity.</p>
              </div>

              <button
                className="modal-close"
                onClick={closeModal}
                disabled={saving}
              >
                <X size={20} />
              </button>
            </div>

            <div className="transaction-type-switch">
              <button
                type="button"
                className={type === "Expense" ? "selected expense-selected" : ""}
                onClick={() => setType("Expense")}
              >
                <ArrowUpRight size={18} /> Expense
              </button>

              <button
                type="button"
                className={type === "Income" ? "selected income-selected" : ""}
                onClick={() => setType("Income")}
              >
                <ArrowDownLeft size={18} /> Income
              </button>
            </div>

            <form onSubmit={handleSubmit} className="transaction-form">
              <label>
                Transaction title
                <input
                  name="title"
                  value={form.title}
                  onChange={updateField}
                  placeholder="e.g. Monthly salary or Grocery shopping"
                  required
                />
              </label>

              <label>
                Amount (₹)
                <input
                  name="amount"
                  type="number"
                  min="0.01"
                  step="0.01"
                  value={form.amount}
                  onChange={updateField}
                  placeholder="Enter amount"
                  required
                />
              </label>

              {type === "Expense" ? (
                <>
                  <label>
                    Category
                    <select name="category" value={form.category} onChange={updateField}>
                      {categories.map(category => (
                        <option key={category} value={category}>
                          {category.replaceAll("_", " ")}
                        </option>
                      ))}
                    </select>
                  </label>

                  <label>
                    Payment method
                    <select name="paymentMethod" value={form.paymentMethod} onChange={updateField}>
                      {paymentMethods.map(method => (
                        <option key={method} value={method}>
                          {method.replaceAll("_", " ")}
                        </option>
                      ))}
                    </select>
                  </label>
                </>
              ) : (
                <>
                  <label>
                    Income source
                    <input
                      name="source"
                      value={form.source}
                      onChange={updateField}
                      placeholder="e.g. Salary, Freelancing"
                      required
                    />
                  </label>

                  <label>
                    Payment method
                    <select name="paymentWay" value={form.paymentWay} onChange={updateField}>
                      {paymentWays.map(method => (
                        <option key={method} value={method}>
                          {method}
                        </option>
                      ))}
                    </select>
                  </label>
                </>
              )}

              <label>
                Description
                <textarea
                  name="description"
                  value={form.description}
                  onChange={updateField}
                  placeholder="Add a short note..."
                  rows={3}
                  required
                />
              </label>

              {error && <div className="alert error">{error}</div>}

              <div className="transaction-form-actions">
                <button
                  type="button"
                  className="button button-outline"
                  onClick={closeModal}
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
                    ? <><LoaderCircle className="spin" size={17} /> Saving...</>
                    : <><>{editItem ? <Pencil size={17} /> : <Plus size={17} />}
					  <span>{editItem ? "UPDATE TRANSACTION" : "NEW TRANSACTION"}</span>
					</></>}
                </button>
              </div>
            </form>
          </section>
        </div>
      )}
    </>
  );
  }