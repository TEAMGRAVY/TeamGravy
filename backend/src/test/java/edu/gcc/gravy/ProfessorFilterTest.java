package edu.gcc.gravy;

import org.junit.jupiter.api.Test;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class ProfessorFilterTest {

    // Helper method to construct sections for testing.
    private Section makeSection(int id, String title, String department, char letter, String professor) {

        Course course = new Course(id, title, department, 3, "Fall");

        Section section = new Section();
        section.setCourse(course);
        section.setSectionID(letter);
        section.setProfessor(professor);

        return section;
    }

    @Test
    void getType() {

        ProfessorFilter filter = new ProfessorFilter("Hutchins");

        // The filter type should always be PROFESSOR
        assertEquals(FilterType.PROFESSOR, filter.getType());
    }

    @Test
    void apply() {

        List<Section> sections = new ArrayList<>();

        sections.add(makeSection(141,"Programming I","COMP",'A',"Hutchins"));
        sections.add(makeSection(210,"Data Structures","COMP",'B',"Hutchins"));
        sections.add(makeSection(101,"Calculus I","MATH",'A',"McIntyre"));

        ProfessorFilter filter = new ProfessorFilter("Hutchins");

        List<Section> results = filter.apply(sections);

        // Only the COMP sections should remain after filtering
        assertEquals(2, results.size());

        for (Section s : results) {
            assertEquals("Hutchins", s.getProfessor());
        }
    }

}