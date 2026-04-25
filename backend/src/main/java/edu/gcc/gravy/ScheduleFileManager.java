package edu.gcc.gravy;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import java.time.LocalTime;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

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
        public List<ShortenedActivity> activities;

        public ScheduleFileFormat(Schedule schedule){
            name = schedule.getScheduleName();
            term = schedule.getScheduleTerm();

            sections = new ArrayList<>();
            for (Section section : schedule.getScheduleSections()){
                sections.add(new ShortenedSection(section));
            }

            activities = new ArrayList<>();
            for (Activity activity : schedule.getScheduleActivities()) {
                activities.add(new ShortenedActivity(activity));
            }
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

            if (activities != null) {
                for (ShortenedActivity activity : activities) {
                    schedule.addActivity(activity.toActivity());
                }
            }

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
                this.term = section.getTerm();
                this.dept = section.getCourse().getDepartment();
                this.courseId = section.getCourse().getCourseID();
                this.sectionID = section.getSectionID();
            }

            // Finds the Section from allSections to reference pre-existing instances.
            public Section toSection(ArrayList<Section> allSections){
                for (Section section : allSections){
                    if (
                        section.getCourse().getTitle().equals(this.name) &&
                        section.getTerm().equals(this.term) &&
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

        public static class ShortenedActivity {
            public String name;
            public String startTime;
            public String endTime;
            public List<String> days;

            public ShortenedActivity(Activity activity) {
                this.name = activity.getName();
                this.startTime = activity.getTime().getStartTime().toString();
                this.endTime = activity.getTime().getEndTime().toString();
                this.days = activity.getTime().getDays().stream()
                        .map(Day::name)
                        .collect(Collectors.toList());
            }

            public Activity toActivity() {
                LocalTime start = LocalTime.parse(startTime);
                LocalTime end = LocalTime.parse(endTime);
                Set<Day> daySet = days.stream().map(Day::valueOf).collect(Collectors.toSet());
                return new Activity(name, new TimeSlot(start, end, daySet));
            }
        }

    }

}
