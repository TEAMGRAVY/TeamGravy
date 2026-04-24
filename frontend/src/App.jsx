import { useState, useEffect } from "react";
import { Routes, Route, Link} from "react-router-dom";
import CalendarPage from "./CalendarPage";
import "./App.css";
import ProfileMenu from "./Profile";
import LoginModal from "./LoginModal"
import ProfileSettingsModal from "./ProfileSettingsModal"

const DAY_LABELS = {
  MONDAY: "Mon", TUESDAY: "Tue", WEDNESDAY: "Wed", THURSDAY: "Thu", FRIDAY: "Fri"
};

// Converts "12:00" to "12:00 PM"
function formatTime(t) {
  if (!t) return "";
  const [h, m] = t.split(":").map(Number);
  const ampm = h >= 12 ? "PM" : "AM";
  return `${h % 12 || 12}:${String(m).padStart(2, "0")} ${ampm}`;
}

// Returns the days a section meets, sorted Mon-Fri
function sectionDays(section) {
  const allDays = section.time.flatMap(slot => slot.days);
  const unique = [...new Set(allDays)];
  const order = ["MONDAY", "TUESDAY", "WEDNESDAY", "THURSDAY", "FRIDAY"];
  return unique.sort((a, b) => order.indexOf(a) - order.indexOf(b));
}

// Builds a time string like "Mon/Wed 10:00 AM–10:50 AM"
export function sectionTimeStr(section) {
  if (!section.time || section.time.length === 0) return "No schedule";
  const slot = section.time[0];
  const days = sectionDays(section).map(d => DAY_LABELS[d]).join("/");
  return `${days} ${formatTime(slot.startTime)}–${formatTime(slot.endTime)}`;
}

// Builds the URL used to add/remove a section from the schedule
function scheduleUrl(s) {
  return `/schedule/${s.course.department}/${s.course.courseID}/${s.sectionID}/${s.term}`;
}

