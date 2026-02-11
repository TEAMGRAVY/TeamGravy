package edu.gcc.gravy;

import java.util.List;

public class Student {
    private int studentID;
    private String major;
    private String minor;
    private String year;
    private List<Schedule> schedules;
    private List<Course> completedCourses;

    public Student() {

    }

    public void createSchedule(String name, String term) {

    }

    public void removeSchedule(Schedule schedule) {

    }

    public List<Schedule> getSchedules() {
        return schedules;
    }

    public String getYear() {
        return year;
    }

    public List<Course> getCompletedCourses() {
        return completedCourses;
    }
}
