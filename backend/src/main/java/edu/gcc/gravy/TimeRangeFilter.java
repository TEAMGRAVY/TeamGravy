package edu.gcc.gravy;

import java.sql.Time;
import java.util.List;
import java.util.Set;

public class TimeRangeFilter extends Filter {
    private Time earliestTime;
    private Time latestTime;
    private Set<Day> days;

    public TimeRangeFilter(Time earliestTime, Time latestTime, Set<Day> days) {
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
