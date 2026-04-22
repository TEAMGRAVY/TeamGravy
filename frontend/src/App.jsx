import { useState, useEffect } from "react";
import { Routes, Route, Link} from "react-router-dom";
import CalendarPage from "./CalendarPage";
import "./App.css";

const DAY_LABELS = {
  MONDAY: "Mon", TUESDAY: "Tue", WEDNESDAY: "Wed", THURSDAY: "Thu", FRIDAY: "Fri"
};

function formatTime(t) {
  if (!t) return "";
  const [h, m] = t.split(":").map(Number);
  const ampm = h >= 12 ? "PM" : "AM";
  return `${h % 12 || 12}:${String(m).padStart(2, "0")} ${ampm}`;
}

function sectionDays(section) {
  const allDays = section.time.flatMap(slot => slot.days);
  const unique = [...new Set(allDays)];
  const order = ["MONDAY", "TUESDAY", "WEDNESDAY", "THURSDAY", "FRIDAY"];
  return unique.sort((a, b) => order.indexOf(a) - order.indexOf(b));
}

export function sectionTimeStr(section) {
  if (!section.time || section.time.length === 0) return "No schedule";
  const slot = section.time[0];
  const days = sectionDays(section).map(d => DAY_LABELS[d]).join("/");
  return `${days} ${formatTime(slot.startTime)}–${formatTime(slot.endTime)}`;
}

function scheduleUrl(s) {
  return `/schedule/${s.course.department}/${s.course.courseID}/${s.sectionID}/${s.course.term}`;
}

export default function App() {
  const [departments, setDepartments] = useState([]);
  const [professors,  setProfessors]  = useState([]);
  const [terms,       setTerms]       = useState([]);

  const [codeQ,    setCodeQ]    = useState("");
  const [keyQ,     setKeyQ]     = useState("");
  const [dept,     setDept]     = useState("");
  const [prof,     setProf]     = useState("");
  const [credits,  setCredits]  = useState("");
  const [timeFrom, setTimeFrom] = useState("");
  const [timeTo,   setTimeTo]   = useState("");
  const [term,     setTerm]     = useState("");
  const [days,     setDays]     = useState([]);

  const [results,  setResults]  = useState([]);
  const [searched, setSearched] = useState(false);

  const [schedule, setSchedule] = useState({ sections: [], totalCredits: 0, daysWithoutClass: 5, longestBreak: 0 });
  const [schedMsg, setSchedMsg] = useState("");
  const [scheduleName, setScheduleName] = useState("My Schedule");

  function toggleDay(day) {
    setDays(prev =>
      prev.includes(day) ? prev.filter(d => d !== day) : [...prev, day]
    );
  }

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

  useEffect(() => {
    const hasInput = codeQ || keyQ || dept || prof || credits || timeFrom || timeTo || term || days.length;
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

      const res  = await fetch(`/search?${params}`);
      const data = await res.json();
      setResults(data);
      setSearched(true);
    }, 300);

    return () => clearTimeout(timer);
  }, [codeQ, keyQ, dept, prof, credits, timeFrom, timeTo, term, days]);

  function reset() {
    setCodeQ(""); setKeyQ(""); setDept(""); setProf("");
    setCredits(""); setTimeFrom(""); setTimeTo(""); setTerm("");
    setDays([]);
    setResults([]); setSearched(false);
  }

  async function loadSchedule() {
    const res  = await fetch("/schedule");
    const data = await res.json();
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

  async function addToSchedule(s) {
    const res = await fetch(scheduleUrl(s), { method: "POST" });
    if (res.ok) {
      setSchedMsg("");
      loadSchedule();
    } else {
      const data = await res.json();
      setSchedMsg(data.error);
    }
  }

  async function removeFromSchedule(s) {
    await fetch(scheduleUrl(s), { method: "DELETE" });
    setSchedMsg("");
    loadSchedule();
  }

  const scheduleIds = new Set(
    schedule.sections.map(s => `${s.course.department}${s.course.courseID}${s.sectionID}${s.course.term}`)
  );

  const scheduledTitles = new Set(
    schedule.sections.map(s => s.course.title)
  );

  return (
    <div>
      <nav className="nav">
        <span className="nav-title">TeamGravy</span>
        <Link to="/" onClick={() => loadSchedule()}>Search</Link>
        <Link to="/calendar" onClick={() => loadSchedule()}>Calendar</Link>
      </nav>

      <div className="sched-bar">
        <span>Schedule:</span>
        <input value={scheduleName} onChange={e => setScheduleName(e.target.value)} />
        <button onClick={() => saveSchedule(scheduleName)}>Save</button>
        <button onClick={() => loadSavedSchedule(scheduleName)}>Load</button>
        <button onClick={() => newSchedule()}>New</button>
      </div>

      <Routes>
        <Route path="/" element={
          <div className="page">

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

              <button className="btn-reset" onClick={reset}>Reset</button>
            </aside>

            <section className="results">
              <div className="results-header">
                {searched ? `${results.length} result${results.length !== 1 ? "s" : ""}` : "Search or filter to see courses"}
              </div>

              <div className="results-list">
                {results.map((s, i) => {
                  const id = `${s.course.department}${s.course.courseID}${s.sectionID}${s.course.term}`;
                  const inSchedule = scheduleIds.has(id);
                  const sameTitle = scheduledTitles.has(s.course.title);

                  return (
                    <div
                      key={i}
                      className={`result-item ${s.isOpen ? "" : "is-closed"} ${sameTitle ? "same-title" : ""}`}
                      title={sameTitle ? "Another section of this class is already in schedule" : ""}
                    >
                      <div className="result-main">
                        <div className="result-code">{s.course.department} {s.course.courseID} {s.sectionID} · {s.course.term}</div>
                        <div className="result-name">{s.course.title}</div>
                        <div className="result-meta">{s.professor[0] ?? "TBA"} · {sectionTimeStr(s)} · {s.course.creditHours} cr · {s.isOpen ? "Open" : "Closed"}</div>
                      </div>

                      {inSchedule ? (
                        <button className="btn-remove" onClick={() => removeFromSchedule(s)}>
                          Remove
                        </button>
                      ) : !s.isOpen ? (
                        <button className="btn-remove" onClick={() => addToSchedule(s)}>
                          Closed
                        </button>
                      ) : (
                        <button className="btn-add" onClick={() => addToSchedule(s)}>
                          Add
                        </button>
                      )}
                    </div>
                  );
                })}
              </div>
            </section>

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
            </aside>

          </div>
        } />

        <Route path="/Calendar" element={<CalendarPage />} />
      </Routes>
    </div>
  );
}