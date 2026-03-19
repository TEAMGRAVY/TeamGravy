package edu.gcc.gravy;

import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Comparator;
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
        ArrayList<Section> alternates = (ArrayList<Section>) section.getCourse().getSections();
        if (alternates == null){
            alternates = new ArrayList<>();
        } else {
            alternates.remove(section);
        }

        errorMessage = null;
        System.out.println("isOpen: " + section.isOpen() + " isFull: " + section.isFull() + " code: " + section.getCourseCode());
        for (Section other : sections) {
            if (section.equals(other)) {
                errorMessage = "Section " + section.getCourseCode() + " is already in your schedule.";
                return false;
            }
        }

        // Next Sprint: disallow adding the same course code twice

        if (!section.isOpen()) {
            errorMessage = "Section " + section.getCourseCode() + " is not open.";
            // Scan through alternate sections for ones that are open.
            for (Section curr : alternates){
                if (curr.isOpen()){
                    errorMessage = errorMessage.concat("\nAlternate open section: " + curr.getCourseCode());
                }
            }
            return false;
        }

        if (section.isFull()) {
            errorMessage = "Section " + section.getCourseCode() + " is full.";
            // Scan through alternate sections for sections that are not full.
            for (Section curr : alternates){
                if (!curr.isFull()){
                    errorMessage = errorMessage.concat("\nAlternate open section: " + curr.getCourseCode());
                }
            }
            return false;
        }

        for (Section other : sections) { // Scan for time conflict error
            if (section.hasTimeConflict(other)) {
                errorMessage = "Section " + section.getCourseCode() + " conflicts with section " +  other.getCourseCode();

                // Scan through alternate sections in the same course for a potential fit.
                for (Section curr : alternates){
                    boolean conflict = false;
                    for (Section comparison : sections) {
                        if (curr.hasTimeConflict(comparison)) {
                            conflict = true;
                        }
                    }
                    // Add the section to the displayed message if no conflicts of same type.
                    if (!conflict) {
                        errorMessage = errorMessage.concat("\nAlternate non-conflicting section: " + curr.getCourseCode());
                    }
                }
                return false;

            }
        }

        for (Activity other: activities) { // Scan for conflict with activity
            if (section.hasTimeConflict(other)) {
                errorMessage = "Section " + section.getCourseCode() + " conflicts with activity " + other.getName();

                // Scan through alternate sections for one that may not conflict with other parts of the schedule.
                for (Section curr : alternates){
                    boolean conflict = false;
                    for (Section comparison : sections) {
                        if (curr.hasTimeConflict(comparison)) {
                            conflict = true;
                        }
                    }
                    if (!conflict) {
                        errorMessage = errorMessage.concat("\nAlternate non-conflicting section: " + curr.getCourseCode());
                    }
                }
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
        errorMessage = null; // Reset error message

        for (Section other : sections) { // Scan for either error
            if (other.hasTimeConflict(activity)) {
                ArrayList<Section> alternates = (ArrayList<Section>) other.getCourse().getSections();
                if (alternates == null){
                    alternates = new ArrayList<>();
                } else {
                    alternates.remove(other);
                }
                errorMessage = "Activity " + activity.getName() + " conflicts with section " + other.getCourseCode();

                // Scan for a section that does not interfere with schedule or activity.
                for (Section alternate : alternates){
                    if (!alternate.hasTimeConflict(activity)){
                        boolean conflict = false;
                        for (Section scheduled : sections){
                            if (alternate.hasTimeConflict(scheduled)) {
                                conflict = true;
                            }
                        }
                        if (!conflict){
                            errorMessage = errorMessage.concat("\nAlternate non-conflicting section: " + alternate.getCourseCode());
                        }

                    }

                }
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

            for (int r = 0; r < 27; r++) { // Search for conflicts first (previous method ensure no conflicts so this is an extra check used in testing)
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

        for (Day day : Day.values()) {
            ArrayList<TimeSlot> slots = new ArrayList<>();

            // Get timeslots per day
            for (Section section: sections) {
                for (TimeSlot slot: section.getTime()) {
                    if (slot.getDays().contains(day)) {
                        slots.add(slot);
                    }
                }
            }

            // Sort slots by start time
            slots.sort(Comparator.comparing(TimeSlot::getStartTime)); // This line was an Intellij suggestion - This sorts by earliest time

            // Compute breaks using LocalTime operations
            for (int i = 1; i < slots.size(); i++) { // Start at 1 to ignore days with 1 or fewer classes
                LocalTime prevEnd = slots.get(i - 1).getEndTime();
                LocalTime nextStart = slots.get(i).getStartTime();

                int breakMinutes = (int) java.time.Duration.between(prevEnd, nextStart).toMinutes(); // Calculates exact break in minutes

                maxBreak = Math.max(maxBreak, breakMinutes);
            }
        }

        return maxBreak;
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
