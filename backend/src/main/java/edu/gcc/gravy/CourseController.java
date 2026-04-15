package edu.gcc.gravy;

import io.javalin.Javalin;
import java.time.LocalTime;
import java.util.stream.Collectors;
import java.util.Arrays;
import java.util.Set;
import java.util.List;
import java.util.Map;
import java.nio.file.Files;
import java.nio.file.Path;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializer;
import java.time.format.DateTimeFormatter;

public class CourseController {

    // The active schedule for the current session
    private static Schedule schedule = new Schedule(null, "My Schedule", "2026_Spring");
    private static final Gson gson = new GsonBuilder()
            .registerTypeAdapter(LocalTime.class, (JsonSerializer<LocalTime>)
                    (src, type, context) -> new JsonPrimitive(src.toString()))
            .create();

    // Find a section in Main.allSections by dept + courseID + sectionID
    private static Section findSection(String dept, String courseID, String sectionID, String term) {
        for (Section s : Main.allSections) {
            if (s.getCourse().getDepartment().equals(dept)
                    && String.valueOf(s.getCourse().getCourseID()).equals(courseID)
                    && String.valueOf(s.getSectionID()).equals(sectionID)
                    && s.getCourse().getTerm().equals(term)) {
                return s;
            }
        }
        return null;
    }

    private static Schedule loadSavedSchedule(String scheduleName){
        ScheduleFileManager manager = ScheduleFileManager.getInstance();
        return manager.LoadSchedule(scheduleName, null, Main.allSections);
    }

    private static boolean saveSchedule(String scheduleName){
        ScheduleFileManager manager = ScheduleFileManager.getInstance();
        return manager.SaveSchedule(scheduleName, schedule);
    }

    public static void registerRoutes(Javalin app) {

        app.get("/health", ctx -> ctx.json(Map.of("status", "ok")));

        // Returns the raw JSON from data_wolfe.json
        // Used by the frontend to populate filter dropdowns
        app.get("/courses", ctx -> {
            String json = Files.readString(Path.of("data_wolfe.json").toAbsolutePath());
            ctx.contentType("application/json");
            ctx.result(json);
        });

        // Main search endpoint, all filtering happens server-side
        //   code, keyword, dept, prof, credits, timeFrom, timeTo, term, isOpen
        app.get("/search", ctx -> {
            String code     = ctx.queryParam("code");
            String keyword  = ctx.queryParam("keyword");
            String dept     = ctx.queryParam("dept");
            String prof     = ctx.queryParam("prof");
            String credits  = ctx.queryParam("credits");
            String timeFrom = ctx.queryParam("timeFrom");
            String timeTo   = ctx.queryParam("timeTo");
            String term = ctx.queryParam("term");
            String isOpenParam = ctx.queryParam("isOpen");

            // Start with a base search on code + keyword, then layer on filters
            Search search = new Search(code, keyword);
            search.setAllSections(Main.allSections);

            if (isOpenParam != null && isOpenParam.equals("true"))
                search.addFilter(new OpenFilter(true));

            if (dept != null && !dept.isBlank())
                search.addFilter(new DepartmentFilter(dept));

            if (prof != null && !prof.isBlank())
                search.addFilter(new ProfessorFilter(prof));

            if (credits != null && !credits.isBlank())
                search.addFilter(new CreditHourFilter(Integer.parseInt(credits)));

            // Converted to the Day enum set expected by TimeRangeFilter
            String daysParam = ctx.queryParam("days");
            Set<Day> daySet = null;
            if (daysParam != null && !daysParam.isBlank()) {
                daySet = Arrays.stream(daysParam.split(","))
                        .map(Day::valueOf)
                        .collect(Collectors.toSet());
            }

            if ((timeFrom != null && !timeFrom.isBlank())
                    || (timeTo != null && !timeTo.isBlank())
                    || daySet != null) {
                LocalTime from = (timeFrom != null && !timeFrom.isBlank()) ? LocalTime.parse(timeFrom, DateTimeFormatter.ofPattern("h:mm a")) : null;
                LocalTime to   = (timeTo   != null && !timeTo.isBlank())   ? LocalTime.parse(timeTo, DateTimeFormatter.ofPattern("h:mm a"))   : null;
                search.addFilter(new TimeRangeFilter(from, to, daySet));
            }

            if (term != null && !term.isBlank())
                search.addFilter(new TermFilter(term));

            ctx.contentType("application/json");
            ctx.result(gson.toJson(search.getResults()));
        });

        // Returns the current schedule
        // GET /schedule — return current sections + metrics
        app.get("/schedule", ctx -> {
            ctx.contentType("application/json");
            ctx.result(gson.toJson(Map.of(
                    "sections",         schedule.getScheduleSections(),
                    "totalCredits",     schedule.getTotalCredits(),
                    "daysWithoutClass", schedule.getDaysWithoutClass(),
                    "longestBreak",     schedule.getLongestBreak()
            )));
        });

        // Adds a section to the schedule
        // POST /schedule/{dept}/{courseID}/{sectionID} — add a section by identity
        // Returns 409 with an error message if the add is rejected
        app.post("/schedule/{dept}/{courseID}/{sectionID}/{term}", ctx -> {
            Section section = findSection(
                    ctx.pathParam("dept"),
                    ctx.pathParam("courseID"),
                    ctx.pathParam("sectionID"),
                    ctx.pathParam("term")
            );

            if (section == null) {
                ctx.status(404).json(Map.of("error", "Section not found"));
                return;
            }

            boolean added = schedule.addSection(section);
            if (added) {
                ctx.status(201).json(Map.of("success", true));
            } else {
                ctx.status(409).json(Map.of("error", schedule.getErrorMessage()));
            }
        });

        // Removes a section from the schedule
        // DELETE /schedule/{dept}/{courseID}/{sectionID} — remove a section by identity
        app.delete("/schedule/{dept}/{courseID}/{sectionID}/{term}", ctx -> {
            Section section = findSection(
                    ctx.pathParam("dept"),
                    ctx.pathParam("courseID"),
                    ctx.pathParam("sectionID"),
                    ctx.pathParam("term")
            );

            if (section == null) {
                ctx.status(404).json(Map.of("error", "Section not found"));
                return;
            }

            boolean removed = schedule.removeSection(section);
            if (removed) {
                ctx.status(204);
            } else {
                ctx.status(404).json(Map.of("error", "Section not in schedule"));
            }
        });

        // POST /schedule/load/{scheduleName} - Load a previously saved schedule.
        app.post("/schedule/load/{scheduleName}", ctx -> {
            Schedule tempSchedule = loadSavedSchedule(
                    ctx.pathParam("scheduleName")
            );
            if (tempSchedule == null){
                ctx.status(404).json(Map.of("error", "Schedule not found"));
                return;
            }
            schedule = tempSchedule;
            ctx.status(204).json(Map.of("success", true));
        });

        // POST /schedule/save/{scheduleName} - Save the schedule.
        app.post("/schedule/save/{scheduleName}", ctx -> {
            boolean saveSuccess = saveSchedule(
                    ctx.pathParam("scheduleName")
            );
            if (!saveSuccess){
                ctx.status(404).json(Map.of("error", "Schedule not saved"));
                return;
            }
            ctx.status(204).json(Map.of("success", true));
        });

        // POST /schedule/new - Creates a new empty schedule object.
        app.post("/schedule/new", ctx -> {
            schedule = new Schedule(null,
                    "New Schedule",
                    "2026_Spring");
        });
    }
}