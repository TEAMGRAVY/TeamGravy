package edu.gcc.gravy;

import java.util.ArrayList;
import java.util.Objects;


public class Section {
    private Course course;
    private char sectionID;
    private String term;
    private ArrayList<String> professor;
    private int capacity;
    private int enrolled;
    private ArrayList<TimeSlot> time;
    private boolean isOpen;
    private String location;

    public Section() {
    }

    public Section(Course course, char sectionID, ArrayList<String> professor, int capacity, int enrolled, ArrayList<TimeSlot> time, boolean isOpen, String location, String term) {
        this.course = course;
        this.sectionID = sectionID;
        this.professor = professor;
        this.capacity = capacity;
        this.enrolled = enrolled;
        this.time = time;
        this.isOpen = isOpen;
        this.location = location;
        this.term = term;
    }

    public Section(Course course, char sectionID, ArrayList<String> professor) {
        this.course = course;
        this.sectionID = sectionID;
        this.professor = professor;
    }

    public boolean isFull() {
        return enrolled >= capacity;
    }

    public boolean hasTimeConflict(Section other) {
        for (TimeSlot slot : time) {
            for (TimeSlot otherSlot : other.getTime()) {
                if (slot.overlaps(otherSlot)) {
                    return true;
                }
            }
        }

        return false;
    }

    public boolean hasTimeConflict(Activity other) {
        for (TimeSlot slot : time) {
            if (slot.overlaps(other.getTime())) {
                return true;
            }
        }

        return false;
    }

    public void setCourse(Course course) {
        this.course = course;
    }

    public void setSectionID(char sectionID) {
        this.sectionID = sectionID;
    }

    public String getCourseCode() {
        return course.getDepartment() + " " + course.getCourseID() + " " + sectionID;
    }

    public Course getCourse() {
        return course;
    }

    public char getSectionID() {
        return sectionID;
    }

    public ArrayList<String> getProfessors() {
        return professor;
    }

    public void setProfessors(ArrayList<String> professor) { this.professor = professor;}

    public int getCapacity() {
        return capacity;
    }

    public int getEnrolled() {
        return enrolled;
    }

    public ArrayList<TimeSlot> getTime() {
        return time;
    }

    public boolean isOpen() { return  isOpen; }

    public  String getLocation() { return location; }

    public String getTerm() {
        return term;
    }

    public void setTerm(String term) {this.term = term;}

    @Override
    public boolean equals(Object o) { // Override for quicker removeSection() in Schedule
        if (this == o) return true;
        if (!(o instanceof Section)) return false;
        Section s = (Section) o;
        return sectionID == s.sectionID &&
                Objects.equals(course, s.course); // Sections are uniquely defined by course & sectionID
    }

    @Override
    public int hashCode() {
        return Objects.hash(course, sectionID);
    }
}
