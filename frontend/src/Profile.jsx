import { useState } from "react";

export default function ProfileMenu({ user, onLogout, onOpenSettings }) {
  const [open, setOpen] = useState(false);

  if (!user) return null;

  return (
    <div className="profile-container">

      {/* Avatar */}
      <div
        className="profile-avatar"
        onClick={() => setOpen(prev => !prev)}
      >
        {user.name?.charAt(0).toUpperCase() || "U"}
      </div>

      {/* Dropdown */}
      {open && (
        <div className="profile-dropdown">

          <div className="profile-header">
            <div className="profile-name">{user.name}</div>
          </div>

          <div className="profile-divider" />

          <button
            className="profile-item"
            onClick={() => {
              setOpen(false);
              onOpenSettings();
            }}
          >
            Settings
          </button>

          <button
            className="profile-item"
            onClick={onLogout}
          >
            Logout
          </button>

        </div>
      )}
    </div>
  );
}