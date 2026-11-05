
export const API = {
  login: "/auth/login",
  register: "/auth/register",
  dashboard: "/dashboard/summary",

  expenses: "/expenses/getAllExpenses",
  addExpense: "/expenses/addExpense",
  updateExpense: (id) => `/expenses/updateExpenseById/${id}`,
  deleteExpense: (id) => `/expenses/deleteExpenseById/${id}`,

  incomes: "/income/getAllIncome",
  addIncome: "/income/addIncome",
  updateIncome: (id) => `/income/updateIncomeByUserId/${id}`,
  deleteIncome: (id) => `/income/deleteIncomeByIncomeId/${id}`,

  budgets: "/budget/getAllBudget",
  addBudget: "/budget/addBudget",
  updateBudget: (id) => `/budget/modifyBudget/${id}`,
  deleteBudget: (id) => `/budget/deleteBudget/${id}`,
  budgetUsage: (id) => `/budget/${id}/usage`,
  reports: "/reports/summary",
  profile: "/users/me",
};
