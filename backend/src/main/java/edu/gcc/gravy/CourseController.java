package edu.gcc.gravy;

import io.javalin.Javalin;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Map;

public class CourseController {

    public static void registerRoutes(Javalin app) {

        app.get("/health", ctx -> ctx.json(Map.of("status", "ok")));

        app.get("/courses", ctx -> {
            String json = Files.readString(Paths.get("data_wolfe.json"));
            ctx.contentType("application/json");
            ctx.result(json);
        });
    }
}