import { useState, useEffect } from "react";

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

function sectionTimeStr(section) {
  if (!section.time || section.time.length === 0) return "No schedule";
  const slot = section.time[0];
  const days = sectionDays(section).map(d => DAY_LABELS[d]).join("/");
  return `${days} ${formatTime(slot.startTime)}–${formatTime(slot.endTime)}`;
}

function scheduleUrl(s) {
  return `/schedule/${s.course.department}/${s.course.courseID}/${s.sectionID}`;
}

const DAYS = ["MONDAY", "TUESDAY", "WEDNESDAY", "THURSDAY", "FRIDAY"];

function timeToMinutes(t) {
  const [h, m] = t.split(":").map(Number);
  return h * 60 + m;
}

function minutesToLabel(m) {
  const h = Math.floor(m / 60);
  const min = m % 60;
  const ampm = h >= 12 ? "PM" : "AM";
  const h12 = h % 12 || 12;
  return `${h12}:${String(min).padStart(2,"0")} ${ampm}`;
}

export default function App() {
  const [departments, setDepartments] = useState([]);
  const [professors,  setProfessors]  = useState([]);

  const [codeQ,    setCodeQ]    = useState("");
  const [keyQ,     setKeyQ]     = useState("");
  const [dept,     setDept]     = useState("");
  const [prof,     setProf]     = useState("");
  const [credits,  setCredits]  = useState("");
  const [timeFrom, setTimeFrom] = useState("");
  const [timeTo,   setTimeTo]   = useState("");

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
      });
    loadSchedule();
  }, []);

  useEffect(() => {
    const hasInput = codeQ || keyQ || dept || prof || credits || timeFrom || timeTo;
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

      const res  = await fetch(`/search?${params}`);
      const data = await res.json();
      setResults(data);
      setSearched(true);
    }, 300);

    return () => clearTimeout(timer);
  }, [codeQ, keyQ, dept, prof, credits, timeFrom, timeTo]);

  function reset() {
    setCodeQ(""); setKeyQ(""); setDept(""); setProf("");
    setCredits(""); setTimeFrom(""); setTimeTo("");
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
    schedule.sections.map(s => `${s.course.department}${s.course.courseID}${s.sectionID}`)
  );

  const START_DAY = 8 * 60;   // 8:00 AM
  const END_DAY   = 21.5 * 60;  // 9:30 PM
  const BLOCK = 30;

  function buildGrid() {

    const grid = {};
    DAYS.forEach(d => grid[d] = {});

    if (!schedule.sections) return grid;

    schedule.sections.forEach(section => {

      if (!section.time) return;

      section.time.forEach(slot => {

        const start = timeToMinutes(slot.startTime);
        const end   = timeToMinutes(slot.endTime);

        const span = Math.ceil((end - start) / BLOCK);

        slot.days.forEach(day => {

          grid[day][start] = {
            span,
            label: `${section.course.department} ${section.course.courseID}`,
          };

          // mark rows covered by span so they aren't drawn again
          for (let t = start + BLOCK; t < end; t += BLOCK) {
            grid[day][t] = { skip: true };
          }

        });

      });

    });

  return grid;
  }

  const grid = buildGrid();

  const timeBlocks = [];
  for (let t = START_DAY; t < END_DAY; t += BLOCK) {
    timeBlocks.push(t);
  }


  return (
    <div>
      <h1>Team Gravy Course Search</h1>

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
          const id = `${s.course.department}${s.course.courseID}${s.sectionID}`;
          const inSchedule = scheduleIds.has(id);
          return (
            <li key={i}>
              <strong>{s.course.department} {s.course.courseID} §{s.sectionID}</strong>
              {" — "}{s.course.title}
              {" — "}{s.professor[0] ?? "TBA"}
              {" — "}{sectionTimeStr(s)}
              {" — "}{s.course.creditHours} cr
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

      <h2>Weekly Schedule Grid</h2>

      <table style={{
        margin: "auto",
        borderCollapse: "collapse"
      }}>

        <thead>
          <tr>
            <th style = {{
              width: "6%"
            }}></th>
            {DAYS.map(d => (
              <th key={d} style={{ width: "18.8%" }}>
                {DAY_LABELS[d]}
              </th>
            ))}
          </tr>
        </thead>

        <tbody>

          {timeBlocks.map(time => (

            <tr key={time}>

              <td
                style={{
                  verticalAlign: "top",
                  padding: 0,
                  fontSize: "12px",
                  position: "relative",
                  top: "-12px"
                }}
              >
                {minutesToLabel(time)}
              </td>

              {DAYS.map(day => {

                const cell = grid[day][time];
                if (cell?.skip) {
                  return <td key={day} style={{display:"none"}}></td>;
                }

                if (cell) {
                  if (cell.label){
                    cell.color = "#a81b1b"
                  }
                  return (
                    <td
                      key={day}
                      rowSpan={cell.span}
                      style={{
                        background: cell.color,
                        border: "1px solid #aaa",
                        padding: "6px",
                        minWidth: "90px"
                      }}
                    >
                      {cell.label}
                    </td>
                  );

                }

                return (
                  <td key={day} style={{ border: "1px solid #ddd" }}></td>
                );

              })}

            </tr>

          ))}

        </tbody>
      </table>

    </div>
  );
}