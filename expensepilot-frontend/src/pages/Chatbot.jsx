import { useEffect, useRef, useState } from "react";
import api from "../api/client";

const suggestions = [
  {
    icon: "💰",
    title: "Analyze my expenses",
    prompt: "Help me understand how I can reduce my expenses."
  },
  {
    icon: "📊",
    title: "Budget advice",
    prompt: "Give me some practical tips to manage my monthly budget."
  },
  {
    icon: "🎯",
    title: "Saving tips",
    prompt: "How can I improve my monthly savings?"
  },
  {
    icon: "💡",
    title: "Financial guidance",
    prompt: "Give me five useful personal finance tips."
  }
];

export default function Chatbot() {
  const [messages, setMessages] = useState([
    {
      role: "assistant",
      content:
        "Hello! I'm ExpensePilot AI 👋\n\nI'm here to help you understand your finances, manage expenses, plan budgets, and improve your savings. What would you like to know?"
    }
  ]);

  const [input, setInput] = useState("");
  const [loading, setLoading] = useState(false);
  const messagesEndRef = useRef(null);
  const inputRef = useRef(null);

  useEffect(() => {
    messagesEndRef.current?.scrollIntoView({ behavior: "smooth" });
  }, [messages, loading]);

  const sendMessage = async (text = input) => {
    const message = text.trim();

    if (!message || loading) return;

    setMessages((previous) => [
      ...previous,
      { role: "user", content: message }
    ]);

    setInput("");
    setLoading(true);

    try {
      // Uses the existing Axios client and its JWT interceptor.
      const response = await api.post("/chat", {
        message: message
      });

      const aiResponse = response.data?.response;

      setMessages((previous) => [
        ...previous,
        {
          role: "assistant",
          content: aiResponse || "I couldn't generate a response. Please try again."
        }
      ]);
    } catch (error) {
      console.error("Chatbot error:", error);

      const errorMessage =
        error.response?.data?.message ||
        "I'm unable to connect right now. Please check your connection and try again.";

      setMessages((previous) => [
        ...previous,
        {
          role: "assistant",
          content: errorMessage,
          isError: true
        }
      ]);
    } finally {
      setLoading(false);
      inputRef.current?.focus();
    }
  };

  const handleSubmit = (event) => {
    event.preventDefault();
    sendMessage();
  };

  const startNewChat = () => {
    setMessages([
      {
        role: "assistant",
        content:
          "Hello again! 👋 What would you like to explore about your finances today?"
      }
    ]);
    setInput("");
    inputRef.current?.focus();
  };

  return (
    <div className="chatbot-page">
      <div className="chatbot-container">

        {/* Header */}
        <div className="chatbot-header">
          <div className="chatbot-header-left">
            <div className="chatbot-avatar">
              <span>✦</span>
            </div>

            <div>
              <h2>ExpensePilot AI</h2>
              <div className="chatbot-status">
                <span className="chatbot-status-dot"></span>
                AI Assistant
              </div>
            </div>
          </div>

          <button
            type="button"
            className="chatbot-new-btn"
            onClick={startNewChat}
            disabled={loading}
          >
            <span>＋</span> New chat
          </button>
        </div>

        {/* Messages */}
        <div className="chatbot-messages">

          {messages.length === 1 && (
            <div className="chatbot-welcome">
              <div className="chatbot-welcome-icon">✦</div>
              <h1>Your personal finance assistant</h1>
              <p>
                Ask questions, explore financial ideas, and get help
                managing your money.
              </p>

              <div className="chatbot-suggestions">
                {suggestions.map((item) => (
                  <button
                    type="button"
                    key={item.title}
                    className="chatbot-suggestion-card"
                    onClick={() => sendMessage(item.prompt)}
                    disabled={loading}
                  >
                    <span className="suggestion-icon">{item.icon}</span>
                    <span>
                      <strong>{item.title}</strong>
                      <small>{item.prompt}</small>
                    </span>
                    <span className="suggestion-arrow">↗</span>
                  </button>
                ))}
              </div>
            </div>
          )}

          {messages.map((message, index) => (
            <div
              key={index}
              className={`chatbot-message-row ${
                message.role === "user" ? "user-message-row" : "ai-message-row"
              }`}
            >
              {message.role === "assistant" && (
                <div className="chatbot-message-avatar">✦</div>
              )}

              <div
                className={`chatbot-message ${
                  message.role === "user"
                    ? "chatbot-user-message"
                    : "chatbot-ai-message"
                } ${message.isError ? "chatbot-error-message" : ""}`}
              >
                {message.content}
              </div>

              {message.role === "user" && (
                <div className="chatbot-user-avatar">You</div>
              )}
            </div>
          ))}

          {loading && (
            <div className="chatbot-message-row ai-message-row">
              <div className="chatbot-message-avatar">✦</div>
              <div className="chatbot-typing">
                <span></span>
                <span></span>
                <span></span>
              </div>
            </div>
          )}

          <div ref={messagesEndRef} />
        </div>

        {/* Input */}
        <div className="chatbot-input-area">
          <form onSubmit={handleSubmit} className="chatbot-input-form">
            <textarea
              ref={inputRef}
              value={input}
              onChange={(event) => setInput(event.target.value)}
              onKeyDown={(event) => {
                if (event.key === "Enter" && !event.shiftKey) {
                  event.preventDefault();
                  handleSubmit(event);
                }
              }}
              placeholder="Ask ExpensePilot AI anything..."
              rows={1}
              maxLength={2000}
              disabled={loading}
            />

            <button
              type="submit"
              className="chatbot-send-btn"
              disabled={!input.trim() || loading}
              aria-label="Send message"
            >
              ➤
            </button>
          </form>

          <p className="chatbot-disclaimer">
            AI can make mistakes. Verify important financial decisions.
          </p>
        </div>

      </div>
    </div>
  );
}