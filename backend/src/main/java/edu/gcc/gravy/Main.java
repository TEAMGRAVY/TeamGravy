package edu.gcc.gravy;

import com.google.gson.Gson;
import com.google.gson.stream.JsonReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;

/**
 * @author: goettelsg24
 */

public class Main {

    public static ArrayList<Section> allSections;

    public static void main(String[] args) {

    }

    public static void run() {
        // Load data - initial reading of the csv or JSON file or database
        // Create Search object
        // Get all user I/O
        // Apply filters
        // Display results
    }

    public static void readJSON(){
        String jsonPath = "./data_wolfe.json";
        try (InputStream inputStream = Files.newInputStream(Path.of(jsonPath));
             JsonReader reader = new JsonReader(new InputStreamReader(inputStream))
             ) {
            // Create Gson instance
            Gson gson = new Gson();

            ArrayList<Section> sections = new ArrayList<>();

            reader.beginArray();
            while (reader.hasNext()) {
                sections.add(((JSONSection)gson.fromJson(reader, JSONSection.class)).toSection());
            }
            reader.endArray();

            // Iterate and print the objects
            for (Section section : sections) {
                System.out.println(section);
            }

            allSections = sections;

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

