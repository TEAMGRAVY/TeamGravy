package edu.gcc.gravy;

import org.junit.jupiter.api.Test;

import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class ScheduleTest {
    // ---------- Helpers ----------

    private Course course(int id, int credits) {
        return new Course(id, "CS " + id, "CS", credits, "Fall");
    }

    ArrayList<String> profs = new ArrayList<>();


    private TimeSlot slot(int sh, int sm, int eh, int em, Day... days) {
        return new TimeSlot(
                LocalTime.of(sh, sm),
                LocalTime.of(eh, em),
                Set.of(days)
        );
    }

    private Section createTestSection(String courseCode, int credits, Day day,
                                      int startHour, int startMin, int endHour, int endMin) {
        // Create a simple Course for testing
        Course course = new Course(1000, courseCode, "CS", credits, "Fall");

        // Create TimeSlot
        TimeSlot timeSlot = new TimeSlot(
                LocalTime.of(startHour, startMin),
                LocalTime.of(endHour, endMin),
                Set.of(day)
        );

        // Use 'A' as sectionID, no professor needed for test
        profs.add("Dr.Smith");
        return new Section(course, 'A', profs, 30, 0, timeSlot);
    }

    private Section section(Course c, char id, TimeSlot t) {
        profs.add("Dr. Smith");
        return new Section(c, id, profs, 30, 10, t);
    }

    private Section fullSection(Course c, char id, TimeSlot t){
        profs.add("Dr. Smith");
        return new Section(c, id, profs, 30, 30, t);
    }

    private Activity activity(String name, TimeSlot t) {
        return new Activity(name, t);
    }

    private Schedule schedule() {
        return new Schedule(null, "Fall Schedule", "Fall");
    }

    // ---------- SECTION TESTS ----------

    @Test
    void addSection_success() {
        Schedule schedule = schedule();

        Section s = section(course(112, 3), 'A',
                slot(10, 0, 11, 15, Day.MONDAY));

        assertTrue(schedule.addSection(s));
        assertEquals(1, schedule.getScheduleSections().size());
    }

    @Test
    void addSection_timeConflict_fails() {
        Schedule schedule = schedule();

        Section s1 = section(course(112, 3), 'A',
                slot(10, 0, 11, 0, Day.MONDAY));

        Section s2 = section(course(220, 3), 'A',
                slot(10, 30, 11, 30, Day.MONDAY));

        schedule.addSection(s1);
        assertFalse(schedule.addSection(s2));
    }

    @Test
    void addSection_fullSection_fails() {
        Schedule schedule = schedule();

        Section full = fullSection(course(112, 3), 'A',
                slot(10, 0, 11, 0, Day.MONDAY));

        assertFalse(schedule.addSection(full));
    }

    @Test
    void removeSection_success() {
        Schedule schedule = schedule();

        Section s = section(course(112, 3), 'A',
                slot(9, 0, 10, 0, Day.MONDAY));

        schedule.addSection(s);
        assertTrue(schedule.removeSection(s));
        assertEquals(0, schedule.getScheduleSections().size());
    }

    @Test
    void removeSection_notPresent_returnsFalse() {
        Schedule schedule = schedule();

        Section s = section(course(112, 3), 'A',
                slot(9, 0, 10, 0, Day.MONDAY));

        assertFalse(schedule.removeSection(s));
    }

    // ---------- ACTIVITY TESTS ----------

    @Test
    void addActivity_success() {
        Schedule schedule = schedule();

        Activity gym = activity("Gym",
                slot(12, 0, 13, 0, Day.MONDAY));

        assertTrue(schedule.addActivity(gym));
        assertEquals(1, schedule.getScheduleActivities().size());
    }

    @Test
    void addActivity_conflictsWithSection_fails() {
        Schedule schedule = schedule();

        Section cs112 = section(course(112, 3), 'A',
                slot(10, 0, 11, 0, Day.MONDAY));

        Activity chapel = activity("Chapel",
                slot(10, 30, 11, 30, Day.MONDAY));

        schedule.addSection(cs112);
        assertFalse(schedule.addActivity(chapel));
    }

    @Test
    void addActivity_conflictsWithActivity_fails() {
        Schedule schedule = schedule();

        Activity gym = activity("Gym",
                slot(12, 0, 13, 0, Day.MONDAY));

        Activity meeting = activity("Club Meeting",
                slot(12, 30, 13, 30, Day.MONDAY));

        schedule.addActivity(gym);
        assertFalse(schedule.addActivity(meeting));
    }

    @Test
    void removeActivity_success() {
        Schedule schedule = schedule();

        Activity gym = activity("Gym",
                slot(12, 0, 13, 0, Day.MONDAY));

        schedule.addActivity(gym);
        assertTrue(schedule.removeActivity(gym));
        assertEquals(0, schedule.getScheduleActivities().size());
    }

    // ---------- CREDIT TESTS ----------

    @Test
    void getTotalCredits_sumsCorrectly() {
        Schedule schedule = schedule();

        Section s1 = section(course(112, 3), 'A',
                slot(9, 0, 10, 0, Day.MONDAY));

        Section s2 = section(course(220, 4), 'A',
                slot(11, 0, 12, 0, Day.TUESDAY));

        schedule.addSection(s1);
        schedule.addSection(s2);

        assertEquals(7, schedule.getTotalCredits());
    }

    @Test
    void getTotalCredits_afterRemoval_updates() {
        Schedule schedule = schedule();

        Section s1 = section(course(112, 3), 'A',
                slot(9, 0, 10, 0, Day.MONDAY));

        schedule.addSection(s1);
        schedule.removeSection(s1);

        assertEquals(0, schedule.getTotalCredits());
    }

    @Test
    void addSection_updatesCalendar_correctly() {
        Schedule schedule = new Schedule(null, "Test", "Fall");
        Section s = createTestSection("CS101", 3, Day.MONDAY, 9, 0, 10, 0); // helper to create a Section with TimeSlot

        boolean added = schedule.addSection(s);
        assertTrue(added);

        boolean[][] calendar = schedule.getCalendar(); // you may need a getter for testing
        TimeSlot t = s.getTime();

        boolean[] days = t.getDayNumbers();
        boolean[] slots = t.getSlotNumbers();

        for (int slot = 0; slot < slots.length; slot++) {
            for (int day = 0; day < days.length; day++) {
                if (slots[slot] && days[day]) {
                    assertTrue(calendar[slot][day], "Calendar slot should be true for section");
                }
            }
        }
    }

    @Test
    void removeCalendar_clearsSlotsCorrectly() {
        Schedule schedule = new Schedule(null, "Test", "Fall");

        // Create a test section on Monday 9:00–10:00
        Section section = createTestSection("CS101", 3, Day.MONDAY, 9, 0, 10, 0);

        // Add it to the schedule
        assertTrue(schedule.addSection(section));

        // Verify that the calendar has slots set to true
        boolean[][] calendar = schedule.getCalendar();
        boolean[] days = section.getTime().getDayNumbers();
        boolean[] slots = section.getTime().getSlotNumbers();
        for (int slot = 0; slot < slots.length; slot++) {
            for (int day = 0; day < days.length; day++) {
                if (slots[slot] && days[day]) {
                    assertTrue(calendar[slot][day], "Calendar slot should be true before removal");
                }
            }
        }

        // Remove the section
        assertTrue(schedule.removeSection(section));

        // Verify that the same slots are now false
        calendar = schedule.getCalendar(); // refresh
        for (int slot = 0; slot < slots.length; slot++) {
            for (int day = 0; day < days.length; day++) {
                if (slots[slot] && days[day]) {
                    assertFalse(calendar[slot][day], "Calendar slot should be false after removal");
                }
            }
        }
    }

    // ---------- ERROR MESSAGING ----------

    @Test
    void addSection_timeConflict_setsErrorMessage() {
        Schedule schedule = schedule();

        Section s1 = section(course(112,3),'A',
                slot(9,0,10,0,Day.MONDAY));

        Section s2 = section(course(220,3),'A',
                slot(9,30,10,30,Day.MONDAY));

        schedule.addSection(s1);

        assertFalse(schedule.addSection(s2));
        assertTrue(schedule.getErrorMessage().contains("conflicts"));
    }

    @Test
    void addSection_fullSection_setsErrorMessage() {
        Schedule schedule = schedule();

        Section full = fullSection(course(112, 3), 'A',
                slot(9,0,10,0, Day.MONDAY));

        assertFalse(schedule.addSection(full));

        assertTrue(schedule.getErrorMessage().contains("full"));
    }

    @Test
    void addSection_activityConflict_setsErrorMessage() {
        Schedule schedule = schedule();

        Activity gym = activity("Gym",
                slot(10,0,11,0,Day.MONDAY));

        Section cs112 = section(course(112,3),'A',
                slot(10,30,11,30,Day.MONDAY));

        schedule.addActivity(gym);

        assertFalse(schedule.addSection(cs112));

        assertTrue(schedule.getErrorMessage().contains("activity"));
    }

    @Test
    void addActivity_sectionConflict_setsErrorMessage() {
        Schedule schedule = schedule();

        Section cs112 = section(course(112,3),'A',
                slot(10,0,11,0,Day.MONDAY));

        Activity gym = activity("Gym",
                slot(10,30,11,30,Day.MONDAY));

        schedule.addSection(cs112);

        assertFalse(schedule.addActivity(gym));

        assertTrue(schedule.getErrorMessage().contains("section"));
    }

    @Test
    void addActivity_activityConflict_setsErrorMessage() {
        Schedule schedule = schedule();

        Activity gym = activity("Gym",
                slot(12,0,13,0,Day.MONDAY));

        Activity meeting = activity("Meeting",
                slot(12,30,13,30,Day.MONDAY));

        schedule.addActivity(gym);

        assertFalse(schedule.addActivity(meeting));

        assertTrue(schedule.getErrorMessage().contains("activity"));
    }

    @Test
    void addSection_success_clearsPreviousError() {
        Schedule schedule = schedule();

        Section full = fullSection(course(112,3),'A',
                slot(9,0,10,0,Day.MONDAY));

        schedule.addSection(full);

        Section s2 = section(course(220,3),'A',
                slot(11,0,12,0,Day.MONDAY));

        assertTrue(schedule.addSection(s2));

        assertNull(schedule.getErrorMessage());
    }


    // ---------- DAYS WITHOUT CLASS TESTS ----------

    @Test
    void getDaysWithoutClass_emptySchedule_returns5() {
        Schedule schedule = schedule();

        assertEquals(5, schedule.getDaysWithoutClass());
    }

    @Test
    void getDaysWithoutClass_oneDayWithClass_returns4() {
        Schedule schedule = schedule();

        Section s = section(course(112, 3), 'A',
                slot(9, 0, 10, 0, Day.MONDAY));

        schedule.addSection(s);

        assertEquals(4, schedule.getDaysWithoutClass());
    }

    @Test
    void getDaysWithoutClass_twoDaysWithClass_returns3() {
        Schedule schedule = schedule();

        Section s1 = section(course(112, 3), 'A',
                slot(9, 0, 10, 0, Day.MONDAY));

        Section s2 = section(course(220, 3), 'A',
                slot(11, 0, 12, 0, Day.WEDNESDAY));

        schedule.addSection(s1);
        schedule.addSection(s2);

        assertEquals(3, schedule.getDaysWithoutClass());
    }

    @Test
    void getDaysWithoutClass_allDaysHaveClass_returns0() {
        Schedule schedule = schedule();

        for (Day day : Day.values()) {
            Section s = section(course(112, 3), 'A',
                    slot(9, 0, 10, 0, day));
            schedule.addSection(s);
        }

        assertEquals(0, schedule.getDaysWithoutClass());
    }

    @Test
    void getDaysWithoutClass_multipleSectionsSameDay_countsOnce() {
        Schedule schedule = schedule();

        Section s1 = section(course(112, 3), 'A',
                slot(9, 0, 10, 0, Day.MONDAY));

        Section s2 = section(course(220, 3), 'A',
                slot(11, 0, 12, 0, Day.MONDAY));

        schedule.addSection(s1);
        schedule.addSection(s2);

        // Only Monday has class
        assertEquals(4, schedule.getDaysWithoutClass());
    }

    // ---------- LONGEST BREAK ----------

    @Test
    void getLongestBreak_emptySchedule_returns0() {
        Schedule schedule = schedule();

        assertEquals(0, schedule.getLongestBreak());
    }

    @Test
    void getLongestBreak_singleClassDay_returns0() {
        Schedule schedule = schedule();

        Section s = section(course(112, 3), 'A',
                slot(10, 0, 11, 0, Day.MONDAY));

        schedule.addSection(s);

        assertEquals(0, schedule.getLongestBreak());
    }

    @Test
    void getLongestBreak_twoClasses_sameDay() {
        Schedule schedule = schedule();

        Section s1 = section(course(112, 3), 'A',
                slot(9, 0, 10, 0, Day.MONDAY));

        Section s2 = section(course(220, 3), 'A',
                slot(12, 0, 13, 0, Day.MONDAY));

        schedule.addSection(s1);
        schedule.addSection(s2);

        // 10–12 = 2 hours
        assertEquals(120, schedule.getLongestBreak());
    }

    @Test
    void getLongestBreak_multipleDays_returnsLargest() {
        Schedule schedule = schedule();

        // Monday small break
        schedule.addSection(section(course(112, 3), 'A',
                slot(9, 0, 10, 0, Day.MONDAY)));

        schedule.addSection(section(course(220, 3), 'A',
                slot(11, 0, 12, 0, Day.MONDAY)));

        // Tuesday larger break
        schedule.addSection(section(course(330, 3), 'A',
                slot(8, 0, 9, 30, Day.TUESDAY)));

        schedule.addSection(section(course(340, 3), 'A',
                slot(14, 0, 15, 30, Day.TUESDAY)));

        // 9:30–2 = 270
        assertEquals(270, schedule.getLongestBreak());
    }

    @Test
    void getLongestBreak_adjacentClasses_returns0() {
        Schedule schedule = schedule();

        Section s1 = section(course(112, 3), 'A',
                slot(9, 0, 10, 0, Day.MONDAY));

        Section s2 = section(course(220, 3), 'A',
                slot(10, 0, 11, 0, Day.MONDAY));

        schedule.addSection(s1);
        schedule.addSection(s2);

        assertEquals(0, schedule.getLongestBreak());
    }

    @Test
    void getLongestBreak_threeClasses_returnsLargestGap() {
        Schedule schedule = schedule();

        schedule.addSection(section(course(101, 3), 'A',
                slot(8, 0, 9, 0, Day.MONDAY)));

        schedule.addSection(section(course(102, 3), 'A',
                slot(11, 0, 12, 0, Day.MONDAY)));

        schedule.addSection(section(course(103, 3), 'A',
                slot(15, 0, 16, 0, Day.MONDAY)));

        // breaks:
        // 9–11 = 120
        // 12–15 = 180
        assertEquals(180, schedule.getLongestBreak());
    }

    @Test
    void getLongestBreak_multiSlotClasses_correctGap() {
        Schedule schedule = schedule();

        // TR style longer classes
        schedule.addSection(section(course(201, 3), 'A',
                slot(8, 0, 9, 30, Day.TUESDAY)));

        schedule.addSection(section(course(202, 3), 'A',
                slot(14, 0, 15, 30, Day.TUESDAY)));

        // 9:30–2 = 270
        assertEquals(270, schedule.getLongestBreak());
    }

    @Test
    void getLongestBreak_labLengthClass() {
        Schedule schedule = schedule();

        // long lab
        schedule.addSection(section(course(301, 4), 'A',
                slot(8, 0, 11, 0, Day.MONDAY)));

        schedule.addSection(section(course(302, 3), 'A',
                slot(14, 0, 15, 0, Day.MONDAY)));

        // 11–2 = 180
        assertEquals(180, schedule.getLongestBreak());
    }

    @Test
    void getLongestBreak_breakAcrossMultipleDays_usesLargest() {
        Schedule schedule = schedule();

        // Monday small break
        schedule.addSection(section(course(101, 3), 'A',
                slot(9, 0, 10, 0, Day.MONDAY)));

        schedule.addSection(section(course(102, 3), 'A',
                slot(11, 0, 12, 0, Day.MONDAY)));

        // Wednesday large break
        schedule.addSection(section(course(103, 3), 'A',
                slot(8, 0, 9, 0, Day.WEDNESDAY)));

        schedule.addSection(section(course(104, 3), 'A',
                slot(15, 0, 16, 0, Day.WEDNESDAY)));

        // 9–15 = 360
        assertEquals(360, schedule.getLongestBreak());
    }

    @Test
    void getLongestBreak_variedSlots() {
        Schedule schedule = new Schedule(null, "Test", "Fall");

        // M/W/F: two 30-min slots (9-10 and 12-1)
        schedule.addSection(createTestSection("CS101", 3, Day.MONDAY, 9,0,10,0));
        schedule.addSection(createTestSection("CS101", 3, Day.MONDAY, 12,0,13,0));
        schedule.addSection(createTestSection("CS101", 3, Day.WEDNESDAY, 9,0,10,0));
        schedule.addSection(createTestSection("CS101", 3, Day.WEDNESDAY, 12,0,13,0));
        schedule.addSection(createTestSection("CS101", 3, Day.FRIDAY, 9,0,10,0));
        schedule.addSection(createTestSection("CS101", 3, Day.FRIDAY, 12,0,13,0));

        // T/R: three-slot classes (8-9:30, 14-15:30)
        schedule.addSection(createTestSection("CS102", 3, Day.TUESDAY, 8,0,9,30));
        schedule.addSection(createTestSection("CS102", 3, Day.TUESDAY, 14,0,15,30));
        schedule.addSection(createTestSection("CS102", 3, Day.THURSDAY, 8,0,9,30));
        schedule.addSection(createTestSection("CS102", 3, Day.THURSDAY, 14,0,15,30));

        // Longest break = 9:30–2 PM = 270 min
        assertEquals(270, schedule.getLongestBreak());
    }

}