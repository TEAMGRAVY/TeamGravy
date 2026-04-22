package edu.gcc.gravy;

import java.util.ArrayList;
import io.javalin.Javalin;

public class Main {
    public static ArrayList<Section> allSections;
    public static void main(String[] args) {
        run();
    }

    public static void run() {
        // Read in JSON file to store all sections.
        try {
            RateMyProfessorClient rmp = new RateMyProfessorClient();
            rmp.exportAllProfessorsToJson("professors.json");
        } catch (Exception e) {
            System.out.println("RMP fetch failed: " + e.getMessage());
        }
        allSections = (new JSONReader()).readJSON();
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
}
