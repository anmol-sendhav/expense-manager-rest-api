
import axios from "axios";

const client = axios.create({
  baseURL: import.meta.env.VITE_API_BASE_URL || "http://localhost:8080"
});

client.interceptors.request.use((config) => {
  const publicEndpoints = [
    "/auth/login",
    "/auth/register"
  ];

  const isPublicEndpoint = publicEndpoints.some(
    (endpoint) => config.url?.startsWith(endpoint)
  );

  if (!isPublicEndpoint) {
    const token = localStorage.getItem("expensepilot_token");

    if (token) {
      config.headers.Authorization = `Bearer ${token}`;
    }
  }

  return config;
});

client.interceptors.response.use(
  (response) => response,
  (error) => {
    if (error.response?.status === 401) {
      localStorage.removeItem("expensepilot_token");
      localStorage.removeItem("expensepilot_user");

      if (
        window.location.pathname !== "/login" &&
        window.location.pathname !== "/register"
      ) {
        window.location.href = "/login";
      }
    }

    return Promise.reject(error);
  }
);

export default client;
