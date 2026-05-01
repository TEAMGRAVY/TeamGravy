import {useState} from "react";

export default function LoginModal({ onLogin }) {
  const BASE = import.meta.env.VITE_BACKEND_URL;
  const [mode, setMode] = useState("login");
  const [username, setUsername] = useState("");
  const [password, setPassword] = useState("");
  const [error, setError] = useState("");

  async function handleSubmit() {
    try {
      let res;

      if (mode === "login") {
        res = await fetch(`${BASE}/profile?username=${username}&password=${password}`);
      } else {
        res = await fetch(`${BASE}/profile/new`, {
          method: "POST",
          headers: { "Content-Type": "application/json" },
          body: JSON.stringify({ username, password })
        });
      }

      if (!res.ok) {
        const data = await res.json();
        setError(data.error || "Failed");
        return;
      }

      const data = await res.json();
      onLogin(data);
    } catch (err) {
      setError("Network error");
    }
  }

  return (
    <div className="modal-overlay-profile">
      <div className="modal-profile">
        <h2>{mode === "login" ? "Sign In" : "Create Profile"}</h2>

        <input
          placeholder="Username"
          value={username}
          onChange={e => setUsername(e.target.value)}
        />

        <input
          type="password"
          placeholder="Password"
          value={password}
          onChange={e => setPassword(e.target.value)}
        />

        <button onClick={handleSubmit}>
          {mode === "login" ? "Sign In" : "Create"}
        </button>

        {error && <div className="error-msg">{error}</div>}

        <div className="switch-mode">
          {mode === "login" ? (
            <span onClick={() => setMode("register")}>
              Create an account
            </span>
          ) : (
            <span onClick={() => setMode("login")}>
              Already have an account?
            </span>
          )}
        </div>
      </div>
    </div>
  );
}