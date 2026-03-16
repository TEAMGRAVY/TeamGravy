package edu.gcc.gravy;

import java.util.ArrayList;
import java.util.List;
import java.io.*;

public class Schedule implements Serializable{
    private Student student;
    private String name;
    private List<Section> sections;
    private List<Activity> activities;
    private String term;
    private String errorMessage;
    // [1/2 hour slot][weekday] - 8:00am - 9:30pm
    private boolean[][] calendar = new boolean[27][5]; // Used for quick checks of time conflicts & updating UI checkboxes

    public Schedule(Student student, String name, String term) {
        this.student = student;
        this.name = name;
        this.term = term;
        sections = new ArrayList<>();
        activities = new ArrayList<>();
    }

    public boolean addSection(Section section) { // Implement prereq/coreq error as additional requirements later - Uses student.getCompletedCourses() & section.getCourse().getPreReqs()/getCoReqs()
        errorMessage = null;

        if (!section.isOpen()) {
            errorMessage = "Section " + section.getCourseCode() + " is not open.";
            return false;
        }

        if (section.isFull()) {
            errorMessage = "Section " + section.getCourseCode() + " is full.";
            return false;
        }

        for (Section other : sections) { // Scan for time conflict error
            if (section.hasTimeConflict(other)) {
                errorMessage = "Section " + section.getCourseCode() + " conflicts with section " +  other.getCourseCode();
                return false;
            }
        }

        for (Activity other: activities) { // Scan for conflict with activity
            if (section.hasTimeConflict(other)) {
                errorMessage = "Section " + section.getCourseCode() + " conflicts with activity " + other.getName();
                return false;
            }
        }

        if(addCalendar(section.getTime())) {
            sections.add(section);
            return true;
        }
        errorMessage = "Internal calendar conflict.";
        return false;
    }

    public boolean removeSection(Section section) {
        if (sections.remove(section)) {
            for (TimeSlot slot : section.getTime()) {
                removeCalendar(slot);
            }
            return true;
        }
        return false;
    }

    public boolean addActivity(Activity activity) {
        errorMessage = null;

        for (Section other : sections) { // Scan for either error
            if (other.hasTimeConflict(activity)) {
                errorMessage = "Activity " + activity.getName() + " conflicts with section " + other.getCourseCode();
                return false;
            }
        }

        for (Activity other: activities) { // Scan for conflict with activity
            if (activity.hasTimeConflict(other)) {
                errorMessage = "Activity " + activity.getName() + " conflicts with activity " + other.getName();
                return false;
            }
        }

        if (addCalendar(activity.getTime())) {
            activities.add(activity);
            return true;
        }
        errorMessage = "Internal calendar conflict.";
        return false;
    }

    public boolean removeActivity(Activity activity) {
        if (activities.remove(activity)) {
            removeCalendar(activity.getTime());
            return true;
        }
        return false;
    }

    public boolean addCalendar(ArrayList<TimeSlot> time) { // Section - multiple TimeSlots
        for (TimeSlot slot : time) {
            boolean[] rows = slot.getSlotNumbers(); // 27
            boolean[] cols = slot.getDayNumbers(); // 5

            for (int r = 0; r < 27; r++) {
                for (int c = 0; c < 5; c++) {
                    if(rows[r] && cols[c] && calendar[r][c]) {
                        return false;
                    }
                }
            }
        }

        for (TimeSlot slot : time) { // Apply changes
            boolean[] rows = slot.getSlotNumbers(); // 27
            boolean[] cols = slot.getDayNumbers(); // 5

            for (int r = 0; r < 27; r++) {
                for (int c = 0; c < 5; c++) {
                    if(rows[r] && cols[c]) {
                        calendar[r][c] = true;
                    }
                }
            }
        }

        return true;
    }

    public boolean addCalendar(TimeSlot time) { // Activity
        boolean[] rows = time.getSlotNumbers(); // 27
        boolean[] cols = time.getDayNumbers(); // 5

        for (int r = 0; r < 27; r++) {
            for (int c = 0; c < 5; c++) {
                if(rows[r] && cols[c]) {
                    if (calendar[r][c]) return false;
                    calendar[r][c] = true;
                }
            }
        }
        return true;
    }

    public void removeCalendar(TimeSlot time) {
        boolean[] rows = time.getSlotNumbers(); // 27
        boolean[] cols = time.getDayNumbers(); // 5

        for (int r = 0; r < 27; r++) {
            for (int c = 0; c < 5; c++) {
                if(rows[r] && cols[c]) {
                    calendar[r][c] = false;
                }
            }
        }
    }

    public int getTotalCredits() {
        int credits = 0;
        for (Section section: sections) {
            credits += section.getCourse().getCreditHours();
        }

        return credits;
    }

    public int getDaysWithoutClass() {
        int daysWOClass = 0;
        boolean noClass;

        for (int c = 0; c < 5; c++) {
            noClass = true;
            for (int r = 0; r < 27; r++) {
                if (calendar[r][c]) {
                    noClass = false;
                    break;
                }
            }
            if (noClass) daysWOClass++;
        }

        return daysWOClass;
    }

    public int getLongestBreak() {
        int maxBreak = 0;

        for (int c = 0; c < 5; c++) {
            int prevEnd = -1;
            int r = 0;
            while (r < 27) {
                int start = r;
                if (calendar[r][c]) { // Start of a class
                    while (r < 27 && calendar[r][c]) { // Find length of the class
                        r++;
                    }
                    int end = r - 1; // End of the class

                    if (prevEnd != -1) {
                        int classBreak = start - prevEnd - 1; // 30 min blocks in between the classes
                        if (classBreak > maxBreak) { maxBreak = classBreak; }
                    }

                    prevEnd = end;
                } else { // No class found
                    r++;
                }
            }
        }

        return maxBreak * 30; // Max break in minutes
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

    public boolean[][] getCalendar() {
        return calendar;
    }
}
