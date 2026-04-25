import { useEffect, useState } from "react";
// Imports from App not used as it would break the loading of page when tried. Issue for Sprint 2.
import "./App.css";

// Page for calendar view of schedule
export default function CalendarPage({ scheduleName, saved, toggleSave }) {
  
const [schedule, setSchedule] = useState({ sections: [], activities: [], totalCredits: 0, daysWithoutClass: 5, longestBreak: 0 });
const [compareMode, setCompareMode] = useState(false);
const [previewSchedule, setPreviewSchedule] = useState(null);

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

async function loadPreviewSchedule(name) {
  const res = await fetch(`/schedule/load/preview/${name}`, {
    method: "POST"
  });

  if (!res.ok) {
    console.error("Failed to load preview schedule");
    return;
  }

  const preview = await fetch("/schedule/preview").then(r => r.json());
  setPreviewSchedule(preview);
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
    loadSchedule();
  }

async function removeActivity(a) {
  await fetch(`/schedule/activity/${a.name}`, { method: "DELETE" });
  loadSchedule();
}

  const START_DAY = 8 * 60;   // 8:00 AM
  const END_DAY   = 21.5 * 60;  // 9:30 PM
  const BLOCK = 30; // 30 Minute Blocks

  //const TOTAL_MINUTES = END_DAY - START_DAY;
  //const CALENDAR_HEIGHT = window.innerHeight - 260;

  //const PX_PER_MIN = CALENDAR_HEIGHT / TOTAL_MINUTES;
  const PX_PER_MIN = 1.1;
  function buildEventsForDay(scheduleObj, day) {
    const events = [];

    // Sections
    scheduleObj.sections.forEach(section => {
      section.time?.forEach(slot => {
        if (!slot.days.includes(day)) return;

        const start = timeToMinutes(slot.startTime);
        const end   = timeToMinutes(slot.endTime);

        events.push({
          label: `${section.course.department} ${section.course.courseID}`,
          top: (start - START_DAY) * PX_PER_MIN,
          height: (end - start) * PX_PER_MIN,
          isActivity: false,
          section: section
        });
      });
    });

    // Activities
    scheduleObj.activities?.forEach(activity => {
      const slot = activity.time;
      if (!slot.days.includes(day)) return;

      const start = timeToMinutes(slot.startTime);
      const end   = timeToMinutes(slot.endTime);

      events.push({
        label: activity.name,
        top: (start - START_DAY) * PX_PER_MIN,
        height: (end - start) * PX_PER_MIN,
        isActivity: true,
        activity: activity
      });
    });

    return events;
  }

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

  function renderCalendar(scheduleObj, label) {
    return (
      <div className="calendar-instance">
        <div className="calendar-label">{label}</div>

        <div className="calendar">
          {/* Header */}
          <div className="calendar-header">
            <div className="time-col"></div>
            {DAYS.map(d => (
              <div key={d} className="day-col-header">
                {DAY_LABELS[d]}
              </div>
            ))}
          </div>

          <div className="calendar-body">
            {/* Time column */}
            <div className="time-column">
              {timeBlocks.map(t => (
                <div key={t} className="time-slot">
                  {minutesToLabel(t)}
                </div>
              ))}
            </div>

            {/* Days */}
            {DAYS.map(day => (
              <div key={day} className="day-column">

                {timeBlocks.map(t => (
                  <div key={t} className="grid-line" />
                ))}

                {buildEventsForDay(scheduleObj, day).map((event, i) => (
                  <div
                    key={i}
                    className={event.isActivity ? "event activity" : "event class"}
                    style={{ top: event.top, height: event.height }}
                  >
                    {event.label}
                  </div>
                ))}

              </div>
            ))}
          </div>
        </div>
      </div>
    );
  }

    return (
    <div className="calendar-page">
      <div className="print-header">
        <h2>{scheduleName ?? "My Schedule"}</h2>
        <p>Total Credits: {schedule.totalCredits} · Days Without Class: {schedule.daysWithoutClass} · Longest Break: {schedule.longestBreak} min</p>
      </div>
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
      <div className="compare-bar">
        <button onClick={() => setCompareMode(prev => !prev)}>
          {compareMode ? "Disable Compare" : "Compare Schedules"}
        </button>

        {compareMode && (
          <input
            placeholder="Schedule name..."
            onKeyDown={(e) => {
              if (e.key === "Enter") {
                loadPreviewSchedule(e.target.value);
              }
            }}
          />
        )}
      </div>

      <h2 style={{ color: "white" }}>Weekly Schedule Grid</h2>
      <div className={`calendar-wrapper ${compareMode ? "compare" : ""}`}>
        <div className="calendar-scroll">

          {renderCalendar(schedule, "Primary")}

          {compareMode && previewSchedule && (
            renderCalendar(previewSchedule, "Preview")
          )}
        </div>
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

        <br></br>
        <hr style={{ borderColor: "var(--border)" }} />
        {saved && saved.size > 0 && (
          <div className="calendar-schedule" style={{ marginTop: "10px" }}>
            <h2 style={{ marginTop: "10px" }}>Saved for Later ({saved?.size ?? 0})</h2>
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
          </div>
        )}

    </div>
  );
}