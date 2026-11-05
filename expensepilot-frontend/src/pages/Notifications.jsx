
import { useCallback, useEffect, useState } from "react";
import { Bell, CheckCheck, Trash2, RefreshCw } from "lucide-react";
import client from "../api/client";

export default function Notifications() {
  const [notifications, setNotifications] = useState([]);
  const [unreadCount, setUnreadCount] = useState(0);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState("");

  const loadNotifications = useCallback(async () => {
    try {
      setError("");

      const [listResponse, countResponse] = await Promise.all([
        client.get("/notifications"),
        client.get("/notifications/unread-count"),
      ]);

      setNotifications(
        Array.isArray(listResponse.data) ? listResponse.data : []
      );

      const count = countResponse.data;
      setUnreadCount(
        typeof count === "number"
          ? count
          : Number(count?.unreadCount ?? count?.count ?? 0)
      );
    } catch (err) {
      setError(
        err.response?.data?.message ||
        "Unable to load notifications. Please try again."
      );
    } finally {
      setLoading(false);
    }
  }, []);

  useEffect(() => {
    loadNotifications();
  }, [loadNotifications]);

  const markAsRead = async (id) => {
    try {
      await client.put(`/notifications/${id}/read`);
      await loadNotifications();
    } catch (err) {
      setError("Unable to mark notification as read.");
    }
  };

  const markAllAsRead = async () => {
    try {
      await client.put("/notifications/read-all");
      await loadNotifications();
    } catch (err) {
      setError("Unable to mark all notifications as read.");
    }
  };

  const deleteNotification = async (id) => {
    try {
      await client.delete(`/notifications/${id}`);
      await loadNotifications();
    } catch (err) {
      setError("Unable to delete notification.");
    }
  };

  const getIcon = (type) => {
    switch ((type || "").toUpperCase()) {
      case "BUDGET":
        return "💰";
      case "WARNING":
        return "⚠️";
      case "SUCCESS":
        return "✅";
      case "REMINDER":
        return "⏰";
      default:
        return "🔔";
    }
  };

  return (
    <div className="notification-page">
      <div className="notification-page-header">
        <div>
          <span className="notification-eyebrow">STAY UPDATED</span>
          <h1>Notifications</h1>
          <p>Important updates about your financial activity.</p>
        </div>

        <button
          className="notification-refresh"
          onClick={loadNotifications}
        >
          <RefreshCw size={16} />
          Refresh
        </button>
      </div>

      <div className="notification-stats">
        <div>
          <span>Total notifications</span>
          <strong>{notifications.length}</strong>
        </div>

        <div>
          <span>Unread notifications</span>
          <strong>{unreadCount}</strong>
        </div>
      </div>

      <div className="notification-toolbar">
        <h2>Recent activity</h2>

        <button
          onClick={markAllAsRead}
          disabled={unreadCount === 0}
        >
          <CheckCheck size={16} />
          Mark all as read
        </button>
      </div>

      {error && <div className="notification-error">{error}</div>}

      {loading ? (
        <div className="notification-empty">Loading notifications...</div>
      ) : notifications.length === 0 ? (
        <div className="notification-empty">
          <Bell size={32} />
          <h3>You're all caught up!</h3>
          <p>Your new notifications will appear here.</p>
        </div>
      ) : (
        <div className="notification-list">
          {notifications.map((item) => (
            <div
              className={`notification-card ${
                item.read ? "" : "notification-unread"
              }`}
              key={item.id}
            >
              <div className="notification-type-icon">
                {getIcon(item.type)}
              </div>

              <div className="notification-details">
                <h3>{item.title}</h3>
                <p>{item.message}</p>
                <small>
                  {item.createdAt
                    ? new Date(item.createdAt).toLocaleString()
                    : ""}
                </small>
              </div>

              <div className="notification-card-actions">
                {!item.read && (
                  <button onClick={() => markAsRead(item.id)}>
                    Mark read
                  </button>
                )}

                <button
                  className="notification-delete"
                  onClick={() => deleteNotification(item.id)}
                  aria-label="Delete notification"
                >
                  <Trash2 size={16} />
                </button>
              </div>
            </div>
          ))}
        </div>
      )}
    </div>
  );
}
