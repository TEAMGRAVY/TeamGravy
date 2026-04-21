import { useEffect, useState } from "react";
// Imports from App not used as it would break the loading of page when tried. Issue for Sprint 2.
import "./App.css";

// Page for calendar view of schedule
export default function CalendarPage() {
  
const [schedule, setSchedule] = useState({ sections: [], activities: [], totalCredits: 0, daysWithoutClass: 5, longestBreak: 0 });

const DAY_LABELS = {
  MONDAY: "Mon", TUESDAY: "Tue", WEDNESDAY: "Wed", THURSDAY: "Thu", FRIDAY: "Fri"
};

// Activity state
const [activityName, setActivityName] = useState("");
const [activityStart, setActivityStart] = useState("");
const [activityEnd, setActivityEnd] = useState("");
const [activityDays, setActivityDays] = useState([]);
const [activityMsg, setActivityMsg] = useState(""); // Error message handling for activity

function scheduleUrl(s) {
  return `/schedule/${s.course.department}/${s.course.courseID}/${s.sectionID}/${s.term}`;
}

useEffect(() => {
  loadSchedule();
}, []);

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

async function loadSchedule() {
    const res  = await fetch("/schedule");
    const data = await res.json();
    setSchedule(data);
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

async function removeFromSchedule(s) {
    await fetch(scheduleUrl(s), { method: "DELETE" });
    setSchedMsg("");
    loadSchedule();
  }

async function removeActivity(a) {
  await fetch(`/schedule/activity/${a.name}`, { method: "DELETE" });
  loadSchedule();
}

  const START_DAY = 8 * 60;   // 8:00 AM
  const END_DAY   = 21.5 * 60;  // 9:30 PM
  const BLOCK = 30; // 30 Minute Blocks

  // Builds the grid for the calendar view
  function buildGrid() {

    const grid = {};
    DAYS.forEach(d => grid[d] = {});

    if (!schedule.sections) return grid;

    schedule.sections.forEach(section => {

      if (!section.time) return;

      section.time.forEach(slot => {

        const start = timeToMinutes(slot.startTime);
        const end   = timeToMinutes(slot.endTime);

        // How many blocks does the class span?
        const span = Math.ceil((end - start) / BLOCK);

        slot.days.forEach(day => {

          grid[day][start] = {
            span,
            label: `${section.course.department} ${section.course.courseID}`,
            isActivity: false
          };

          // mark rows covered by span so they aren't drawn again
          for (let t = start + BLOCK; t < end; t += BLOCK) {
            grid[day][t] = { skip: true };
          }

        });

      });

    });

    // Loop through activities - modeled after sections and AI
    schedule.activities?.forEach(activity => {
      if (!activity.time) return;

      const slot = activity.time;
      const start = timeToMinutes(slot.startTime);
      const end   = timeToMinutes(slot.endTime);
      const span  = Math.ceil((end - start) / BLOCK);

      slot.days.forEach(day => {
        grid[day][start] = { span, label: activity.name, isActivity: true };
        for (let t = start + BLOCK; t < end; t += BLOCK) {
          grid[day][t] = { skip: true };
        }
      });
    });

  return grid;
  }

  const grid = buildGrid();

  const timeBlocks = [];
  for (let t = START_DAY; t < END_DAY; t += BLOCK) {
    timeBlocks.push(t);
  }

  async function addActivity() {
    const res = await fetch("/schedule/activity", {
      method: "POST",
      headers: {
        "Content-Type": "application/json"
      },
      body: JSON.stringify({
        name: activityName,
        startTime: activityStart,
        endTime: activityEnd,
        days: activityDays
      })
    });

    if (res.ok) {
      setActivityMsg("");
      loadSchedule();
    } else {
      const data = await res.json();
      setActivityMsg(data.error);
    }
  }

    return (
    <div className="calendar-page">
      <h1 style={{ color: "white" }}>Calendar</h1>
      <h2>Schedule</h2>
      <div className="metrics">
        <div className="metric-row"><span>Total credits</span><span>{schedule.totalCredits}</span></div>
        <div className="metric-row"><span>Days without class</span><span>{schedule.daysWithoutClass}</span></div>
        <div className="metric-row"><span>Longest break</span><span>{schedule.longestBreak} min</span></div>
      </div>

      <br/>
      <hr></hr>
      <br/>
      <div className="custom-activity">
        <h3>Add Activity</h3>

        {/* Name */}
        <input
          placeholder="Activity name"
          value={activityName}
          onChange={e => setActivityName(e.target.value)}
        />

         {/* Start Time */}
          <input
            type="time"
            value={activityStart}
            onChange={e => setActivityStart(e.target.value)}
          />

          {/* End Time */}
          <input
            type="time"
            value={activityEnd}
            onChange={e => setActivityEnd(e.target.value)}
          />

        {/* Days */}
        <div className="day-checks">
          {Object.entries(DAY_LABELS).map(([day, label]) => (
            <label key={day}>
              <input
                type="checkbox"
                checked={activityDays.includes(day)}
                onChange={() =>
                  setActivityDays(prev =>
                    prev.includes(day)
                      ? prev.filter(d => d !== day)
                      : [...prev, day]
                  )
                }
              />
              {label}
            </label>
          ))}
        </div>

        <button onClick={addActivity}>Add Activity</button>
        {activityMsg && <div style={{ color: "red" }}>{activityMsg}</div>}
      </div>
      <br/>
      <hr />
      <br/>
      <h2 style={{ color: "white" }}>Weekly Schedule Grid</h2>
      <div className="calendar-grid-wrapper">
          <table className="calendar-grid">

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

                  <td className="time-label">
                    {minutesToLabel(time)}
                  </td>

                  {DAYS.map(day => {

                    const cell = grid[day][time];
                    if (cell?.skip) {
                      return <td key={day} style={{display:"none"}}></td>;
                    }

                    if (cell) {
                      return (
                        <td
                          key={day}
                          rowSpan={cell.span}
                          className={cell.isActivity ? "activity-cell" : "class-cell"}
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
      <hr />

      <div className="calendar-schedule">
          <div className="schedule-items">
            {schedule.sections.map((s, i) => (
              <div key={i} className="sched-item">
                <div className="sched-info">
                  <div className="sched-code">{s.course.department} {s.course.courseID} §{s.sectionID}</div>
                  <div className="sched-name">{s.course.title}</div>
                  <div className="sched-time">{sectionTimeStr(s)}</div>
                </div>
                <button className="btn-remove" onClick={() => removeFromSchedule(s)}>✕</button>
              </div>
            ))}
            {schedule.activities?.map((a, i) => (
              <div key={`activity-${i}`} className="sched-item">
                <div className="sched-info">
                  <div className="sched-code">{a.name}</div>
                  <div className="sched-time">{formatTime(a.time.startTime)}–{formatTime(a.time.endTime)}</div>
                </div>
                <button className="btn-remove" onClick={() => removeActivity(a)}>✕</button>
              </div>
            ))}
          </div>
        </div>

    </div>
  );
}