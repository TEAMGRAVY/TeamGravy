package edu.gcc.gravy;

import java.time.LocalTime;
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
            // Check days if set - otherwise skip
            if (days != null && !section.getTime().sharesDay(days)) continue;

            LocalTime startTime = section.getTime().getStartTime();

            // Check earliestTime if set
            if (earliestTime != null && startTime.isBefore(earliestTime)) continue;
            // Check latestTime if set
            if (latestTime != null && startTime.isAfter(latestTime)) continue;

            result.add(section); // If the section passed all filters

            // Probably less efficient version of the same logic:
//            if (days != null) { // Days were selected in the filter
//                if (section.getTime().sharesDay(days)) { // Matches days selected
//                    if (earliestTime == null && latestTime == null) { // No time constraints
//                        result.add(section);
//                    } else if (earliestTime == null) { // User only selected latestTime
//                        if (latestTime.isAfter(section.getTime().getStartTime())) result.add(section);
//                    } else if (latestTime == null) { // User only selected earliestTime
//                        if (earliestTime.isBefore(section.getTime().getStartTime())) result.add(section);
//                    } else { // Time constraints for both
//                        if(latestTime.isAfter(section.getTime().getStartTime()) && earliestTime.isBefore(section.getTime().getStartTime())) result.add(section);
//                    }
//                }
//            } else { // No days selected, just time(s) - Then assume at least one time constraint
//                if (earliestTime == null) {
//                    if (latestTime.isAfter(section.getTime().getStartTime())) result.add(section);
//                } else if (latestTime == null) {
//                    if (earliestTime.isBefore(section.getTime().getStartTime())) result.add(section);
//                } else { // Time constraints for both
//                    if(latestTime.isAfter(section.getTime().getStartTime()) && earliestTime.isBefore(section.getTime().getStartTime())) result.add(section);
//                }
//            }
        }
        return result;
    }
}
