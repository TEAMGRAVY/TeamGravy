package edu.gcc.gravy;
import java.io.Serializable;
import java.util.List;

public class Course {
    private int courseID;
    private String title;
    private String department;
    private int creditHours;
    private String term;
    private List<Course> preReqs;
    private List<Course> coReqs;
    private String yearStanding;
    private List<Section> sections;
    private double rating;

    public Course(int courseID, String title, String department, int creditHours, String term) {
        this.courseID = courseID;
        this.title = title;
        this.department = department;
        this.creditHours = creditHours;
        this.term = term;
        // May be more depending on the database
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

    public String getTerm() {
        return term;
    }

    public List<Course> getPreReqs() {
        return preReqs;
    }

    public List<Course> getCoReqs() {
        return coReqs;
    }

    public String getYearStanding() {
        return yearStanding;
    }

    public List<Section> getSections() {
        return sections;
    }

    public double getRating() {
        return rating;
    }

}
