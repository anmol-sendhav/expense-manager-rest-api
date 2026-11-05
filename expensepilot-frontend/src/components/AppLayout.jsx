
import { useState } from "react";
import { NavLink, Outlet, useNavigate } from "react-router-dom";
import {
  Bell,
  LayoutDashboard,
  ArrowLeftRight,
  TrendingUp,
  Target,
  ChartNoAxesCombined,
  Settings,
  LogOut,
  UserRound,
  ShieldCheck,
  ChevronDown,
  Sparkles
} from "lucide-react";

import { useAuth } from "../context/AuthContext";
import Brand from "./Brand";

const links = [
  ["/dashboard", "Dashboard", LayoutDashboard],
  ["/transactions", "Transactions", ArrowLeftRight],
  ["/income", "Income", TrendingUp],
  ["/budgets", "Budgets", Target],
  ["/reports", "Reports & Analytics", ChartNoAxesCombined],
  ["/chatbot", "AI Assistant", Sparkles]
];

export default function AppLayout() {
  const { logout, user } = useAuth();
  const navigate = useNavigate();

  const [profileOpen, setProfileOpen] = useState(false);

  const name =
    user?.name || user?.fullName || user?.email || "Personal account";

	const getProfileImage = (picture) => {
	  if (!picture) return null;

	  if (picture.startsWith("http")) {
	    return picture;
	  }

	  const baseUrl = (
	    import.meta.env.VITE_API_BASE_URL || "http://localhost:8080"
	  ).replace(/\/$/, "");

	  return `${baseUrl}${picture.startsWith("/") ? "" : "/"}${picture}`;
	};

  const profileImage = getProfileImage(user?.picture);

  const handleLogout = () => {
    logout();
    navigate("/login");
  };

  const closeMenus = () => {
    setProfileOpen(false);
  };

  const openNotifications = () => {
    closeMenus();
    navigate("/notifications");
  };

  return (
    <div className="app-shell">
      <aside className="sidebar">
        <Brand />

        <div className="nav-label">WORKSPACE</div>

        <nav>
          {links.map(([to, label, Icon]) => (
            <NavLink
              key={to}
              to={to}
              className={({ isActive }) =>
                `nav-item ${isActive ? "active" : ""}`
              }
              onClick={closeMenus}
            >
              <Icon size={19} />
              <span>{label}</span>
            </NavLink>
          ))}
        </nav>

        <div className="nav-label secondary-nav-label">ACCOUNT</div>

        <nav>
          <NavLink
            to="/settings"
            className={({ isActive }) =>
              `nav-item ${isActive ? "active" : ""}`
            }
            onClick={closeMenus}
          >
            <Settings size={19} />
            <span>Settings</span>
          </NavLink>
        </nav>

        <div className="sidebar-note">
          <div className="note-icon">
            <Sparkles size={19} />
          </div>

          <b>Smarter money habits</b>

          <p>
            Understand your spending and build better financial habits.
          </p>
        </div>

        <div className="sidebar-user">
          <div className="avatar">
            {profileImage ? (
              <img src={profileImage} alt="Profile" />
            ) : (
              String(name).charAt(0).toUpperCase()
            )}
          </div>

          <div className="sidebar-user-info">
            <b>{name}</b>
            <small>Personal account</small>
          </div>
        </div>
      </aside>

      <main className="main-area">
        <header className="topbar">
          <div className="mobile-brand">
            <Brand />
          </div>

          <div className="top-spacer" />

          <div className="topbar-actions">

            {/* Notifications */}
			<button
			  type="button"
			  className="icon-button"
			  aria-label="Open notifications"
			  onClick={() => {
			    setProfileOpen(false);
			    navigate("/notifications");
			  }}
			>
			  <Bell size={19} />
			</button>

            {/* Profile */}
            <div className="header-menu">
              <button
                className="profile-trigger"
                aria-expanded={profileOpen}
                onClick={() => setProfileOpen(!profileOpen)}
              >
                <div className="avatar">
                  {profileImage ? (
                    <img src={profileImage} alt="Profile" />
                  ) : (
                    String(name).charAt(0).toUpperCase()
                  )}
                </div>

                <span className="top-user-info">
                  <b>{name}</b>
                  <small>Personal account</small>
                </span>

                <ChevronDown size={16} />
              </button>

              {profileOpen && (
                <div className="header-dropdown profile-dropdown">

                  <div className="profile-dropdown-header">
                    <div className="avatar">
                      {profileImage ? (
                        <img src={profileImage} alt="Profile" />
                      ) : (
                        String(name).charAt(0).toUpperCase()
                      )}
                    </div>

                    <div>
                      <b>{name}</b>
                      <small>{user?.email || "Personal account"}</small>
                    </div>
                  </div>

                  <div className="dropdown-divider" />

                  <button
                    className="dropdown-item"
                    onClick={() => {
                      closeMenus();
                      navigate("/settings");
                    }}
                  >
                    <UserRound size={17} />
                    My Profile
                  </button>

                  <button
                    className="dropdown-item"
                    onClick={() => {
                      closeMenus();
                      navigate("/settings");
                    }}
                  >
                    <ShieldCheck size={17} />
                    Account & Security
                  </button>

                  <button
                    className="dropdown-item"
                    onClick={() => {
                      closeMenus();
                      navigate("/settings");
                    }}
                  >
                    <Settings size={17} />
                    Settings
                  </button>

                  <div className="dropdown-divider" />

                  <button
                    className="dropdown-item logout-item"
                    onClick={handleLogout}
                  >
                    <LogOut size={17} />
                    Logout
                  </button>

                </div>
              )}
            </div>
          </div>
        </header>

        <div className="page-wrap">
          <Outlet />
        </div>
      </main>
    </div>
  );
}
