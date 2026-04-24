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
    private String email;
    //private List<Course> coursesTaken;
    private preferences preferences;

    public Profile() {}
    public Profile(String username, String password) throws NoSuchAlgorithmException {
        name = username;
        hashedPassword = HashUtils.sha256(password);
        this.preferences = new preferences();
        setPreferences(true, true, true);
        ProfileFileManager.getInstance().SaveProfile(username, this);
    }


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

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
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

    public String updateProfile(String attribute, String value) {
        return switch (attribute) {
            case "name" -> {
                this.name = value;
                yield "Success";
            }
            case "email" -> {
                this.email = value;
                yield "Success";
            }
            case "gradYear" -> {
                this.gradYear = Integer.parseInt(value);
                yield "Success";
            }
            case "major" -> {
                this.major = value;
                yield "Success";
            }
            default -> preferences.setPreference(attribute, Boolean.parseBoolean(value));
        };
    }

    private static class preferences {
        public preferences(){}
        public boolean darkMode;
        public boolean longestBreak;
        public boolean showWarnings;

        public String setPreference(String preference, boolean value){
            return switch (preference) {
                case "darkMode" -> {
                    darkMode = value;
                    yield "Success";
                }
                case "longestBreak" -> {
                    longestBreak = value;
                    yield "Success";
                }
                case "showWarnings" -> {
                    showWarnings = value;
                    yield "Success";
                }
                default -> "No such property.";
            };
        }
    }
}
