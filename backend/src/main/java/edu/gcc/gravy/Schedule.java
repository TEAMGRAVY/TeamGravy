package edu.gcc.gravy;

import java.util.ArrayList;
import java.util.List;

public class Schedule {
    private String name;
    private List<Section> sections;
    private List<Activity> activities;
    private String term;
    private String errorMessage;
    private boolean[][] calendar = new boolean[5][26];

    public Schedule(String name, String term) {
        this.name = name;
        this.term = term;
        sections = new ArrayList<>();
        activities = new ArrayList<>();
    }

    public boolean addSection(Section section) {
        return false;
    }

    public boolean removeSection(Section section) {
        return false;
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

    public boolean hasConflict(Section other) {
        return false;
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

    public String getErrorMessage() {
        return errorMessage;
    }

}
