package edu.gcc.gravy;

import org.junit.jupiter.api.Test;

import java.time.LocalTime;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class TimeSlotTest {

    // Helper method to create TimeSlot objects
    private TimeSlot slot(int startHour, int startMin,
                          int endHour, int endMin,
                          Day... days) {
        return new TimeSlot(
                LocalTime.of(startHour, startMin),
                LocalTime.of(endHour, endMin),
                Set.of(days)
        );
    }

    @Test
    void getDuration() {
        TimeSlot slot = new TimeSlot(
                LocalTime.of(9, 0),
                LocalTime.of(10, 30),
                Set.of(Day.MONDAY, Day.WEDNESDAY)
        );

        assertEquals(90, slot.getDuration());
    }

    @Test
    void overlaps_true_sameDay_overlapTime() {
        TimeSlot a = slot(9, 0, 10, 0, Day.MONDAY);
        TimeSlot b = slot(9, 30, 10, 30, Day.MONDAY);

        assertTrue(a.overlaps(b));
    }

    @Test
    void overlaps_false_sameDay_noOverlap() {
        TimeSlot a = slot(9, 0, 10, 0, Day.MONDAY);
        TimeSlot b = slot(10, 0, 11, 0, Day.MONDAY);

        assertFalse(a.overlaps(b));
    }

    @Test
    void overlaps_false_differentDays() {
        TimeSlot a = slot(9, 0, 10, 0, Day.MONDAY);
        TimeSlot b = slot(9, 0, 10, 0, Day.TUESDAY);

        assertFalse(a.overlaps(b));
    }

    @Test
    void overlaps_oneContainsOther() {
        TimeSlot a = slot(9, 0, 12, 0, Day.MONDAY);
        TimeSlot b = slot(10, 0, 11, 0, Day.MONDAY);

        assertTrue(a.overlaps(b));
    }

    @Test
    void overlaps_touchingReverseOrder() {
        TimeSlot a = slot(10, 0, 11, 0, Day.MONDAY);
        TimeSlot b = slot(9, 0, 10, 0, Day.MONDAY);

        assertFalse(a.overlaps(b));
    }

    @Test
    void overlaps_identicalSlots() {
        TimeSlot a = slot(9, 0, 10, 0, Day.MONDAY);
        TimeSlot b = slot(9, 0, 10, 0, Day.MONDAY);

        assertTrue(a.overlaps(b));
    }

    @Test
    void overlaps_sharedOneOfMultipleDays() {
        TimeSlot a = slot(9, 0, 10, 0, Day.MONDAY, Day.WEDNESDAY);
        TimeSlot b = slot(9, 30, 10, 30, Day.WEDNESDAY);

        assertTrue(a.overlaps(b));
    }

    @Test
    void isAfter_true() {
        TimeSlot slot = slot(10, 0, 11, 0, Day.MONDAY);

        assertTrue(slot.startsAfter(LocalTime.of(9, 0)));
    }

    @Test
    void isAfter_false() {
        TimeSlot slot = slot(10, 0, 11, 0, Day.MONDAY);

        assertFalse(slot.startsAfter(LocalTime.of(10, 30)));
    }

    @Test
    void isAfter_edge_equalTime() {
        TimeSlot slot = slot(10, 0, 11, 0, Day.MONDAY);

        assertTrue(slot.startsAfter(LocalTime.of(10, 0)));
    }

    @Test
    void isBefore_true() {
        TimeSlot slot = slot(9, 0, 10, 0, Day.MONDAY);

        assertTrue(slot.endsBefore(LocalTime.of(11, 0)));
    }

    @Test
    void isBefore_false() {
        TimeSlot slot = slot(9, 0, 10, 0, Day.MONDAY);

        assertFalse(slot.endsBefore(LocalTime.of(9, 30)));
    }

    @Test
    void isBefore_edge_equalTime() {
        TimeSlot slot = slot(9, 0, 10, 0, Day.MONDAY);

        assertTrue(slot.endsBefore(LocalTime.of(10, 0)));
    }
}