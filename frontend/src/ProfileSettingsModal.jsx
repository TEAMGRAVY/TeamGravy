import { useState } from "react";

export default function ProfileSettingsModal({ user, onClose, onUpdate }) {

  // Core fields
  const [name, setName] = useState(user.name || "");
  const [email, setEmail] = useState(user.email || "");
  const [major, setMajor] = useState(user.major || "");
  const [gradYear, setGradYear] = useState(user.gradYear || "");

  // Password (optional)
  const [password, setPassword] = useState("");

  // Preferences (your boolean array order)
  const [prefs, setPrefs] = useState({
    darkMode: user.preferences?.[0] ?? true,
    longestBreak: user.preferences?.[1] ?? true,
    showWarnings: user.preferences?.[2] ?? true
  });

  function togglePref(key) {
    setPrefs(prev => ({ ...prev, [key]: !prev[key] }));
  }

  async function updateField(attribute, value) {
    await fetch("/profile/update", {
      method: "PATCH",
      headers: { "Content-Type": "application/json" },
      body: JSON.stringify({ attribute, value: String(value) })
    });
  }

  async function saveProfile(){
      const res = await fetch(`/profile/save/`, { method: "POST" });
      }

  async function handleSave() {
    try {
      // Core fields
      await updateField("name", name);
      await updateField("email", email);
      await updateField("major", major);
      await updateField("gradYear", gradYear);

      // Password (only if entered)
      if (password.trim()) {
        await updateField("password", password);
      }

      // Preferences
      await updateField("darkMode", prefs.darkMode);
      await updateField("longestBreak", prefs.longestBreak);
      await updateField("showWarnings", prefs.showWarnings);

      // Update frontend user state
      onUpdate({
        ...user,
        name,
        email,
        major,
        gradYear,
        preferences: [
          prefs.darkMode,
          prefs.longestBreak,
          prefs.showWarnings
        ]
      });
      saveProfile();
      onClose();

    } catch (err) {
      console.error(err);
      alert("Failed to update profile");
    }
  }

  return (
    <div className="modal-overlay" onClick={onClose}>
      <div className="modal-box" onClick={e => e.stopPropagation()}>

        <h3>Profile Settings</h3>

        {/* BASIC INFO */}
        <div className="settings-section">
          <h4>Basic Info</h4>

          <input value={name} onChange={e => setName(e.target.value)} placeholder="Name" />
          <input value={email} onChange={e => setEmail(e.target.value)} placeholder="Email" />
          <input value={major} onChange={e => setMajor(e.target.value)} placeholder="Major" />
          <input value={gradYear} onChange={e => setGradYear(e.target.value)} placeholder="Grad Year" />
        </div>

        {/* PASSWORD */}
        <div className="settings-section">
          <h4>Security</h4>

          <input
            type="password"
            placeholder="New Password (optional)"
            value={password}
            onChange={e => setPassword(e.target.value)}
          />
        </div>

        {/* PREFERENCES */}
        <div className="settings-section">
          <h4>Preferences</h4>

          <div className="settings-row">
            <label>
            <span>Dark Mode</span>
            <input
              type="checkbox"
              checked={prefs.darkMode}
              onChange={() => togglePref("darkMode")}
            />
            </label>
            <label>
            <span>Show Longest Break</span>
            <input
              type="checkbox"
              checked={prefs.longestBreak}
              onChange={() => togglePref("longestBreak")}
            />
            </label>
            <label>
            <span>Show Credit Warnings</span>
            <input
              type="checkbox"
              checked={prefs.showWarnings}
              onChange={() => togglePref("showWarnings")}
            />
            </label>
          </div>
        </div>

        <div className="modal-actions">
          <button className="btn-modal-keep" onClick={onClose}>Cancel</button>
          <button className="btn-modal-undo" onClick={handleSave}>Save</button>
        </div>

      </div>
    </div>
  );
}