export default function App() {
  // Dropdown options populated from /courses on load
  const [departments, setDepartments] = useState([]);
  const [professors,  setProfessors]  = useState([]);
  const [terms,       setTerms]       = useState([]);

  // Filter state maps to query param sent to /search
  const [codeQ,    setCodeQ]    = useState("");
  const [keyQ,     setKeyQ]     = useState("");
  const [dept,     setDept]     = useState("");
  const [prof,     setProf]     = useState("");
  const [credits,  setCredits]  = useState("");
  const [timeFrom, setTimeFrom] = useState("");
  const [timeTo,   setTimeTo]   = useState("");
  const [term,     setTerm]     = useState("");
  const [days,     setDays]     = useState([]);
  const [isOpen,   setIsOpen]   = useState(false);

  // Search results returned from /search
  const [results,  setResults]  = useState([]);
  const [searched, setSearched] = useState(false);

  // Schedule state (sections + metrics from schedule)
  const [schedule, setSchedule] = useState({ sections: [], totalCredits: 0, daysWithoutClass: 5, longestBreak: 0 });
  const [schedMsg, setSchedMsg] = useState("");
  const [scheduleName, setScheduleName] = useState("My Schedule");

  // creditWarning controls whether the high credit modal is visible.
  // lastAdded holds the section that pushed the total over the 18-credit limit.
  const [creditWarning, setCreditWarning] = useState(false);
  const [lastAdded,     setLastAdded]     = useState(null);

  // lowCreditWarning controls whether the low credit modal is visible.
  // lastRemoved holds the section that was just deleted and caused the
  // total to cross down through the 12-credit full-time student threshold.
  const [lowCreditWarning, setLowCreditWarning] = useState(false);
  const [lastRemoved,      setLastRemoved]      = useState(null);

  // Show section details
  const [selectedSection, setSelectedSection] = useState(null);

  // Saved for later sections
  // Initialize from localStorage
  const [saved, setSaved] = useState(() => {
    try {
      const stored = localStorage.getItem("savedSections");
      return stored ? new Map(JSON.parse(stored)) : new Map();
    } catch { return new Map(); }
  });
  // Make it a collapsible list
  const [savedOpen, setSavedOpen] = useState(false);

  // Save to localStorage whenever saved changes
  useEffect(() => {
    try {
      localStorage.setItem("savedSections", JSON.stringify([...saved]));
    } catch {}
  }, [saved]);

  // Profile
  const [user, setUser] = useState(null);
  const [showSettings, setShowSettings] = useState(false);

  // Toggles a day in/out of the days filter array
  function toggleDay(day) {
    setDays(prev =>
      prev.includes(day) ? prev.filter(d => d !== day) : [...prev, day]
    );
  }

  // Load dropdown options from /courses and fetch the current schedule
  useEffect(() => {
    fetch("/courses")
      .then(r => r.json())
      .then(json => {
        const raw = Array.isArray(json) ? json : (json.classes || []);
        setDepartments([...new Set(raw.map(c => c.subject))].sort());
        setProfessors([...new Set(raw.flatMap(c => c.faculty))].sort());
        setTerms([...new Set(raw.map(c => c.semester).filter(Boolean))].sort().reverse());
      });
    loadSchedule();
  }, []);

  // Search — fires 300ms after the user stops changing any filter
  // If nothing is filled in, clears results instead of searching
  useEffect(() => {
    const hasInput = codeQ || keyQ || dept || prof || credits || timeFrom || timeTo || term || days.length || isOpen;
    if (!hasInput) { setResults([]); setSearched(false); return; }

    const timer = setTimeout(async () => {
      const params = new URLSearchParams();
      if (codeQ)       params.set("code",     codeQ.trim());
      if (keyQ)        params.set("keyword",  keyQ.trim());
      if (dept)        params.set("dept",     dept);
      if (prof)        params.set("prof",     prof);
      if (credits)     params.set("credits",  credits);
      if (timeFrom)    params.set("timeFrom", timeFrom);
      if (timeTo)      params.set("timeTo",   timeTo);
      if (term)        params.set("term",     term);
      if (days.length) params.set("days",     days.join(","));
      if (isOpen)      params.set("isOpen",   "true");

      const res  = await fetch(`/search?${params}`);
      const data = await res.json();
      setResults(data);
      setSearched(true);
    }, 300);

    // Cancel the previous timer if the user types again before 300ms
    return () => clearTimeout(timer);
  }, [codeQ, keyQ, dept, prof, credits, timeFrom, timeTo, term, days, isOpen]);

  // Clears all filters and results
  function reset() {
    setCodeQ(""); setKeyQ(""); setDept(""); setProf("");
    setCredits(""); setTimeFrom(""); setTimeTo(""); setTerm("");
    setDays([]);
    setIsOpen(false);
    setResults([]); setSearched(false);
  }

  async function loadSchedule() {
    const res  = await fetch("/schedule");
    const data = await res.json();
    console.log(data);
    setSchedule(data);
  }

  async function loadSavedSchedule(scheduleName) {
    await fetch(`/schedule/load/${scheduleName}`, { method: "POST" });
    loadSchedule();
  }

  async function saveSchedule(scheduleName) {
    const res = await fetch(`/schedule/save/${scheduleName}`, { method: "POST" });
    if (res.ok) {
      setSchedMsg("Saved successfully");
    } else {
      const data = await res.json();
      setSchedMsg(data.error);
    }
  }

  async function newSchedule() {
    await fetch(`/schedule/new`, { method: "POST" });
    loadSchedule();
  }

  // Sends a POST to add a section to the schedule.
  // If the backend rejects it the existing error message is shown.
  // After a successful add we fetch the updated schedule immediately so
  // we have the real new totalCredits before deciding whether to warn.
  // If the total has crossed above 18 we store the section and open the
  // high credit modal so the student can undo if they added by mistake.
  async function addToSchedule(s) {
    const res = await fetch(scheduleUrl(s), { method: "POST" });
    if (res.ok) {
      setSchedMsg("");
      const updated = await fetch("/schedule").then(r => r.json());
      setSchedule(updated);
      if (updated.totalCredits > 18) {
        setLastAdded(s);
        setCreditWarning(true);
      }
    } else {
      const data = await res.json();
      setSchedMsg(data.error);
    }
  }

  // Removes the last added section by calling the same DELETE endpoint
  // that removeFromSchedule uses. Once the section is gone, lastAdded is
  // cleared so a stale value can never be re-used by a later modal open.
  async function undoAdd() {
    if (lastAdded) {
      await fetch(scheduleUrl(lastAdded), { method: "DELETE" });
      setLastAdded(null);
      loadSchedule();
    }
    setCreditWarning(false);
  }

  // Sends a DELETE to remove a section from the schedule.
  // Before deleting we snapshot the current credit total. After the delete
  // we fetch the updated total. If the snapshot was at or above the 12-credit
  // full-time threshold and the new total has dropped below it, we store the
  // removed section and open the low credit warning modal. This means the
  // warning only fires when the student actively crosses down through 12 —
  // building a new schedule from scratch will never trigger it.
  async function removeFromSchedule(s) {
    const creditsBefore = schedule.totalCredits;
    await fetch(scheduleUrl(s), { method: "DELETE" });
    setSchedMsg("");
    const updated = await fetch("/schedule").then(r => r.json());
    setSchedule(updated);
    if (creditsBefore >= 12 && updated.totalCredits < 12) {
      setLastRemoved(s);
      setLowCreditWarning(true);
    }
  }

  // Restores the last removed section by re-adding it via the same POST
  // endpoint that addToSchedule uses. Once the section is back in the
  // schedule, lastRemoved is cleared so a stale value can never be
  // accidentally re-used if the modal were somehow reopened later.
  async function undoRemove() {
    if (lastRemoved) {
      await fetch(scheduleUrl(lastRemoved), { method: "POST" });
      setLastRemoved(null);
      loadSchedule();
    }
    setLowCreditWarning(false);
  }

  // save a section from search results for later
  function toggleSave(s) {
    const id = `${s.course.department}${s.course.courseID}${s.sectionID}${s.term}`;
    setSaved(prev => {
      const next = new Map(prev);
      next.has(id) ? next.delete(id) : next.set(id, s);
      return next;
    });
  }

  // Set of IDs for sections currently in the schedule, used to show Add vs Remove
  // Includes term so sections from different semesters don't collide!!!!
  const scheduleIds = new Set(
    schedule.sections.map(s => `${s.course.department}${s.course.courseID}${s.sectionID}${s.term}`)
  );

  // Set of course titles currently in the schedule, used to grey out other sections of the same course
  const scheduledTitles = new Set(
    schedule.sections.map(s => s.course.title)
  );

  // NOTE THAT MUCH OF THIS STYLIZATION WAS TWEAKED BY AI, ORIGINAL FORMATTING EXISTS IN TAG
  return (

    <div>

        {!user && (
                                <LoginModal onLogin={setUser} />
                      )}

      <nav className="nav">
        <span className="nav-title">TeamGravy</span>
        <Link to="/" onClick={() => loadSchedule()}>Search</Link>
        <Link to="/calendar" onClick={() => loadSchedule()}>Calendar</Link>
      </nav>

      <ProfileMenu
        user={user}
        onLogout={() => setUser(null)}
        onOpenSettings={() => setShowSettings(true)}
      />

      {showSettings && (
        <ProfileSettingsModal
          user={user}
          onClose={() => setShowSettings(false)}
          onUpdate={setUser}
        />
      )}

      <div className="sched-bar">
        <span>Schedule:</span>
        <input value={scheduleName} onChange={e => setScheduleName(e.target.value)} />
        <button onClick={() => saveSchedule(scheduleName)}>Save</button>
        <button onClick={() => loadSavedSchedule(scheduleName)}>Load</button>
        <button onClick={() => newSchedule()}>New</button>
        <button onClick={() => window.print()}>Download PDF</button>
      </div>

      <Routes>
        <Route path="/" element={
          <div className="page">

            {/* ── Filters ── */}
            <aside className="filters">
              <label>Course Code
                <input value={codeQ} onChange={e => setCodeQ(e.target.value)} placeholder="e.g. COMP 350" />
              </label>
              <label>Keyword
                <input value={keyQ} onChange={e => setKeyQ(e.target.value)} placeholder="e.g. programming" />
              </label>
              <hr className="filter-divider" />
              <label>Department
                <select value={dept} onChange={e => setDept(e.target.value)}>
                  <option value="">All</option>
                  {departments.map(d => <option key={d} value={d}>{d}</option>)}
                </select>
              </label>
              <label>Professor
                <select value={prof} onChange={e => setProf(e.target.value)}>
                  <option value="">All</option>
                  {professors.map(p => <option key={p} value={p}>{p}</option>)}
                </select>
              </label>
              <label>Term
                <select value={term} onChange={e => setTerm(e.target.value)}>
                  <option value="">All</option>
                  {terms.map(t => <option key={t} value={t}>{t.replace("_", " ")}</option>)}
                </select>
              </label>
              <label>Credits
                <input value={credits} onChange={e => setCredits(e.target.value)} placeholder="e.g. 3" />
              </label>
              <label>From
                <input value={timeFrom} onChange={e => setTimeFrom(e.target.value)} placeholder="e.g. 8:00 AM" />
              </label>
              <label>To
                <input value={timeTo} onChange={e => setTimeTo(e.target.value)} placeholder="e.g. 5:00 PM" />
              </label>
              <hr className="filter-divider" />
              <div style={{ fontSize: "0.7rem", color: "var(--sub)", marginBottom: "2px" }}>Days</div>
              <div className="day-checks">
                {Object.entries(DAY_LABELS).map(([day, label]) => (
                  <label key={day}>
                    <input type="checkbox" checked={days.includes(day)} onChange={() => toggleDay(day)} />
                    {label}
                  </label>
                ))}
              </div>
              <hr className="filter-divider" />
              <div style={{ fontSize: "0.7rem", color: "var(--sub)", marginBottom: "2px" }}>Availability</div>
              <div className="day-checks">
                <label>
                  <input type="checkbox" checked={isOpen} onChange={e => setIsOpen(e.target.checked)} />
                  Open
                </label>
              </div>
              <button className="btn-reset" onClick={reset}>Reset</button>
            </aside>

            {/* ── Results ── */}
            <section className="results">
              <div className="results-header">
                {searched ? `${results.length} result${results.length !== 1 ? "s" : ""}` : "Search or filter to see courses"}
              </div>
              <div className="results-list">
                {results.map((s, i) => {
                  const id = `${s.course.department}${s.course.courseID}${s.sectionID}${s.term}`;
                  const inSchedule = scheduleIds.has(id);
                  const sameTitle = !inSchedule && scheduledTitles.has(s.course.title);
                  return (
                    <div key={i}
                      className={`result-item ${s.isOpen ? "" : "is-closed"} ${sameTitle ? "same-title" : ""}`}
                      title={sameTitle ? "Another section of this class is already in your schedule" : ""}
                    >
                      <div className="result-main">
                        <div className="result-code">{s.course.department} {s.course.courseID} {s.sectionID} · {s.term}</div>
                        <div className="result-name">{s.course.title}</div>
                        <div className="result-meta">{s.professor[0] ?? "TBD"} · {sectionTimeStr(s)} · {s.course.creditHours} cr · {s.isOpen ? "Open" : "Closed"}</div>
                        <button className="btn-details" onClick={() => setSelectedSection(s)}>
                            Show Details
                        </button>
                      </div>
                      {inSchedule ? (
                        <button className="btn-remove" onClick={() => removeFromSchedule(s)}>Remove</button>
                      ) : !s.isOpen ? (
                        <button className="btn-remove" onClick={() => addToSchedule(s)}>Closed</button>
                      ) : (
                        <button className="btn-add" onClick={() => addToSchedule(s)}>Add</button>
                      )}
                    </div>
                  );
                })}
              </div>
            </section>

            {/* ── Schedule ── */}
            <aside className="schedule-panel">
              <h2>My Schedule</h2>
              {schedMsg && <div className="error-msg">{schedMsg}</div>}
              <div className="schedule-items">
                {schedule.sections.map((s, i) => (
                  <div key={i} className="sched-item">
                    <div className="sched-info">
                      <div className="sched-code">{s.course.department} {s.course.courseID} {s.sectionID}</div>
                      <div className="sched-name">{s.course.title}</div>
                      <div className="sched-time">{sectionTimeStr(s)}</div>
                    </div>
                    <button className="btn-remove" onClick={() => removeFromSchedule(s)}>✕</button>
                  </div>
                ))}
              </div>
              <div className="metrics">
                <div className="metric-row"><span>Total credits</span><span>{schedule.totalCredits}</span></div>
                <div className="metric-row"><span>Days without class</span><span>{schedule.daysWithoutClass}</span></div>
                <div className="metric-row"><span>Longest break</span><span>{schedule.longestBreak} min</span></div>
              </div>

              {saved.size > 0 && (
                <>
                  <hr className="filter-divider" />
                  <h2
                    style={{ cursor: "pointer", display: "flex", justifyContent: "space-between", padding: "10px 14px" }}
                    onClick={() => setSavedOpen(prev => !prev)}
                  >
                    <span>SAVED FOR LATER ({saved.size})</span>
                    <span>{savedOpen ? "▲" : "▼"}</span>
                  </h2>
                  {savedOpen && (
                    <div className="schedule-items">
                      {[...saved.values()].map((s, i) => (
                        <div key={i} className="sched-item">
                          <div className="sched-info">
                            <div className="sched-code">{s.course.department} {s.course.courseID} {s.sectionID}</div>
                            <div className="sched-name">{s.course.title}</div>
                            <div className="sched-time" style={{ color: s.isOpen ? "var(--green)" : "var(--red)" }}>
                              {s.isOpen ? "Open" : "Closed"}
                            </div>
                          </div>
                          <button className="btn-remove" onClick={() => toggleSave(s)}>✕</button>
                        </div>
                      ))}
                    </div>
                  )}
                </>
              )}
            </aside>

          </div>
        } />

        <Route path="/Calendar" element={
          <CalendarPage
            scheduleName={scheduleName}
            saved={saved}
            toggleSave={toggleSave}
          />}
        />
      </Routes>

      {/* High credit warning modal — fires when adding a section pushes the
          student's total above the 18-credit recommended limit. Red styling
          signals the more urgent of the two credit warnings. The ?. optional
          chaining on lastAdded guards the brief render frame while the modal
          closes and lastAdded is being cleared back to null. */}
      {creditWarning && (
        <div className="modal-overlay" onClick={() => setCreditWarning(false)}>
          <div className="modal-box" onClick={e => e.stopPropagation()}>

            <div className="modal-header">
              <div className="modal-icon">
                <svg width="14" height="14" viewBox="0 0 16 16" fill="none">
                  <path d="M8 2L14.9 14H1.1L8 2Z" stroke="var(--red)" strokeWidth="1.4"/>
                  <line x1="8" y1="7" x2="8" y2="10" stroke="var(--red)" strokeWidth="1.5"/>
                  <circle cx="8" cy="12" r="0.7" fill="var(--red)"/>
                </svg>
              </div>
              <span className="modal-title">Exceeds maximum credit hour limit</span>
            </div>

            <p className="modal-body">
              Adding{" "}
              <span className="modal-course">
                {lastAdded?.course.department} {lastAdded?.course.courseID} {lastAdded?.sectionID}
              </span>{" "}
              brings your total to{" "}
              <span className="modal-credits">{schedule.totalCredits} credits</span>,
              which exceeds the 18-credit recommended limit.
            </p>

            <div className="modal-actions">
              <button className="btn-modal-keep" onClick={() => setCreditWarning(false)}>
                Keep it
              </button>
              <button className="btn-modal-undo" onClick={undoAdd}>
                Undo add
              </button>
            </div>

          </div>
        </div>
      )}

      {/* Low credit warning modal — uses amber styling instead of red to
          visually separate "dropped too low" from "went too high". Only
          fires when the student actively crosses down through 12 credits,
          so building a new schedule from scratch never triggers it. The ?.
          optional chaining on lastRemoved guards the same brief closing
          frame described above. */}
      {lowCreditWarning && (
        <div className="modal-overlay" onClick={() => setLowCreditWarning(false)}>
          <div className="modal-box" onClick={e => e.stopPropagation()}>

            <div className="modal-header">
              <div className="modal-icon modal-icon--warn">
                <svg width="14" height="14" viewBox="0 0 16 16" fill="none">
                  <path d="M8 2L14.9 14H1.1L8 2Z" stroke="var(--yellow)" strokeWidth="1.4"/>
                  <line x1="8" y1="7" x2="8" y2="10" stroke="var(--yellow)" strokeWidth="1.5"/>
                  <circle cx="8" cy="12" r="0.7" fill="var(--yellow)"/>
                </svg>
              </div>
              <span className="modal-title">Below full-time credit minimum</span>
            </div>

            <p className="modal-body">
              Removing{" "}
              <span className="modal-course">
                {lastRemoved?.course.department} {lastRemoved?.course.courseID} {lastRemoved?.sectionID}
              </span>{" "}
              brings your total to{" "}
              <span className="modal-credits">{schedule.totalCredits} credits</span>,
              which is below the 12-credit minimum required to be a full-time student.
            </p>

            <div className="modal-actions">
              <button className="btn-modal-keep" onClick={() => setLowCreditWarning(false)}>
                Keep removal
              </button>
              <button className="btn-modal-restore" onClick={undoRemove}>
                Undo remove
              </button>
            </div>

          </div>
        </div>
      )}

      {/* Show section details modal */}
      {selectedSection && (
        <div className="modal-overlay" onClick={() => setSelectedSection(null)}>
          <div className="modal-box" onClick={e => e.stopPropagation()}>
            <button className="modal-close" onClick={() => setSelectedSection(null)}>✕</button>
            <div className="modal-header">
              <span className="modal-title">
                {selectedSection.course.department} {selectedSection.course.courseID} {selectedSection.sectionID} - {selectedSection.course.title}
              </span>
            </div>

            <p>Professor: {selectedSection.professor[0] ?? "TBD"}</p>
            <p>Location: {selectedSection.location}</p>
            <p>Enrolled: {selectedSection.enrolled} / {selectedSection.capacity}</p>
            <p>Credits: {selectedSection.course.creditHours}</p>
            <p>Status: {selectedSection.isOpen ? "Open" : "Closed"}</p>
            <p>Time: {sectionTimeStr(selectedSection)}</p>
          </div>
        </div>
      )}

    </div>
  );
}