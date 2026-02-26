package edu.gcc.gravy;

import java.util.ArrayList;
import java.util.List;

public class Schedule {
    private Student student;
    private String name;
    private List<Section> sections;
    private List<Activity> activities;
    private String term;
    private String errorMessage;
    // [1/2 hour slot][weekday]
    private boolean[][] calendar = new boolean[26][5]; // Used for quick checks of time conflicts & updating UI checkboxes

    public Schedule(Student student, String name, String term) {
        this.student = student;
        this.name = name;
        this.term = term;
        sections = new ArrayList<>();
        activities = new ArrayList<>();
    }

    public boolean addSection(Section section) { // Implement prereq/coreq error as additional requirements later - Uses student.getCompletedCourses() & section.getCourse().getPreReqs()/getCoReqs()
        boolean timeConflict = false;
        boolean sectionFull = section.isFull();

        if (sectionFull) {
            getErrorMessage(timeConflict, sectionFull);
            return false;
        }

        for (Section other : sections) { // Scan for either error
            if (!timeConflict) {
                timeConflict = section.hasTimeConflict(other);
            }
        }

        if (!timeConflict) {
            for (Activity other: activities) { // Scan for conflict with activity
                if (!timeConflict) {
                    timeConflict = section.hasTimeConflict(other);
                }
            }
        }

        if (timeConflict) { // Error occured
            getErrorMessage(timeConflict, sectionFull); // Change later based on GUI
            return false;
        }

        if(addCalendar(section.getTime())) {
            sections.add(section); // should this increase the section's enrolled? - Probably this is separate from myGCC so no
            return true;
        }
        return false;
    }

    public boolean removeSection(Section section) {
        if (sections.remove(section)) {
            removeCalendar(section.getTime());
            return true;
        }
        return false;
    }

    public boolean addActivity(Activity activity) {
        boolean timeConflict = false;
        for (Section other : sections) { // Scan for either error
            if (!timeConflict) {
                timeConflict = other.hasTimeConflict(activity);
            }
        }

        for (Activity other: activities) { // Scan for conflict with activity
            if (!timeConflict) {
                timeConflict = activity.hasTimeConflict(other);
            }
        }

        if (timeConflict) { // Error occured
            getErrorMessage(timeConflict, false); // Change later based on GUI
            return false;
        }

        if (addCalendar(activity.getTime())) {
            activities.add(activity);
            return true;
        }
        return false;
    }

    public boolean removeActivity(Activity activity) {
        if (activities.remove(activity)) {
            removeCalendar(activity.getTime());
            return true;
        }
        return false;
    }

    public boolean addCalendar(TimeSlot time) {
        boolean[] rows = time.getSlotNumbers(); // 26
        boolean[] cols = time.getDayNumbers(); // 5

        for (int r = 0; r < 26; r++) {
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
        boolean[] rows = time.getSlotNumbers(); // 26
        boolean[] cols = time.getDayNumbers(); // 5

        for (int r = 0; r < 26; r++) {
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
            for (int r = 0; r < 26; r++) {
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

    public boolean[][] getCalendar() {
        return calendar;
    }
}
