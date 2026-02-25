// mock courses for frontend testing
// replace with fetch() json and pass through mapCourse() later

// const rawData = await fetch("http://backend/json").then(r => r.json());
// const MOCK_COURSES = rawData.map(mapCourse);
const RAW_COURSES = [
    {
        "credits": 3,
        "faculty": ["Graybill, Keith B."],
        "is_lab": false,
        "is_open": true,
        "location": "SHAL 316",
        "name": "PRINCIPLES OF ACCOUNTING I",
        "number": 201,
        "open_seats": 1,
        "section": "A",
        "semester": "2023_Fall",
        "subject": "ACCT",
        "times": [
          { "day": "T", "end_time": "16:45:00", "start_time": "15:30:00" },
          { "day": "R", "end_time": "16:45:00", "start_time": "15:30:00" }
        ],
        "total_seats": 30
    }
];

// when backend ready, keep this function
// saw RAW_COURSES for fetch()
function mapCourse(raw) {
  return {
    id:         `${raw.subject}${raw.number}-${raw.section}`,
    code:       `${raw.subject}${raw.number}`,
    name:       raw.name,
    professor:  raw.faculty.length > 0 ? raw.faculty[0] : "TBA",
    department: raw.subject,
    credits:    raw.credits,
    section:    raw.section,
    location:   raw.location,
    isLab:      raw.is_lab,
    isOpen:     raw.is_open,
    semester:   raw.semester,
    days:       raw.times.map(t => t.day),
    startTime:  raw.times[0].start_time.slice(0, 5),
    endTime:    raw.times[0].end_time.slice(0, 5),
    enrolled:   raw.total_seats - raw.open_seats,
    capacity:   raw.total_seats,
  };
}

// used by App.js
const MOCK_COURSES = RAW_COURSES.map(mapCourse);
const DEPARTMENTS = [...new Set(MOCK_COURSES.map(c => c.department))];
const DAY_ORDER = ["M", "T", "W", "R", "F"];
const DAY_LABELS   = { M: "Mon", T: "Tue", W: "Wed", R: "Thu", F: "Fri" };
const COLORS       = ["#4f7cac","#c05640","#3a9e6e","#8e6bbf","#c07830","#4498a8","#b05878","#5a7e40","#8f6040","#5060b0"];