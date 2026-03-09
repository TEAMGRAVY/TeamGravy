package edu.gcc.gravy;

public class Section {
    private Course course;
    private char sectionID;
    private String professor;
    private int capacity;
    private int enrolled;
    private TimeSlot time;

    public Section() {

    }

    public boolean isFull() {
        return enrolled >= capacity;
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
}
