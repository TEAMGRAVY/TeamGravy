package edu.gcc.gravy;

public class Activity {
    private String name;
    private TimeSlot time;

    public Activity(String name, TimeSlot time) {
        this.name = name;
        this.time = time;
    }

    public String getName() {
        return name;
    }

    public TimeSlot getTime() {
        return time;
    }
}
