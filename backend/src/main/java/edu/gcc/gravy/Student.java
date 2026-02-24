package edu.gcc.gravy;

import java.util.ArrayList;
import java.util.List;

public class Student {
    private int studentId;
    private String major;
    private String minor;
    private String year;
    private List<Schedule> schedules;
    private List<Course> completedCourses;

    public Student(int studentId, String major, String minor, String year) {
        this.studentId = studentId;
        this.major = major;
        this.minor = minor;
        this.year = year;
        this.schedules = new ArrayList<>();
        this.completedCourses = new ArrayList<>();
    }

    public void createSchedule(String name, String term) {
        Schedule schedule = new Schedule(this, name, term);
        schedules.add(schedule);
    }

    public void removeSchedule(Schedule schedule) {
        schedules.remove(schedule);
    }

    public void addCompletedCourses(Course course) {
        this.completedCourses.add(course);
    }

    public List<Schedule> getSchedules() {
        return schedules;
    }

    public String getYear() {
        return year;
    }

    public String getMajor() {
        return major;
    }

    public String getMinor() {
        return minor;
    }

    public List<Course> getCompletedCourses() {
        return completedCourses;
    }
}
