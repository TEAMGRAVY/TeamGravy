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
        return List.of();
    }
}
