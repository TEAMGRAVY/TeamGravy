package edu.gcc.gravy;

import org.junit.jupiter.api.Test;

import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class SectionTest {

    private TimeSlot slot(int start, int end, Day day) {
        return new TimeSlot(
                LocalTime.of(start, 0),
                LocalTime.of(end, 0),
                Set.of(day)
        );
    }

    private Section buildSection(boolean open, int capacity, int enrolled, ArrayList<TimeSlot> times) {
        return new Section(
                new Course(101, "Test", "CS", 3),
                'A',
                new ArrayList<>(List.of("Prof")),
                capacity,
                enrolled,
                times,
                open,
                "Room",
                "Fall"
        );
    }

    @Test
    void isFull() {
        assertTrue(buildSection(true, 10, 10, new ArrayList<>()).isFull());
        assertFalse(buildSection(true, 10, 5, new ArrayList<>()).isFull());
    }

    @Test
    void hasTimeConflict_section() {
        ArrayList<TimeSlot> t1 = new ArrayList<>();
        t1.add(slot(9, 10, Day.MONDAY));

        ArrayList<TimeSlot> t2 = new ArrayList<>();
        t2.add(slot(9, 10, Day.MONDAY));

        ArrayList<TimeSlot> t3 = new ArrayList<>();
        t3.add(slot(11, 12, Day.MONDAY));

        Section s1 = buildSection(true, 10, 5, t1);
        Section s2 = buildSection(true, 10, 5, t2);
        Section s3 = buildSection(true, 10, 5, t3);

        assertTrue(s1.hasTimeConflict(s2));
        assertFalse(s1.hasTimeConflict(s3));
    }

    @Test
    void set_and_get_course() {
        Section s = new Section();
        Course c = new Course(202, "New", "EE", 4);

        s.setCourse(c);

        assertEquals(c, s.getCourse());
    }

    @Test
    void set_and_get_sectionID() {
        Section s = new Section();

        s.setSectionID('Z');

        assertEquals('Z', s.getSectionID());
    }

    @Test
    void getCourseCode() {
        Course c = new Course(101, "Test", "CS", 3);
        Section s = new Section(c, 'A', new ArrayList<>());

        assertEquals("CS 101 A", s.getCourseCode());
    }

    @Test
    void professors() {
        ArrayList<String> profs = new ArrayList<>(List.of("Dr. A"));

        Section s = new Section(null, 'A', profs);

        assertEquals(profs, s.getProfessors());

        ArrayList<String> newProfs = new ArrayList<>(List.of("Dr. B"));
        s.setProfessors(newProfs);

        assertEquals(newProfs, s.getProfessors());
    }

    @Test
    void capacity_and_enrolled() {
        Section s = buildSection(true, 40, 15, new ArrayList<>());

        assertEquals(40, s.getCapacity());
        assertEquals(15, s.getEnrolled());
    }

    @Test
    void getTime() {
        ArrayList<TimeSlot> times = new ArrayList<>();
        Section s = buildSection(true, 30, 10, times);

        assertEquals(times, s.getTime());
    }

    @Test
    void isOpen() {
        assertTrue(buildSection(true, 10, 5, new ArrayList<>()).isOpen());
        assertFalse(buildSection(false, 10, 5, new ArrayList<>()).isOpen());
    }

    @Test
    void getLocation() {
        Section s = buildSection(true, 10, 5, new ArrayList<>());

        assertEquals("Room", s.getLocation());
    }

    @Test
    void term() {
        Section s = new Section();

        s.setTerm("Spring");

        assertEquals("Spring", s.getTerm());
    }

    @Test
    void equals_and_hashCode() {
        Course c1 = new Course(101, "Test", "CS", 3);
        Course c2 = new Course(101, "Test", "CS", 3);

        Section s1 = new Section(c1, 'A', new ArrayList<>());
        Section s2 = new Section(c1, 'A', new ArrayList<>());
        Section s3 = new Section(c2, 'A', new ArrayList<>());
        Section s4 = new Section(c1, 'B', new ArrayList<>());

        assertEquals(s1, s2);
        assertEquals(s1, s3);
        assertNotEquals(s1, s4);
        assertNotEquals(s1, null);
        assertNotEquals(s1, "string");

        assertEquals(s1.hashCode(), s2.hashCode());
    }
}