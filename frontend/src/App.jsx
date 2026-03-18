import { useState, useEffect } from "react";
import { Routes, Route, Link} from "react-router-dom";
import CalendarPage from "./CalendarPage";

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

  const [results,  setResults]  = useState([]);
  const [searched, setSearched] = useState(false);

  const [schedule, setSchedule] = useState({ sections: [], totalCredits: 0, daysWithoutClass: 5, longestBreak: 0 });
  const [schedMsg, setSchedMsg] = useState("");

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
    const hasInput = codeQ || keyQ || dept || prof || credits || timeFrom || timeTo || term;
    if (!hasInput) { setResults([]); setSearched(false); return; }

    const timer = setTimeout(async () => {
      const params = new URLSearchParams();
      if (codeQ)    params.set("code",     codeQ.trim());
      if (keyQ)     params.set("keyword",  keyQ.trim());
      if (dept)     params.set("dept",     dept);
      if (prof)     params.set("prof",     prof);
      if (credits)  params.set("credits",  credits);
      if (timeFrom) params.set("timeFrom", timeFrom);
      if (timeTo)   params.set("timeTo",   timeTo);
      if (term)     params.set("term",     term);

      const res  = await fetch(`/search?${params}`);
      const data = await res.json();
      setResults(data);
      setSearched(true);
    }, 300);

    return () => clearTimeout(timer);
  }, [codeQ, keyQ, dept, prof, credits, timeFrom, timeTo, term]);

  function reset() {
    setCodeQ(""); setKeyQ(""); setDept(""); setProf("");
    setCredits(""); setTimeFrom(""); setTimeTo(""); setTerm("");
    setResults([]); setSearched(false);
  }

  async function loadSchedule() {
    const res  = await fetch("/schedule");
    const data = await res.json();
    setSchedule(data);
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

  return (
    <div>

      <nav style={{ marginBottom: "20px" }}>
        <Link to="/">Search</Link>
        {" | "}
        <Link to="/calendar">Calendar</Link>
      </nav>

      <Routes>
        <Route
        path="/"
        element={<>
      <label>
        Course Code:{" "}
        <input value={codeQ} onChange={e => setCodeQ(e.target.value)} placeholder="e.g. COMP, ACCT101" />
      </label>
      {" "}
      <label>
        Keyword:{" "}
        <input value={keyQ} onChange={e => setKeyQ(e.target.value)} placeholder="e.g. programming" />
      </label>

      <br /><br />

      <label>
        Department:{" "}
        <select value={dept} onChange={e => setDept(e.target.value)}>
          <option value="">All</option>
          {departments.map(d => <option key={d} value={d}>{d}</option>)}
        </select>
      </label>
      {" "}
      <label>
        Professor:{" "}
        <select value={prof} onChange={e => setProf(e.target.value)}>
          <option value="">All</option>
          {professors.map(p => <option key={p} value={p}>{p}</option>)}
        </select>
      </label>
      {" "}
      <label>
        Term:{" "}
        <select value={term} onChange={e => setTerm(e.target.value)}>
          <option value="">All</option>
          {terms.map(t => <option key={t} value={t}>{t.replace("_", " ")}</option>)}
        </select>
      </label>
      {" "}
      <label>
        Credits:{" "}
        <input value={credits} onChange={e => setCredits(e.target.value)} placeholder="e.g. 3" size="3" />
      </label>
      {" "}
      <label>
        From:{" "}
        <input value={timeFrom} onChange={e => setTimeFrom(e.target.value)} placeholder="08:00" size="6" />
      </label>
      {" "}
      <label>
        To:{" "}
        <input value={timeTo} onChange={e => setTimeTo(e.target.value)} placeholder="17:00" size="6" />
      </label>

      <br /><br />

      <button onClick={reset}>Reset</button>

      {searched && <p>{results.length} result{results.length !== 1 ? "s" : ""}</p>}
      <ul>
        {results.map((s, i) => {
          const id = `${s.course.department}${s.course.courseID}${s.sectionID}${s.course.term}`;
          const inSchedule = scheduleIds.has(id);
          return (
            <li key={i}>
              <strong>{s.course.department} {s.course.courseID} §{s.sectionID}</strong>
              {" — "}{s.course.title}
              {" — "}{s.professor[0] ?? "TBA"}
              {" — "}{sectionTimeStr(s)}
              {" — "}{s.course.creditHours} cr
              {" — "}{s.course.term}
              {" — "}{s.isOpen ? "Open" : "Closed"}
              {" "}
              <button onClick={() => inSchedule ? removeFromSchedule(s) : addToSchedule(s)}>
                {inSchedule ? "Remove" : "Add"}
              </button>
            </li>
          );
        })}
      </ul>

      <hr />

      <h2>My Schedule</h2>
      {schedMsg && <p style={{ color: "red" }}>{schedMsg}</p>}
      <p>Total credits: {schedule.totalCredits}</p>
      <p>Days without class: {schedule.daysWithoutClass}</p>
      <p>Longest break: {schedule.longestBreak} min</p>
      <ul>
        {schedule.sections.map((s, i) => (
          <li key={i}>
            <strong>{s.course.department} {s.course.courseID} §{s.sectionID}</strong>
            {" — "}{s.course.title}
            {" — "}{sectionTimeStr(s)}
            {" "}
            <button onClick={() => removeFromSchedule(s)}>Remove</button>
          </li>
        ))}
      </ul>
      <hr />
        </>
        }
      />
      <Route
      path="/Calendar"
      element={<CalendarPage />}
      />
      </Routes>

    </div>
  );
}