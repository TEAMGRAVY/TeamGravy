package edu.gcc.gravy;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

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
        sections = new ArrayList<>();
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

    @Override
    public String toString() {
        return department + " " + courseID;
    }

    @Override
    public boolean equals(Object o) { // Override so schedule can check if courses are equal based on department and id
        if (this == o) return true;
        if (!(o instanceof Course)) return false;
        Course other = (Course) o;
        return this.courseID == other.courseID &&
                this.department.equals(other.department);
    }

    @Override
    public int hashCode() {
        return Objects.hash(courseID, department);
    }
}
