import { useEffect, useState } from "react";

export default function CalendarPage() {
  
const [schedule, setSchedule] = useState({ sections: [], totalCredits: 0, daysWithoutClass: 5, longestBreak: 0 });

const DAY_LABELS = {
  MONDAY: "Mon", TUESDAY: "Tue", WEDNESDAY: "Wed", THURSDAY: "Thu", FRIDAY: "Fri"
};

useEffect(() => {
    loadSchedule();
})

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
      <h1>Calendar Page</h1>
      <h2>Schedule</h2>
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
      <hr />
    </div>
  );
}