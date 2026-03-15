package edu.gcc.gravy;

import io.javalin.Javalin;
import java.time.LocalTime;
import java.util.List;
import java.util.Map;
import java.nio.file.Files;
import java.nio.file.Path;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializer;

public class CourseController {

    private static final Schedule schedule = new Schedule(null, "My Schedule", "2026_Spring");
    private static final Gson gson = new GsonBuilder()
            .registerTypeAdapter(LocalTime.class, (JsonSerializer<LocalTime>)
                    (src, type, context) -> new JsonPrimitive(src.toString()))
            .create();

    public static void registerRoutes(Javalin app) {

        app.get("/health", ctx -> ctx.json(Map.of("status", "ok")));

        app.get("/courses", ctx -> {
            String json = Files.readString(Path.of("data_wolfe.json").toAbsolutePath());
            ctx.contentType("application/json");
            ctx.result(json);
        });

        //   code, keyword, dept, prof, credits, timeFrom, timeTo
        app.get("/search", ctx -> {
            String code     = ctx.queryParam("code");
            String keyword  = ctx.queryParam("keyword");
            String dept     = ctx.queryParam("dept");
            String prof     = ctx.queryParam("prof");
            String credits  = ctx.queryParam("credits");
            String timeFrom = ctx.queryParam("timeFrom");
            String timeTo   = ctx.queryParam("timeTo");

            Search search = new Search(code, keyword);
            search.setAllSections(Main.allSections);

            if (dept != null && !dept.isBlank())
                search.addFilter(new DepartmentFilter(dept));

            if (prof != null && !prof.isBlank())
                search.addFilter(new ProfessorFilter(prof));

            if (credits != null && !credits.isBlank())
                search.addFilter(new CreditHourFilter(Integer.parseInt(credits)));

            if ((timeFrom != null && !timeFrom.isBlank()) || (timeTo != null && !timeTo.isBlank())) {
                LocalTime from = (timeFrom != null && !timeFrom.isBlank()) ? LocalTime.parse(timeFrom) : null;
                LocalTime to   = (timeTo   != null && !timeTo.isBlank())   ? LocalTime.parse(timeTo)   : null;
                search.addFilter(new TimeRangeFilter(from, to, null));
            }

            ctx.contentType("application/json");
            ctx.result(gson.toJson(search.getResults()));
        });

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

        // POST /schedule/{index} — add a section by its index in Main.allSections
        app.post("/schedule/{index}", ctx -> {
            int index = Integer.parseInt(ctx.pathParam("index"));

            if (index < 0 || index >= Main.allSections.size()) {
                ctx.status(404).json(Map.of("error", "Section not found"));
                return;
            }

            Section section = Main.allSections.get(index);
            boolean added = schedule.addSection(section);

            if (added) {
                ctx.status(201).json(Map.of("success", true));
            } else {
                ctx.status(409).json(Map.of("error", schedule.getErrorMessage()));
            }
        });

        // DELETE /schedule/{index} — remove a section by its index in Main.allSections
        app.delete("/schedule/{index}", ctx -> {
            int index = Integer.parseInt(ctx.pathParam("index"));

            if (index < 0 || index >= Main.allSections.size()) {
                ctx.status(404).json(Map.of("error", "Section not found"));
                return;
            }

            Section section = Main.allSections.get(index);
            boolean removed = schedule.removeSection(section);

            if (removed) {
                ctx.status(204);
            } else {
                ctx.status(404).json(Map.of("error", "Section not in schedule"));
            }
        });
    }
}