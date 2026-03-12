package edu.gcc.gravy;
import io.javalin.Javalin;

public class Main {
    public static void main(String[] args) {
        Javalin app = Javalin.create(config -> {
            config.staticFiles.add("public");

            config.bundledPlugins.enableCors(cors -> {
                cors.addRule(it -> {
                    it.anyHost();
                });
            });
        }).start(7000);
        CourseController.registerRoutes(app);
    }

    public static void run() {
        // Load data - initial reading of the csv or JSON file or database
        // Create Search object
        // Get all user I/O
        // Apply filters
        // Display results
    }
}