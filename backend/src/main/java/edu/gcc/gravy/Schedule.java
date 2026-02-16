package edu.gcc.gravy;

import java.util.ArrayList;
import java.util.List;

public class Schedule {
    private String name;
    private List<Section> sections;
    private List<Activity> activities;
    private String term;
    private String errorMessage;
    private boolean[][] calendar = new boolean[5][26]; // Used for quick checks of time conflicts & updating UI checkboxes

    public Schedule(String name, String term) {
        this.name = name;
        this.term = term;
        sections = new ArrayList<>();
        activities = new ArrayList<>();
    }

    public boolean addSection(Section section) {

        boolean timeConflict = false;
        boolean sectionFull = false;
        for (Section other : sections) { // Scan for either error
            if (!timeConflict) {
                timeConflict = section.hasTimeConflict(other);
            }
            if (!sectionFull) {
                sectionFull = section.isFull();
            }
        }

        if (timeConflict || sectionFull) { // Error occured
            getErrorMessage(timeConflict, sectionFull); // Change later based on GUI
            return false;
        }

        sections.add(section);
        updateCalendar(section.getTime());
        return true;
    }

    public boolean removeSection(Section section) {
        return false;
    }

    public boolean updateCalendar(TimeSlot time) {

    }

    public boolean addActivity(Activity activity) {
        return false;
    }

    public boolean removeActivity(Activity activity) {
        return false;
    }

    public int getTotalCredits() {
        return -1;
    }

    public int getDaysWithoutClass() {
        return -1;
    }

    public int getLongestBreak() {
        return -1;
    }

    public String getScheduleName() {
        return name;
    }

    public List<Section> getScheduleSections() {
        return sections;
    }

    public List<Activity> getScheduleActivities() {
        return activities;
    }

    public String getScheduleTerm() {
        return term;
    }

    public String getErrorMessage(boolean timeConflict, boolean sectionFull) {
        return errorMessage;
    }

}
