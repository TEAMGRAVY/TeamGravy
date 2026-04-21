package edu.gcc.gravy;

import org.junit.jupiter.api.Test;

import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class JSONSectionTest {

    @Test
    void toSection() {
        Course baseCourse = new Course(101, "Intro to Testing", "CS", 3);

        // Give the base section MULTIPLE times to hit reverse loop
        ArrayList<TimeSlot> baseTimes = new ArrayList<>();
        baseTimes.add(new TimeSlot(LocalTime.of(9, 0), LocalTime.of(10, 0), Set.of(Day.MONDAY)));
        baseTimes.add(new TimeSlot(LocalTime.of(10, 0), LocalTime.of(11, 0), Set.of(Day.TUESDAY)));

        Section baseSection = new Section(
                baseCourse,
                'A',
                new ArrayList<>(java.util.List.of("Dr. Smith")),
                30,
                25,
                baseTimes,
                true,
                "Room 101",
                "Fall 2026"
        );

        JSONSection json = new JSONSection(baseSection);

        // Now override times to hit ALL switch cases
        json.times = new ArrayList<>();

        String[] days = {"M", "T", "W", "R", "F", "X"}; // X = default case

        for (String d : days) {
            JSONSection.time t = json.new time(
                    new TimeSlot(
                            LocalTime.of(9, 0),
                            LocalTime.of(10, 0),
                            Set.of(Day.MONDAY) // doesn't matter, gets overridden
                    )
            );
            t.day = d;
            t.start_time = "09:00";
            t.end_time = "10:00";
            json.times.add(t);
        }

        json.open_seats = 5;
        json.total_seats = 30;
        json.section = "A";
        json.is_open = true;

        ArrayList<Course> allCourses = new ArrayList<>();

        // First call → adds course
        Section section = json.toSection(allCourses);

        assertEquals(1, allCourses.size());
        assertEquals(6, section.getTime().size()); // all times processed

        // Second call → reuse existing course
        Section section2 = json.toSection(allCourses);

        assertEquals(1, allCourses.size());
        assertSame(allCourses.get(0), section2.getCourse());
    }
}