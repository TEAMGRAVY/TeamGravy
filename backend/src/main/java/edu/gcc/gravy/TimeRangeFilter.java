package edu.gcc.gravy;

import java.time.LocalTime;
import java.util.List;
import java.util.Set;

public class TimeRangeFilter extends Filter {
    private LocalTime earliestTime;
    private LocalTime latestTime;
    private Set<Day> days;

    public TimeRangeFilter(LocalTime earliestTime, LocalTime latestTime, Set<Day> days) { // These are based off the startTime of the section
        super(FilterType.TIMERANGE);
        this.earliestTime = earliestTime;
        this.latestTime = latestTime;
        this.days = days;
    }

    @Override
    public List<Section> apply(List<Section> sections) {
        List<Section> result = new ArrayList<>();
        for (Section section : sections) {
            // Check days if set - otherwise skip
            if (days != null && !section.getTime().sharesDay(days)) continue;

            LocalTime startTime = section.getTime().getStartTime();

            // Check earliestTime if set
            if (earliestTime != null && startTime.isBefore(earliestTime)) continue;
            // Check latestTime if set
            if (latestTime != null && startTime.isAfter(latestTime)) continue;

            result.add(section); // If the section passed all filters
        }
        return result;
    }
}
