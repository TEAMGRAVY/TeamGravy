package edu.gcc.gravy;

import java.awt.*;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;


public class Profile {
    private String name;
    private String hashedPassword;
    private int gradYear;
    private String major;
    //private List<Course> coursesTaken;
    private preferences preferences;

    public void setName(String name){
        this.name = name;
    }

    public String getName(){
        return name;
    }

    public void setMajor(String major){
        this.major = major;
    }

    public String getMajor(){
        return major;
    }

    public void setGradYear(int gradYear){
        this.gradYear = gradYear;
    }

    public int getGradYear(){
        return gradYear;
    }

    public void setHashedPassword(String password) throws NoSuchAlgorithmException {
        this.hashedPassword = HashUtils.sha256(password);
    }

    public String getHashedPassword(){
        return hashedPassword;
    }

//    public void addCourse(Course addition){
//        if (coursesTaken == null){
//            coursesTaken = new ArrayList<>();
//        }
//        coursesTaken.add(addition);
//    }

//    public String removeCourse(Course removal){
//        if (coursesTaken == null){
//            return "No courses in list";
//        }
//        return (coursesTaken.remove(removal)) ? "Success" : "Course not in list";
//    }
//
//    public List<Course> getCoursesTaken() {
//        return coursesTaken;
//    }

    public void setPreferences(boolean darkMode, boolean longestBreak, boolean showWarnings){
        if (preferences == null){
            preferences = new preferences();
        }
        preferences.darkMode = darkMode;
        preferences.longestBreak = longestBreak;
        preferences.showWarnings = showWarnings;
    }

    public boolean[] getPreferences() {
        return new boolean[]{
                preferences.darkMode,
                preferences.longestBreak,
                preferences.showWarnings
        };
    }

    private static class preferences {
        public preferences(){}
        public boolean darkMode;
        public boolean longestBreak;
        public boolean showWarnings;
    }
}
