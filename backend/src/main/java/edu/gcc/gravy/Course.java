package edu.gcc.gravy;
import java.io.Serializable;
import java.util.List;

public class Course {
    private int courseID;
    private String title;
    private String department;
    private int creditHours;
    private List<Course> preReqs;
    private List<Course> coReqs;
    private String yearStanding;
    private List<Section> sections;

    public Course(int courseID, String title, String department, int creditHours) {
        this.courseID = courseID;
        this.title = title;
        this.department = department;
        this.creditHours = creditHours;
    }

    public int getCourseID() {
        return courseID;
    }

    public String getTitle() {
        return title;
    }

    public String getDepartment() {
        return department;
    }

    public int getCreditHours() {
        return creditHours;
    }

    public List<Section> getSections() {
        return sections;
    }

    // To be used for next sprint:
    public List<Course> getPreReqs() {
        return preReqs;
    }

    public List<Course> getCoReqs() { return coReqs; }

    public String getYearStanding() {
        return yearStanding;
    }

}
