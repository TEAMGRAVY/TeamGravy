package edu.gcc.gravy;

import com.google.gson.Gson;
import com.google.gson.stream.JsonReader;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;



public class JSONReader {

    public ArrayList<Course> allCourses = new ArrayList<>();

    public ArrayList<Section> readJSON() {
        String jsonPath = "data_wolfe.json";
        try (InputStream inputStream = Files.newInputStream(Path.of(jsonPath).toAbsolutePath());
             JsonReader reader = new JsonReader(new InputStreamReader(inputStream))
        ) {
            // Create Gson instance
            Gson gson = new Gson();

            ArrayList<Section> sections = new ArrayList<>();

            reader.beginArray();

            while (reader.hasNext()) {
                Section newSection = ((JSONSection) gson.fromJson(reader, JSONSection.class)).toSection(allCourses);
                sections.add(newSection);
            }
            reader.endArray();


            // Iterate and print the objects
            for (Section section : sections) {
                System.out.println(section.getCourse().getTitle());
                System.out.print(section.getCourse().getDepartment() + " " + section.getCourse().getCourseID());
                System.out.println(section.getSectionID());
                System.out.println();
            }

            for (Course curr : allCourses){
                System.out.println(curr.getTitle());
            }

            System.out.println(sections.size());
            System.out.println(allCourses.size());

            return sections;

        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }
}
