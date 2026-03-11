package edu.gcc.gravy;

import java.util.List;

public class JSONSection {
    public int credits;
    public String[] faculty;
    public boolean is_lab;
    public boolean is_open;
    public String location;
    public String name;
    public int number;
    public int open_seats;
    public String section;
    public String semester;
    public String subject;
    public List<time> times;
    public int total_seats;


    public static class time{
        public String day;
        public String end_time;
        public String start_time;
    }

    public Section toSection(){
        Section current = new Section();
    }

}
