package edu.gcc.gravy;

import java.sql.Time;
import java.util.Set;

public class TimeSlot {
    private Time startTime;
    private Time endTime;
    private Set<Day> days;

    public TimeSlot(Time startTime, Time endTime, Set<Day> days) {
        this.startTime = startTime;
        this.endTime = endTime;
        this.days = days;
    }

    public Time getStartTime() {
        return startTime;
    }

    public Time getEndTime() {
        return endTime;
    }

    public Set<Day> getDays() {
        return days;
    }

    public int getDuration() {
        return -1;
    }

    public boolean overlaps(TimeSlot other) {
        return false;
    }

    public boolean isBefore(TimeSlot other) {
        return false;
    }

    public boolean isAfter(TimeSlot other) {
        return false;
    }
}
