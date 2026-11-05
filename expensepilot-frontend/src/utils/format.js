export const money = value => new Intl.NumberFormat("en-IN", {
  style: "currency", currency: "INR", maximumFractionDigits: 0
}).format(Number(value || 0));
export const getArray = data => Array.isArray(data) ? data : (data?.content || data?.data || data?.items || []);
export const errorMessage = error => error?.response?.data?.message || error?.response?.data?.error || error.message || "Something went wrong";