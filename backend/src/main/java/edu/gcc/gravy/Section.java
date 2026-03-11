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

    public Section(Course course, char sectionID, String professor, int capacity, int enrolled, TimeSlot time) {
        this.course = course;
        this.sectionID = sectionID;
        this.professor = professor;
        this.capacity = capacity;
        this.enrolled = enrolled;
        this.time = time;
    }

    public Section(Course course, char sectionID, String professor) {
        this.course = course;
        this.sectionID = sectionID;
        this.professor = professor;
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

    public void setProfessor(String professor) { this.professor = professor;}

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
