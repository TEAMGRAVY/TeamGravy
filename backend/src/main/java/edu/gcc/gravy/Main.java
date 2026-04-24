package edu.gcc.gravy;

import java.util.ArrayList;
import io.javalin.Javalin;
import okhttp3.OkHttpClient;

public class Main {
    public static ArrayList<Section> allSections;
    private static final String BASE_URL = "https://qenvprrucbmyklugcqal.supabase.co";
    private static final String API_KEY = "sb_publishable_jgJshvRezu6kzrxtPQ2mGA_fzUh6Ln5";
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
        SupabaseService supabase = new SupabaseService(
            new OkHttpClient(),
            BASE_URL,
            API_KEY
        );
        AuthMiddleware auth = new AuthMiddleware();
        CourseController.registerRoutes(app, supabase, auth);
    }
}
