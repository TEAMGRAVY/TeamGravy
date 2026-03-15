import { useState, useEffect } from "react";

export default function App() {
  const [allCourses, setAllCourses]   = useState([]);
  const [filtered, setFiltered]       = useState([]);
  const [departments, setDepartments] = useState([]);
  const [professors, setProfessors]   = useState([]);

  const [codeQ,    setCodeQ]    = useState("");
  const [keyQ,     setKeyQ]     = useState("");
  const [dept,     setDept]     = useState("");
  const [prof,     setProf]     = useState("");
  const [crMin,    setCrMin]    = useState("");
  const [crMax,    setCrMax]    = useState("");
  const [openOnly, setOpenOnly] = useState(false);

  useEffect(() => {
    fetch("/courses")
      .then(r => r.json())
      .then(json => {
        const raw = Array.isArray(json) ? json : (json.classes || []);
        setAllCourses(raw);
        setDepartments([...new Set(raw.map(c => c.subject))].sort());
        setProfessors([...new Set(raw.flatMap(c => c.faculty))].sort());
      });
  }, []);

  useEffect(() => {
    const cq  = codeQ.trim().toLowerCase();
    const kq  = keyQ.trim().toLowerCase();
    const min = parseFloat(crMin) || 0;
    const max = parseFloat(crMax) || Infinity;

    const hasInput = cq || kq || dept || prof || crMin || crMax || openOnly;
    if (!hasInput) { setFiltered([]); return; }

    setFiltered(allCourses.filter(c => {
      const code = `${c.subject}${c.number}${c.section}`.toLowerCase();
      if (cq       && !code.includes(cq))                 return false;
      if (kq       && !c.name.toLowerCase().includes(kq)) return false;
      if (dept     && c.subject !== dept)                  return false;
      if (prof     && !c.faculty.includes(prof))           return false;
      if (c.credits < min || c.credits > max)              return false;
      if (openOnly && !c.is_open)                          return false;
      return true;
    }));
  }, [allCourses, codeQ, keyQ, dept, prof, crMin, crMax, openOnly]);

  function reset() {
    setCodeQ(""); setKeyQ(""); setDept(""); setProf("");
    setCrMin(""); setCrMax(""); setOpenOnly(false);
  }

  return (
    <div>
      <h1>TeamGravy</h1>

      <label>
        Course Code:{" "}
        <input value={codeQ} onChange={e => setCodeQ(e.target.value)} placeholder="e.g. ACCT, CS101" />
      </label>
      {" "}
      <label>
        Keyword:{" "}
        <input value={keyQ} onChange={e => setKeyQ(e.target.value)} placeholder="e.g. accounting" />
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
        Credits: Min{" "}
        <input value={crMin} onChange={e => setCrMin(e.target.value)} placeholder="0" size="3" />
        {" "}Max{" "}
        <input value={crMax} onChange={e => setCrMax(e.target.value)} placeholder="6" size="3" />
      </label>
      {" "}
      <label>
        <input type="checkbox" checked={openOnly} onChange={e => setOpenOnly(e.target.checked)} />
        {" "}Open only
      </label>

      <br /><br />

      <button onClick={reset}>Reset</button>

      <p>{filtered.length} results</p>
      <ul>
        {filtered.map((c, i) => (
          <li key={i}>
            {c.subject} {c.number} {c.section} — {c.name} ({c.credits} cr) — {c.faculty[0] ?? "TBA"} — {c.is_open ? "Open" : "Closed"}
          </li>
        ))}
      </ul>
    </div>
  );
}