
import { useEffect, useState } from "react";

const API_BASE_URL =
  import.meta.env.VITE_API_BASE_URL || "http://localhost:8080";

export default function NotificationBell() {
  const [unreadCount, setUnreadCount] = useState(0);

  useEffect(() => {
    const loadUnreadCount = async () => {
      const token = localStorage.getItem("expensepilot_token");
      if (!token) return;

      try {
        const response = await fetch(
          `${API_BASE_URL}/notifications/unread-count`,
          {
            headers: {
              Authorization: `Bearer ${token}`,
            },
          }
        );

        if (!response.ok) return;

        const data = await response.json();
        const count =
          typeof data === "number"
            ? data
            : Number(data?.unreadCount ?? data?.count ?? 0);

        setUnreadCount(count);
      } catch (error) {
        console.error("Unable to load unread count:", error);
      }
    };

    loadUnreadCount();
  }, []);

  return (
    <a
      href="/notifications"
      className="notification-bell"
      aria-label="Open notifications"
      title="Notifications"
    >
      <span>🔔</span>
      {unreadCount > 0 && (
        <span className="notification-badge">
          {unreadCount > 99 ? "99+" : unreadCount}
        </span>
      )}
    </a>
  );
}
