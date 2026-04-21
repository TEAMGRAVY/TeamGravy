package edu.gcc.gravy;

import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class OpenFilterTest {

    @Test
    void getType() {
        OpenFilter filter = new OpenFilter(true);

        assertEquals(FilterType.OPEN, filter.getType());
    }

    @Test
    void apply() {
        // Minimal setup (only isOpen matters)
        Section openSection = new Section(
                null, 'A', new ArrayList<>(),
                30, 10, new ArrayList<>(),
                true, "Room 1", "Fall"
        );

        Section closedSection = new Section(
                null, 'B', new ArrayList<>(),
                30, 30, new ArrayList<>(),
                false, "Room 2", "Fall"
        );

        List<Section> sections = Arrays.asList(openSection, closedSection);

        // Filter for open sections
        OpenFilter openFilter = new OpenFilter(true);
        List<Section> openResults = openFilter.apply(sections);

        assertEquals(1, openResults.size());
        assertTrue(openResults.contains(openSection));
        assertFalse(openResults.contains(closedSection));

        // Filter for closed sections (covers other branch)
        OpenFilter closedFilter = new OpenFilter(false);
        List<Section> closedResults = closedFilter.apply(sections);

        assertEquals(1, closedResults.size());
        assertTrue(closedResults.contains(closedSection));
        assertFalse(closedResults.contains(openSection));
    }
}