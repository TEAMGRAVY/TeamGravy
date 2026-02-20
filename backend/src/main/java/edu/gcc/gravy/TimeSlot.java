package edu.gcc.gravy;

import java.time.LocalTime;
import java.util.Set;

public class TimeSlot {
    private LocalTime startTime;
    private LocalTime endTime;
    private Set<Day> days;
    private static final LocalTime DAY_START = LocalTime.of(8, 0);

    public TimeSlot(LocalTime startTime, LocalTime endTime, Set<Day> days) {
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

    public boolean[] getDayNumbers() { // Used for updateCalendar()
        boolean[] result = new boolean[5];
        Day[] allDays = Day.values();

        for (int i = 0; i < 5; i++) {
            result[i] = days.contains(allDays[i]);
        }

        return result;
    }

    public boolean[] getSlotNumbers() {
        boolean[] result = new boolean[26];

        int startIndex = (startTime.toSecondOfDay() - DAY_START.toSecondOfDay()) / (30 * 60);
        int endIndex = (endTime.toSecondOfDay() - DAY_START.toSecondOfDay()) / (30 * 60);

        startIndex = Math.max(0, startIndex);
        endIndex = Math.min(26, endIndex);

        for (int i = startIndex; i < endIndex; i++) {
            result[i] = true;
        }

        return result;
    }

    public int getDuration() { // Returns number of minutes in class
        return (int) Duration.between(startTime, endTime).toMinutes();
    }

    private boolean sharesDay(Timeslot other) {
        for (Day day: days) {
            if (other.days.contains(day)) return true;
        }

        return false;
    }

    private boolean sharesDay(Set<Day> other) {
        for (Day day: days) {
            if (other.contains(day)) return true;
        }

        return false;
    }

    public boolean overlaps(TimeSlot other) {
        if(!sharesDay(other)) return false;

        return startTime.isBefore(other.endTime) && other.startTime().isBefore(endTime);
    }

    public boolean startsAfter(LocalTime time) {
        return !startTime.isBeofore(time);
    }

    public boolean endsBefore(LocalTime time) {
        return !endTime.isAfter(time);
    }

    public boolean occursOn(Set<Day> otherDays) {
        for (Day day: otherDays) {
            if (!days.contains(day)) return false;
        }
        return true;
    }
}
