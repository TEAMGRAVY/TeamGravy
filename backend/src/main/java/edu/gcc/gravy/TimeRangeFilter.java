package edu.gcc.gravy;

import java.time.LocalTime;
import java.util.List;
import java.util.Set;

public class TimeRangeFilter extends Filter {
    private LocalTime earliestTime;
    private LocalTime latestTime;
    private Set<Day> days;

    public TimeRangeFilter(LocalTime earliestTime, LocalTime latestTime, Set<Day> days) {
        super(FilterType.TIMERANGE);
        this.earliestTime = earliestTime;
        this.latestTime = latestTime;
        this.days = days;
    }

    @Override
    public List<Section> apply(List<Section> sections) {
        List<Section> result = new ArrayList<>();
        for (Section section : sections) {
            if (section.getTime().sharesDay(days)) {
                if (earliestTime == NULL && latestTime == NULL) { // No time constraints
                    result.add(section);
                } else if (earliestTime == NULL) {
                    if (latestTime.isAfter(section.getTime().getStartTime())) result.add(section);
                } else if (latestTime == NULL) {

                } else { // Time constraints for both

                }

            }
        }
        return List.of();
    }
}
