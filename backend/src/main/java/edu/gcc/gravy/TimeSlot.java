package edu.gcc.gravy;

import java.time.LocalTime;
import java.util.Set;
import java.time.Duration;

public class TimeSlot {
    private LocalTime startTime;
    private LocalTime endTime;
    private Set<Day> days;
    private static final LocalTime DAY_START = LocalTime.of(8, 0);

    public TimeSlot(LocalTime startTime, LocalTime endTime, Set<Day> days) {
        if (endTime.isBefore(startTime)) {
            throw new IllegalArgumentException("End time must be after start time.");
        }

        this.startTime = startTime;
        this.endTime = endTime;
        this.days = days;
    }

    public LocalTime getStartTime() {
        return startTime;
    }

    public LocalTime getEndTime() {
        return endTime;
    }

    public Set<Day> getDays() {
        return days;
    }

    public boolean[] getDayNumbers() { // Used for updateCalendar()
        boolean[] result = new boolean[5];
        Day[] allDays = Day.values();

        // Mark slots that match days
        for (int i = 0; i < 5; i++) {
            result[i] = days.contains(allDays[i]);
        }

        return result;
    }

    public boolean[] getSlotNumbers() {
        boolean[] result = new boolean[27];

        // Convert start and end times into seconds since midnight,
        // then shift relative to DAY_START and divide by 30 minutes (in seconds)
        // to get the corresponding slot indices
        int startIndex = (startTime.toSecondOfDay() - DAY_START.toSecondOfDay()) / (30 * 60);
        int endIndex = (endTime.toSecondOfDay() - DAY_START.toSecondOfDay()) / (30 * 60);

        // Limit indices to valid bounds to prevent array out-of-bounds errors
        startIndex = Math.max(0, startIndex);
        endIndex = Math.min(27, endIndex);

        // Mark all slots that fall within the time range
        for (int i = startIndex; i < endIndex; i++) {
            result[i] = true;
        }

        return result;
    }

    public int getDuration() { // Returns number of minutes in class
        return (int) Duration.between(startTime, endTime).toMinutes();
    }

//    public boolean sharesDay(TimeSlot other) { // Used in overlaps to check one
//        for (Day day: days) {
//            if (other.days.contains(day)) return true;
//        }
//
//        return false;
//    }

    public boolean sharesDay(Set<Day> other) { // Used in TimeRangeFilter to check a set of Days
        for (Day day: days) {
            if (other.contains(day)) return true;
        }

        return false;
    }

    public boolean overlaps(TimeSlot other) {
        if(!sharesDay(other.days)) return false;

        return startTime.isBefore(other.endTime) && other.getStartTime().isBefore(endTime);
    }

    public boolean startsAfter(LocalTime time) {
        return !startTime.isBefore(time);
    }

    public boolean endsBefore(LocalTime time) {
        return !endTime.isAfter(time);
    }
}
