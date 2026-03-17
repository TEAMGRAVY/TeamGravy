package edu.gcc.gravy;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

public class TermFilterTest {

    private List<Section> sections;

    private Section makeSection(String term) {
        Course course = new Course(101, "Test Course", "CS", 3, term);
        TimeSlot slot = new TimeSlot(LocalTime.of(9, 0), LocalTime.of(10, 0), Set.of(Day.MONDAY));
        ArrayList<TimeSlot> slots = new ArrayList<>(List.of(slot));
        return new Section(course, 'A', new ArrayList<>(List.of("Dr. Smith")), 30, 10, slots, true, "Room 101");
    }

    @BeforeEach
    public void setUp() {
        sections = new ArrayList<>();
        sections.add(makeSection("2026_Spring"));
        sections.add(makeSection("2026_Spring"));
        sections.add(makeSection("2025_Fall"));
        sections.add(makeSection("2026_Fall"));
    }

    @Test
    public void testFilterType() {
        TermFilter filter = new TermFilter("2026_Spring");
        assertEquals(FilterType.TERM, filter.getType());
    }

    @Test
    public void testMatchingTermReturnsCorrectSections() {
        TermFilter filter = new TermFilter("2026_Spring");
        List<Section> results = filter.apply(sections);
        assertEquals(2, results.size());
        assertTrue(results.stream().allMatch(s -> s.getCourse().getTerm().equals("2026_Spring")));
    }

    @Test
    public void testNonMatchingTermReturnsEmpty() {
        TermFilter filter = new TermFilter("2024_Spring");
        List<Section> results = filter.apply(sections);
        assertTrue(results.isEmpty());
    }

    @Test
    public void testSingleMatchingTerm() {
        TermFilter filter = new TermFilter("2026_Fall");
        List<Section> results = filter.apply(sections);
        assertEquals(1, results.size());
        assertEquals("2026_Fall", results.get(0).getCourse().getTerm());
    }

    @Test
    public void testEmptyInputList() {
        TermFilter filter = new TermFilter("2026_Spring");
        List<Section> results = filter.apply(new ArrayList<>());
        assertTrue(results.isEmpty());
    }

    @Test
    public void testOriginalListUnmodified() {
        TermFilter filter = new TermFilter("2026_Spring");
        filter.apply(sections);
        assertEquals(4, sections.size());
    }
}
