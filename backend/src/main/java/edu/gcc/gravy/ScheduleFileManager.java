package edu.gcc.gravy;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Manages saved schedule files in place of a database.
 */
public class ScheduleFileManager {

    private static ScheduleFileManager manager = null;

    private ScheduleFileManager(){}

    public static ScheduleFileManager getInstance(){
        if (manager == null){
            manager = new ScheduleFileManager();
        }
        return manager;
    }

    // Saves the schedule object by converting it to a smaller format
    // and then writing it to a json file
    public boolean SaveSchedule(String fileName, Schedule object){
        Gson gson = new GsonBuilder()
                .setPrettyPrinting()
                .create();
        try (FileWriter writer = new FileWriter(fileName)) {
            gson.toJson(new ScheduleFileFormat(object), writer);
            return true;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }

    public Schedule LoadSchedule(String fileName, Student student, ArrayList<Section> allSections){
        Gson gson = new Gson();
        try (FileReader reader = new FileReader(fileName)){
            return gson.fromJson(reader, ScheduleFileFormat.class)
                    .toSchedule(student, allSections);
        } catch (IOException e){
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Format used to save schedules to json files.
     */
    public static class ScheduleFileFormat{
        public String name;
        public String term;
        public List<ShortenedSection> sections;
        //public List<Activity> activities;

        public ScheduleFileFormat(Schedule schedule){
            name = schedule.getScheduleName();
            term = schedule.getScheduleTerm();
            sections = new ArrayList<>();
            for (Section section : schedule.getScheduleSections()){
                sections.add(new ShortenedSection(section));
            }
            //activities = schedule.getScheduleActivities();
        }

        /**
         * Builds the schedule object from the saved sections in the formatting.
         * @param student - student object for future features.
         * @param allSections - ArrayList that contains all pre-existing sections
         * @return A schedule object with all saved sections.
         */
        public Schedule toSchedule(Student student, ArrayList<Section> allSections){
            Schedule schedule = new Schedule(student, this.name, this.term);
            for (ShortenedSection section : sections){
                schedule.addSection(section.toSection(allSections));
            }
//            for (Activity activity : activities){
//                schedule.addActivity(activity);
//            }
            return schedule;
        }

        /**
         * Limited section information into the most basic details to store in compact json files.
         */
        public static class ShortenedSection{
            public String name;
            public String term;
            public String dept;
            public int courseId;
            public char sectionID;

            public ShortenedSection(Section section){
                this.name = section.getCourse().getTitle();
                this.term = section.getCourse().getTerm();
                this.dept = section.getCourse().getDepartment();
                this.courseId = section.getCourse().getCourseID();
                this.sectionID = section.getSectionID();
            }

            // Finds the Section from allSections to reference pre-existing instances.
            public Section toSection(ArrayList<Section> allSections){
                for (Section section : allSections){
                    if (
                        section.getCourse().getTitle().equals(this.name) &&
                        section.getCourse().getTerm().equals(this.term) &&
                        section.getCourse().getDepartment().equals(this.dept) &&
                        section.getCourse().getCourseID() == this.courseId &&
                        section.getSectionID() == this.sectionID
                    ){
                        return section;
                    }
                }
                return null;
            }


        }

    }

}
