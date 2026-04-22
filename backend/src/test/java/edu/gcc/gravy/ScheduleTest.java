package edu.gcc.gravy;

import org.junit.jupiter.api.Test;

import java.time.LocalTime;
import java.util.List;
import java.util.ArrayList;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class ScheduleTest {

    // ─────────────────────────────────────────────────────────────
    // HELPERS
    // ─────────────────────────────────────────────────────────────

    private int courseCounter = 100;

    /** Fresh professor list every call — avoids shared-list mutation between tests */
    private ArrayList<String> profs() {
        ArrayList<String> p = new ArrayList<>();
        p.add("Dr. Smith");
        return p;
    }

    private Course course(int id, int credits) {
        return new Course(id, "CS " + id, "CS", credits);
    }

    private TimeSlot slot(int sh, int sm, int eh, int em, Day... days) {
        return new TimeSlot(
                LocalTime.of(sh, sm),
                LocalTime.of(eh, em),
                Set.of(days)
        );
    }

    private Section section(Course c, char id, TimeSlot t) {
        return new Section(c, id, profs(), 30, 10,
                new ArrayList<>(List.of(t)), true, "", "Fall 2026");
    }

    private Section section(Course c, char id, ArrayList<TimeSlot> slots) {
        return new Section(c, id, profs(), 30, 10,
                slots, true, "", "Fall 2026");
    }

    private Section fullSection(Course c, char id, TimeSlot t) {
        return new Section(c, id, profs(), 30, 30,
                new ArrayList<>(List.of(t)), true, "", "Fall 2026");
    }

    private Section closedSection(Course c, char id, TimeSlot t) {
        return new Section(c, id, profs(), 30, 10,
                new ArrayList<>(List.of(t)), false, "", "Fall 2026");
    }

    private Activity activity(String name, TimeSlot t) {
        return new Activity(name, t);
    }

    private Schedule schedule() {
        return new Schedule(null, "Fall Schedule", "Fall");
    }

    // Unique course counter so two calls never accidentally share a Course object
    private Section uniqueSection(String title, int credits, Day day,
                                  int sh, int sm, int eh, int em) {
        Course c = new Course(courseCounter++, title, "CS", credits);
        return new Section(c, 'A', profs(), 30, 10,
                new ArrayList<>(List.of(slot(sh, sm, eh, em, day))),
                true, "", "Fall 2026");
    }

    // ─────────────────────────────────────────────────────────────
    // GETTERS
    // ─────────────────────────────────────────────────────────────

    @Test
    void getScheduleName_returnsCorrectName() {
        Schedule s = new Schedule(null, "Spring Schedule", "Spring");
        assertEquals("Spring Schedule", s.getScheduleName());
    }

    @Test
    void getScheduleTerm_returnsCorrectTerm() {
        Schedule s = new Schedule(null, "Spring Schedule", "Spring");
        assertEquals("Spring", s.getScheduleTerm());
    }

    // ─────────────────────────────────────────────────────────────
    // addSection — basic
    // ─────────────────────────────────────────────────────────────

    @Test
    void addSection_success() {
        Schedule s = schedule();
        assertTrue(s.addSection(section(course(112, 3), 'A', slot(10, 0, 11, 15, Day.MONDAY))));
        assertEquals(1, s.getScheduleSections().size());
    }

    @Test
    void addSection_differentCourses_bothSucceed() {
        Schedule s = schedule();
        assertTrue(s.addSection(section(course(112, 3), 'A', slot(9, 0, 10, 0, Day.MONDAY))));
        assertTrue(s.addSection(section(course(220, 3), 'A', slot(11, 0, 12, 0, Day.MONDAY))));
        assertEquals(2, s.getScheduleSections().size());
    }

    @Test
    void addSection_lastSlotClass_succeeds() {
        Schedule s = schedule();
        assertTrue(s.addSection(section(course(450, 3), 'A', slot(20, 30, 21, 30, Day.MONDAY))));
    }

    // ─────────────────────────────────────────────────────────────
    // addSection — closed section
    // ─────────────────────────────────────────────────────────────

    @Test
    void addSection_closedSection_fails() {
        Schedule s = schedule();
        assertFalse(s.addSection(closedSection(course(112, 3), 'A', slot(9, 0, 10, 0, Day.MONDAY))));
        assertTrue(s.getErrorMessage().contains("not open"));
    }

    /** Covers the concat("Alternate open section") branch inside the isOpen() block */
    @Test
    void addSection_closedSection_openAlternateListedInError() {
        Schedule s = schedule();
        Course shared = new Course(500, "Algorithms", "CS", 3);

        Section closedA = new Section(shared, 'A', profs(), 30, 10,
                new ArrayList<>(List.of(slot(9, 0, 10, 0, Day.MONDAY))), false, "", "Fall 2026");
        Section openB = new Section(shared, 'B', profs(), 30, 10,
                new ArrayList<>(List.of(slot(11, 0, 12, 0, Day.MONDAY))), true, "", "Fall 2026");
        shared.getSections().add(openB);

        assertFalse(s.addSection(closedA));
        assertTrue(s.getErrorMessage().contains("Alternate open section"));
    }

    /** Covers the isOpen() alternate loop with no open alternates (no concat happens) */
    @Test
    void addSection_closedSection_noOpenAlternate_noAlternateInError() {
        Schedule s = schedule();
        Course shared = new Course(501, "Compilers", "CS", 3);

        Section closedA = new Section(shared, 'A', profs(), 30, 10,
                new ArrayList<>(List.of(slot(9, 0, 10, 0, Day.MONDAY))), false, "", "Fall 2026");
        Section closedB = new Section(shared, 'B', profs(), 30, 10,
                new ArrayList<>(List.of(slot(11, 0, 12, 0, Day.MONDAY))), false, "", "Fall 2026");
        shared.getSections().add(closedB);

        assertFalse(s.addSection(closedA));
        assertFalse(s.getErrorMessage().contains("Alternate open section"));
    }

    // ─────────────────────────────────────────────────────────────
    // addSection — full section
    // ─────────────────────────────────────────────────────────────

    @Test
    void addSection_fullSection_fails() {
        Schedule s = schedule();
        assertFalse(s.addSection(fullSection(course(112, 3), 'A', slot(10, 0, 11, 0, Day.MONDAY))));
        assertTrue(s.getErrorMessage().contains("full"));
    }

    /** Covers the concat("Alternate open section") branch inside the isFull() block */
    @Test
    void addSection_fullSection_nonFullAlternateListedInError() {
        Schedule s = schedule();
        Course shared = new Course(502, "Networks", "CS", 3);

        Section fullA = new Section(shared, 'A', profs(), 30, 30,
                new ArrayList<>(List.of(slot(9, 0, 10, 0, Day.MONDAY))), true, "", "Fall 2026");
        Section nonFullB = new Section(shared, 'B', profs(), 30, 10,
                new ArrayList<>(List.of(slot(11, 0, 12, 0, Day.MONDAY))), true, "", "Fall 2026");
        shared.getSections().add(nonFullB);

        assertFalse(s.addSection(fullA));
        assertTrue(s.getErrorMessage().contains("Alternate open section"));
    }

    /** Covers the isFull() alternate loop where all alternates are also full (no concat) */
    @Test
    void addSection_fullSection_allAlternatesFull_noAlternateInError() {
        Schedule s = schedule();
        Course shared = new Course(503, "OS", "CS", 3);

        Section fullA = new Section(shared, 'A', profs(), 30, 30,
                new ArrayList<>(List.of(slot(9, 0, 10, 0, Day.MONDAY))), true, "", "Fall 2026");
        Section fullB = new Section(shared, 'B', profs(), 30, 30,
                new ArrayList<>(List.of(slot(11, 0, 12, 0, Day.MONDAY))), true, "", "Fall 2026");
        shared.getSections().add(fullB);

        assertFalse(s.addSection(fullA));
        assertFalse(s.getErrorMessage().contains("Alternate open section"));
    }

    // ─────────────────────────────────────────────────────────────
    // addSection — section time conflict
    // ─────────────────────────────────────────────────────────────

    @Test
    void addSection_timeConflict_fails() {
        Schedule s = schedule();
        s.addSection(section(course(112, 3), 'A', slot(10, 0, 11, 0, Day.MONDAY)));
        assertFalse(s.addSection(section(course(220, 3), 'A', slot(10, 30, 11, 30, Day.MONDAY))));
        assertTrue(s.getErrorMessage().contains("conflicts"));
    }

    @Test
    void addSection_conflictAtLastSlot_fails() {
        Schedule s = schedule();
        s.addSection(section(course(101, 3), 'A', slot(20, 0, 21, 0, Day.MONDAY)));
        assertFalse(s.addSection(section(course(102, 3), 'A', slot(20, 30, 21, 30, Day.MONDAY))));
    }

    /** Covers the concat("Alternate non-conflicting section") branch in the time-conflict block */
    @Test
    void addSection_timeConflict_nonConflictingAlternateListedInError() {
        Schedule s = schedule();
        Course shared = new Course(504, "AI", "CS", 3);

        s.addSection(section(course(220, 3), 'A', slot(9, 0, 10, 0, Day.MONDAY)));

        Section conflictA = new Section(shared, 'A', profs(), 30, 10,
                new ArrayList<>(List.of(slot(9, 30, 10, 30, Day.MONDAY))), true, "", "Fall 2026");
        Section clearB = new Section(shared, 'B', profs(), 30, 10,
                new ArrayList<>(List.of(slot(14, 0, 15, 0, Day.MONDAY))), true, "", "Fall 2026");
        shared.getSections().add(clearB);

        assertFalse(s.addSection(conflictA));
        assertTrue(s.getErrorMessage().contains("Alternate non-conflicting section"));
    }

    /** Covers conflict = true inside the time-conflict alternate loop */
    @Test
    void addSection_timeConflict_allAlternatesAlsoConflict_notListedInError() {
        Schedule s = schedule();
        Course shared = new Course(505, "Security", "CS", 3);

        s.addSection(section(course(220, 3), 'A', slot(9, 0, 10, 0, Day.MONDAY)));

        Section conflictA = new Section(shared, 'A', profs(), 30, 10,
                new ArrayList<>(List.of(slot(9, 30, 10, 30, Day.MONDAY))), true, "", "Fall 2026");
        Section conflictB = new Section(shared, 'B', profs(), 30, 10,
                new ArrayList<>(List.of(slot(9, 0, 10, 0, Day.MONDAY))), true, "", "Fall 2026");
        shared.getSections().add(conflictB);

        assertFalse(s.addSection(conflictA));
        assertFalse(s.getErrorMessage().contains("Alternate non-conflicting section"));
    }

    // ─────────────────────────────────────────────────────────────
    // addSection — activity conflict
    // ─────────────────────────────────────────────────────────────

    @Test
    void addSection_activityConflict_fails() {
        Schedule s = schedule();
        s.addActivity(activity("Gym", slot(10, 0, 11, 0, Day.MONDAY)));
        assertFalse(s.addSection(section(course(112, 3), 'A', slot(10, 30, 11, 30, Day.MONDAY))));
        assertTrue(s.getErrorMessage().contains("activity"));
    }

    /** Covers concat("Alternate non-conflicting section") inside the activity-conflict block */
    @Test
    void addSection_activityConflict_nonConflictingAlternateListedInError() {
        Schedule s = schedule();
        Course shared = new Course(506, "ML", "CS", 3);

        s.addActivity(activity("Gym", slot(10, 0, 11, 0, Day.MONDAY)));

        Section conflictA = new Section(shared, 'A', profs(), 30, 10,
                new ArrayList<>(List.of(slot(10, 30, 11, 30, Day.MONDAY))), true, "", "Fall 2026");
        Section clearB = new Section(shared, 'B', profs(), 30, 10,
                new ArrayList<>(List.of(slot(14, 0, 15, 0, Day.MONDAY))), true, "", "Fall 2026");
        shared.getSections().add(clearB);

        assertFalse(s.addSection(conflictA));
        assertTrue(s.getErrorMessage().contains("Alternate non-conflicting section"));
    }

    /** Covers conflict = true inside the activity-conflict alternate loop */
    @Test
    void addSection_activityConflict_alternateClashesWithSchedule_notListedInError() {
        Schedule s = schedule();
        Course shared = new Course(507, "PLang", "CS", 3);

        s.addSection(section(course(220, 3), 'A', slot(14, 0, 15, 0, Day.MONDAY)));
        s.addActivity(activity("Gym", slot(10, 0, 11, 0, Day.MONDAY)));

        Section conflictA = new Section(shared, 'A', profs(), 30, 10,
                new ArrayList<>(List.of(slot(10, 30, 11, 30, Day.MONDAY))), true, "", "Fall 2026");
        // Alternate B is clear of gym but clashes with the scheduled section
        Section conflictB = new Section(shared, 'B', profs(), 30, 10,
                new ArrayList<>(List.of(slot(14, 0, 15, 0, Day.MONDAY))), true, "", "Fall 2026");
        shared.getSections().add(conflictB);

        assertFalse(s.addSection(conflictA));
        assertFalse(s.getErrorMessage().contains("Alternate non-conflicting section"));
    }

    // ─────────────────────────────────────────────────────────────
    // addSection — success clears error
    // ─────────────────────────────────────────────────────────────

    @Test
    void addSection_success_clearsPreviousError() {
        Schedule s = schedule();
        s.addSection(fullSection(course(112, 3), 'A', slot(9, 0, 10, 0, Day.MONDAY)));
        assertTrue(s.addSection(section(course(220, 3), 'A', slot(11, 0, 12, 0, Day.MONDAY))));
        assertNull(s.getErrorMessage());
    }

    // ─────────────────────────────────────────────────────────────
    // addSection — duplicate / same-course checks
    // ─────────────────────────────────────────────────────────────

    @Test
    void addSection_duplicateSection_fails() {
        Schedule s = schedule();
        Section sec = section(course(112, 3), 'A', slot(9, 0, 10, 0, Day.MONDAY));
        s.addSection(sec);
        assertFalse(s.addSection(sec));
        assertTrue(s.getErrorMessage().contains("already in your schedule"));
        assertEquals(1, s.getScheduleSections().size());
    }

    @Test
    void addSection_sameCourseAlternateSection_fails() {
        Schedule s = schedule();
        Course shared = course(112, 3);
        Section sA = section(shared, 'A', slot(9, 0, 10, 0, Day.MONDAY));
        Section sB = section(shared, 'B', slot(11, 0, 12, 0, Day.TUESDAY));
        s.addSection(sA);
        assertFalse(s.addSection(sB));
        assertTrue(s.getErrorMessage().contains("already in your schedule"));
        assertEquals(1, s.getScheduleSections().size());
        assertEquals(3, s.getTotalCredits());
    }

    @Test
    void addSection_duplicateAfterRemoval_succeeds() {
        Schedule s = schedule();
        Section sec = section(course(112, 3), 'A', slot(9, 0, 10, 0, Day.MONDAY));
        s.addSection(sec);
        s.removeSection(sec);
        assertTrue(s.addSection(sec));
        assertEquals(1, s.getScheduleSections().size());
    }

    // ─────────────────────────────────────────────────────────────
    // removeSection
    // ─────────────────────────────────────────────────────────────

    @Test
    void removeSection_success() {
        Schedule s = schedule();
        Section sec = section(course(112, 3), 'A', slot(9, 0, 10, 0, Day.MONDAY));
        s.addSection(sec);
        assertTrue(s.removeSection(sec));
        assertEquals(0, s.getScheduleSections().size());
    }

    @Test
    void removeSection_notPresent_returnsFalse() {
        Schedule s = schedule();
        assertFalse(s.removeSection(section(course(112, 3), 'A', slot(9, 0, 10, 0, Day.MONDAY))));
    }

    // ─────────────────────────────────────────────────────────────
    // addActivity
    // ─────────────────────────────────────────────────────────────

    @Test
    void addActivity_success() {
        Schedule s = schedule();
        assertTrue(s.addActivity(activity("Gym", slot(12, 0, 13, 0, Day.MONDAY))));
        assertEquals(1, s.getScheduleActivities().size());
    }

    @Test
    void addActivity_conflictsWithSection_fails() {
        Schedule s = schedule();
        s.addSection(section(course(112, 3), 'A', slot(10, 0, 11, 0, Day.MONDAY)));
        assertFalse(s.addActivity(activity("Chapel", slot(10, 30, 11, 30, Day.MONDAY))));
        assertTrue(s.getErrorMessage().contains("section"));
    }

    @Test
    void addActivity_conflictsWithActivity_fails() {
        Schedule s = schedule();
        s.addActivity(activity("Gym", slot(12, 0, 13, 0, Day.MONDAY)));
        assertFalse(s.addActivity(activity("Club Meeting", slot(12, 30, 13, 30, Day.MONDAY))));
        assertTrue(s.getErrorMessage().contains("activity"));
    }

    /** Covers concat("Alternate non-conflicting section") inside addActivity */
    @Test
    void addActivity_sectionConflict_nonConflictingAlternateListedInError() {
        Schedule s = schedule();
        Course shared = new Course(508, "DB", "CS", 3);

        Section s1 = new Section(shared, 'A', profs(), 30, 10,
                new ArrayList<>(List.of(slot(10, 0, 11, 0, Day.MONDAY))), true, "", "Fall 2026");
        s.addSection(s1);

        Section clearB = new Section(shared, 'B', profs(), 30, 10,
                new ArrayList<>(List.of(slot(14, 0, 15, 0, Day.MONDAY))), true, "", "Fall 2026");
        shared.getSections().add(clearB);

        assertFalse(s.addActivity(activity("Meeting", slot(10, 30, 11, 30, Day.MONDAY))));
        assertTrue(s.getErrorMessage().contains("Alternate non-conflicting section"));
    }

    /** Covers outer if (!alternate.hasTimeConflict(activity)) == false path */
    @Test
    void addActivity_sectionConflict_alternateAlsoConflictsWithActivity_notListed() {
        Schedule s = schedule();
        Course shared = new Course(509, "Vision", "CS", 3);

        Section s1 = new Section(shared, 'A', profs(), 30, 10,
                new ArrayList<>(List.of(slot(10, 0, 11, 0, Day.MONDAY))), true, "", "Fall 2026");
        s.addSection(s1);

        Section conflictB = new Section(shared, 'B', profs(), 30, 10,
                new ArrayList<>(List.of(slot(10, 30, 11, 30, Day.MONDAY))), true, "", "Fall 2026");
        shared.getSections().add(conflictB);

        assertFalse(s.addActivity(activity("Meeting", slot(10, 30, 11, 30, Day.MONDAY))));
        assertFalse(s.getErrorMessage().contains("Alternate non-conflicting section"));
    }

    /** Covers conflict = true inside addActivity's inner schedule-scan loop */
    @Test
    void addActivity_sectionConflict_alternateClashesWithOtherScheduledSection_notListed() {
        Schedule s = schedule();
        Course shared = new Course(510, "Graphics", "CS", 3);

        Section s1 = new Section(shared, 'A', profs(), 30, 10,
                new ArrayList<>(List.of(slot(10, 0, 11, 0, Day.MONDAY))), true, "", "Fall 2026");
        s.addSection(s1);

        // Blocker occupies Tuesday 14:00 so alternate B will clash with it
        s.addSection(section(course(220, 3), 'A', slot(14, 0, 15, 0, Day.TUESDAY)));

        Section conflictB = new Section(shared, 'B', profs(), 30, 10,
                new ArrayList<>(List.of(slot(14, 0, 15, 0, Day.TUESDAY))), true, "", "Fall 2026");
        shared.getSections().add(conflictB);

        assertFalse(s.addActivity(activity("Meeting", slot(10, 30, 11, 30, Day.MONDAY))));
        assertFalse(s.getErrorMessage().contains("Alternate non-conflicting section"));
    }

    // ─────────────────────────────────────────────────────────────
    // removeActivity
    // ─────────────────────────────────────────────────────────────

    @Test
    void removeActivity_success() {
        Schedule s = schedule();
        Activity gym = activity("Gym", slot(12, 0, 13, 0, Day.MONDAY));
        s.addActivity(gym);
        assertTrue(s.removeActivity(gym));
        assertEquals(0, s.getScheduleActivities().size());
    }

    @Test
    void removeActivity_notPresent_returnsFalse() {
        Schedule s = schedule();
        assertFalse(s.removeActivity(activity("Gym", slot(12, 0, 13, 0, Day.MONDAY))));
    }

    // ─────────────────────────────────────────────────────────────
    // addCalendar direct calls — false-return paths
    // (unreachable via addSection/addActivity because their guards fire first)
    // ─────────────────────────────────────────────────────────────

    @Test
    void addCalendar_arrayList_conflictReturnsFalse() {
        Schedule s = schedule();
        s.addSection(section(course(112, 3), 'A', slot(9, 0, 10, 0, Day.MONDAY)));

        ArrayList<TimeSlot> conflicting = new ArrayList<>();
        conflicting.add(slot(9, 30, 10, 30, Day.MONDAY));
        assertFalse(s.addCalendar(conflicting));
    }

    @Test
    void addCalendar_singleSlot_conflictReturnsFalse() {
        Schedule s = schedule();
        s.addActivity(activity("Gym", slot(12, 0, 13, 0, Day.MONDAY)));
        assertFalse(s.addCalendar(slot(12, 30, 13, 30, Day.MONDAY)));
    }

    // ─────────────────────────────────────────────────────────────
    // Calendar state verification
    // ─────────────────────────────────────────────────────────────

    @Test
    void addSection_updatesCalendar_correctly() {
        Schedule s = schedule();
        Section sec = uniqueSection("CS101", 3, Day.MONDAY, 9, 0, 10, 0);
        assertTrue(s.addSection(sec));

        boolean[][] calendar = s.getCalendar();
        for (TimeSlot t : sec.getTime()) {
            boolean[] dayBits  = t.getDayNumbers();
            boolean[] slotBits = t.getSlotNumbers();
            for (int r = 0; r < slotBits.length; r++) {
                for (int c = 0; c < dayBits.length; c++) {
                    if (slotBits[r] && dayBits[c]) {
                        assertTrue(calendar[r][c], "Calendar slot should be true for section");
                    }
                }
            }
        }
    }

    @Test
    void removeCalendar_clearsSlotsCorrectly() {
        Schedule s = schedule();
        Section sec = uniqueSection("CS101", 3, Day.MONDAY, 9, 0, 10, 0);
        assertTrue(s.addSection(sec));
        assertTrue(s.removeSection(sec));

        boolean[][] calendar = s.getCalendar();
        for (TimeSlot t : sec.getTime()) {
            boolean[] dayBits  = t.getDayNumbers();
            boolean[] slotBits = t.getSlotNumbers();
            for (int r = 0; r < slotBits.length; r++) {
                for (int c = 0; c < dayBits.length; c++) {
                    if (slotBits[r] && dayBits[c]) {
                        assertFalse(calendar[r][c], "Calendar slot should be false after removal");
                    }
                }
            }
        }
    }

    @Test
    void addSection_multipleTimeSlots_updatesCalendarCorrectly() {
        Schedule s = schedule();

        TimeSlot lecture = new TimeSlot(LocalTime.of(9, 0),  LocalTime.of(10, 0),
                Set.of(Day.MONDAY, Day.WEDNESDAY, Day.FRIDAY));
        TimeSlot lab     = new TimeSlot(LocalTime.of(13, 0), LocalTime.of(14, 0),
                Set.of(Day.THURSDAY));

        ArrayList<TimeSlot> times = new ArrayList<>();
        times.add(lecture);
        times.add(lab);

        Section sec = new Section(new Course(101, "Test", "CS", 3), 'A', profs(),
                30, 10, times, true, "STEM 101", "Fall 2026");

        assertTrue(s.addSection(sec));

        boolean[][] calendar = s.getCalendar();
        for (TimeSlot t : sec.getTime()) {
            boolean[] dayBits  = t.getDayNumbers();
            boolean[] slotBits = t.getSlotNumbers();
            for (int r = 0; r < slotBits.length; r++) {
                for (int c = 0; c < dayBits.length; c++) {
                    if (slotBits[r] && dayBits[c]) {
                        assertTrue(calendar[r][c]);
                    }
                }
            }
        }
    }

    @Test
    void removeSection_multipleTimeSlots_clearsCalendar() {
        Schedule s = schedule();

        TimeSlot lecture = new TimeSlot(LocalTime.of(9, 0),  LocalTime.of(10, 0),
                Set.of(Day.MONDAY, Day.WEDNESDAY, Day.FRIDAY));
        TimeSlot lab     = new TimeSlot(LocalTime.of(13, 0), LocalTime.of(14, 0),
                Set.of(Day.THURSDAY));

        ArrayList<TimeSlot> times = new ArrayList<>();
        times.add(lecture);
        times.add(lab);

        Section sec = new Section(new Course(101, "Test", "CS", 3), 'A', profs(),
                30, 10, times, true, "STEM 101", "Fall 2026");

        s.addSection(sec);
        s.removeSection(sec);

        boolean[][] calendar = s.getCalendar();
        for (TimeSlot t : sec.getTime()) {
            boolean[] dayBits  = t.getDayNumbers();
            boolean[] slotBits = t.getSlotNumbers();
            for (int r = 0; r < slotBits.length; r++) {
                for (int c = 0; c < dayBits.length; c++) {
                    if (slotBits[r] && dayBits[c]) {
                        assertFalse(calendar[r][c]);
                    }
                }
            }
        }
    }

    // ─────────────────────────────────────────────────────────────
    // getTotalCredits
    // ─────────────────────────────────────────────────────────────

    @Test
    void getTotalCredits_sumsCorrectly() {
        Schedule s = schedule();
        s.addSection(section(course(112, 3), 'A', slot(9,  0, 10, 0, Day.MONDAY)));
        s.addSection(section(course(220, 4), 'A', slot(11, 0, 12, 0, Day.TUESDAY)));
        assertEquals(7, s.getTotalCredits());
    }

    @Test
    void getTotalCredits_afterRemoval_updates() {
        Schedule s = schedule();
        Section sec = section(course(112, 3), 'A', slot(9, 0, 10, 0, Day.MONDAY));
        s.addSection(sec);
        s.removeSection(sec);
        assertEquals(0, s.getTotalCredits());
    }

    // ─────────────────────────────────────────────────────────────
    // getDaysWithoutClass
    // ─────────────────────────────────────────────────────────────

    @Test
    void getDaysWithoutClass_emptySchedule_returns5() {
        assertEquals(5, schedule().getDaysWithoutClass());
    }

    @Test
    void getDaysWithoutClass_oneDayWithClass_returns4() {
        Schedule s = schedule();
        s.addSection(section(course(112, 3), 'A', slot(9, 0, 10, 0, Day.MONDAY)));
        assertEquals(4, s.getDaysWithoutClass());
    }

    @Test
    void getDaysWithoutClass_twoDaysWithClass_returns3() {
        Schedule s = schedule();
        s.addSection(section(course(112, 3), 'A', slot(9,  0, 10, 0, Day.MONDAY)));
        s.addSection(section(course(220, 3), 'A', slot(11, 0, 12, 0, Day.WEDNESDAY)));
        assertEquals(3, s.getDaysWithoutClass());
    }

    @Test
    void getDaysWithoutClass_allDaysHaveClass_returns0() {
        Schedule s = schedule();
        int id = 200;
        for (Day d : Day.values()) {
            s.addSection(section(new Course(id++, "X", "CS", 3), 'A',
                    slot(9, 0, 10, 0, d)));
        }
        assertEquals(0, s.getDaysWithoutClass());
    }

    @Test
    void getDaysWithoutClass_multipleSectionsSameDay_countsOnce() {
        Schedule s = schedule();
        s.addSection(section(course(112, 3), 'A', slot(9,  0, 10, 0, Day.MONDAY)));
        s.addSection(section(course(220, 3), 'A', slot(11, 0, 12, 0, Day.MONDAY)));
        assertEquals(4, s.getDaysWithoutClass());
    }

    // ─────────────────────────────────────────────────────────────
    // getLongestBreak
    // ─────────────────────────────────────────────────────────────

    @Test
    void getLongestBreak_emptySchedule_returns0() {
        assertEquals(0, schedule().getLongestBreak());
    }

    @Test
    void getLongestBreak_singleClassDay_returns0() {
        Schedule s = schedule();
        s.addSection(section(course(112, 3), 'A', slot(10, 0, 11, 0, Day.MONDAY)));
        assertEquals(0, s.getLongestBreak());
    }

    @Test
    void getLongestBreak_twoClasses_sameDay_120min() {
        Schedule s = schedule();
        s.addSection(section(course(112, 3), 'A', slot(9,  0, 10, 0, Day.MONDAY)));
        s.addSection(section(course(220, 3), 'A', slot(12, 0, 13, 0, Day.MONDAY)));
        assertEquals(120, s.getLongestBreak());
    }

    @Test
    void getLongestBreak_adjacentClasses_returns0() {
        Schedule s = schedule();
        s.addSection(section(course(112, 3), 'A', slot(9,  0, 10, 0, Day.MONDAY)));
        s.addSection(section(course(220, 3), 'A', slot(10, 0, 11, 0, Day.MONDAY)));
        assertEquals(0, s.getLongestBreak());
    }

    @Test
    void getLongestBreak_threeClasses_returnsLargestGap() {
        Schedule s = schedule();
        s.addSection(section(course(101, 3), 'A', slot(8,  0, 9,  0, Day.MONDAY)));
        s.addSection(section(course(102, 3), 'A', slot(11, 0, 12, 0, Day.MONDAY)));
        s.addSection(section(course(103, 3), 'A', slot(15, 0, 16, 0, Day.MONDAY)));
        // gaps: 120 and 180
        assertEquals(180, s.getLongestBreak());
    }

    @Test
    void getLongestBreak_multipleDays_returnsLargest() {
        Schedule s = schedule();
        s.addSection(section(course(112, 3), 'A', slot(9,  0, 10, 0, Day.MONDAY)));
        s.addSection(section(course(220, 3), 'A', slot(11, 0, 12, 0, Day.MONDAY)));
        s.addSection(section(course(330, 3), 'A', slot(8,  0, 9, 30, Day.TUESDAY)));
        s.addSection(section(course(340, 3), 'A', slot(14, 0, 15, 30, Day.TUESDAY)));
        assertEquals(270, s.getLongestBreak());
    }

    @Test
    void getLongestBreak_withLateNightClass() {
        Schedule s = schedule();
        s.addSection(section(course(101, 3), 'A', slot(9,  0, 10, 0, Day.MONDAY)));
        s.addSection(section(course(102, 3), 'A', slot(20, 30, 21, 30, Day.MONDAY)));
        assertEquals(630, s.getLongestBreak());
    }

    @Test
    void getLongestBreak_adjacentLateClasses() {
        Schedule s = schedule();
        s.addSection(section(course(101, 3), 'A', slot(19, 30, 20, 30, Day.MONDAY)));
        s.addSection(section(course(102, 3), 'A', slot(20, 30, 21, 30, Day.MONDAY)));
        assertEquals(0, s.getLongestBreak());
    }

    @Test
    void getLongestBreak_sectionWithMultipleTimeSlots() {
        Schedule s = schedule();
        ArrayList<TimeSlot> times = new ArrayList<>();
        times.add(slot(9,  0, 10, 0, Day.MONDAY));
        times.add(slot(14, 0, 15, 0, Day.MONDAY));
        s.addSection(section(course(200, 3), 'A', times));
        assertEquals(240, s.getLongestBreak()); // 10:00 → 14:00
    }

    @Test
    void getLongestBreak_handlesFiftyMinuteClasses() {
        Schedule s = schedule();
        s.addSection(section(course(101, 3), 'A', slot(12, 0, 12, 50, Day.MONDAY)));
        s.addSection(section(course(102, 3), 'A', slot(13, 0, 13, 50, Day.MONDAY)));
        s.addSection(section(course(103, 3), 'A', slot(15, 0, 15, 50, Day.MONDAY)));
        assertEquals(70, s.getLongestBreak()); // 1:50 → 3:00
    }

    @Test
    void getLongestBreak_breakAcrossMultipleDays_usesLargest() {
        Schedule s = schedule();
        s.addSection(section(course(101, 3), 'A', slot(9,  0, 10, 0, Day.MONDAY)));
        s.addSection(section(course(102, 3), 'A', slot(11, 0, 12, 0, Day.MONDAY)));
        s.addSection(section(course(103, 3), 'A', slot(8,  0, 9,  0, Day.WEDNESDAY)));
        s.addSection(section(course(104, 3), 'A', slot(15, 0, 16, 0, Day.WEDNESDAY)));
        assertEquals(360, s.getLongestBreak()); // 9:00 → 15:00
    }
}