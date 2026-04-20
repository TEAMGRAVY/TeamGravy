package edu.gcc.gravy;

import java.util.ArrayList;
import java.util.List;

public class Profile {
    private String name;
    private int gradYear;
    private String major;
    private List<Course> coursesTaken;

    void setName(String name){
        this.name = name;
    }

    void setMajor(String major){
        this.major = major;
    }

    void setGradYear(int gradYear){
        this.gradYear = gradYear;
    }
}
