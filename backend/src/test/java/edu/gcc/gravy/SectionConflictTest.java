package edu.gcc.gravy;

import org.junit.jupiter.api.Test;

import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class SectionConflictTest {
    private Course cs112() {
        return new Course(112, "Object-Oriented Programming", "CS", 3, "Fall");
    }

    private Course cs220() {
        return new Course(220, "Data Structures", "CS", 3, "Fall");
    }

    private TimeSlot slot(int startH, int startM, int endH, int endM, Day... days) {
        return new TimeSlot(LocalTime.of(startH, startM),
                LocalTime.of(endH, endM),
                Set.of(days));
    }

    private Section section(Course c, char id, ArrayList<TimeSlot> timeSlots) {
        return new Section(c, id, "Dr. Smith", 30, 10, timeSlots, false, "");
    }

    private Activity activity(String name, TimeSlot t) {
        return new Activity(name, t);
    }

    @Test
    void noConflictBetweenSections() {
        Section s1 = section(cs112(), 'A', new ArrayList<>(List.of(
                slot(9,0,10,0, Day.MONDAY)
        )));
        Section s2 = section(cs220(), 'A', new ArrayList<>(List.of(
                slot(10,0,11,0, Day.MONDAY)
        )));

        assertFalse(s1.hasTimeConflict(s2));
    }

    @Test
    void conflictBetweenSectionsSingleSlot() {
        Section s1 = section(cs112(), 'A', new ArrayList<>(List.of(
                slot(9,0,10,0, Day.MONDAY)
        )));
        Section s2 = section(cs220(), 'A', new ArrayList<>(List.of(
                slot(9,30,10,30, Day.MONDAY)
        )));

        assertTrue(s1.hasTimeConflict(s2));
    }

    @Test
    void conflictBetweenSectionsMultipleSlots() {
        Section s1 = section(cs112(), 'A', new ArrayList<>(List.of(
                slot(9,0,10,0, Day.MONDAY),
                slot(11,0,12,0, Day.WEDNESDAY)
        )));
        Section s2 = section(cs220(), 'A', new ArrayList<>(List.of(
                slot(10,0,11,0, Day.MONDAY),
                slot(11,30,12,30, Day.WEDNESDAY)
        )));

        assertTrue(s1.hasTimeConflict(s2));
    }

    @Test
    void noConflictDifferentDays() {
        Section s1 = section(cs112(), 'A', new ArrayList<>(List.of(
                slot(9,0,10,0, Day.MONDAY)
        )));
        Section s2 = section(cs220(), 'A', new ArrayList<>(List.of(
                slot(9,0,10,0, Day.TUESDAY)
        )));

        assertFalse(s1.hasTimeConflict(s2));
    }

    @Test
    void sectionConflictWithActivity() {
        Section s1 = section(cs112(), 'A', new ArrayList<>(List.of(
                slot(9,0,10,0, Day.MONDAY)
        )));
        Activity a1 = activity("Gym", slot(9,30,10,30, Day.MONDAY));

        assertTrue(s1.hasTimeConflict(a1));
    }

    @Test
    void sectionNoConflictWithActivity() {
        Section s1 = section(cs112(), 'A', new ArrayList<>(List.of(
                slot(9,0,10,0, Day.MONDAY)
        )));
        Activity a1 = activity("Gym", slot(10,0,11,0, Day.MONDAY));

        assertFalse(s1.hasTimeConflict(a1));
    }

    @Test
    void multipleTimeslotSectionNoConflict() {
        Section s1 = section(cs112(), 'A', new ArrayList<>(List.of(
                slot(9,0,10,0, Day.MONDAY),
                slot(11,0,12,0, Day.WEDNESDAY)
        )));
        Section s2 = section(cs220(), 'A', new ArrayList<>(List.of(
                slot(10,0,11,0, Day.MONDAY),
                slot(12,0,13,0, Day.WEDNESDAY)
        )));

        assertFalse(s1.hasTimeConflict(s2));
    }
}