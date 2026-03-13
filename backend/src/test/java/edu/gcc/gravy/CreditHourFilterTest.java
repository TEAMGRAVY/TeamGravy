package edu.gcc.gravy;

import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class CreditHourFilterTest {

    // Helper method to construct sections for testing.
    private Section makeSection(int id, String title, String department, char letter, int creditHours) {

        Course course = new Course(id, title, department, creditHours, "Fall");

        Section section = new Section();
        section.setCourse(course);
        section.setSectionID(letter);

        return section;
    }

    @Test
    void getType() {

        CreditHourFilter filter = new CreditHourFilter(3);

        // The filter type should always be DEPARTMENT
        assertEquals(FilterType.CREDITHOUR, filter.getType());
    }

    @Test
    void apply() {

        List<Section> sections = new ArrayList<>();

        sections.add(makeSection(141,"Programming I","COMP",'A',3));
        sections.add(makeSection(210,"Data Structures","COMP",'B',3));
        sections.add(makeSection(101,"Calculus I","MATH",'A',4));
        sections.add(makeSection(101,"Calculus II","MATH",'B',4));
        sections.add(makeSection(101,"Advanced Programming","MATH",'B',3));

        CreditHourFilter filter = new CreditHourFilter(3);

        List<Section> results = filter.apply(sections);

        // Only the COMP sections should remain after filtering
        assertEquals(3, results.size());

        for (Section s : results) {
            assertEquals(3, s.getCourse().getCreditHours());
        }
    }

    @Test
    void applyWithEmptyList() {
        CreditHourFilter filter = new CreditHourFilter(3);

        List<Section> sections = new ArrayList<>();

        List<Section> results = filter.apply(sections);

        assertTrue(results.isEmpty());
    }

    @Test
    void applyWithNoMatches() {

        List<Section> sections = new ArrayList<>();

        sections.add(makeSection(101,"Calculus I","MATH",'A',4));
        sections.add(makeSection(102,"Calculus II","MATH",'B',4));
        sections.add(makeSection(101,"Independent Research","MATH",'A',2));

        CreditHourFilter filter = new CreditHourFilter(3);

        List<Section> results = filter.apply(sections);

        assertTrue(results.isEmpty());
    }

}