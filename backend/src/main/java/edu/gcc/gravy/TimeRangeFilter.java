package edu.gcc.gravy;

import java.time.LocalTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class TimeRangeFilter extends Filter {
    private LocalTime earliestTime;
    private LocalTime latestTime;
    private Set<Day> days;

    public TimeRangeFilter(LocalTime earliestTime, LocalTime latestTime, Set<Day> days) { // Are these times just based on the startTime of the section?
        super(FilterType.TIMERANGE);
        this.earliestTime = earliestTime;
        this.latestTime = latestTime;
        this.days = days;
    }

    @Override
    public List<Section> apply(List<Section> sections) {
        List<Section> result = new ArrayList<>();

        for (Section section : sections) {
            boolean matchesTime = true;

            Set<Day> sectionDays = new HashSet<>();
            for (TimeSlot slot : section.getTime()) { // Get all the days this section is held on
                sectionDays.addAll(slot.getDays());
            }

            boolean matchesDay = (days == null) || sectionDays.containsAll(days); // If day filter is not set or the section contains all days in the filter

            for (TimeSlot slot : section.getTime()) {
                LocalTime startTime = slot.getStartTime();

                // Check earliestTime if set
                if (earliestTime != null && startTime.isBefore(earliestTime)) {
                    matchesTime = false;
                    break;
                }

                // Check latestTime if set
                if (latestTime != null && startTime.isAfter(latestTime)) {
                    matchesTime = false;
                    break;
                }
            }

            // If the section passed all filters
            if (matchesTime && matchesDay) {
                result.add(section);
            }
        }

        return result;
    }
}
