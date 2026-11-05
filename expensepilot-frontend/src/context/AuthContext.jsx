
import { createContext, useContext, useState } from "react";
import api from "../api/client";

const AuthContext = createContext(null);

// Reject an expired JWT during app startup. A token can remain in
// localStorage after its server-side expiry and otherwise block the login page.
function getValidStoredToken() {
  const savedToken = localStorage.getItem("expensepilot_token");
  if (!savedToken) return null;

  try {
    const payloadPart = savedToken.split(".")[1];
    if (!payloadPart) throw new Error("Invalid JWT");

    const normalized = payloadPart.replace(/-/g, "+").replace(/_/g, "/");
    const payload = JSON.parse(atob(normalized));
    if (payload.exp && payload.exp * 1000 <= Date.now()) {
      throw new Error("Expired JWT");
    }
    return savedToken;
  } catch {
    localStorage.removeItem("expensepilot_token");
    localStorage.removeItem("expensepilot_user");
    return null;
  }
}

export function AuthProvider({ children }) {
  const [token, setToken] = useState(() => getValidStoredToken());
  
  const [user, setUser] = useState(() => {
    try {
      const saved = localStorage.getItem("expensepilot_user");
      return saved ? JSON.parse(saved) : null;
    } catch {
      return null;
    }
  });

  const updateUser = (updatedUser) => {
    const nextUser = {
      ...user,
      ...updatedUser
    };

    localStorage.setItem("expensepilot_user", JSON.stringify(nextUser));
    setUser(nextUser);
  };
  
  const login = async (email, password) => {
    const response = await api.post("/auth/login", {
      email,
      password
    });

    // Your AuthController returns the JWT as a plain String.
    const jwt =
      typeof response.data === "string"
        ? response.data.trim()
        : response.data?.token || response.data?.accessToken;

    if (!jwt) {
      throw new Error("The server did not return an authentication token.");
    }

    localStorage.setItem("expensepilot_token", jwt);
    setToken(jwt);

    // Login succeeds using the JWT returned by /auth/login.
    // Fetching /users/me is intentionally kept out of this step until
    // that endpoint is verified against the Spring Boot controller.
    const userData = { email };

    localStorage.setItem("expensepilot_user", JSON.stringify(userData));
    setUser(userData);

    return response.data;
  };

  const register = async (name, email, password) => {
    return api.post("/auth/register", {
      username: name,
      email,
      password
    });
  };

  const logout = () => {
    localStorage.removeItem("expensepilot_token");
    localStorage.removeItem("expensepilot_user");

    setToken(null);
    setUser(null);
  };

  return (
    <AuthContext.Provider
      value={{
        token,
        user,
        isAuthenticated: Boolean(token),
        login,
        register,
        logout,
		updateUser
      }}
    >
      {children}
    </AuthContext.Provider>
  );
}

export function useAuth() {
  return useContext(AuthContext);
}
