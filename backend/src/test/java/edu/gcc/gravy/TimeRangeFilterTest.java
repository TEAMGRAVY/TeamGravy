package edu.gcc.gravy;

import org.junit.jupiter.api.Test;

import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class TimeRangeFilterTest {
    private Course cs112() {
        return new Course(
                112,
                "Object-Oriented Programming",
                "CS",
                3,
                "Fall"
        );
    }

    private Course cs220() {
        return new Course(
                220,
                "Data Structures",
                "CS",
                3,
                "Fall"
        );
    }

    // -------- Helpers --------

    private TimeSlot slot(int startHour, int startMin,
                          int endHour, int endMin,
                          Day... days) {
        return new TimeSlot(
                LocalTime.of(startHour, startMin),
                LocalTime.of(endHour, endMin),
                Set.of(days)
        );
    }

    private Section section(Course course, char id, ArrayList<TimeSlot> slots) {
        return new Section(
                course,
                id,
                "Dr. Miller",
                30,
                18,
                slots,
                false,
                ""
        );
    }

    // -------- Tests --------

    @Test
    void filters_outEarlyMorningClasses() {
        Section earlyCS112 = section(
                cs112(),
                'A',
                new ArrayList<>(List.of(slot(8, 0, 9, 15, Day.MONDAY, Day.WEDNESDAY, Day.FRIDAY)))
        );

        Section midMorningCS220 = section(
                cs220(),
                'A',
                new ArrayList<>(List.of(slot(10, 0, 11, 15, Day.MONDAY, Day.WEDNESDAY, Day.FRIDAY)))
        );

        TimeRangeFilter filter =
                new TimeRangeFilter(LocalTime.of(9, 0), null, null);

        List<Section> result =
                filter.apply(List.of(earlyCS112, midMorningCS220));

        assertEquals(1, result.size());
        assertTrue(result.contains(midMorningCS220));
    }

    @Test
    void filters_outLateClasses() {
        Section morning = section(
                cs112(),
                'A',
                new ArrayList<>(List.of(slot(9, 0, 10, 15, Day.TUESDAY, Day.THURSDAY)))
        );

        Section afternoon = section(
                cs220(),
                'B',
                new ArrayList<>(List.of(slot(14, 0, 15, 15, Day.TUESDAY, Day.THURSDAY)))
        );

        TimeRangeFilter filter =
                new TimeRangeFilter(null, LocalTime.of(12, 0), null);

        List<Section> result =
                filter.apply(List.of(morning, afternoon));

        assertEquals(1, result.size());
        assertTrue(result.contains(morning));
    }

    @Test
    void filters_bySpecificDays_onlyMWF() {
        Section mwf = section(
                cs112(),
                'A',
                new ArrayList<>(List.of(slot(10, 0, 11, 15, Day.MONDAY, Day.WEDNESDAY, Day.FRIDAY)))
        );

        Section tth = section(
                cs220(),
                'A',
                new ArrayList<>(List.of(slot(10, 0, 11, 15, Day.TUESDAY, Day.THURSDAY)))
        );

        TimeRangeFilter filter =
                new TimeRangeFilter(null, null, Set.of(Day.MONDAY, Day.WEDNESDAY, Day.FRIDAY));

        List<Section> result =
                filter.apply(List.of(mwf, tth));

        assertEquals(1, result.size());
        assertTrue(result.contains(mwf));
    }

    @Test
    void filters_byDayAndTime_combinedConstraint() {
        Section good = section(
                cs112(),
                'A',
                new ArrayList<>(List.of(slot(10, 0, 11, 15, Day.MONDAY, Day.WEDNESDAY)))
        );

        Section wrongTime = section(
                cs220(),
                'A',
                new ArrayList<>(List.of(slot(8, 0, 9, 15, Day.MONDAY, Day.WEDNESDAY)))
        );

        Section wrongDay = section(
                cs220(),
                'B',
                new ArrayList<>(List.of(slot(10, 0, 11, 15, Day.TUESDAY, Day.THURSDAY)))
        );

        TimeRangeFilter filter =
                new TimeRangeFilter(
                        LocalTime.of(9, 0),
                        LocalTime.of(12, 0),
                        Set.of(Day.MONDAY, Day.WEDNESDAY)
                );

        List<Section> result =
                filter.apply(List.of(good, wrongTime, wrongDay));

        assertEquals(1, result.size());
        assertTrue(result.contains(good));
    }

    @Test
    void noFilters_returnsAllSections() {
        Section a = section(cs112(), 'A',
                new ArrayList<>(List.of(slot(9, 0, 10, 15, Day.MONDAY))));

        Section b = section(cs220(), 'A',
                new ArrayList<>(List.of(slot(11, 0, 12, 15, Day.TUESDAY))));

        TimeRangeFilter filter =
                new TimeRangeFilter(null, null, null);

        List<Section> result = filter.apply(List.of(a, b));

        assertEquals(2, result.size());
    }

    @Test
    void includes_class_exactly_at_earliestTime() {
        Section exact = section(
                cs112(),
                'A',
                new ArrayList<>(List.of(slot(10, 0, 11, 0, Day.MONDAY, Day.WEDNESDAY, Day.FRIDAY)))
        );

        TimeRangeFilter filter =
                new TimeRangeFilter(LocalTime.of(10, 0), null,
                        Set.of(Day.MONDAY, Day.WEDNESDAY, Day.FRIDAY));

        List<Section> result = filter.apply(List.of(exact));

        assertEquals(1, result.size());
    }

    @Test
    void excludes_class_before_earliestTime() {
        Section before = section(
                cs112(),
                'A',
                new ArrayList<>(List.of(slot(9, 59, 11, 0, Day.MONDAY, Day.WEDNESDAY, Day.FRIDAY)))
        );

        TimeRangeFilter filter =
                new TimeRangeFilter(LocalTime.of(10, 0), null,
                        Set.of(Day.MONDAY, Day.WEDNESDAY, Day.FRIDAY));

        List<Section> result = filter.apply(List.of(before));

        assertTrue(result.isEmpty());
    }

    @Test
    void includes_if_shares_any_requested_day() {
        Section mondayOnly = section(
                cs112(),
                'A',
                new ArrayList<>(List.of(slot(10, 0, 11, 0, Day.MONDAY)))
        );

        TimeRangeFilter filter =
                new TimeRangeFilter(LocalTime.of(10, 0), null,
                        Set.of(Day.MONDAY, Day.WEDNESDAY, Day.FRIDAY));

        List<Section> result = filter.apply(List.of(mondayOnly));

        assertEquals(1, result.size());
    }
}