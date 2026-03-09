package edu.gcc.gravy;

import java.util.Objects;

public class Section {
    private Course course;
    private char sectionID;
    private String professor;
    private int capacity;
    private int enrolled;
    private TimeSlot time;
    private boolean isOpen;
    private String location;

    public Section(Course course, char sectionID, String professor, int capacity, int enrolled, TimeSlot time, boolean isOpen, String location) {
        this.course = course;
        this.sectionID = sectionID;
        this.professor = professor;
        this.capacity = capacity;
        this.enrolled = enrolled;
        this.time = time;
        this.isOpen = isOpen;
        this.location = location;
    }

    public boolean isFull() {
        return enrolled >= capacity;
    }

    public boolean hasTimeConflict(Section other) {
        return time.overlaps(other.getTime());
    }

    public boolean hasTimeConflict(Activity other) {
        return time.overlaps(other.getTime());
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

    public String getProfessor() {
        return professor;
    }

    public int getCapacity() {
        return capacity;
    }

    public int getEnrolled() {
        return enrolled;
    }

    public TimeSlot getTime() {
        return time;
    }

    public boolean isOpen() { return  isOpen; }

    public  String getLocation() { return location; }

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
